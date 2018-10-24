package com.jk51.modules.appInterface.service;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.redis.RedisUtil;
import com.jk51.model.*;

import com.jk51.model.netease.NeteaseAccid;
import com.jk51.model.order.SBAppLogs;
import com.jk51.modules.appInterface.mapper.*;
import com.jk51.modules.appInterface.util.AuthToken;
import com.jk51.modules.appInterface.util.LoginINfo;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.im.mapper.ChAnswerRelationMapper;
import com.jk51.modules.im.mapper.ChOrderRemindMapper;
import com.jk51.modules.im.mapper.ChUserPushInfoMapper;
import com.jk51.modules.im.mapper.YbTUserAccountMapper;
import com.jk51.modules.im.netease.NeteaseIm;
import com.jk51.modules.im.netease.Util;
import com.jk51.modules.im.netease.dao.NetaeaseAccidDao;
import com.jk51.modules.im.netease.service.NeteaseService;
import com.jk51.modules.im.service.GeTuiPush;
import com.jk51.modules.im.service.InitialMessage;
import com.jk51.modules.im.service.PushServeService;
import com.jk51.modules.im.service.PushType;
import com.jk51.modules.im.service.wechatUtil.WechatInfo;
import com.jk51.modules.im.service.wechatUtil.WechatUtil;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.storage.FileSystemStorageService;
import com.jk51.modules.store.service.BAppLogsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;


import static java.util.stream.Collectors.toList;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-01
 * 修改记录:
 */
@Service
public class UsersService {
   private static Logger logger = LoggerFactory.getLogger(UsersService.class);
    //微信里面
    private static final String SOURCE_TYPE_WECHAT = "wechat_";
    //app里面的h5
    private static final String SOURCE_TYPE_H5     = "h5_";
    @Autowired
    private FileSystemStorageService fileSystemStorageService;
    @Autowired
    private StoreAdminMapper storeAdminMapper;
    @Autowired
    private ChUserMapper chUserMapper;
    @Autowired
    private ChPharmacistMapper chPharmacistMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminextMapper;
    @Autowired
    private ChUseHasBankMapper chUseHasBankMapper;
    @Autowired
    private ChAccountLogMapper chAccountLogMapper;
    @Autowired
    private ChOrderRemindMapper chOrderRemindMapper;
    @Autowired
    private ChUserPushInfoMapper chUserPushInfoMapper;
    @Autowired
    private ChMemberMapper chMemberMapper;
    @Autowired
    private YbTUserAccountMapper ybTUserAccountMapper;
    @Autowired
    private ChUserLoginHistoryMapper chUserLoginHistoryMapper;
    @Autowired
    private BGoodsPrebookMapper bGoodsPrebookMapper;
    @Autowired
    private GeTuiPush geTuiPush;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AppVersionMapper appVersionMapper;
    @Autowired
    private ChAnswerRelationMapper chAnswerRelationMapper;
    @Autowired
    private BMessageSenderMapper bMessageSenderMapper;
    @Autowired
    private PushServeService pushServeService;
    @Autowired
    private WechatUtil wechatUtil;
    @Autowired
    private BAppLogsService appLogsService;
    @Autowired
    private AppH5VersionMapper appH5VersionMapper;
    @Autowired
    private Util util;
    @Autowired
    private NeteaseService neteaseService;


    private static List<String> sources = null;
    // 用户token前缀
    static {
        sources = new ArrayList<String>();
        sources.add("wechat_");
        sources.add("h5_");
        sources.add("app_");
        sources.add("helper_");
    }


    //修改密码
    @Transactional
    public Map<String,Object> resetpwd(String phone, String password) {

        Map<String,Object> result = new HashMap<String,Object>();
        if(StringUtil.isEmpty(phone)){
            result.put("status","ERROR");
            result.put("errorMessage","手机号码不能为空");
            return result;
        }
        if(!StringUtil.isMobileNO(phone)){
            result.put("status","ERROR");
            result.put("errorMessage","手机号码格式不对");
            return result;
        }

        if(StringUtil.isEmpty(password)){
            result.put("status","ERROR");
            result.put("errorMessage","密码不能为空");
            return result;
        }

        //查询app用户是否存在
        List<StoreAdmin> storeAdmins = storeAdminMapper.findStoreAdmin(phone);
        if(StringUtil.isEmpty(storeAdmins)){
            result.put("status","ERROR");
            result.put("errorMessage","用户不存在");
            return result;
        }

        StoreAdmin storeAdmin = storeAdmins.get(0);
        try {
            password = EncryptUtils.encryptToSHA1(EncryptUtils.encryptToSHA1(password));
        } catch (Exception e) {
            logger.error("密码加密报错",e);

            result.put("status","ERROR");
            result.put("errorMessage","密码加密报错");
            return result;
        }
        storeAdmin.setUser_pwd(password);
        storeAdmin.setUpdate_time(new Timestamp(System.currentTimeMillis()));
        int num = storeAdminMapper.updateByPrimaryKey(storeAdmin);

        if(num !=1){
            result.put("status","ERROR");
            result.put("errorMessage","密码修改失败");
            return result;
        }

        //设置返回参数
        Map<String,Object> results = new HashMap<String,Object>();
        results.put("storeAdmin",storeAdmin);
        result.put("status","OK");
        result.put("results",results);

        return result;

    }


