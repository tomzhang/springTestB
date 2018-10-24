package com.jk51.modules.faceplusplus.service;

import com.gexin.rp.sdk.base.uitls.MD5Util;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.modules.faceplusplus.config.DetectConfig;
import com.jk51.modules.faceplusplus.constant.TencentAiConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
/**
 * 签名计算以及请求demo
 *
 * @author su_jian
 * 2017-11-13
 */

@Component
public class TencentAi {

    @Autowired
    private DetectConfig detectConfig;

    /**
     * 人脸分析
     *
     * @param imageUrl
     * @return
     */
    public Map detect(String imageUrl) {
        Map params = new HashMap<>();
        params.put("mode", "0");
        params.put("image", ImageUtil.getImageStrFromUrl(imageUrl));
        return sendRequest(TencentAiConstant.DETECT, params);
    }

    /**
     * 身份证识别
     *
     * @param imageUrl
     * @return
     */
    public Map idCard(String imageUrl) {
        Map params = new HashMap<>();
        params.put("card_type", "0");
        params.put("image", ImageUtil.getImageStrFromUrl(imageUrl));
        return sendRequest(TencentAiConstant.OCR_ID_CARD, params);
    }

    /**
     * 五官定位
     *
     * @param imageUrl
     * @return
     */
    public Map faceShape(String imageUrl) {
        Map params = new HashMap<>();
        params.put("mode", "0");
        params.put("image", ImageUtil.getImageStrFromUrl(imageUrl));
        return sendRequest(TencentAiConstant.FACE_SHAPE, params);
    }

    /**
     * 获取所有的参数签名字符窜
     *
     * @param params 参数
     * @return sign str
     */
    public String getFullUrlSign(Map<String, String> params) {
        String tempUrl;
        String url = null;
        try {
            tempUrl = getParamString(params);
            url = tempUrl + "&app_key=" + detectConfig.getTencentAppKey();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return MD5Util.getMD5Format(url).toUpperCase();
    }

    /**
     * 获取带签名字符串
     *
     * @param params 生成带签名字符串的对象
     * @return 带签名字符串
     */
    public static String getParamString(Map<String, String> params) throws UnsupportedEncodingException {
        String charset = "UTF-8";
        String[] paramArr = params.keySet().toArray(new String[0]);
        Arrays.sort(paramArr);
        StringBuilder sb = new StringBuilder("");
        for (String key : paramArr) {
            if (0 < sb.length()) {
                sb.append("&");
            }
            String value = params.get(key);
            if (null != value) {
                sb.append(key).append("=").append(URLEncoder.encode(value, charset));
            }
        }
        return sb.toString().trim();
    }

    public void paramSign(Map<String, String> param){
        param.put("app_id", detectConfig.getTencentAppId());
        param.put("nonce_str", UUID.randomUUID().toString().replaceAll("-", "").substring(0,9));
        long time = new Date().getTime()/1000;
        param.put("time_stamp", time + "");
        String signStr = getFullUrlSign(param);
        param.put("sign", signStr);
    }

    public Map sendRequest(String url, Map<String, String> param){
        paramSign(param);
        try {
            return JacksonUtils.json2map(OkHttpUtil.postMap(url, param));
        } catch (Exception e) {

            return null;
        }
    }

}
