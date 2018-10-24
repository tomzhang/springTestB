package com.jk51.modules.member.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.approve.FaceApprove;
import com.jk51.model.order.SBMemberInfo;
import com.jk51.modules.faceplusplus.constant.FacePlusPlusConstant;
import com.jk51.modules.faceplusplus.service.DetectService;
import com.jk51.modules.faceplusplus.service.FacePlusPlus;
import com.jk51.modules.member.mapper.FaceApproveMapper;
import com.jk51.modules.persistence.mapper.SBMemberInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/1/29
 * 修改记录:
 */
@Service
public class FaceApproveService {
    private static Logger logger = LoggerFactory.getLogger(FaceApproveService.class);

    @Autowired
    private FaceApproveMapper faceApproveMapper;
    @Autowired
    private FacePlusPlus facePlus;
    @Autowired
    private SBMemberInfoMapper sbMemberInfoMapper;
    @Autowired
    private DetectService detectService;


    public Integer add(FaceApprove faceApprove) {
        return faceApproveMapper.add(faceApprove);
    }

    public List<FaceApprove> getBySiteIdAndMemberId(Integer siteId, Integer memberId, Integer type) {

        List<FaceApprove> faceApproveLst = faceApproveMapper.getBySiteIdAndMemberId(siteId,memberId,type);
//        String leftEye = null;
//        String rightEye = null;
//        String emotion = null;
//        for (FaceApprove faceApprove : faceApproveLst) {
//            leftEye = faceApprove.getLeftEye();
//            rightEye = faceApprove.getRightEye();
//            emotion = faceApprove.getEmotion();
//
//            try {
//                Map map1 = JacksonUtils.json2map(emotion);
//                emotion = getMaxkeyFromMap(map1);
//                faceApprove.setEmotion(emotion);
//
//                Map map2 = JacksonUtils.json2map(leftEye);
//                leftEye = getMaxkeyFromMap(map2);
//                faceApprove.setLeftEye(leftEye);
//
//                Map map3 = JacksonUtils.json2map(rightEye);
//                rightEye = getMaxkeyFromMap(map3);
//                faceApprove.setRightEye(rightEye);
//
//            } catch (Exception e) {
//                logger.error("解析出错{}",e.getMessage());
//            }
//
//        }

        return faceApproveLst;
    }

    @Transactional
    public Integer parseFaceImg(Integer siteId, Integer memberId, String faceImg) {
        FaceApprove faceApprove = detectService.detect(siteId,faceImg);
        faceApprove.setSiteId(siteId);
        faceApprove.setMemberId(memberId);
        faceApprove.setImg(faceImg);
        if(faceApprove.getStatus().equals("fail")){
            return -1;
        }

        Integer x = add(faceApprove);
        if(x==1){
            SBMemberInfo memberInfo = sbMemberInfoMapper.getMemberInfo(memberId,siteId);
            if(memberInfo!=null){
                if(memberInfo.getIs_approve_avatar()!=null && memberInfo.getIs_approve_avatar()==1){
                    return x;
                }
                memberInfo.setIs_approve_avatar(1);
                x = sbMemberInfoMapper.updateMemberInfoByMemberId(memberInfo);
            }
        }
        return x;
    }


    /**
     * 获取value最大值对应的key
     * @param map
     * @return
     */
    public String getMaxkeyFromMap(Map map){
        if(map==null){
            return null;
        }
        List list = new ArrayList<>();
        double value=0.0;
        String maxKey = null;

        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            value = Double.parseDouble(entry.getValue().toString());
            list.add(entry.getValue());
            Collections.sort(list);
            if(value == Double.parseDouble(list.get(list.size()-1).toString())){
                maxKey = entry.getKey().toString();
            }
        }

        return maxKey;
    }


    public FaceApprove parseFaceImg2(Integer siteId, String faceImg) {
        FaceApprove faceApprove = detectService.detect(siteId,faceImg);

        if(faceApprove.getStatus().equals("fail")){
            return faceApprove;
        }

        faceApprove.setSiteId(siteId);
        faceApprove.setImg(faceImg);


//        try {
//            Map map1 = JacksonUtils.json2map(faceApprove.getEmotion());
//            String emotion = getMaxkeyFromMap(map1);
//            faceApprove.setEmotion(emotion);
//
//            Map map2 = JacksonUtils.json2map(faceApprove.getLeftEye());
//            String leftEye = getMaxkeyFromMap(map2);
//            faceApprove.setLeftEye(leftEye);
//
//            Map map3 = JacksonUtils.json2map(faceApprove.getRightEye());
//            String rightEye = getMaxkeyFromMap(map3);
//            faceApprove.setRightEye(rightEye);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return faceApprove;
    }
}