    //app登录并返回userInfo,accessToken
    public Map<String,Object> userToken(LoginINfo login) {

        String phone = login.getPhone();
        String password = login.getPassword();
        String device_token =  login.getDevice_token();
        String cid =  login.getCid();
        int loginType =  login.getLoginType();

        //loginType==2时，在缓存中获取验证码
        if(loginType==2){

            String verificationCode = stringRedisTemplate.opsForValue().get(phone);
           return verificationLogin(password,verificationCode);
        }

        Map<String,Object> result = new HashMap<String,Object>();

        if(StringUtil.isEmpty(phone)){
            result.put("status","ERROR");
            result.put("errorMessage","账户为空！");
            return result;
        }
        if(!StringUtil.isMobileNO(phone)){
            result.put("status","ERROR");
            result.put("errorMessage","请输入正确的手机号码！");
            return result;
        }

        if(StringUtil.isEmpty(cid)){
            result.put("status","ERROR");
            result.put("errorMessage","APP初始化失败(cid为空)");
            return result;
        }
        if(StringUtil.isEmpty(password)){
            result.put("status","ERROR");
            result.put("errorMessage","密码为空！");
            return result;
        }
        if(StringUtil.isEmpty(loginType)){
            result.put("status","ERROR");
            result.put("errorMessage","loginType为空！");
            return result;
        }

        //密码加密
        try {
            password = EncryptUtils.encryptToSHA1(EncryptUtils.encryptToSHA1(password));
        } catch (Exception e) {
            logger.error("加密异常",e);
        }

        //查询app用户是否存在
        List<Map<String,Object>> userInfos = storeAdminMapper.findStoreAdminByPhoneAndPassword(phone,password);
        if(StringUtil.isEmpty(userInfos)||userInfos.isEmpty()){
            result.put("status","ERROR");
            result.put("errorMessage","用户名或密码错误");
            return result;
        }

        //获取userInfo------根据app需要的参数，测试时在调整参数
        Map<String,Object> userInfo = userInfos.get(0);

        Map<String,Integer> status = storeAdminMapper.selectStatue(Integer.parseInt(userInfo.get("siteId").toString()),Integer.parseInt(userInfo.get("storeAdminId").toString()));


        if(status.get("mStu")!=0){
            result.put("status","ERROR");
            result.put("errorMessage","您所在的商户已被禁止登陆");
            return result;
        }else if(status.get("sStu")!=1){
            result.put("status","ERROR");
            result.put("errorMessage","您所在的门店已被禁止登陆");
            return  result;
        }else if(status.get("saStu")!=1){
            result.put("status","ERROR");
            result.put("errorMessage","您的账户被禁止登录");
            return result;
        }else{
            logger.info("用户登录,siteId[{}],storeadminId:[{}]",Integer.parseInt(userInfo.get("siteId").toString()),Integer.parseInt(userInfo.get("storeAdminId").toString()));
        }

        //获取accessToken
        String accessToken = getAccessToken(userInfo);

        //通知该用户的其他客户端下线
        String userId = ((Integer)userInfo.get("userId")).toString();
        noticeOtherClientQuit(cid,userId,device_token);


        //多个账号登录相同设配时，删除之前的登录记录
        deleteOthersChUserPushInfo(cid,userId);

        //更新用户和客户端在ch_user_push_info中的记录，软删除以前的记录，插入现在的登录记录(cid就是pushid)
        setUserPushInfo(cid,userId,device_token);

        //查询rl_token
        String tl_token = getRLToken((Integer) userInfo.get("siteId"),(Integer) userInfo.get("storeAdminId"));
        userInfo.put("rlToken",tl_token);

        //保存登录信息
        saveLoginHistory((Integer)userInfo.get("userId"));

        //设置返回值
        result.put("status","OK");
        Map<String,Object> results = new HashMap<String,Object>();
        results.put("userInfo",userInfo);
        results.put("accessToken",accessToken);
        result.put("results",results);
        return result;
    }

