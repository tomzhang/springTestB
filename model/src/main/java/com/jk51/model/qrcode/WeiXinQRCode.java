package com.jk51.model.qrcode;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-06-07
 * 修改记录:
 */
public class WeiXinQRCode {
    // 获取的二维码
    private String ticket;
    // 二维码的有效时间,单位为秒,最大不超过2592000（即30天）
    private int expire_seconds;
    // 二维码图片解析后的地址
    private String url;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getExpire_seconds() {
        return expire_seconds;
    }

    public void setExpire_seconds(int expire_seconds) {
        this.expire_seconds = expire_seconds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
