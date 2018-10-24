package com.jk51.modules.member.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.approve.IdentityApprove;
import com.jk51.model.order.SBMember;
import com.jk51.model.order.SBMemberInfo;
import com.jk51.modules.faceplusplus.constant.FacePlusPlusConstant;
import com.jk51.modules.faceplusplus.service.FacePlusPlus;
import com.jk51.modules.member.mapper.IdentityApproveMapper;
import com.jk51.modules.member.request.IdentityApproveParm;
import com.jk51.modules.offline.service.OfflineMemberService;
import com.jk51.modules.persistence.mapper.SBMemberInfoMapper;
import com.jk51.modules.persistence.mapper.SBMemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/1/19
 * 修改记录:
 */
@Service
public class IdentityApproveService {
    private static final Logger logger = LoggerFactory.getLogger(IdentityApproveService.class);

    @Autowired
    private IdentityApproveMapper identityApproveMapper;
    @Autowired
    private SBMemberInfoMapper sbMemberInfoMapper;
    @Autowired
    private SBMemberMapper sbMemberMapper;
    @Autowired
    private OfflineMemberService offlineMemberService;
    @Autowired
    private FacePlusPlus facePlus;


    public Integer add(IdentityApprove identityApprove) {

        IdentityApprove identityApprove2 = getByMemberIdAndSiteId(identityApprove.getSiteId(), identityApprove.getMemberId());
        if(identityApprove2 != null){
            return -2;
        }
        return identityApproveMapper.add(identityApprove);
    }

    public Integer upd(IdentityApprove identityApprove) {
        return identityApproveMapper.upd(identityApprove);
    }

    public IdentityApprove getByMemberIdAndSiteId(Integer siteId, Integer memberId) {
        return identityApproveMapper.getByMemberIdAndSiteId(siteId, memberId);
    }

    public List<IdentityApprove> getLstBySiteId(IdentityApproveParm parms) {
        return identityApproveMapper.getLstBySiteId(parms);
    }

    public Map<String,Object> parseIdcardImg(String img) {
        return facePlus.idCard(img);
    }

    /**
     * 人工审核
     * @param identityApprove
     * @return
     */
    @Transactional
    public Integer audit(IdentityApprove identityApprove) {

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-DD-MM");
            Integer x = identityApproveMapper.audit(identityApprove);
            //审核通过--更新用户 姓名、年龄、生日、身份证（考虑erp信息）
            //todo  暂时不做处理（等产品定文档）
//            if(x==1 && identityApprove.getStatus()==1){
//                identityApprove = getByMemberIdAndSiteId(identityApprove.getSiteId(),identityApprove.getMemberId());
//                if(identityApprove != null){
//                    //根据身份证号读取信息
//                    Map<String,Object> frontImg = approveIdcardNum(identityApprove.getIdcardNumber());
//                    SBMember member = sbMemberMapper.selectByPhoneNum(identityApprove.getMobile(), identityApprove.getSiteId());
//                    //进行更新用户数据
//                    member.setSex(Integer.parseInt(frontImg.get("sex").toString()));
//                    member.setName(identityApprove.getName());
//                    member.setIdcard_number(identityApprove.getIdcardNumber());
//                    x = sbMemberMapper.updateMemberByMemberId(member);//会员主表
//                    if(x==1){
//                        SBMemberInfo memberInfo = sbMemberInfoMapper.getMemberInfo(member.getBuyer_id(),identityApprove.getSiteId());
//                        memberInfo.setBirthday(format.parse(frontImg.get("birthday").toString()));
////                        memberInfo.setAge(null);
//                        x = sbMemberInfoMapper.updateMemberInfoByMemberId(memberInfo);//会员从表
//                        if(x == 1){
//                            //更新erp信息
//                            Map<String, Object> requestParams = new HashMap<>();
//                            requestParams.put("sex",frontImg.get("sex"));
//                            requestParams.put("name",identityApprove.getName());
//                            requestParams.put("mobile",identityApprove.getMobile());
//                            requestParams.put("birthday",format.parse(frontImg.get("birthday").toString()));
//                            Map<String, Object> res = offlineMemberService.updateMemberinfo(requestParams);
//                            logger.info("线下erp更新结果：",JacksonUtils.mapToJson(res));
//                        }
//                    }
//                }
//
//            }

            return x;
        }catch (Exception e){
            logger.error("审核处理异常",e.getMessage());
            return -1;
        }
    }

    /**
     * todo 后期用到要考虑商家扣款问题
     * 智能审核流程( face++ )
     */