    //验证码验证登录
    private Map<String,Object> verificationLogin(String password,String verificationCode) {
        Map<String,Object> result = new HashMap<String,Object>();
        if(password.equals(verificationCode)){
            result.put("status","OK");
        }else{
            result.put("status","ERROR");
            result.put("errorMessage","验证码验证失败");
        }

        return result;

    }

    //保存登录记录
    private void saveLoginHistory(int userId) {
        ChUserLoginHistory chUserLoginHistory = new ChUserLoginHistory();
        chUserLoginHistory.setUser_id(userId);
        chUserLoginHistory.setUser_type(2);
        chUserLoginHistoryMapper.insert(chUserLoginHistory);
    }

    //获取rl_token
    private String getRLToken(Integer siteId, Integer storeAdminId) {

        String rl_token =  siteId.toString()+"_"+storeAdminId.toString();

        return "helper_"+rl_token;
    }

    //更新用户和客户端在ch_user_push_info中的记录，软删除以前的记录，插入现在的登录记录(cid就是pushid)
    @Transactional
    public void setUserPushInfo(String cid, String userId, String device_token) {

        String app_name = "pharmacist";

        //软删除clentid等于现在的用户（也就是其他客户端）
        int num = chUserPushInfoMapper.deleteByCidAndUserIdAndAppName(cid,userId,app_name);

        //记录当前客户端的登录记录
        ChUserPushInfo chUserPushInfo = new ChUserPushInfo();
        chUserPushInfo.setApp_name(app_name);
        chUserPushInfo.setDevice_token(device_token);
        chUserPushInfo.setPush_id(cid);
        chUserPushInfo.setUser_id(Integer.valueOf(userId));
        int n = chUserPushInfoMapper.insert(chUserPushInfo);
    }

    //通知该用户的其他客户端下线
    private void noticeOtherClientQuit(String cid,String userId,String deviceToken){

        //查询用户最新的登录记录，并与该client_id是一致
        List<ChUserPushInfo> chUserPushInfoList =  chUserPushInfoMapper.findByUserId(userId,cid);
        if(StringUtil.isEmpty(chUserPushInfoList)||chUserPushInfoList.size()==0){
            return;
        }
        ChUserPushInfo chUserPushInfo = chUserPushInfoList.get(0);
        if(chUserPushInfo.getPush_id().equals(cid)){
            return;
        }

        //设置个推的提醒信息
        GeTuiNoticeMessage geTuiNoticeMessage = new GeTuiNoticeMessage();
        geTuiNoticeMessage.setAnchor("QUIT");


        //推送信息到指定的客户端
        geTuiPush.pushToClient(chUserPushInfo.getPush_id(),geTuiNoticeMessage.getContent(),chUserPushInfo.getDevice_token());


    }

    //多个账号登录相同设配时，删除之前的登录记录
    private void deleteOthersChUserPushInfo(String pushId,String userId){
        Integer num = chUserPushInfoMapper.deleteByPushId(pushId,userId);
    }


    //生成accesToken
    private String getAccessToken(Map<String, Object> userInfo) {

        AuthToken token = new AuthToken();
        token.setUserId((Integer) userInfo.get("userId"));
        token.setPharmacistId((Integer) userInfo.get("pharmacistId"));
        token.setStoreId((Integer) userInfo.get("storeId"));
        token.setStoreUserId((Integer) userInfo.get("storeUserId"));
        token.setPhone((String) userInfo.get("phone"));
        token.setSiteId((Integer) userInfo.get("siteId"));
        token.setStoreAdminId((Integer) userInfo.get("storeAdminId"));
        String json = null;
        try {
            json = JacksonUtils.obj2json(token);
        } catch (Exception e) {
            logger.error("生成accesToken实例失败",e);
        }
        return EncryptUtils.base64EncodeToString(json.getBytes());
    }

    //解析Token
    public AuthToken parseAccessToken(String accessToken){


        String token = EncryptUtils.base64DecodeToString(accessToken.getBytes());
        AuthToken authToken = null;
        try {
            authToken = JacksonUtils.json2pojo(token,AuthToken.class);

        } catch (Exception e) {
            logger.error("解析AccessToken失败,报错信息{},token{}",e,token);
            return null;
        }

        return authToken;
    }

