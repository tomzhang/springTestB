package com.jk51.modules.tpl.sign;

import com.jk51.commons.string.StringUtil;
import com.jk51.modules.tpl.util.MD5Utils;
import com.jk51.modules.tpl.util.URLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
/**
 * 签名 获取token
 */
public class OpenSignHelper {
    private static final Log logger = LogFactory.getLog(OpenSignHelper.class);

    /**
     * 请求token时生成签名
     * @return
     */
    public static String generateSign(String appId, String salt, String secretKey) {
        StringBuilder seed = new StringBuilder();
        seed.append("app_id=").append(appId).append("&salt=").append(salt).append("&secret_key=").append(secretKey);
        String sign = "";
        try {
            String encodeString = URLUtils.getInstance().urlEncode(seed.toString());
            sign = MD5Utils.getMD5Code(encodeString);
            logger.info(String.format("querySting: %s, encodeString: %s, sign: %s", seed.toString(), encodeString, sign));
        } catch (UnsupportedEncodingException e) {
            logger.error("不支持的编码类型, %s", e);
        }
        return sign;
    }

    /**
     * 业务请求生成签名
     */
    public static String generateBusinessSign(Map<String, Object> sigStr) {
        StringBuilder seed = new StringBuilder();
        Set<String> set = sigStr.keySet();
        for (String key : set) {
            seed.append(key).append("=").append(sigStr.get(key)).append("&");
        }
        String queryString = StringUtil.stripEnd(seed.toString(), "&");
        logger.info(String.format("count string is %s", queryString));
        return MD5Utils.getMD5Code(queryString);
    }
}