//    @Transactional
//    public Integer audit(IdentityApprove identityApprove) {
//
//        try {
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-DD-MM");
//            Integer x = identityApproveMapper.audit(identityApprove);
//
//            //todo 审核通过--更新用户 姓名、年龄、生日、身份证（考虑erp信息）
//            if(x==1 && identityApprove.getStatus()==1){
//                identityApprove = getByMemberIdAndSiteId(identityApprove.getSiteId(),identityApprove.getMemberId());
//                if(identityApprove != null){
//                    //todo 进行身份证正面照识别
//                    Map<String,Object> frontImg = parseIdcardImg(FacePlusPlusConstant.ALi_URL+identityApprove.getFrontImg());
//                    logger.info("身份证识别结果{}",frontImg);
////                if(frontImg==null || frontImg.containsKey("error_message")){
////                    return 0;
////                }
//
//                    SBMember member = sbMemberMapper.selectByPhoneNum(identityApprove.getMobile(), identityApprove.getSiteId());
//                    //todo 识别身份证信息--进行更新用户数据
//                    member.setSex(frontImg.get("gender").equals("女")?0:frontImg.get("gender").equals("男")?1:3);
//                    member.setName(frontImg.get("name").toString());
//                    member.setIdcard_number(identityApprove.getIdcardNumber());
//                    x = sbMemberMapper.updateMemberByMemberId(member);//会员主表
//                    if(x==1){
//                        SBMemberInfo memberInfo = sbMemberInfoMapper.getMemberInfo(member.getBuyer_id(),identityApprove.getSiteId());
//                        memberInfo.setBirthday(format.parse(frontImg.get("birthday").toString()));
////                        memberInfo.setAge(null);
//                        x = sbMemberInfoMapper.updateMemberInfoByMemberId(memberInfo);//会员从表
//                        if(x == 1){
//                            //todo 更新erp信息
//                            Map<String, Object> requestParams = new HashMap<>();
//                            requestParams.put("sex",frontImg.get("gender").equals("女")?0:frontImg.get("gender").equals("男")?1:3);
//                            requestParams.put("name",frontImg.get("name").toString());
//                            requestParams.put("mobile",identityApprove.getMobile());
//                            requestParams.put("birthday",format.parse(frontImg.get("birthday").toString()));
//                            Map<String, Object> res = offlineMemberService.updateMemberinfo(requestParams);
//                            logger.info("线下erp更新结果：",JacksonUtils.mapToJson(res));
//                        }
//                    }
//                }
//
//            }
//
//            return x;
//        }catch (Exception e){
//            logger.error("审核处理异常",e.getMessage());
//            return -1;
//        }
//    }

    /**
     * 处理身份证号码
     * 前1、2位数字表示：所在省份的代码；
     * 2.第3、4位数字表示：所在城市的代码；
     * 3.第5、6位数字表示：所在区县的代码；
     * 4.第7~14位数字表示：出生年、月、日；
     * 5.第15、16位数字表示：所在地的派出所的代码；
     * 6.第17位数字表示性别：奇数表示男性，偶数表示女性；
     * @param idcardNum
     * @return
     */
    public Map<String, Object> approveIdcardNum(String idcardNum){
        Map<String, Object> map = new HashMap<>();
        String num17 = idcardNum.substring(16,17);//身份证倒数第二位  判断性别
        if(Integer.parseInt(num17)%2==0){
            map.put("sex","0");//女
        }else{
            map.put("sex","1");//男
        }
        String num7_14 = idcardNum.substring(6,14).substring(0,4)+"-"+idcardNum.substring(6,14).substring(4,6)+"-"+idcardNum.substring(6,14).substring(6,8);
        map.put("birthday",num7_14);
        return map;
    }

    public List<Map> getNumByType(Integer siteId) {
        return identityApproveMapper.getNumByType(siteId);
    }

    public List<Map> getNumByStatus(Integer siteId) {
        return identityApproveMapper.getNumByStatus(siteId);
    }
}