    //检查店员
    public Map<String,Object> checkUser(String accessToken) {

        AuthToken authToken = parseAccessToken(accessToken);
        Map<String,Object> result = new HashMap<String,Object>();
        if(StringUtil.isEmpty(authToken)){
            result.put("status","ERROR");
            result.put("errorMessage","accessToken解析失败！");
            return result;
        }

        StoreAdminExt storeAdminext = storeAdminextMapper.selectByPrimaryKey(authToken.getStoreUserId(),authToken.getSiteId());

        if(StringUtil.isEmpty(storeAdminext) /*|| storeAdminext.getIs_del()*/){
            result.put("status","ERROR");
            result.put("errorMessage","店员不存在！");
            return result;
        }

        if(storeAdminext.getStore_id() !=authToken.getStoreId()){
            result.put("status","ERROR");
            result.put("errorMessage","此店员在其它门店！");
            return result;
        }

        result.put("status","OK");
        return result;

    }


    //获取用户中心信息
    public Map<String,Object> getUsersCenters(String accessToken) {

        Map<String,Object> result = new HashMap<String,Object>();
        AuthToken authToken = parseAccessToken(accessToken);
        if(StringUtil.isEmpty(authToken)){
            result.put("status","ERROR");
            result.put("errorMessage","accessToken解析失败！");
            return result;
        }

        ChPharmacist chPharmacist = chPharmacistMapper.selectByPrimaryKey(authToken.getPharmacistId(),authToken.getSiteId());

        Map<String,Object> results = new HashMap<String,Object>();
        if(StringUtil.isEmpty(chPharmacist)){

            results.put("isOnline",0);
            results.put("isRemind",0);
            results.put("isBindBankCard",0);
            results.put("totalIncome", 0);
            results.put("currentMonthIncome",0);
            results.put("orderRemindCount", 0);
            results.put("preOrderCount",0);
            result.put("status","OK");
            result.put("results",results);
            return result;
        }


        List<ChUserHasBank> use_has_bank = chUseHasBankMapper.findByUserId(chPharmacist.getUserId());

        //查询所有的收入
        Long total_income = chAccountLogMapper.countTotalIncome(chPharmacist.getUserId(),null);

        //查询当月的收入
        String current_moth = DateUtils.toString(new Date(),"yyyy-MM");
        Long current_month_income = chAccountLogMapper.countTotalIncome(chPharmacist.getUserId(),current_moth);

        //查门店所有未读的提醒
        Integer order_remind_count  = chOrderRemindMapper.findNotReaded(authToken.getSiteId(),authToken.getStoreId());

      /*  //查询已读提醒的所有OrderId
        List<String> allIsReadedOrderId = chOrderRemindMapper.fingIsReadedOrderId(chPharmacist.getId());
*/
       /* int order_remind_count = 0;
        for(ChOrderRemind or:notReaded){
            if(allIsReadedOrderId.contains(or.getOrder_id())){
                order_remind_count++;
            }
        }*/

        //查询预约单数
        Integer pre_order_count = getPreOrderCount(authToken.getStoreUserId(),authToken.getSiteId());


        results.put("isOnline",chPharmacist.getIsOnline());
        results.put("isRemind",chPharmacist.getIsRemind());
        results.put("isBindBankCard", StringUtil.isEmpty(use_has_bank)?0:1);
        results.put("totalIncome", StringUtil.isEmpty(total_income)?0:total_income);
        results.put("currentMonthIncome", StringUtil.isEmpty(current_month_income)?0:current_month_income);
        results.put("orderRemindCount", StringUtil.isEmpty(order_remind_count)?0:order_remind_count);
        results.put("preOrderCount", StringUtil.isEmpty(pre_order_count)?0:pre_order_count);
        result.put("status","OK");
        result.put("results",results);
      return result;
    }

    //查询PreOrderCount
    private Integer getPreOrderCount(int storeUserId, int siteId) {

        return bGoodsPrebookMapper.getPreOrderCount(storeUserId,siteId);

    }

    //设置上线
    public Map<String,Object> setOnline(String accessToken) {

        Map<String,Object> result = new HashMap<String,Object>();
        AuthToken authToken = parseAccessToken(accessToken);
        if(StringUtil.isEmpty(authToken)){
            result.put("status","ERROR");
            result.put("errorMessage","accessToken解析失败！");
            return result;
        }

        int num = chPharmacistMapper.updateOnlineByPharmacistId(1,authToken.getPharmacistId(),authToken.getSiteId());

        if(num==1){
            result.put("status","OK");
            OfflineChangeLog(authToken.getStoreAdminId(),1,authToken.getSiteId());

            return result;
        }else{
            result.put("status","ERROR");
            result.put("errorMessage","设置失败！");
            return result;
        }

    }


