package com.jk51.modules.store.service;

import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.des.SEncryptUtils;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.im.SGeTuiPush;
import com.jk51.model.Group;
import com.jk51.model.clerkvisit.BVisitMessage;
import com.jk51.model.order.*;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.role.ManagerHasRole;
import com.jk51.modules.appInterface.service.AppClerkVisitService;
import com.jk51.modules.appInterface.service.AppMemberService;
import com.jk51.modules.authority.mapper.ManagerHasRoleMapper;
import com.jk51.modules.authority.service.ManagerService;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.service.CouponFilterService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.im.event.DelayedMessageProduce;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.merchant.mapper.ClerkReturnVisitMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.persistence.mapper.*;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.service.PromotionsActivityService;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.userScenarios.service.QrcodeService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 门店后台查询用户退款列表
 * 作者: zhangkuncheng
 * 创建日期: 2017/5/19
 * 修改记录:
 */
@Service
public class ClerkService {

    public static final Logger log = LoggerFactory.getLogger(ClerkService.class);

    @Autowired
    private SStoreAdminextMapper storeAdminextMapper;
    @Autowired
    private SBStoresMapper storeMapper;
    @Autowired
    private SGroupMapper groupMapper;
    @Autowired
    private SStoreAdminMapper storeAdminMapper;
    @Autowired
    private SChUserMapper chUserMapper;
    @Autowired
    private SChPharmacistMapper chPharmacistMapper;
    @Autowired
    private SGroupMemberMapper groupMemberMapper;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private StmpMetaMapper metaMapper;
    @Autowired
    private BTradesMapper bTradesMapper;
    @Autowired
    private SStoreAdminDeployMapper storeAdminDeployMapper;
    @Autowired
    private SGeTuiPush geTuiPush;
    @Autowired
    private QrcodeService qrcodeService;
    @Autowired
    private DelayedMessageProduce delayedMessageProduce;
    @Autowired
    private ManagerHasRoleMapper managerHasRoleMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    ClerkReturnVisitMapper clerkReturnVisitMapper;
    @Autowired
    CouponActivityMapper couponActivityMapper;
    @Autowired
    PromotionsActivityMapper promotionsActivityMapper;
    @Autowired
    AppMemberService appMemberService;
    @Autowired
    AppClerkVisitService appClerkVisitService;
    @Autowired
    PromotionsFilterService promotionsFilterService;
    @Autowired
    CouponFilterService couponFilterService;
    @Autowired
    MemberMapper memberMapper;
    @Autowired
    PromotionsActivityService promotionsActivityService;



    // key: storeId  value  : storeName
    private static Map<Integer, String> storeMap = new HashMap<>();
    //key:  storeadminId value :clerkname
    private static Map<Integer, SStoreAdminext> clerkMap = new HashMap<>();

    private Integer getInteger(String key, HttpServletRequest request) {
        try {
            return Integer.parseInt((String) request.getSession().getAttribute(key));
        } catch (ClassCastException e) {
            return (Integer) request.getSession().getAttribute(key);
        }
    }


    //查询所有店员
    public List<SClerkDetail> seleteSelectiv(Integer siteId, Integer storeId, String mobile, Date start, Date end) {
        List<SClerkDetail> storeAdminexts = storeAdminextMapper.selectSelectiv(siteId, storeId, mobile, start, end);
        return storeAdminexts;
    }

    public List<SStoreAdminext> seleteSelective(Integer siteId, Integer storeId, String mobile, Date start, Date end) {
        List<SStoreAdminext> storeAdminexts = storeAdminextMapper.selectSelective(siteId, storeId, mobile, start, end);
        return storeAdminexts;
    }

    public SBStores getStoreName(Integer siteId, Integer storeId) {

        SBStores store = storeMapper.selectByPrimaryKey(siteId, storeId);
        return store;
    }

    public List<Group> selectGroups(Integer siteId) {
        return groupMapper.selectBySiteId(siteId);
    }

    //添加店员
    public SStoreAdmin selectByMobile(String mobile) {
        return storeAdminMapper.selectByMobile(mobile);
    }

    public int insertSelective(SStoreAdmin storeAdmin) {
        return storeAdminMapper.insertSelective(storeAdmin);
    }

    public int insertSelectiveTwo(SStoreAdminext storeAdminext) {
        return storeAdminextMapper.insertSelective(storeAdminext);
    }

    public int insertSelectiveThree(SChUser chUser) {
        return chUserMapper.insertSelective(chUser);
    }

    public int insertSelectiveFore(SChPharmacist chPharmacist) {
        return chPharmacistMapper.insertSelective(chPharmacist);
    }

    public List<String> selectInviteCodeMax(Integer site_id) {
        return storeAdminextMapper.selectInviteCodeMax(site_id);
    }

