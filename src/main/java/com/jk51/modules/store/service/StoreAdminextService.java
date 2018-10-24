package com.jk51.modules.store.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.order.SPage;
import com.jk51.model.order.SStoreAdminext;
import com.jk51.modules.appInterface.util.ResultMap;
import com.jk51.modules.persistence.mapper.SStoreAdminextMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 门店后台查询用户退款列表
 * 作者: zhangkuncheng
 * 创建日期: 2017/5/16
 * 修改记录:
 */
@Service
public class StoreAdminextService {

    public static final Logger logger = LoggerFactory.getLogger(SRefundService.class);

    @Autowired
    private SStoreAdminextMapper adminextMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public Map<String,Object> selectClerkList(SStoreAdminext storeAdminext, SPage page){
        try {
            logger.info("---------JK-SERVICEpagexxxxxxxxxxxxxxx---------:{}", JacksonUtils.obj2json(page));
        } catch (Exception e) {

        }
        Map<String,Object> map = new HashMap<>();
        page.setRowsIndex(new Long(page.getPageIndex()-1l)*page.getPageSize());
        storeAdminext.setIs_del(0);
        List<SStoreAdminext> list = adminextMapper.selectClerkList(storeAdminext,page);

        page.setPrePageIndex(page.getPageIndex()-1l);
        page.setNextPageIndex(page.getPageIndex()+1l);
        page.setRowsIndex(new Long(page.getPageIndex()-1l)*page.getPageSize());
        page.setCount(adminextMapper.selectClerkCount(storeAdminext));
        page.setPageCount((page.getCount()-1l)/page.getPageSize()+1l);

        map.put("clerkList",list);
        map.put("page",page);

        return map;

    }
    /*public List<SStoreAdminext> selectClerkList(SStoreAdminext storeAdminext, SPage page){

        page.setRowsIndex(new Long(page.getPageIndex()-1l)*page.getPageSize());
        storeAdminext.setIs_del(0);
        return adminextMapper.selectClerkList(storeAdminext,page);

    }

    public SPage selectClerkCount(SStoreAdminext storeAdminext,SPage page){

        page.setPrePageIndex(page.getPageIndex()-1l);
        page.setNextPageIndex(page.getPageIndex()+1l);
        page.setRowsIndex(new Long(page.getPageIndex()-1l)*page.getPageSize());
        page.setCount(adminextMapper.selectClerkCount(storeAdminext));
        page.setPageCount((page.getCount()-1l)/page.getPageSize()+1l);

        return page;

    }*/

    public SStoreAdminext selectClerkByInviteCode(Integer siteId,String inviteCode){


        List<SStoreAdminext> adminexts1 = adminextMapper.selectClerkListByInviteCode(siteId,inviteCode);

        if(adminexts1 != null && adminexts1.size() == 1){
            return adminexts1.get(0);
        }else{
            List<SStoreAdminext> adminexts2 = adminextMapper.selectClerkListLikeByInviteCode(siteId,inviteCode);

            if(adminexts2 != null && adminexts2.size() == 1){
                return adminexts2.get(0);
            }
        }

        return null;
    }

    public int eidtAvatar(Integer siteId, Integer id, String avatar) {
        return adminextMapper.editAvatar(siteId, id, avatar);
    }

    public Map queryItem(Integer siteId, Integer id) {
        return adminextMapper.queryItem(siteId, id);
    }

    public Map forgetPwd(Integer siteId, String phone, String pwd, String code) {
        String c = stringRedisTemplate.opsForValue().get("_app_pwd_code" + phone) + "";
        if (!c.equals(code)) return ResultMap.errorResult("验证码不正确");
        int i = adminextMapper.forgetPwd(siteId, phone, pwd);
        if (i == 1) return ResultMap.successResult();
        return ResultMap.errorResult("用户不存在");
    }

    public void saveForgetPwdCode(String phone, String code) {
        stringRedisTemplate.opsForValue().set("_app_pwd_code" + phone, code, 3, TimeUnit.MINUTES);
    }
}