    private int OfflineChangeLog(int storeAdminId,int num,int siteId){

        SBAppLogs logs = new SBAppLogs();
        logs.setAction("修改online");
        logs.setRemark("修改online,operator_id保存[storeAdminId],operator_type保存[num]");
        logs.setOperator_id(storeAdminId);
        logs.setOperator_type(num);
        logs.setSite_id(siteId);
        return appLogsService.insertSelective(logs);
    }

    //设置离线
    public Map<String,Object> setOffline(String accessToken) {

        Map<String,Object> result = new HashMap<String,Object>();
        AuthToken authToken = parseAccessToken(accessToken);
        if(StringUtil.isEmpty(authToken)){
            result.put("status","ERROR");
            result.put("errorMessage","accessToken解析失败！");
            return result;
        }

        int num = chPharmacistMapper.updateOnlineByPharmacistId(0,authToken.getPharmacistId(),authToken.getSiteId());

        if(num==1){
            result.put("status","OK");

            OfflineChangeLog(authToken.getStoreAdminId(),0,authToken.getSiteId());
            return result;
        }else{
            result.put("status","ERROR");
            result.put("errorMessage","设置失败！");
            return result;
        }
    }



    //获取联系人信息
    public Map<String,Object> getFriendsInfo(String member_ids, String authToken) {

        Map<String,Object> result = new HashMap<String,Object>();
        if(StringUtil.isEmpty(member_ids)){
            result.put("status","ERROR");
            result.put("errorMessage","member_ids为空！");
            return result;
        }

        AuthToken authToken1 = parseAccessToken(authToken);
        if(StringUtil.isEmpty(authToken1)){
            result.put("status","ERROR");
            result.put("errorMessage","accessToken解析失败！");
            return result;
        }

        member_ids = member_ids.replace("member_ids=","");
        String[] memberIdList = member_ids.split(",");


        //List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();

        List<Map<String,Object>> results = Arrays.asList(memberIdList)
            .parallelStream()
            .map(memberId->getMeberInfo(memberId,authToken1.getSiteId()))
            .collect(toList());


        //过滤为空的信息
        results = results.parallelStream().filter(meberInfo->!StringUtil.isEmpty(meberInfo)).collect(toList());


        result.put("status","OK");
        result.put("results",results);
        return result;
    }



    private Map<String,Object> getMeberInfo(String memberId, int siteId) {

        Map<String,Object> map = new HashMap<String,Object>();
        if(StringUtil.isEmpty(memberId) && memberId.indexOf(SOURCE_TYPE_WECHAT)==-1){
            return map;
        }

        //查询ch_answer_relation
        ChAnswerRelation chr = chAnswerRelationMapper.findByUserOpenId(memberId);

        String ybmemberId = getYbmemberIdByToken(memberId);
        Map<String,Object> memberInfo = chMemberMapper.getMemberInfo(ybmemberId,siteId);

        if(!StringUtil.isNumber(ybmemberId)){
            return map;
        }

        //查询微信信息
        WechatInfo wechatInfo = wechatUtil.getWechatInfo(siteId,Integer.parseInt(ybmemberId));

        if(memberInfo!=null&&!StringUtil.isEmpty(chr)){
            memberInfo.put("memberId",memberId);
            memberInfo.put("openId",memberId);
            memberInfo.put("imServiceId",chr.getIm_service_id());
            memberInfo.put("imRecodeId",chr.getIm_recode_id());
            memberInfo.put("wechatNickName",StringUtil.isEmpty(wechatInfo)?"":StringUtil.isEmpty(wechatInfo.getNickname())?"":wechatInfo.getNickname());
            memberInfo.put("headImgUrl",StringUtil.isEmpty(wechatInfo)?"":StringUtil.isEmpty(wechatInfo.getHeadimgurl())?"":wechatInfo.getHeadimgurl());
            return memberInfo;
        }else{

            return map;
        }
    }