    public void insertListTo(List<SGroupMember> list) {
        groupMemberMapper.insertList(list);
    }

    public SChUser selectByMobileCh(String mobile) {
        return chUserMapper.selectByMobile(mobile);
    }

    public SStoreAdminext selectByMobileExt(String mobile) {
        return storeAdminextMapper.selectByMobileExt(mobile);
    }

    //修改
    public Map<String, Object> selectByPrimeryKey(Integer siteId, Integer storeadminId) {
        Map<String, Object> map = new HashMap<>();
        map.put("admin", storeAdminMapper.selectByPrimaryKey(storeadminId, siteId));
        map.put("adminext", storeAdminextMapper.selectByStoreAdminKey(siteId, storeadminId));
        return map;
    }

    /**
     * 店员insert
     *
     * @param storeAdmin
     * @param storeAdminext
     * @param roleIds
     * @param groupIds
     * @return
     */
    @Transactional
    public String insert(SStoreAdmin storeAdmin, SStoreAdminext storeAdminext, List<Integer> roleIds, List<Integer> groupIds, Integer siteId, Integer storeId) throws Exception {
        log.info("===SERVICE-clerk日志===:{},{},{},{}", JacksonUtils.obj2json(storeAdmin).toString(), JacksonUtils.obj2json(storeAdminext).toString(), roleIds, groupIds);
       /* Integer siteId = getInteger("siteId", request);
        Integer storeId = getInteger("storeId", request);*/
      /*  Integer siteId = 100176;
        Integer storeId = 15;*/

        storeAdmin.setSite_id(siteId);
        storeAdmin.setStore_id(storeId);
        SBStores stores = getStoreName(siteId, storeId);

        Integer code = 200;
        String msg = "添加成功";

        SStoreAdmin sad = storeAdminMapper.selectByMobile(storeAdminext.getMobile());
        if (!StringUtil.isEmpty(sad) && false == sad.getIs_del()) {
            code = 400;
            msg = storeAdminext.getMobile() + "：手机号码已使用!";
            return "{" +
                "code:" + code +
                ", msg:'" + msg + '\'' + '}';
        }
        storeAdmin.setUser_name(storeAdminext.getMobile());
        storeAdmin.setUser_pwd(SEncryptUtils.encryptToSHA1(SEncryptUtils.encryptToSHA1(storeAdmin.getUser_pwd())));
        storeAdmin.setUser_type(2);
        storeAdmin.setChat(0);
        int i = storeAdminMapper.insertSelective(storeAdmin);
        if (0 != i) {
            storeAdminext.setStoreadmin_id(storeAdmin.getId());
            storeAdminext.setSite_id(storeAdmin.getSite_id());
            storeAdminext.setStore_id(storeAdmin.getStore_id());

            Integer maxInvitCode = null;
            try {
                maxInvitCode = storeAdminextMapper.selectInviteCodeMax(storeAdmin.getSite_id()).parallelStream()
                    .filter(item -> StringUtil.isNotBlank(item))
                    .map(item -> item.substring(item.length() - 5))
                    .filter(item -> item.matches("^\\d+$"))
                    .mapToInt(item -> Integer.valueOf(item)).max().getAsInt();
            } catch (NoSuchElementException e) {
                maxInvitCode = 0;
            }

            String ivcode = "000000" + (maxInvitCode + 1);
            storeAdminext.setClerk_invitation_code(storeAdmin.getStore_id() + "_" + ivcode.substring(ivcode.length() - 5));

            log.info(storeAdmin.toString());
            int j = storeAdminextMapper.insertSelective(storeAdminext);
            //生成二维码
            Map<String, Object> qrcodeParam = new HashMap();
            qrcodeParam.put("siteId", storeAdminext.getSite_id());
            qrcodeParam.put("sceneStr", storeAdminext.getClerk_invitation_code());
            qrcodeParam.put("type", 1);
            qrcodeService.createQrcode(qrcodeParam);

            if (0 != j) {
//                ChPharmacist chPharmacist = new ChPharmacist();
                SChUser chUser = new SChUser();
                chUser.setCountry("China");
                chUser.setProvince(stores.getProvince());
                chUser.setPhone(storeAdminext.getMobile());
                chUser.setUserType(2);

                //插入ch_user表
                int insertChUser = chUserMapper.insertSelective(chUser);
                if (insertChUser != 0) {
                    //插入药师表
                    SChPharmacist chPharmacist = new SChPharmacist();
                    chPharmacist.setUserId(chUser.getId().intValue());
                    chPharmacist.setSiteId(stores.getSiteId());
                    chPharmacist.setStoreId(storeId);
                    chPharmacist.setStoreUserId(storeAdminext.getId());
                    chPharmacist.setStoreName(stores.getName());
                    int pharmacist = chPharmacistMapper.insertSelective(chPharmacist);
                    log.info("chuser的id是[{}]", chUser.getId());
                    if (pharmacist == 0) {
                        return "{" +
                            "code:500,msg:'添加店员失败，药师表插入失败'}";
                    }
                }
                if (groupIds != null) {
                    groupMemberMapper.insertList(insertList(storeAdmin, groupIds));
                }
                if (roleIds != null && roleIds.size() != 0) {
                    List<ManagerHasRole> list = doHttpPost(storeAdmin, roleIds);
                    String httpResponse = managerService.addRoleToManager(list);
                    // String httpResponse = doHttpPost(pathUrlConfig.getPathUrl() + "/manager/addManagerHasRole", storeAdmin, roleIds);
                    if (httpResponse.contains("success")) {
                        log.info("####店员新增成功####增加店员：" + storeAdmin.getUser_name());
                        code = 200;
                    }
                } else {
                    code = 200;
                }

                return
                    "{code:" + code +
                        ",msg:'" + msg + '\'' + '}';

            } else {
                throw new RuntimeException("添加店员失败");
            }
        } else {
            throw new RuntimeException("数据库操作失败");
        }
//        log.error("添加店员失败");
//        return "500";
    }

