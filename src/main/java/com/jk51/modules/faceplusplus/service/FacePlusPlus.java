package com.jk51.modules.faceplusplus.service;


import com.jk51.commons.http.HttpClient;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.faceplusplus.constant.FacePlusPlusConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FacePlusPlus {

    private static Logger logger = LoggerFactory.getLogger(FacePlusPlus.class);

    /**
     * 人脸识别
     *
     * @param imageUrl
     */
    public Map detect(String imageUrl){

        Map param = getBaseParamMap();
        param.put("return_landmark", 0);
        param.put("image_url", imageUrl);
        param.put("return_attributes","gender,age,smiling,headpose,facequality,blur,eyestatus,emotion,ethnicity,beauty,mouthstatus,eyegaze,skinstatus");
        try {
            String str = HttpClient.doHttpPost(FacePlusPlusConstant.DETECT, param);
            logger.info(str);
            return JacksonUtils.json2map(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 身份证识别
     *
     * @param imageUrl
     */
    public Map idCard(String imageUrl){

        Map param = getBaseParamMap();
        param.put("legality", 1);
        param.put("image_url", imageUrl);
        try {
            String str = HttpClient.doHttpPost(FacePlusPlusConstant.OCR_ID_CARD, param);
            logger.info(str);
            return JacksonUtils.json2map(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void handleIdCardResult(String str){
        try {
            Map map = JacksonUtils.json2map(str);
            if (StringUtil.isEmpty(map.get("error_message"))) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Map getBaseParamMap(){
        Map param = new HashMap();
        param.put("api_key", "9fV7Sg8aOys7IsXWO4nZzGLvsZY3QSSn");//todo
        param.put("api_secret", "qRX3P3UxRR21088zX04ubpy5gv6LTWCy");//todo
        return param;
    }

}
