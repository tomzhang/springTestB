package com.jk51.modules.wechat.service;

import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.encode.Base64Coder;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.approve.IdentityApprove;
import com.jk51.model.merchant.MemberCardSet;
import com.jk51.model.order.SBMember;
import com.jk51.model.order.SBMemberInfo;
import com.jk51.modules.appInterface.util.ResultMap;
import com.jk51.modules.coupon.service.CouponSendService;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.integral.service.IntegerRuleService;
import com.jk51.modules.member.service.IdentityApproveService;
import com.jk51.modules.merchant.service.MemberCardSetService;
import com.jk51.modules.offline.mapper.MerchantERPMapper;
import com.jk51.modules.offline.service.OfflineMemberService;
import com.jk51.modules.persistence.mapper.LoginLogMapper;
import com.jk51.modules.persistence.mapper.SBMemberInfoMapper;
import com.jk51.modules.persistence.mapper.SBMemberMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-31
 * 修改记录:
 */
@Service
public class WechatMemberService {
    private Logger log = LoggerFactory.getLogger(WechatMemberService.class);
    @Autowired
    private SBMemberMapper sbMemberMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private SBMemberInfoMapper sbMemberInfoMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;
    @Autowired
    private StoreMemberService storeMemberService;
    @Autowired
    private CouponSendService couponSendService;
    @Autowired
    IntegerRuleService integerRuleService;
    @Autowired
    private LoginLogMapper loginLogMapper;
    @Autowired
    private MerchantERPMapper erpMapper;
    @Autowired
    private MemberCardSetService memberCardSetService;
    @Autowired
    private IdentityApproveService identityApproveService;
    @Autowired
    private OfflineMemberService offlineMemberService;

    public void updateIdNum(Integer siteId, String mobile, String certif_no, Integer age) {
        sbMemberMapper.updateIdNum(siteId, mobile, certif_no, age);
    }

    public List<Map<String, Object>> getOfflineInfo(Integer siteId, String mobile) {
        return sbMemberMapper.getOfflineInfo(siteId, mobile);
    }

    public List<Map<String, Object>> getCardNo(Integer siteId, String mobile) {
        return sbMemberMapper.getCardNo(siteId, mobile);
    }

    public SBMember selectByPhoneNum(String mobile, Integer siteId) {
        return sbMemberMapper.selectByPhoneNum(mobile, siteId);
    }

    public Map<String, Object> loginByUnameAndPwd(String username, String password, Integer siteId) throws NoSuchAlgorithmException {
        if (StringUtil.isEmpty(username) && StringUtil.isEmpty(password)) {    //用户名密码非空校验
            return ResultMap.errorResult("用户名或密码不能为空");
        }

        SBMember member = sbMemberMapper.selectByPhoneNum(username, siteId);
        if (member != null) {
            if (member.getPasswd().equals(EncryptUtils.encryptToSHA1(EncryptUtils.encryptToSHA1(password)))) {
                if (member.getBan_status() == -9 || member.getBan_status() == -1) {
                    return ResultMap.errorResult("您的账户处于限制状态");
                } else {
                    if (member.getIs_activated() == 0) {
                        SBMember member1 = new SBMember();
                        member1.setIs_activated(1);
                        member1.setMember_id(member.getMember_id());
                        member1.setSite_id(member.getSite_id());
                        sbMemberMapper.updateMemberByMemberId(member1);
                        log.info("微信登录强制激活会员siteId：" + siteId + ",username:" + username);
//                      return ResultMap.errorResult("用户未激活");
                        couponSendService.sendCoupon(siteId,member.getMember_id());
                    }
                }
                HashMap<String, Object> results = new HashMap<>();
                sbMemberMapper.updateLastLoginTime(member.getSite_id(), member.getMember_id());
                results.put("msg", "登录成功");
                results.put("member", member);
//                    log.info("erp-start",member);
//                    erpGetUser(member);
                Map<String, Object> memberInfo = queryMemberInfoByPhoneNum(username, siteId);
                offlineMemberService.getuser(siteId, username, null);
                results.put("memberInfo", memberInfo);
                return results;
            } else {
                return ResultMap.errorResult("用户名或密码错误");
            }
        } else {
            return ResultMap.errorResult("该用户尚未注册");
        }
    }

