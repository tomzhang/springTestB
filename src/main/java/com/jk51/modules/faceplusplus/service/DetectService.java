package com.jk51.modules.faceplusplus.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.approve.FaceApprove;
import com.jk51.modules.faceplusplus.mapper.DetectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DetectService {

    private static Logger logger = LoggerFactory.getLogger(DetectService.class);

    @Autowired
    private FacePlusPlus facePlusPlus;

    @Autowired
    private TencentAi tencentAi;

    @Autowired
    private DetectMapper detectMapper;

    /**
     * 人脸识别接口
     *
     * @param siteId
     * @param imageUrl
     * @return
     */
    public FaceApprove detect(Integer siteId, String imageUrl){
        FaceApprove item;
        try {
            Map map = tencentAi.detect(imageUrl);
            item = tencentAiResult(map);
//            if(siteId.intValue() > 0 ){
//            }else {
//                item = facePlusPlusResult(facePlusPlus.detect(imageUrl));
//            }

            detectMapper.detectLog(siteId + "", imageUrl, JacksonUtils.mapToJson(map), null);
        } catch (Exception e) {
            logger.error("detect.error:",e);
            detectMapper.detectLog(siteId + "", imageUrl, e.toString(), "detect face error");
            return new FaceApprove("fail","人脸识别接口异常"+e.toString());
        }
        return item;
    }

    /**
     * 身份证识别接口
     *
     * @param siteId
     * @param imageUrl
     * @return
     */
    public Map idCard(Integer siteId, String imageUrl){
        Map map = new HashMap();
        try {
            Map m = tencentAi.idCard(imageUrl);
            map = tencentIdCardResult(m);
//            if(siteId.intValue() > 0 ){
//                map.put("type", 2);
//            }else {
//                map = facePlusPlus.idCard(imageUrl);
//                map.put("type", 1);
//            }


            Map data = (Map)m.get("data");
            data.put("frontimage", "");
            data.put("backimage", "");
            detectMapper.detectLog(siteId + "", imageUrl, JacksonUtils.mapToJson(m), null);
        } catch (Exception e) {
            detectMapper.detectLog(siteId + "", imageUrl, e.toString(), "detect idCard error");
            logger.error("error:",e);
        }
        return map;
    }

    public FaceApprove facePlusPlusResult(Map map){
        FaceApprove item= new FaceApprove();
        try {
            if(map.containsKey("error_message")){
                item.setMsg(map.get("error_message") + "");
                item.setStatus("fail");
                return item;
            }

            item.setType(1);

            JSONArray faces = JSONArray.parseArray(JacksonUtils.obj2json(map.get("faces")));
            JSONObject jsonObject = (JSONObject) faces.get(0);
            JSONObject attributes = jsonObject.getJSONObject("attributes");

            String image_id = map.get("image_id").toString();
            String gender = attributes.getString("gender").equals("Male")?"男":"女";
            Integer age = Integer.parseInt(attributes.getString("age"));
            String beauty = attributes.getString("beauty").toString();
            String glass = attributes.getString("glass");
            String emotion = attributes.getString("emotion").toString();
            String ethnicity = attributes.getString("ethnicity");
            String mouthstatus = attributes.getJSONObject("mouthstatus").toJSONString();
            String skinstatus = attributes.getJSONObject("skinstatus").toJSONString();
            String leftEye = attributes.getJSONObject("eyestatus").get("left_eye_status").toString();
            String rightEye = attributes.getJSONObject("eyestatus").get("right_eye_status").toString();
            String smile = attributes.getString("smile").toString();

            item.setImageId(image_id);
            item.setGender(gender);
            item.setAge(age);
            item.setBeauty(beauty);
            item.setGlass(glass);
            item.setEmotion(emotion);
            item.setEthnicity(ethnicity);
            item.setMouthstatus(mouthstatus);
            item.setSkinstatus(skinstatus);
            item.setLeftEye(leftEye);
            item.setRightEye(rightEye);
            item.setSmile(smile);

            item.setStatus("success");
        } catch (Exception e) {
            logger.error("facePlusPlusResult,返回数据解析异常{}",e.getMessage());
            item.setMsg(item.getMsg() + "------" + e.toString());
            item.setStatus("fail");
            return item;
        }
        return item;
    }

    public FaceApprove tencentAiResult(Map map){
        FaceApprove item= new FaceApprove();
        try {
            if(Integer.parseInt(map.get("ret").toString()) != 0 ){//0表示成功，非0表示出错
                item.setMsg(map.get("msg") + "");
                item.setStatus("fail");
                return item;
            }

            item.setType(2);

            JSONObject facesItem = JSONObject.parseObject(JacksonUtils.obj2json(map.get("data")));
            JSONArray faces = facesItem.getJSONArray("face_list");
            JSONObject attributes = (JSONObject) faces.get(0);

            String gender = attributes.getIntValue("gender") >= 50 ? "男" : "女";
            Integer age = attributes.getInteger("age");
            String beauty = attributes.getString("beauty");
            String glass = attributes.getString("glass");
            String smile = attributes.getString("expression").toString();//微笑[0~100] （0-没有笑容，50-微笑，100-大笑）

            item.setGender(gender);
            item.setAge(age);
            item.setBeauty(beauty);
            item.setGlass(glass);
            item.setSmile(smile);

            item.setStatus("success");
        } catch (Exception e) {
            logger.error("tencentAiResult,返回数据解析异常{}",e.getMessage());
            item.setMsg(item.getMsg() + "------" + e.toString());
            item.setStatus("fail");
            return item;
        }
        return item;
    }

    public Map tencentIdCardResult(Map m){

        Map map = new HashMap();
        try {
            JSONObject cardItem = JSONObject.parseObject(JacksonUtils.obj2json(m.get("data")));
            if(Integer.parseInt(m.get("ret")+"") != 0){
                map.put("error_message", m.get("msg"));
                return map;
            }
            Map cards = new HashMap();
            cards.put("name", cardItem.getString("name"));
            cards.put("race", cardItem.getString("nation"));
            cards.put("address", cardItem.getString("address"));
            cards.put("birthday", cardItem.getString("birth"));
            cards.put("issued_by", cardItem.getString("authority"));
            cards.put("valid_date", cardItem.getString("valid_date"));
            cards.put("id_card_number", cardItem.getString("id"));
            cards.put("gender", cardItem.getString("sex"));
            List list = new ArrayList<>();
            list.add(cards);
            map.put("cards", list);
        } catch (Exception e) {
            logger.error("error:",e);
        }

        return map;
    }



}
