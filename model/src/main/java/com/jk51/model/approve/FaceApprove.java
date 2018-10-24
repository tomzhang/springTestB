package com.jk51.model.approve;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 人脸识别实体类
 * 作者: chen_pt
 * 创建日期: 2018/1/29
 * 修改记录:
 */
public class FaceApprove {

    private Integer id;
    private Integer siteId;
    private Integer memberId;
    private String imageId;  //图片在系统中的标识
    private String img;
    private String gender;
    private Integer age;
    private String beauty;//颜值
    private String glass;//是否佩戴眼镜
    private String smile;//微笑程度
    private String emotion;//情绪
    private String ethnicity;//人种
    private String mouthstatus;//嘴部状态
    private String skinstatus;//面部特征
    private String leftEye;
    private String rightEye;
    private Date createTime;
    private Date updateTime;

    private Integer type;//1:Face++,2:腾讯AI
    private String msg;//错误描述

    private String status;//success:成功,fail:失败

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSmile() {
        return smile;
    }

    public void setSmile(String smile) {
        this.smile = smile;
    }

    public String getBeauty() {
        return beauty;
    }

    public void setBeauty(String beauty) {
        this.beauty = beauty;
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

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGlass() {
        return glass;
    }

    public void setGlass(String glass) {
        this.glass = glass;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getMouthstatus() {
        return mouthstatus;
    }

    public void setMouthstatus(String mouthstatus) {
        this.mouthstatus = mouthstatus;
    }

    public String getSkinstatus() {
        return skinstatus;
    }

    public void setSkinstatus(String skinstatus) {
        this.skinstatus = skinstatus;
    }

    public String getLeftEye() {
        return leftEye;
    }

    public void setLeftEye(String leftEye) {
        this.leftEye = leftEye;
    }

    public String getRightEye() {
        return rightEye;
    }

    public void setRightEye(String rightEye) {
        this.rightEye = rightEye;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public FaceApprove() {
        this.status = "success";
    }

    public FaceApprove(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
