package com.jk51.modules.registration.request;

import com.jk51.model.Goods;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;

/**
 * Created by tianwenlong on 2017/4/7.
 */

public class DoctorAndGoodS {
        private Integer id;
        private Integer siteId;
        private Integer goodsId;
        private String doctorImg;
        private String doctorName;
        private String doctorPhone;
        private Integer sex;
        private String doctorPosition;
        private Integer status;
        private String describe;
        private String honour;
        private String introduce;
        private String stores;
        private String remark;
        private String userCateid;//分类，前期固定
        //商品信息
        private String goodsTitle;//品名
        private Integer scheduleTime;;//有效期
        private Integer servceNum;//服务次数
        private Integer marketPrice;//价格
        private String goodRemark;//说明
        private Integer goodsStatus;//商品状态：1.上架，2.下架
        private MultipartFile file;//头像文件

    public String getUserCateid() {
        return userCateid;
    }

    public void setUserCateid(String userCateid) {
        this.userCateid = userCateid;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public Integer getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Integer scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public Integer getServceNum() {
        return servceNum;
    }

    public void setServceNum(Integer servceNum) {
        this.servceNum = servceNum;
    }

    public Integer getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Integer marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getGoodRemark() {
        return goodRemark;
    }

    public void setGoodRemark(String goodRemark) {
        this.goodRemark = goodRemark;
    }

    public Integer getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(Integer goodsStatus) {
        this.goodsStatus = goodsStatus;
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

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getDoctorImg() {
        return doctorImg;
    }

    public void setDoctorImg(String doctorImg) {
        this.doctorImg = doctorImg;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }

    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getDoctorPosition() {
        return doctorPosition;
    }

    public void setDoctorPosition(String doctorPosition) {
        this.doctorPosition = doctorPosition;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getHonour() {
        return honour;
    }

    public void setHonour(String honour) {
        this.honour = honour;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getStores() {
        return stores;
    }

    public void setStores(String stores) {
        this.stores = stores;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
