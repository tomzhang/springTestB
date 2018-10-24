package com.jk51.configuration;

import javax.servlet.http.HttpServletRequest;

/**
 *Sessionkey的常量
 */
public class SessionConfig  {

    /**
     * 商户id
     */
    public static final String SITEID_KEY="siteId";
    /**
     * 门店id
     */
    public static final String STOREID_KEY="storeId";
    /**
     * 登录用户名
     */
    public static final String USER_NAME_KEY="userName";
    /**
     * 登录用户ID
     */
    public static final String USER_ID_KEY="userID";
    /**
     *微信登录Token
     */
    public static final String TOKEN_KEY="wxToken";

    /**
     *用户唯一id
     */
    public static final String BUYERID_KEY="buyerId";

    /**
     * 用户类型
     */
    public static final String USER_TYPE = "userType";
    /**
     * 登录用户手机号
     */
    public static final String USER_MOBILE_KEY="mobile";

    public static final Integer TIMEOUT=60;

    public static final Integer WXTIMEOUT=1440 * 60;

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (!org.apache.commons.lang.StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!org.apache.commons.lang.StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }
}
