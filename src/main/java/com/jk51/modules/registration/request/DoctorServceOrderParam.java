package com.jk51.modules.registration.request;

/**
 * Created by mqq on 2017/4/8.
 */
public class DoctorServceOrderParam {

    private Integer id;
    private Integer siteId;
    private Integer goodsId;
    private Integer doctorId;
    private Integer servceUseDetailid;
    private Integer memberId;

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getServceUseDetailid() {
        return servceUseDetailid;
    }

    public void setServceUseDetailid(Integer servceUseDetailid) {
        this.servceUseDetailid = servceUseDetailid;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
}
