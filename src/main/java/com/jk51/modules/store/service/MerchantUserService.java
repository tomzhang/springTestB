package com.jk51.modules.store.service;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.configuration.SessionConfig;
import com.jk51.model.order.Refund;
import com.jk51.model.order.SBGoodsPrebook;
import com.jk51.model.order.SManager;
import com.jk51.model.order.SMerchant;
import com.jk51.model.order.response.RefundQueryReq;
import com.jk51.model.role.Permission;
import com.jk51.modules.authority.service.ManagerService;
import com.jk51.modules.authority.service.PermissionService;
import com.jk51.modules.authority.service.RoleService;
import com.jk51.modules.distribution.service.DistributionService;
import com.jk51.modules.persistence.mapper.BTradesMapper;
import com.jk51.modules.persistence.mapper.SBGoodsPrebookMapper;
import com.jk51.modules.persistence.mapper.SManagerMapper;
import com.jk51.modules.persistence.mapper.SMerchantMapper;
import com.jk51.modules.trades.mapper.RefundMapper;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 商户增加用户
 * 作者: zhangkuncheng
 * 创建日期: 2017/5/27
 * 修改记录:
 */
@Service
public class MerchantUserService {

    private static final Logger log = LoggerFactory.getLogger(MerchantUserService.class);

    @Autowired
    private SManagerMapper managerMapper;
    @Autowired
    private SMerchantMapper merchantMapper;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DistributionService distributionService;
    @Autowired
    private SBGoodsPrebookMapper sbGoodsPrebookMapper;


    @Transactional
    public SManager insertManager(SManager sManager) {
        managerMapper.insertManager(sManager);
        return sManager;
    }
    public Integer updateManagerTo(SManager sManager){
        return managerMapper.updateManager(sManager);
    }
    public List<SManager> selectAll(Integer siteId, String username, String realname, Integer isActive) {
        List<SManager> managerList = managerMapper.selectAll(siteId, username, realname, isActive);
        return managerList;
    }
    public SManager selectBySelective(Integer id, String username, Integer siteId) {
        return managerMapper.selectBySelective(id, username, siteId);
    }
    public List<SManager> selectByUsername(Integer id, String username, Integer siteId) {
        return managerMapper.selectByUsername(id, username, siteId);
    }
    public String getUserNamebyPrimaryKey(Integer id, Integer site_id) {
        return managerMapper.getUserNamebyPrimaryKey(id, site_id);
    }
    public List<SMerchant> getMerchants(Integer siteId, String username, String password){
        return merchantMapper.getMerchants(siteId, username, password);
    }
    public List<SManager> getUserName(Integer siteId, String username, String password){
        return managerMapper.getUserName(siteId, username, password);
    }
    public int selectStatus(Integer seteId){
        return merchantMapper.selectStatus(seteId);
    }
    public void updateLoginCount(Integer siteId, String username, String password){
        managerMapper.updateLoginCount(siteId, username, password);
    }
    //添加预约商品
    public Integer insertSelective(SBGoodsPrebook bGoodsPrebook) {
        return sbGoodsPrebookMapper.insertSelective(bGoodsPrebook);
    }