    /**
     * 店员update
     *
     * @param storeAdmin
     * @param storeAdminext
     * @param roleIds
     * @return
     * @throws Exception
     */
    @Transactional
    public String update(SStoreAdmin storeAdmin, SStoreAdminext storeAdminext, List<Integer> roleIds, List<Integer> groupIds) throws Exception {
        String pwd = storeAdmin.getUser_pwd();
        storeAdmin.setUser_pwd(StringUtil.isBlank(storeAdmin.getUser_pwd()) ? null : EncryptUtils.encryptToSHA(EncryptUtils.encryptToSHA(storeAdmin.getUser_pwd())));

        storeAdmin.setUser_name(storeAdminext.getMobile());
        int i = storeAdminMapper.updateByPrimaryKeySelective(storeAdmin);
        if (i == 0) return "500";
        storeAdminext.setSite_id(storeAdmin.getSite_id());
        storeAdminext.setStoreadmin_id(storeAdmin.getId());
        storeAdminext.setStore_id(storeAdmin.getStore_id());
        storeAdminext.setStoreadmin_status(storeAdmin.getStatus());
        int j = storeAdminextMapper.updateByPrimaryKeySelective(storeAdminext);
        if (groupIds != null && groupIds.size() != 0) {
            SGroupMember groupMember = null;
            List<SGroupMember> groupMembers = new ArrayList<>();
            for (Integer groupId : groupIds) {
                groupMember = new SGroupMember();
                groupMember.setSite_id(storeAdmin.getSite_id());
                groupMember.setStore_admin_id(String.valueOf(storeAdmin.getId()));
                groupMember.setGroup_id(groupId);
                groupMember.setCreate_at(new Date());
                groupMember.setIs_close(0);
                groupMembers.add(groupMember);
            }
            int k = groupMemberMapper.updateList(storeAdmin.getSite_id(), storeAdmin.getId());
            int l = groupMemberMapper.insertList(groupMembers);
            if (l == 0) throw new RuntimeException();
        }

        if (storeAdmin.getUser_pwd() != null) {//修改密码通知APP重新登录
            try {
                notifyChangePassword(storeAdmin.getSite_id(), storeAdmin.getId(), storeAdmin.getStore_id(), pwd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //String url = pathUrlConfig.getPathUrl() + "/manager/addManagerHasRole";
        if (0 != i && 0 != j) {
            if (roleIds != null && roleIds.size() != 0) {
                List<ManagerHasRole> list = doHttpPost(storeAdmin, roleIds);
                managerHasRoleMapper.deleteByManagerKey(storeAdmin.getSite_id(), 130, storeAdmin.getStore_id(), storeAdmin.getId());
                String httpResult = managerService.addRoleToManager(list);
                //String httpResult = doHttpPost( storeAdmin, roleIds);
                if (httpResult.equals("success")) {
                    return "200";
                } else {
                    throw new RuntimeException("远程调用失败" + httpResult);
                }
            } else {
                return "您未设置用户角色";

            }

        } else {
            throw new RuntimeException("数据库操作失败");
        }
    }

    /**
     * 店员详情
     *
     * @throws Exception
     */
    public List<SBStores> selectStores(Integer siteId, Integer storeId) {
        return storeMapper.selectAllStore(siteId, null, null, storeId);
    }

    public List<Integer> selectGroupIdsTo(Integer siteId, Integer storeadminId) {
        return groupMemberMapper.selectGroupIds(siteId, storeadminId);
    }

    public SClerkDetail selectClerkDetail(Integer siteId, Integer id) {
        return storeAdminMapper.selectClerkDetail(siteId, id);
    }

    /**
     * 门店调配
     *
     * @throws Exception
     */
    public String checkMateCode(Integer siteId, Integer storeId, String metaCode) {

        String metaKey = String.valueOf(storeId);

        SMeta meta = metaMapper.selectBySiteIdAndKey(siteId, metaKey);
        if (null == meta)
            return "500";
        try {
            boolean codeflag = meta.getMetaVal().equals(EncryptUtils.encryptToSHA(EncryptUtils.encryptToSHA(metaCode)));
            if (codeflag) return "200";
            return "500";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "500";
    }

    /**
     * 门店调配  更改店员storeId字段并插入storeAdminDeploy信息
     *
     * @return
     */
    @Transactional
    public Map<String, Object> storeDeploy(Integer siteId, Integer storeAmidId, Integer storeId, Integer operatorId, Integer newStoreId, String merchantUser) {

        Map<String, Object> deployMap = new HashMap<>();

        Integer deployAble = bTradesMapper.deployAble(siteId, storeId, storeAmidId);
        if (deployAble != 0) {
            deployMap.put("code", 300);
            deployMap.put("msg", "该店员存在未结束订单，不可调配");
            return deployMap;
        }

        String clerkInvitationCode = null;

        try {

            SStoreAdminext storeAdminext = storeAdminextMapper.getStoreAdminExtById(siteId + "", storeAmidId + "");

            if (storeAdminext != null && StringUtil.isNotBlank(storeAdminext.getClerk_invitation_code()) && storeId != null) {
                clerkInvitationCode = newStoreId + "_" + storeAdminext.getClerk_invitation_code().split("_")[1];
            } else {
                log.error("调配失败,店员邀请码合成异常，请稍后重试或联系客服");
                deployMap.put("code", 500);
                deployMap.put("msg", "调配失败,店员信息异常，请稍后重试或联系客服");
                return deployMap;
            }

        } catch (Exception e) {
            log.error("调配失败,店员邀请码合成异常，请稍后重试或联系客服", e);
            deployMap.put("code", 500);
            deployMap.put("msg", "调配失败,店员信息异常，请稍后重试或联系客服");
            return deployMap;
        }

        int i = storeAdminMapper.updateStoreIdByPrimaryKey(siteId, storeAmidId, newStoreId, clerkInvitationCode);

        if (0 != i) {
            SStoreAdminDeploy storeAdminDeploy = new SStoreAdminDeploy();
            storeAdminDeploy.setSite_id(siteId);
            storeAdminDeploy.setStore_admin_id(storeAmidId);
            storeAdminDeploy.setPre_store_id(storeId);
            storeAdminDeploy.setNew_store_id(newStoreId);
            storeAdminDeploy.setOperator_id(operatorId);
            storeAdminDeploy.setCreate_time(new Date());

            storeAdminDeploy.setMerchant_user(merchantUser);

            int j = storeAdminDeployMapper.insertSelective(storeAdminDeploy);
            if (0 != j) {
                //通知app用户下线 强制重新登录
                try {
                    geTuiPush.noticeOtherAppQuit(storeAmidId);

                    notifyChangeStores(siteId, storeAmidId, newStoreId, storeId);
                } catch (Exception e) {
                    log.error("deploy:强制下线失败");
                }

                deployMap.put("code", 200);
                deployMap.put("msg", "调配成功");
                managerHasRoleMapper.deleteByManagerKey(siteId, 130, null, storeAmidId);
                return deployMap;
            } else
                throw new RuntimeException("门店调配失败");
        }
        deployMap.put("code", 500);
        deployMap.put("msg", "调配失败，请稍后重试");
        return deployMap;
    }


    /**
     * 通知调配门店
     */
    public void notifyChangeStores(Integer siteId, Integer storeAdminId, Integer newStoreId, Integer oldStoreId) throws Exception {
        Map messageMap = new HashMap();
        messageMap.put("messageType", PushType.NOTIFY_CHANGE_STORES.getValue());
        messageMap.put("siteId", String.valueOf(siteId));
        messageMap.put("storeAdminId", String.valueOf(storeAdminId));
        messageMap.put("storeId", String.valueOf(newStoreId));
        messageMap.put("oldStoreId", String.valueOf(oldStoreId));
        /*CloudQueue queue = CloudQueueFactory.create(NotifyShipmentPushServe.topicName);
        Message message = new Message();
        message.setMessageBody(JSON.toJSONString(messageMap));
        try {
            Message result = queue.putMessage(message);
            log.info("成功加入队列：" + NotifyShipmentPushServe.topicName);
        }catch (Exception e){
            log.debug("发送到消息队列失败 body:{} error:{}", message.getMessageBodyAsString(), e.getMessage());
        }*/

        delayedMessageProduce.delayedMessageProduce(messageMap, String.valueOf(siteId), PushType.NOTIFY_CHANGE_STORES.getValue(), null, null);
    }

    /**
     * 通知密码重置
     */
    public void notifyChangePassword(Integer siteId, Integer storeAdminId, Integer storeId, String password) throws Exception {
        Map messageMap = new HashMap();
        messageMap.put("messageType", PushType.NOTIFY_CHANGE_PASSWORD.getValue());
        messageMap.put("siteId", String.valueOf(siteId));
        messageMap.put("storeAdminId", String.valueOf(storeAdminId));
        messageMap.put("storeId", String.valueOf(storeId));
        messageMap.put("password", password);
        /*CloudQueue queue = CloudQueueFactory.create(NotifyShipmentPushServe.topicName);
        Message message = new Message();
        message.setMessageBody(JSON.toJSONString(messageMap));
        try {
            Message result = queue.putMessage(message);
            log.info("成功加入队列：" + NotifyShipmentPushServe.topicName);
        }catch (Exception e){
            log.debug("发送到消息队列失败 body:{} error:{}", message.getMessageBodyAsString(), e.getMessage());
        }*/
        delayedMessageProduce.delayedMessageProduce(messageMap, String.valueOf(siteId), PushType.NOTIFY_CHANGE_PASSWORD.getValue(), null, null);
    }

    /**
     * 通知店员呗删除
     */
    public void constraintOut(Integer siteId, Integer storeAdminId, Integer storeId) throws Exception {
        Map messageMap = new HashMap();
        messageMap.put("messageType", PushType.CONSTRAINT_OUT.getValue());
        messageMap.put("siteId", String.valueOf(siteId));
        messageMap.put("storeAdminId", String.valueOf(storeAdminId));
        messageMap.put("storeId", String.valueOf(storeId));
        delayedMessageProduce.delayedMessageProduce(messageMap, String.valueOf(siteId), PushType.CONSTRAINT_OUT.getValue(), null, null);
    }

    public List<StoreDeployInfo> storeDeployInfo(Integer siteId, Integer storeId, String clerkName, String mobile) {

        List<SBStores> stores = selectStores(siteId, null);
        for (SBStores s : stores
            ) {
            storeMap.put(s.getId(), s.getName());
        }
        List<SStoreAdminext> storeAdminexts = storeAdminextMapper.selectBySiteId(siteId);
        for (SStoreAdminext sa : storeAdminexts
            ) {
            clerkMap.put(sa.getStoreadmin_id(), sa);
        }
        List<SStoreAdminDeploy> storeAdminDeploys = storeAdminDeployMapper.selectByStore(siteId, storeId, clerkName, mobile);

        List<StoreDeployInfo> storeDeployInfos = new ArrayList<>();
        StoreDeployInfo storeDeployInfo = null;
        for (SStoreAdminDeploy sad : storeAdminDeploys
            ) {
            storeDeployInfo = new StoreDeployInfo();
            SStoreAdminext ext = clerkMap.get(sad.getStore_admin_id());
            if (ext == null) continue;
            storeDeployInfo.setClerkName(ext.getName());
            storeDeployInfo.setClerkMobile(ext.getMobile());
            storeDeployInfo.setPerStore(storeMap.get(sad.getPre_store_id()));
            storeDeployInfo.setNewStore(storeMap.get(sad.getNew_store_id()));

            if (!StringUtil.isEmpty(sad.getMerchant_user())) {
                storeDeployInfo.setOperator(sad.getMerchant_user());
            } else {
                SStoreAdmin storeAdmin = storeAdminMapper.selectByPrimaryKey(sad.getOperator_id(), siteId);
                storeDeployInfo.setOperator(storeAdmin == null ? "" : storeAdmin.getUser_name());
            }

            storeDeployInfo.setCreateTime(sad.getCreate_time());
            storeDeployInfos.add(storeDeployInfo);
        }
        return storeDeployInfos;
    }

    public List<SClerkInfo> getClerkInfo(Integer siteId, Integer storeId, String username, String realName, Integer active, Integer pageNum, Integer pageSize) {

        List<SClerkInfo> clerkInfos = storeAdminMapper.selectClerkInfo(siteId, storeId, username, realName, active);
        return clerkInfos;
    }

    //店长授权码
    public String insertMeta(Integer siteId, Integer storeId, String authCode) {
        metaMapper.delAutoCode(siteId, storeId.toString());

        SMeta meta = new SMeta();

        try {
            meta.setSiteId(siteId);
            meta.setMetaVal(EncryptUtils.encryptToSHA(EncryptUtils.encryptToSHA(authCode)));
            meta.setMetaKey(String.valueOf(storeId));
            meta.setMetaType("store_auth_code");
            meta.setMetaDesc("设置门店授权码");
            meta.setMetaStatus(1);
            int i = metaMapper.insertSelective(meta);
            if (0 != i)
                return "200";
            return "500";
        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
            log.error("设置授权码失败" + e);
        }
        return "500";
    }

    /**
     * 店员删除操作 is_del=1
     *
     * @param siteId
     * @param id
     * @return
     */
    @Transactional
    public String delClerk(Integer siteId, Integer id) {
        int i = storeAdminMapper.deleteClerkByPrimaryKey(siteId, id);
//        if (i == 1) {//店员删除，强制退出
//            try {
//                constraintOut(siteId, id);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        if (0 != i)

            return "200";
        else
            throw new RuntimeException("操作失败");
    }

    /**
     * delete by storeAdminId and siteId
     *
     * @param siteId
     * @param id
     * @return
     */
    @Transactional
    public String delete(Integer siteId, Integer id) throws Exception {
        int i = storeAdminMapper.deleteByPrimaryKey(id, siteId);
        int j = storeAdminextMapper.deleleBySiteIdAndStoreAdminId(siteId, id);
        if (0 != i && 0 != j) {
            Map<String, Object> param = new HashedMap();
            param.put("siteId", siteId);
            param.put("id", id);
            String httpResponse = HttpClient.doHttpPost("", param);
            if (httpResponse.contains("success")) {
                return "200";
            } else {
                throw new RuntimeException("服务请求失败");
            }
        } else {
            throw new RuntimeException("数据库操作失败");
        }
    }

    /**
     * 通过登录名获得用户
     *
     * @param siteId
     * @param username
     * @param password
     * @return
     */
    public SStoreAdmin seleByName(Integer siteId, String username, String password) {
        return storeAdminMapper.selectByName(siteId, username, password);
    }

    /**
     * 通过商户id获取管理员帐号或storeId
     *
     * @param siteId
     * @return
     */
    public SStoreAdmin selectAdminByUserTypeOrStoreId(Integer siteId, Integer storeId) {
        return storeAdminMapper.selectAdminByUserTypeOrStoreId(siteId, storeId);
    }

    /**
     * 修改密码
     *
     * @return
     */
    public String changePwd(Integer site_id, Integer storeadmin_id, String oldPwd, String newPwd) {
        SStoreAdmin storeAdmin = storeAdminMapper.selectByPrimaryKey(storeadmin_id, site_id);
        if (storeAdmin.getUser_pwd().equals(sha2(oldPwd))) {
            Integer result = storeAdminMapper.changePwd(site_id, storeadmin_id, sha2(newPwd));
            if (result == 1) {
                return "200";
            }
        } else {
            return "300";
        }
        return "500";
    }

    /**
     * 密码加密
     *
     * @param str
     * @return
     */
    private String sha2(String str) {
        try {
            return SEncryptUtils.encryptToSHA1(SEncryptUtils.encryptToSHA1(str));
        } catch (NoSuchAlgorithmException e) {
            log.error("密码加密失败" + e);
        }
        return null;
    }


    public Map<String, Integer> selectStatue(Integer siteId, Integer id) {
        return storeAdminMapper.selectStatue(siteId, id);
    }

    /**
     * 服务组批量操作
     *
     * @param storeAdmin
     * @param groupIds
     * @return
     */
    private List<SGroupMember> insertList(SStoreAdmin storeAdmin, List<Integer> groupIds) {
        List<SGroupMember> groupMembers = new ArrayList<>();
        SGroupMember groupMember = null;
        for (Integer groupid : groupIds) {
            groupMember = new SGroupMember();
            groupMember.setSite_id(storeAdmin.getSite_id());
            groupMember.setGroup_id(groupid);
            groupMember.setStore_admin_id(String.valueOf(storeAdmin.getId()));
            groupMember.setCreate_at(new Date());
            groupMember.setIs_close(0);
            groupMembers.add(groupMember);

        }
        return groupMembers;
    }

    /**
     * 发送http请求
     *
     * @param
     * @param storeAdmin
     * @param roleIds
     * @return 字符串信息
     * @throws IOException
     */
    private List<ManagerHasRole> doHttpPost(SStoreAdmin storeAdmin, List<Integer> roleIds) throws IOException {
        List<ManagerHasRole> managerHasRoles = new ArrayList<>();
        ManagerHasRole managerHasRole = null;
        for (Integer roleId : roleIds) {
            managerHasRole = new ManagerHasRole();
            managerHasRole.setManager_id(storeAdmin.getId());
            managerHasRole.setSite_id(storeAdmin.getSite_id());
            managerHasRole.setRole_id(roleId);
            managerHasRole.setStore_id(storeAdmin.getStore_id());
            managerHasRole.setPlatform(130);
            managerHasRoles.add(managerHasRole);
        }
        return managerHasRoles;
    }

    /**
     * 根据商户的编号查询到商户的微信域名并生成二维码图片地址
     *
     * @param siteId
     * @return
     */
    public String selectShopWxUrlBySiteId(Integer siteId) {
        return storeAdminMapper.selectShopWxUrlBySiteId(siteId);
    }

    @Transactional
    public Integer updateLoginCount(Integer site_id, String username, String userPwd) {
        return storeAdminMapper.updateLoginCount(site_id, username, userPwd);
    }

    public int updateClerkDel(Integer siteId, Integer storeId, Integer id) {
        Integer i = storeAdminMapper.updateClerkDel(siteId, storeId, id);
        if (i == 2) {//店员删除，强制退出
            try {
                constraintOut(siteId, id,storeId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    //查询app端回访列表
    public List<Map<String,Object>> selectClerkVisitList(Map<String,Object> map) {
        List<Map<String, Object>> maps = storeAdminDeployMapper.selectClerkVisitList(map);
        for (int x=0;x<maps.size();x++){
            String[] activityIds=maps.get(x).get("activityIds").toString().split(",");
            String siteId=maps.get(x).get("siteId").toString();
            List<String> nameList=new ArrayList<>();
            for (int y=0;y<activityIds.length;y++) {
                PromotionsActivity promotionsActivity=promotionsActivityMapper.getPromotionsActivityDetail(Integer.valueOf(siteId),Integer.valueOf(activityIds[y]));
                if(promotionsActivity!=null){
                    nameList.add(promotionsActivity.getTitle());
                }
            }

            maps.get(x).put("nameList",nameList);
        }
        //查询对应的回访记录
//        List<Map<String,Object>> listLog = storeAdminDeployMapper.selectClerkListLog(map);
        //过滤数据,获取最新记录
//        List<Map<String,Object>> result = new ArrayList<>();
       /* Set<String> filter = new HashSet<>();
        maps.stream().forEach(visList -> {
            String id = String.valueOf(visList.get("id").toString());
            listLog.stream().forEach(visLog -> {
                String clerkVisitId = String.valueOf(visLog.get("clerkVisitId"));
                if(id.equals(clerkVisitId)) {
                    if(filter.add(id)){
                        visList.put("visitLog",visLog);
                    }

                }

            });
//            if(filter.add(id)) {
//                result.add(visLog);
//            }
        });*/
        return maps;
    }


    public int changeClerkStatus(Map<String,Object> map){
        return storeAdminDeployMapper.changeClerkStatus(map);
    }

    public List<Map<String,Object>> getClerkList(Map<String,Object> map) {
        return storeAdminextMapper.getClerksList(map);
    }

    @Transactional
    public Boolean changeClerk(Map<String,Object> map) {
        //先根据列表ID查询,调配前店员信息
        List<Map<String,Object>> admins = storeAdminextMapper.getadminInfo(map);
        Map<String,Object> logger = new HashedMap();
        logger.put("siteId",map.get("siteId"));
        logger.put("clerkId",map.get("clerkId"));
        logger.put("clerkName",map.get("clerkName"));
        admins.stream().forEach(adm -> {
            logger.put("cvId",adm.get("id"));
            logger.put("preClerkId",adm.get("storeAdminId"));
            logger.put("preStoreId",adm.get("storeId"));
            logger.put("preClerkName",adm.get("adminName"));
            logger.put("operId",map.get("operId"));
            logger.put("operName",map.get("operName"));
            logger.put("nStoreId",map.get("storeId"));
            storeAdminextMapper.addChangeClerkLog(logger);
        });
        int i = storeAdminextMapper.changeClerk(map);
        if(i > 0) {
            for(int x=0;x<admins.size();x++){
                //消息主题
                List<BVisitMessage> list=new ArrayList<>();
                BVisitMessage bVisitMessage=new BVisitMessage();
                bVisitMessage.setId(Integer.valueOf(admins.get(x).get("id").toString()));
                bVisitMessage.setGid(admins.get(x).get("goodIds").toString());
                bVisitMessage.setAid(admins.get(x).get("activityIds").toString());
                bVisitMessage.setBid(Integer.valueOf(admins.get(x).get("buyerId").toString()));
                bVisitMessage.setbName(admins.get(x).get("buyerName").toString());
                list.add(bVisitMessage);
                String taskName="回访任务";
                if(Integer.valueOf(admins.get(x).get("status").toString())==20){
                    try {
                        appMemberService.notifyVisitMessage(list,Integer.valueOf(map.get("siteId").toString()),
                            Integer.valueOf(map.get("clerkId").toString()),Integer.valueOf(map.get("storeId").toString()), PushType.TASK_VISIT,taskName);
                    } catch (Exception e) {
                        log.error("回访任务:{}通知发送失败,失败原因:{}",e);
                    }
                }
            }
            return true;
        }else {
            return false;
        }
    }

    public List<Map<String,Object>> getConsumerList(Map<String, Object> parameterMap) {
        List<Map<String,Object>> tradeList=bTradesMapper.selectConsumerList(parameterMap);
//        Integer userId=Integer.valueOf(parameterMap.get("userId").toString());
        Integer siteId=Integer.valueOf(parameterMap.get("siteId").toString());
//        Integer buyerId=Integer.valueOf(parameterMap.get("buyerId").toString());
//        List<PromotionsActivity> promotionsActivityList=promotionsActivityMapper.getPromotionsActivitiesByStatusAndSiteId(Integer.valueOf(parameterMap.get("siteId").toString()));
//        String[] style={"110","120","130","140","150"};
        List<PromotionsActivity> releasePomotionsActivity =promotionsActivityService.findAllReleasePromotionsActivityForBuyer(siteId,null,0);

        if(tradeList.size()!=0){
            for(int i=0;i<tradeList.size();i++){
                Map<String,Object> map=tradeList.get(i);
//                if(map.get("post_style").toString().equals("160") || map.get("post_style").toString().equals("170") ||
//                map.get("post_style").toString().equals("150") ){
////                    SBStores sbStores= storeMapper.selectByPrimaryKey(map.get("site_id").toString(),map.get("self_taken_store").toString());
//                    SBStores sbStores= storeMapper.selectByPrimaryKey(map.get("site_id").toString(),map.get("assigned_stores").toString());
//
//                    if(Objects.isNull(sbStores)){
//                        map.put("storename","总部");
//                    }else{
//                        map.put("storename",sbStores.getName());
//                    }
//                }
                //获取订单下商品 以及商品对应的活动
//                List<Orders> ordersList = ordersMapper.getOrdersListByTradesId(map.get("site_id").toString(), Long.parseLong(map.get("trades_id").toString()));//获取订单下商品
                List<Map<String,Object>> ordersList = ordersMapper.getOrdersListByTradesIdAndSiteId(map.get("site_id").toString(),Long.valueOf(map.get("trades_id").toString()));
                for(int y=0;y<ordersList.size();y++){
                    Map<String,Object> orderMap= ordersList.get(y);
                    Object goodsId=orderMap.get("goods_id");
//                    result.put("isNeedAuth", false);
//                    EasyToSeeParam easyToSeeParam =new EasyToSeeParam();
//                    CouponFilterParams couponFilterParams=new CouponFilterParams();
//                    Integer goods_Id= Integer.valueOf(goodsId.toString());
//                    easyToSeeParam.setSiteId(siteId);
//                    easyToSeeParam.setGoodsIds(goods_Id.toString());
//                    easyToSeeParam.setUserId(userId);
//
//                    couponFilterParams.setGoodsId(goods_Id.toString());
//                    couponFilterParams.setSiteId(siteId);
//                    couponFilterParams.setUserId(userId);
//                    Integer buyerId = null;
                    //所有正在发布的活动

                    if (Objects.nonNull(releasePomotionsActivity) && releasePomotionsActivity.size() > 0) {
                        ArrayList<String> nameList=new ArrayList<>();
                        for(int t=0;t<releasePomotionsActivity.size();t++){
                            PromotionsActivity promotionsActivity = releasePomotionsActivity.get(t);
                            //参与商品
                            PromotionsRule promotionsRule = promotionsActivity.getPromotionsRule();
                            Map<String, String> goodsIds = PromotionsRuleService.getGoodsIds(promotionsRule);
                            String[] split1 = goodsIds.get("goodsIds").split(",");
                            if(Arrays.asList(split1).contains(goodsId.toString())){
                                nameList.add(promotionsActivity.getTitle());
                            }
                        }
                        if(nameList.size()==0){
                            nameList.add("暂无活动");
                        }
                        map.put("activityList",nameList);
                    }
                }
                map.put("ordersList",ordersList);
            }
        }
        return tradeList;
    }
    public SStoreAdminext getStoreAdminExtById(String siteId, String storeAmidId) {
        SStoreAdminext storeAdminext = storeAdminextMapper.getStoreAdminExtById(siteId , storeAmidId );
        return storeAdminext;
    }
}