    /**
     * 根据荣联token去获取ybmember_id
     * @param token string
     * @return string
     *
     * ps: 在2017年5月27，为了解决同一个手机号在不同商家的微信注册造成的容联用户同名，修改微信容联用户名从"wechat_"+buyer_id;修改为"wechat_"+site_id+"_"+buyer_id;
     *      故同步修改通过微信token获取buyer_id的方式满足新旧数据
     */
    public String  getYbmemberIdByToken(String token){

        String[] strs = token.split("_");
        if(strs.length==2){
            return strs[1];
        }else if(strs.length==3){
            return strs[2];
        }else{
            return "";
        }
    }




    public Map<String,Object> remind(String accessToken) {

        Map<String,Object> result = new HashMap<String,Object>();
        AuthToken authToken = parseAccessToken(accessToken);
        if(StringUtil.isEmpty(authToken)){
            result.put("status","ERROR");
            result.put("errorMessage","accessToken解析失败！");
            return result;
        }

        return updateRemind(authToken.getPharmacistId(),authToken.getSiteId(),1);
    }


    public Map<String,Object> unremind(String accessToken) {

        Map<String,Object> result = new HashMap<String,Object>();
        AuthToken authToken = parseAccessToken(accessToken);
        if(StringUtil.isEmpty(authToken)){
            result.put("status","ERROR");
            result.put("errorMessage","accessToken解析失败！");
            return result;
        }

        //修改系统通知状态
        return updateRemind(authToken.getPharmacistId(),authToken.getSiteId(),0);
    }

    //修改系统通知状态
    @Transactional
    public Map<String,Object> updateRemind(int pharmacistId, int siteId, int is_remind) {

        Map<String,Object> result = new HashMap<String,Object>();
        int num = chPharmacistMapper.updateRemind(pharmacistId,siteId,is_remind);
        if(num==1){
            result.put("status","OK");
            return result;
        }else{
            result.put("status","ERROR");
            result.put("errorMessage","修改系统通知状态失败");
            return result;
        }
    }

    //查询最新的app版本信息
    public Map<String,Object> getNewestAppVersios(int versios) {

        Map<String,Object> result = new HashMap<String,Object>();
        AppVersion appVersion = appVersionMapper.getNewestAppVersios(versios);
        if(StringUtil.isEmpty(appVersion)){
            result.put("status","ERROR");
            result.put("errorMassege","查询app最新的版本信息失败");
            return result;
        }

        result.put("status","OK");
        result.put("results",appVersion);
        return result;
    }
//------------------------------------------------------------------------------------------------------------------