    /*public String getUserName(Integer siteId, String username, String password, HttpServletRequest request,String IP) {
        //String IP = SessionConfig.getIpAddr(request);
        if (username.equals("admin")) {
            List<SMerchant> merchants = merchantMapper.getMerchants(siteId, username, password);
            if (merchants.size() > 0) {

                //判断商户登录状态
                if (merchants.get(0).getIs_frozen() != 0) {
                    return "该商家禁止登录，请联系管理员！";
                }

                request.getSession().setAttribute(SessionConfig.USER_ID_KEY, merchants.get(0).getId());
                request.getSession().setAttribute(SessionConfig.USER_NAME_KEY, merchants.get(0).getSeller_nick());
                request.getSession().setAttribute(SessionConfig.USER_TYPE, 110);
                request.getSession().setAttribute("loginType", "0");
                //勿删，session中的SessionConfig.USER_NAME_KEY和SessionConfig.USER_NAME_KEY会被其他操作修改，这里单独存放登录名
                request.getSession().setAttribute("change_pwd_uname", username);
                request.getSession().setAttribute("change_pwd_user_id", merchants.get(0).getId());

                //stringRedisTemplate.opsForValue().set(IP + "_" + SessionConfig.USER_ID_KEY, merchants.get(0).getId().toString(), SessionConfig.TIMEOUT, TimeUnit.MINUTES);
                //stringRedisTemplate.opsForValue().set(IP + "_" + SessionConfig.USER_NAME_KEY, merchants.get(0).getMerchant_name(), SessionConfig.TIMEOUT, TimeUnit.MINUTES);

                Session session = SecurityUtils.getSubject().getSession();
                session.setAttribute("siteId", merchants.get(0).getMerchant_id());
                session.setAttribute("platform", 120);
                session.setAttribute("name", merchants.get(0).getSeller_nick());
                session.setAttribute("id", merchants.get(0).getId());
                Integer isOpen = getDistributorAccess(merchants.get(0).getMerchant_id());
                session.setAttribute("isOpen",isOpen);

                //权限备用
                if ("admin".equals(merchants.get(0).getSeller_nick())) {
                    session.setAttribute("IsAdmin", "true");
                }

                return "200";
            } else {
                List<SManager> managers = managerMapper.getUserName(siteId, username, password);
                if (managers.size() > 0) {
                    //判断商户登录状态
                    if (merchantMapper.selectStatus(managers.get(0).getSiteId()) != 0) {//is_frozen
                        return "该商家禁止登录，请联系管理员！";
                    } else if (managers.get(0).getIsActive() != 1) {
                        return "您的用户已被禁止登陆,请联系管理员修改";
                    } else {
                        log.info("管理员登陆：[{}]", managers.get(0));
                    }

                    managerMapper.updateLoginCount(siteId, username, password);
                    request.getSession().setAttribute(SessionConfig.USER_ID_KEY, managers.get(0).getId());
                    request.getSession().setAttribute(SessionConfig.USER_NAME_KEY, managers.get(0).getUsername());
                    request.getSession().setAttribute("loginType", "1");
                    request.getSession().setAttribute(SessionConfig.USER_TYPE, 300);
                    //勿删，session中的SessionConfig.USER_NAME_KEY会被其他操作修改，这里单独存放登录名
                    request.getSession().setAttribute("change_pwd_uname", username);
                    request.getSession().setAttribute("change_pwd_user_id", managers.get(0).getId());

                    //stringRedisTemplate.opsForValue().set(IP + "_" + SessionConfig.USER_ID_KEY, managers.get(0).getId().toString(), SessionConfig.TIMEOUT, TimeUnit.MINUTES);
                    //stringRedisTemplate.opsForValue().set(IP + "_" + SessionConfig.USER_NAME_KEY, managers.get(0).getUsername(), SessionConfig.TIMEOUT, TimeUnit.MINUTES);
                    setSession(managers.get(0), request);
                    return "200";
                } else return "用户名或密码错误";
            }
        } else {
            List<SManager> managers = managerMapper.getUserName(siteId, username, password);
            if (managers.size() > 0) {

                //判断商户登录状态
                if (merchantMapper.selectStatus(managers.get(0).getSiteId()) != 0) {
                    return "该商家禁止登录，请联系管理员！";
                } else if (managers.get(0).getIsActive() != 1) {
                    return "您的用户已被禁止登陆,请联系管理员修改";
                } else {
                    log.info("管理员登陆：[{}]", managers.get(0));
                }

                managerMapper.updateLoginCount(siteId, username, password);
                request.getSession().setAttribute(SessionConfig.USER_ID_KEY, managers.get(0).getId());
                request.getSession().setAttribute(SessionConfig.USER_NAME_KEY, managers.get(0).getUsername());
                request.getSession().setAttribute("loginType", "1");
                request.getSession().setAttribute(SessionConfig.USER_TYPE, 300);
                //勿删，session中的SessionConfig.USER_NAME_KEY会被其他操作修改，这里单独存放登录名
                request.getSession().setAttribute("change_pwd_uname", username);
                request.getSession().setAttribute("change_pwd_user_id", managers.get(0).getId());


                //Map<String, Object> list = merchantRoleService.selectPermissionByM(siteId, managers.get(0).getId(),120);
                //List<RoleVO> roleVOList=storeRoleService.getRoleListBymanagerId(siteId, managers.get(0).getId(),120);

                //stringRedisTemplate.opsForValue().set(IP + "_" + SessionConfig.USER_ID_KEY, managers.get(0).getId().toString(), SessionConfig.TIMEOUT, TimeUnit.MINUTES);
                // stringRedisTemplate.opsForValue().set(IP + "_" + SessionConfig.USER_NAME_KEY, managers.get(0).getUsername(), SessionConfig.TIMEOUT, TimeUnit.MINUTES);
                setSession(managers.get(0), request);

                return "200";
            } else return "用户名或密码错误";
        }
    }*/


