package com.jk51.model.statistics;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-06-20
 * 修改记录:网页浏览统计
 */
public class WebPage {

    private Integer siteId;//站点id
    private Integer id;
    private Integer storeId;//门店编号
    private String ip;//访问页面的ip
    private String url;//访问页面地址
    private String webPages;//被访页面名称
    private Integer goodsId;//被访商品编号
    private Integer memberId;//会员编号
    private String openId;//当前人员标识
    private Timestamp createTime;//进入时间
    private Timestamp leftTime;//离开时间
    private Integer tayTime;//距离上一次浏览的的时间
    private String ipName;//区域名称
    private Integer dataStatus;//数据状态

    public Integer getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(Integer dataStatus) {
        this.dataStatus = dataStatus;
    }

    public Integer getTayTime() {
        return tayTime;
    }

    public void setTayTime(Integer tayTime) {
        this.tayTime = tayTime;
    }

    public String getIpName() {
        return ipName;
    }

    public void setIpName(String ipName) {
        this.ipName = ipName;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWebPages() {
        return webPages;
    }

    public void setWebPages(String webPages) {
        this.webPages = webPages;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getLeftTime() {
        return leftTime;
    }

    public void setLeftTime(Timestamp leftTime) {
        this.leftTime = leftTime;
    }

    @Override
    public String toString() {
        return "WebPage{" +
                "siteId=" + siteId +
                ", id=" + id +
                ", storeId=" + storeId +
                ", ip='" + ip + '\'' +
                ", url='" + url + '\'' +
                ", webPages='" + webPages + '\'' +
                ", goodsId=" + goodsId +
                ", memberId=" + memberId +
                ", openId='" + openId + '\'' +
                ", createTime=" + createTime +
                ", leftTime=" + leftTime +
                '}';
    }
}