    public Map<String,Object> userToken2(LoginINfo login) {
        String phone = login.getPhone();
        String password = login.getPassword();
        String device_token =  login.getDevice_token();
        String cid =  login.getCid();
        int loginType =  login.getLoginType();

        //loginType==2时，在缓存中获取验证码
        if(loginType==2){
            String verificationCode = stringRedisTemplate.opsForValue().get(phone);
            return verificationLogin(password,verificationCode);
        }

        Map<String,Object> result = new HashMap<String,Object>();

        if(StringUtil.isEmpty(phone)){
            result.put("status","ERROR");
            result.put("errorMessage","账户为空！");
            return result;
        }
        if(!StringUtil.isMobileNO(phone)){
            result.put("status","ERROR");
            result.put("errorMessage","请输入正确的手机号码！");
            return result;
        }
        /*if(StringUtil.isEmpty(cid)){
            if("13817711111".equals(phone)){
                cid = "7180049d84a729b93cb3adc314b6f35e";
            }else {
                result.put("status","ERROR");
                result.put("errorMessage","APP初始化失败(cid为空)");
                return result;
            }
        }*/
        if(StringUtil.isEmpty(password)){
            result.put("status","ERROR");
            result.put("errorMessage","密码为空！");
            return result;
        }
        if(StringUtil.isEmpty(loginType)){
            result.put("status","ERROR");
            result.put("errorMessage","loginType为空！");
            return result;
        }

        //密码加密
        try {
            password = EncryptUtils.encryptToSHA1(EncryptUtils.encryptToSHA1(password));
        } catch (Exception e) {
            logger.error("加密异常",e);
        }

        //查询app用户是否存在
        List<Map<String,Object>> userInfos = storeAdminMapper.findStoreAdminByPhoneAndPassword(phone,password);
        if(StringUtil.isEmpty(userInfos)||userInfos.isEmpty()){
            result.put("status","ERROR");
            result.put("errorMessage","用户名或密码错误");
            return result;
        }

        //获取userInfo------根据app需要的参数，测试时在调整参数
        Map<String,Object> userInfo = userInfos.get(0);
        Map<String,Integer> status = storeAdminMapper.selectStatue(Integer.parseInt(userInfo.get("siteId").toString()),Integer.parseInt(userInfo.get("storeAdminId").toString()));

        if(status.get("mStu")!=0){
            result.put("status","ERROR");
            result.put("errorMessage","您所在的商户已被禁止登陆");
            return result;
        }else if(status.get("sStu")!=1){
            result.put("status","ERROR");
            result.put("errorMessage","您所在的门店已被禁止登陆");
            return  result;
        }else if(status.get("saStu")!=1){
            result.put("status","ERROR");
            result.put("errorMessage","您的账户被禁止登录");
            return result;
        }else{
            logger.info("用户登录,siteId[{}],storeadminId:[{}]",Integer.parseInt(userInfo.get("siteId").toString()),Integer.parseInt(userInfo.get("storeAdminId").toString()));
        }

        //获取accessToken
        String accessToken = getAccessToken(userInfo);

        //通知该用户的其他客户端下线
        String userId = ((Integer)userInfo.get("userId")).toString();

       /* noticeOtherClientQuit(cid,userId,device_token);
        //多个账号登录相同设配时，删除之前的登录记录
        deleteOthersChUserPushInfo(cid,userId);
        //更新用户和客户端在ch_user_push_info中的记录，软删除以前的记录，插入现在的登录记录(cid就是pushid)
        setUserPushInfo(cid,userId,device_token);*/

        try {//通知该用户的其他客户端下线
            if(!StringUtil.isEmpty(cid)){
                notifyOtherClientQuit(userInfo.get("siteId").toString(), userInfo.get("storeId").toString(), userInfo.get("storeAdminId").toString(), cid, "login");//其它所有设备登录的都强制退出
                String clientId = bMessageSenderMapper.getPushClientId(userInfo.get("siteId").toString(), userInfo.get("storeAdminId").toString());//获取推送ID
                if(clientId == null){
                    BMessageSender bMessageSender = new BMessageSender();
                    bMessageSender.setSiteId(Integer.parseInt(userInfo.get("siteId").toString()));
                    bMessageSender.setStoreAdminId(Integer.parseInt(userInfo.get("storeAdminId").toString()));
                    bMessageSender.setClientId(cid);
                    bMessageSender.setDeviceToken(device_token!=null?device_token:"");
                    bMessageSenderMapper.insertSelective(bMessageSender);
                }else if(!clientId.equals(cid)){
                    bMessageSenderMapper.updateClinetId(userInfo.get("siteId").toString(), userInfo.get("storeAdminId").toString(), cid, device_token);
                }
                /*stringRedisTemplate.opsForValue().set("notifyId_" + userInfo.get("siteId").toString() + "_" + userInfo.get("storeAdminId").toString(), cid);*/
                //stringRedisTemplate.opsForValue().set("notifyId_" + userInfo.get("siteId").toString() + "_" + userInfo.get("storeAdminId").toString() + "_" + cid, cid);

                String key = "notifyId_" + userInfo.get("siteId").toString() + "_" + userInfo.get("storeAdminId").toString();
                RedisUtil.setAdd(key,cid);

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        //查询rl_token
        String tl_token = getRLToken((Integer) userInfo.get("siteId"),(Integer) userInfo.get("storeAdminId"));
        userInfo.put("rlToken",tl_token);

        //保存登录信息
        saveLoginHistory((Integer)userInfo.get("userId"));

        //设置返回值
        result.put("status","OK");
        Map<String,Object> results = new HashMap<String,Object>();
        results.put("userInfo",userInfo);
        results.put("accessToken",accessToken);

        //如果切换成网易IM，返回对应的参数
        boolean neteaseActive = util.neteaseActive();
        results.put("neteaseActive",neteaseActive);
        if(neteaseActive){

            try{
                NeteaseAccid neteaseAccid = neteaseService.getNeteaseAccid(tl_token);
                results.put("neteaseToken",neteaseAccid.getToken());
                results.putIfAbsent("accid",neteaseAccid.getAccid());
            }catch (Exception e){
                results.put("neteaseCreateError",e.getMessage());
            }

        }


        result.put("results",results);
        return result;
    }

    public Map<String,Object> deldevicetoken(String siteId, String storeId, String storeAdminId, String clientId) throws Exception{
        Map<String,Object> result = new HashMap<>();
        /*stringRedisTemplate.delete("notifyId_" + siteId + "_" + storeAdminId);*/
        //stringRedisTemplate.delete("notifyId_" + siteId + "_" + storeAdminId + "_" + clientId);
        String key = "notifyId_" + siteId + "_" + storeAdminId;
        RedisUtil.setRemove(key,clientId);
        result.put("status", "OK");
        return result;
    }


    public Map<String,Object> updateClinetId(String siteId, String storeId, String storeAdminId, String clientId, String isLogin, String deviceToken) throws Exception{
        //更新肯定是登录以后,客户端cid不同时才会更新
        Map<String,Object> result = new HashMap<>();
        if(!StringUtil.isEmpty(clientId)){
            notifyOtherClientQuit(siteId, storeId, storeAdminId, clientId, "updateClinetId");//其它所有设备登录的都强制退出

            String cid = bMessageSenderMapper.getPushClientId(siteId, storeAdminId);//获取推送ID
            if(cid == null){
                BMessageSender bMessageSender = new BMessageSender();
                bMessageSender.setSiteId(Integer.parseInt(siteId));
                bMessageSender.setStoreAdminId(Integer.parseInt(storeAdminId));
                bMessageSender.setClientId(clientId);
                bMessageSender.setDeviceToken(!StringUtil.isEmpty(deviceToken)?deviceToken:"");
                bMessageSenderMapper.insertSelective(bMessageSender);
            }else if(!clientId.equals(cid)){
                bMessageSenderMapper.updateClinetId(siteId, storeAdminId, clientId, !StringUtil.isEmpty(deviceToken)?deviceToken:"");
            }

            /*stringRedisTemplate.opsForValue().set("notifyId_" + siteId + "_" + storeAdminId, clientId);*/
//            stringRedisTemplate.delete(stringRedisTemplate.keys("notifyId_" + siteId + "_" + storeAdminId + "_" + "*"));
            //stringRedisTemplate.opsForValue().set("notifyId_" + siteId + "_" + storeAdminId + "_" + clientId, clientId);

            RedisUtil.setAdd("notifyId_" + siteId + "_" + storeAdminId,clientId);
        }
        result.put("status","OK");
        return result;
    }

    /**
     *登录通知其它client退出
     */
    public void notifyOtherClientQuit(String siteId, String storeId, String storeAdminId, String loginClientId, String loginType){
        if(StringUtil.isEmpty(loginClientId)){
            return;
        }

        Map<String, Object> message = new HashMap<>();
        message.put("clientId", loginClientId);//登陆者所在设备
        message.put("loginType", loginType);

        //Set<String> clientIdKeys = stringRedisTemplate.keys("notifyId_" + siteId + "_" + storeAdminId + "_" + "*");
       // Set<String> clientIdKeys = RedisUtil.scan("notifyId_" + siteId + "_" + storeAdminId + "_" + "*");
        String key = "notifyId_" + siteId + "_" + storeAdminId;
        Set<String> clientIds = RedisUtil.setGetAllValue(key);
        if(clientIds!=null && clientIds.size()!=0){
            message.put("cIds", JSON.toJSONString(clientIds));
            for(String cid : clientIds){
                if(!StringUtil.isEmpty(cid) && !cid.equals(loginClientId)){
                    message.put("cId", cid);
                    InitialMessage initialMessage = new InitialMessage(siteId, storeId, storeAdminId);
                    initialMessage.setMessageType(PushType.NOTIFY_OTHER_CLIENT_QUIT.getValue());
                    initialMessage.setMessageMapJSON(JSON.toJSONString(message));
                    try {
                        pushServeService.pushMessageToSingleClientId(initialMessage, cid);
                        RedisUtil.setRemove(key, cid);//。。。。。
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * app接受参数日志
     *
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> insertPhoneLog(Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        try {
            //执行添加
            Integer i = bMessageSenderMapper.insertPhoneLog(params);
            if (i == 1){
                map.put("msg", "添加成功");
                map.put("status", 0);
                return map;
            }else {
                map.put("msg", "添加失败");
                map.put("status", -1);
                return map;
            }
        } catch (Exception e) {
            logger.info("添加异常:{}", e);
            map.put("msg", "添加失败");
            map.put("status", -1);
            return map;
        }
    }


    public Result getAppH5NewestVersion() {

        AppH5Version appH5Version = appH5VersionMapper.findNewestVersion();

        if(appH5Version == null){
            return Result.fail("查询结果为空");
        }

        return Result.success(appH5Version);
    }

    public Result addAppH5NewestVersion(AppH5Version param) {

        int num = appH5VersionMapper.insert(param);

        if(num == 1){
            return Result.success();
    }

        return Result.fail("添加数据失败");
    }
}