    /*private void setSession(SManager manager, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("siteId", manager.getSiteId());
        session.setAttribute("platform", 120);
        session.setAttribute("name", manager.getUsername());
        session.setAttribute("id", manager.getId());
        //权限备用
        Integer siteId = manager.getSiteId();
        Integer managerId = manager.getId();
        Integer platform = 120;

        Map<String, Object> map = new HashMap<>();
        map.put("siteId",siteId);
        map.put("managerId",managerId);
        map.put("platform",platform);
        Map<String, Object> list = managerService.selectPermission(map);
        List<Permission> listAll = permissionService.selectPermisssionAll();
        StringBuffer permisssionAll = new StringBuffer();
        for (Permission permission : listAll) {
            permisssionAll.append(permission.getAction() + ",");
        }
        session.setAttribute("permission", list);
        if ("admin".equals(manager.getUsername())) {
            session.setAttribute("IsAdmin", "true");
        }
        stringRedisTemplate.opsForValue().set("permissionAll", permisssionAll.toString());
        Integer isOpen = getDistributorAccess(manager.getSiteId());
        session.setAttribute("isOpen",isOpen);
    }*/

    /*private Integer getDistributorAccess(Integer siteId){

        Map<String, Object> map = distributionService.getDistributorBySiteId(siteId);
        ReturnDto returnDto = ReturnDto.buildSuccessReturnDto(map);
        Integer isOpen = 999;
        try{
            if(returnDto.getValue() != null){
                isOpen = Integer.parseInt(JacksonUtils.json2map(JacksonUtils.obj2json(returnDto.getValue())).get("is_open").toString());
                //isOpen = Integer.parseInt(JacksonUtils.json2map(JacksonUtils.obj2json(map.get("value"))).get("is_open").toString());
            }
        }catch (Exception e){
            log.error("获取失败" + e);
        }*/


        /*String url = servicePath + "/distribution/getStores/"+siteId;
        Map<String,Object> map = null;
        Integer isOpen = 999;

        try{
            String resultJson = HttpClient.doHttpGet(url);
            map = JacksonUtils.json2map(resultJson);
            System.out.println(map.get("value"));
            if(map.get("value") != null){
                isOpen = Integer.parseInt(JacksonUtils.json2map(JacksonUtils.obj2json(map.get("value"))).get("is_open").toString());
            }
        }catch (Exception e){
            log.error("获取失败" + e);
        }*/

      /*  return isOpen;
    }*/

    /*private Integer getDistributorAccess(Integer siteId){
        String url = servicePath + "/distribution/getStores/"+siteId;
        Map<String,Object> map = null;
        Integer isOpen = 999;

        try{
            String resultJson = HttpClient.doHttpGet(url);
            map = JacksonUtils.json2map(resultJson);
            System.out.println(map.get("value"));
            if(map.get("value") != null){
                isOpen = Integer.parseInt(JacksonUtils.json2map(JacksonUtils.obj2json(map.get("value"))).get("is_open").toString());
            }
        }catch (Exception e){
            log.error("获取失败" + e);
        }

        return isOpen;
    }*/
}