    public Map<String, Object> queryMemberInfoByPhoneNum(String phone, Integer site_id) {
        HashMap<String, Object> result = null;
        try {
            result = memberMapper.queryMemberInfoByPhoneNum(phone, site_id);
            if (result == null) {
                return ResultMap.errorResult("抱歉，会员信息不存在");
            }
            return result;
        } catch (Exception e) {
            return ResultMap.errorResult("查询会员信息失败");
        }
    }

    @Transactional(propagation = Propagation.NESTED)
    public Map<String, Object> loginByUnameAndVcode(Integer siteId, String phoneNum, String inviteCode, String passwd,Integer mem_source, String secondToken) {
        HashMap<String, Object> results = new HashMap<>();
        SBMember member = sbMemberMapper.selectByPhoneNum(phoneNum, siteId);
        boolean flag = true;
        if (member != null) {//该用户存在
            if (member.getBan_status() == -9 || member.getBan_status() == -1) {
                return ResultMap.errorResult("您的账户处于限制状态");
            } else {
                //校验info会员扩展信息表是否存在
                SBMemberInfo bMemberInfo = sbMemberInfoMapper.getMemberInfo(member.getBuyer_id(), member.getSite_id());
                if(member.getIs_activated() == 1){
                    flag = false;
                }
                if (member.getIs_activated() == 0) {
                    SBMember member1 = new SBMember();
                    member1.setIs_activated(1);
                    member1.setMember_id(member.getMember_id());
                    member1.setSite_id(member.getSite_id());
                    log.info("微信登录强制激活会员siteId：" + siteId + ",phoneNum:" + phoneNum);
                    if(StringUtil.isEmpty(member1.getRegister_clerks())&&!StringUtil.isEmpty(bMemberInfo)){
                        //邀请码处理
                        try {
                            if (!StringUtil.isBlank(inviteCode)) {
                                List<StoreAdminExt> storeAdminExtList = storeAdminExtMapper.selectClerkListLikeByInviteCode(siteId, inviteCode);
                                if (!(CollectionUtils.isEmpty(storeAdminExtList) || storeAdminExtList.size() == 0)) {
                                    StoreAdminExt storeAdminext = storeAdminExtList.get(0);
                                    if (storeAdminext != null) {
                                        String ivcode = storeAdminext.getClerk_invitation_code();
                                        if (ivcode.contains("_"))
                                            bMemberInfo.setInvite_code(storeAdminext.getClerk_invitation_code());
                                        else {
                                            bMemberInfo.setInvite_code(storeAdminext.getStore_id() + "_" + storeAdminext.getClerk_invitation_code());
                                        }
                                        member1.setRegister_clerks(Long.parseLong(String.valueOf(storeAdminext.getId())));
                                        member1.setRegister_stores(storeAdminext.getStore_id());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error("inviteCode:" + inviteCode + ",查询错误", e);
                        }
                    }
                    sbMemberMapper.updateMemberByMemberId(member1);
                    sbMemberInfoMapper.updateInviteCodeByMemberId(bMemberInfo);
                    offlineMemberService.getuser(siteId, phoneNum, inviteCode);
                }



                if (bMemberInfo == null) {
                    bMemberInfo = new SBMemberInfo();
                    bMemberInfo.setSite_id(member.getSite_id());
                    bMemberInfo.setMember_id(member.getBuyer_id());
                    bMemberInfo.setSecondToken(secondToken);
                    sbMemberInfoMapper.insertSelective(bMemberInfo);
                }
                results.put("msg", "登录成功");
            }
        } else {
            SBMember member1 = new SBMember();
            SBMemberInfo memberInfo1 = new SBMemberInfo();
            if(!StringUtil.isEmpty(inviteCode)&&inviteCode.indexOf("store")>-1){
                String[] split = inviteCode.split("_");
                if(!StringUtil.isEmpty(split)&&split.length==2){
                    member1.setRegister_stores(Integer.parseInt(split[1]));
                }
            }else{
                //邀请码是否查到都要存 （Date 20170406 17:21）
                memberInfo1.setInvite_code(inviteCode);
                //邀请码处理
                try {
                    if (!StringUtil.isBlank(inviteCode)) {
                        List<StoreAdminExt> storeAdminExtList = storeAdminExtMapper.selectClerkListLikeByInviteCode(siteId, inviteCode);
                        if (!(CollectionUtils.isEmpty(storeAdminExtList) || storeAdminExtList.size() == 0)) {
                            StoreAdminExt storeAdminext = storeAdminExtList.get(0);
                            if (storeAdminext != null) {
                                String ivcode = storeAdminext.getClerk_invitation_code();
                                if (ivcode.contains("_"))
                                    memberInfo1.setInvite_code(storeAdminext.getClerk_invitation_code());
                                else {
                                    memberInfo1.setInvite_code(storeAdminext.getStore_id() + "_" + storeAdminext.getClerk_invitation_code());
                                }
                                member1.setRegister_clerks(Long.parseLong(String.valueOf(storeAdminext.getId())));
                                member1.setRegister_stores(storeAdminext.getStore_id());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("inviteCode:" + inviteCode + ",查询错误", e);
                }
            }

            member1.setMobile(phoneNum);
            member1.setSite_id(siteId);
            member1.setPasswd(passwd);

            //用户来源: 110 (网站) ; 120（微信）; 130（app）; 140 (后台手工录入) ; 9999（其它）,
            member1.setMem_source(mem_source);
            memberInfo1.setSite_id(siteId);
            try {
                memberInfo1.setSecondToken(secondToken);
                int i = storeMemberService.addMember(member1, memberInfo1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            member = sbMemberMapper.selectByPhoneNum(phoneNum, siteId);
            if (member != null) {
                //送积分
                Map<String, Object> map = new HashMap();
                map.put("siteId", member.getSite_id());
                map.put("buyerId", member.getBuyer_id());
                map.put("buyerNick", member.getBuyer_nick());
                log.info("siteId:[{}],buyerId[{}],buyerNick:[{}]", member.getSite_id(), member.getBuyer_id(), member.getBuyer_nick());
//                map = wechatIntegralService.integral4Regist(map);
                map = integerRuleService.getIntegralByRegister(map);
                results.put("status", 1);
                results.put("info", map);
//                log.info("erp-start",member);
//                erpGetUser(member);

            } else {
                results.put("status", 0);
            }

        }
        if(flag) {
            couponSendService.sendCoupon(member.getSite_id(), member.getMember_id());
        }
        if(!StringUtil.isEmpty(secondToken)){
            sbMemberInfoMapper.updateSecondTokenByMemberId(secondToken,member.getBuyer_id(),member.getSite_id());
        }
        sbMemberMapper.updateLastLoginTime(member.getSite_id(), member.getMember_id());
        Map<String, Object> memberInfo = queryMemberInfoByPhoneNum(member.getMobile(), member.getSite_id());
        offlineMemberService.getuser(siteId, phoneNum, inviteCode);
        results.put("member", member);
        results.put("memberInfo", memberInfo);
        return results;
    }

    @Transactional
    public Map<String, Object> loginByUnameAndVcode2(Map<String, Object> params) throws Exception{
        HashMap<String, Object> result = new HashMap<>();
        Integer siteId = Integer.parseInt((String) params.get("siteId"));
        String mobile = (String) params.get("mobile");

        Map<String, Object> member = memberMapper.queryMemberInfoByPhoneNum(mobile, siteId);
        if(member != null){
            result.put("member", member);
        }else {
            SBMember member1 = new SBMember();
            SBMemberInfo memberInfo1 = new SBMemberInfo();

            member1.setMobile(mobile);
            member1.setSite_id(siteId);
            member1.setMem_source(120);//用户来源: 110 (网站) ; 120（微信）; 130（app）; 140 (后台手工录入) ; 9999（其它）,
            memberInfo1.setSite_id(siteId);
            int i = storeMemberService.addMember(member1, memberInfo1);

            member = memberMapper.queryMemberInfoByPhoneNum(mobile, siteId);
            if (member != null) {//送积分
                Map<String, Object> map = new HashMap();
                map.put("siteId", member.get("siteId"));
                map.put("buyerId", member.get("buyerId"));
                map.put("buyerNick", member.get("buyerNick"));
                log.info("siteId:[{}],buyerId[{}],buyerNick:[{}]", member.get("siteId"), member.get("buyerId"), member.get("buyerNick"));
//                map = wechatIntegralService.integral4Regist(map);
                try {
                    couponSendService.sendCoupon(Integer.parseInt(String.valueOf(member.get("siteId"))), Integer.parseInt(String.valueOf(member.get("memberId"))));
                    map = integerRuleService.getIntegralByRegister(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result.put("member", member);
//                log.info("erp-start",member);
//                erpGetUser(member);
            } else {
                throw new RuntimeException("memberException");
            }
        }
        try {
            sbMemberMapper.updateLastLoginTime(Integer.parseInt(String.valueOf(member.get("siteId"))), Integer.parseInt(String.valueOf(member.get("memberId"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Object> getCheckin(Integer siteId, Integer memberId) {
        return memberMapper.getCheckin(siteId, memberId);
    }

    public Integer setCheckin(Integer siteId, Integer memberId, Integer checkinNum) {
        return memberMapper.setCheckin(siteId, memberId, checkinNum);
    }

    public Map checkMember(Map map) {
        return memberMapper.checkMember(map);
    }

    public Integer updatePassword(Map map) {
        return memberMapper.updatePassword(map);
    }

    public Integer updatePasswordByMobile(Map map) {
        return memberMapper.updatePasswordByMobile(map);
    }

    @Transactional
    public Integer setUserPassword(Map<String, Object> params) {
        Integer x = 0;
        String inviteCode="";
        if(params.containsKey("inviteCode")){
            inviteCode = params.get("inviteCode").toString();
        }
        Integer siteId = Integer.parseInt(params.get("siteId").toString());

//        String buyerNick = params.get("buyerNick").toString();
        String phoneNum = params.get("phoneNum").toString();

        SBMember member1 = sbMemberMapper.selectByPhoneNum(phoneNum, siteId);

        if(member1 != null){

//            member1.setBuyer_nick(buyerNick);
            String passwd = null;
            if(params.containsKey("passwd")){
                passwd = params.get("passwd").toString();
                member1.setPasswd(passwd);
            }

            if(/*x==1 && */StringUtil.isNotEmpty(inviteCode)){
                SBMemberInfo bMemberInfo = sbMemberInfoMapper.getMemberInfo(member1.getBuyer_id(), member1.getSite_id());
                if(bMemberInfo!=null && StringUtil.isEmpty(bMemberInfo.getInvite_code())){//如果邀请码不存在 才会更新
                    //邀请码是否查到都要存 （Date 20170406 17:21）
                    bMemberInfo.setInvite_code(inviteCode);
                    //邀请码处理
                    try {
                        if (!StringUtil.isBlank(inviteCode)) {
                            StoreAdminExt storeAdminext = storeAdminExtMapper.selectClerkListLikeByInviteCode(siteId, inviteCode).get(0);
                            if (storeAdminext != null) {
                                String ivcode = storeAdminext.getClerk_invitation_code();
                                if (ivcode.contains("_"))
                                    bMemberInfo.setInvite_code(storeAdminext.getClerk_invitation_code());
                                else {
                                    bMemberInfo.setInvite_code(storeAdminext.getStore_id() + "_" + storeAdminext.getClerk_invitation_code());
                                }
                                member1.setRegister_clerks(Long.parseLong(String.valueOf(storeAdminext.getId())));
                                member1.setRegister_stores(storeAdminext.getStore_id());
                            }
                        }
                    } catch (Exception e) {
                        log.error("inviteCode:" + inviteCode + ",查询错误", e);
                    }
                    x = sbMemberInfoMapper.updateMemberInfoByMemberId(bMemberInfo);
                }
            }
            x = sbMemberMapper.updateMemberByMemberId(member1);
        }
        return x;
    }

    @Transactional
    public Integer updUserInfo(Map<String, Object> params) {
        Integer x = 0;

        String inviteCode="";
        if(params.containsKey("inviteCode")){
            inviteCode = params.get("inviteCode").toString();
        }
        Integer siteId = Integer.parseInt(params.get("siteId").toString());
        String phoneNum = params.get("mobile").toString();

        Integer sex = null;
        if(params.containsKey("sex")){
            sex = Integer.parseInt(params.get("sex").toString());
        }

        String buyerNick = null;
        if(params.containsKey("buyerNick")){
            buyerNick = params.get("buyerNick").toString();
            try {
                buyerNick = Base64Coder.encode(buyerNick);
            } catch (UnsupportedEncodingException e) {
                log.error("buyerNick:" + buyerNick + ",编码错误", e);
            }
        }

        String name = null;
        if(params.containsKey("name")){
            name = params.get("name").toString();
        }

        String email = null;
        if(params.containsKey("email")){
            email = params.get("email").toString();
        }

        String avatar = null;
        if(params.containsKey("avatar")){
            avatar = params.get("avatar").toString();
        }

        Date birthDay = null;
        if(params.containsKey("birthday")){
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
            try {
                birthDay = sdf.parse(params.get("birthday").toString());
//                birthDay = new Date(params.get("birthday").toString());//前端直接传标准时间格式date
            } catch (Exception e) {
                log.error("日期转换异常：",e);
            }
        }

        Integer province = null, city = null, area = null;
        if(params.containsKey("province") && params.containsKey("city") && params.containsKey("area")
            && !"null".equals(params.get("province").toString()) && !"null".equals(params.get("city").toString())
            && !"null".equals(params.get("area").toString()) && StringUtil.isNotEmpty(params.get("province").toString())
            && StringUtil.isNotEmpty(params.get("city").toString()) && StringUtil.isNotEmpty(params.get("area").toString())){
            province = Integer.parseInt(params.get("province").toString());
            city = Integer.parseInt(params.get("city").toString());
            area = Integer.parseInt(params.get("area").toString());
        }

        String address = "";
        if(params.containsKey("address")){
            address = params.get("address").toString();
        }

        SBMember member = sbMemberMapper.selectByPhoneNum(phoneNum, siteId);
        if(member != null){
            member.setBuyer_nick(buyerNick);
            member.setEmail(email);
            member.setName(name);
            member.setSex(sex);
            x = sbMemberMapper.updateMemberByMemberId(member);//会员主表
            if(x == 1){
                SBMemberInfo memberInfo = sbMemberInfoMapper.getMemberInfo(member.getBuyer_id(),siteId);
                if(memberInfo != null){
                    memberInfo.setAvatar(avatar);
                    if(birthDay!=null){
                        memberInfo.setBirthday(birthDay);
                    }
                    if(params.containsKey("isApproveAvatar")){
                        memberInfo.setIs_approve_avatar(1);
                    }
                    memberInfo.setAddress(address);
                    memberInfo.setProvince(province);
                    memberInfo.setCity(city);
                    memberInfo.setArea(area);

                    //邀请码处理
//                    memberInfo.setInvite_code(inviteCode);
//                    try {
//                        if (!StringUtil.isBlank(inviteCode)) {
//                            StoreAdminExt storeAdminext = storeAdminExtMapper.selectClerkListLikeByInviteCode(siteId, inviteCode).get(0);
//                            if (storeAdminext != null) {
//                                String ivcode = storeAdminext.getClerk_invitation_code();
//                                if (ivcode.contains("_"))
//                                    memberInfo.setInvite_code(storeAdminext.getClerk_invitation_code());
//                                else {
//                                    memberInfo.setInvite_code(storeAdminext.getStore_id() + "_" + storeAdminext.getClerk_invitation_code());
//                                }
//                                member.setRegister_clerks(Long.parseLong(String.valueOf(storeAdminext.getId())));
//                                member.setRegister_stores(storeAdminext.getStore_id());
//                            }
//                        }
//                    } catch (Exception e) {
//                        log.error("inviteCode:" + inviteCode + ",查询错误", e);
//                    }

                    x = sbMemberInfoMapper.updateMemberInfoByMemberId(memberInfo);//会员从表
                }
            }

        }
        return x;
    }

    public Map recoreLoginLog(String siteId, String mobile, String buyerId, String inviteCode, String ip){
        Map map = new HashMap();
        int i = loginLogMapper.recordLog(siteId, mobile, buyerId, inviteCode, ip);
        map.put("count", i);
        return map;
    }

    public Map<String,Object> getUserInfo(Map<String, Object> params) {
        try {
            Map<String,Object> map = new HashMap<>();
            String mobile = params.get("mobile").toString();
            Integer siteId = Integer.parseInt(params.get("siteId").toString());
            SBMember member = sbMemberMapper.selectByPhoneNum(mobile, siteId);

            if(member != null){
                String sex=StringUtil.isEmpty(member.getSex())?"保密":(member.getSex()==0?"女":(member.getSex()==1?"男":"保密"));

                if(Base64Coder.isBase64(member.getBuyer_nick()))
                    map.put("buyerNick",Base64Coder.decode(member.getBuyer_nick()));
                else
                    map.put("buyerNick",member.getBuyer_nick());

                map.put("name",member.getName());
                map.put("sex",sex);
                map.put("mobile",mobile);
                map.put("email",member.getEmail());
                map.put("openId",member.getOpen_id());

                SBMemberInfo memberInfo = sbMemberInfoMapper.getMemberInfo(member.getBuyer_id(),siteId);
                if(memberInfo != null){
                    map.put("avatar",memberInfo.getAvatar());
                    map.put("birthDay",memberInfo.getBirthday());
                    map.put("inviteCode",memberInfo.getInvite_code());
                    map.put("address",memberInfo.getAddress()==null?"":memberInfo.getAddress());
                    map.put("province",memberInfo.getProvince());
                    map.put("city",memberInfo.getCity());
                    map.put("area",memberInfo.getArea());
                    map.put("shipNumber",memberInfo.getMembership_number());

                    map.put("isApproveAvatar",memberInfo.getIs_approve_avatar()==null?0:memberInfo.getIs_approve_avatar());
                }

                Integer isErp = 300;//300 商户未开通erp，100 商户开通但用户未接通，200 商户用户，均打通erp
                Map<String, Object> erp = erpMapper.selectMerchantERPInfo(siteId);
                if(erp != null){
                    Integer status = Integer.parseInt(erp.get("status").toString());// 0 关闭  1 正常
                    Integer userIsErp = member.getFirst_erp();// 是否同步过erp 0 否 1 是

                    if(status==1 && userIsErp==1){
                        isErp = 200;
                    }else if(status==1 && userIsErp==0){
                        isErp = 100;
                    }
                }
                map.put("isErp",isErp);
            }
            //获取商家会员卡信息
            MemberCardSet cardSet = memberCardSetService.getBySiteId(siteId);
            if(cardSet != null){
                map.put("cardImg",cardSet.getBgImg());
                map.put("cardTitle",cardSet.getTitle());
                map.put("cardMKWords",cardSet.getMkWords());
            }else {
                map.put("cardImg","");
                map.put("cardTitle","");
                map.put("cardMKWords","");
            }

            //获取会员认证信息
            IdentityApprove identityApprove = identityApproveService.getByMemberIdAndSiteId(siteId, member.getBuyer_id());
            if(!StringUtil.isEmpty(member.getIdcard_number())){//身份证存在即为已认证
                map.put("isApproveIdentity",1);
            }else if (identityApprove==null){
                map.put("isApproveIdentity",-1);
            }else {
                map.put("isApproveIdentity",identityApprove.getStatus());
            }

            return map;
        }catch (Exception e){
            log.error("获取个人信息异常",e);
            return null;
        }
    }

    public SBMember getBMember(Map<String, Object> params) {
        String mobile = params.get("mobile").toString();
        Integer siteId = Integer.parseInt(params.get("siteId").toString());
        SBMember member = sbMemberMapper.selectByPhoneNum(mobile, siteId);
        return member;
    }

    public Map recordLoginLog2(Map<String,Object> params){
        Map map = new HashMap();
        int i = loginLogMapper.recordLoginLog2(params);
        map.put("count", i);
        return map;
    }

    public Map getTokenByMemberId(Map<String,Object> params){
        Map map = new HashMap();
        String token = null;
        if (!StringUtil.isEmpty(params) && !StringUtil.isEmpty(params.get("source"))){
            token = loginLogMapper.getTokenByMemberId(params);
        }
        map.put("token", token);
        return map;
    }
}
