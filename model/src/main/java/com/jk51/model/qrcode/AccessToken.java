package com.jk51.model.qrcode;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-06-07
 * 修改记录:
 */
public class AccessToken {
    // 获取到的凭证
    private String token;
    // 凭证有效时间，单位：秒
    private int expiresIn;

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public int getExpiresIn() {
        return expiresIn;
    }
    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }
}
