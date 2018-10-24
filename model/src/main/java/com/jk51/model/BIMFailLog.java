package com.jk51.model;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-09-21
 * 修改记录:
 */
public class BIMFailLog {

    private Integer id;
    private String sender;
    private String receiver;
    //b_im_recode.id
    private Integer msg_id;
    //消息类型
    private Integer msg_type;
    //聊天一次服务的id
    private Integer imServiceId;
    private Integer site_id;
    //json封装发送消息格式
    private String msgContent;
    private Integer isRace;
    private Integer isSystemMessage;
    private String errorMessage;
    private Date createTime;
    private Date updateTime;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Integer getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(Integer msg_id) {
        this.msg_id = msg_id;
    }

    public Integer getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(Integer msg_type) {
        this.msg_type = msg_type;
    }

    public Integer getImServiceId() {
        return imServiceId;
    }

    public void setImServiceId(Integer imServiceId) {
        this.imServiceId = imServiceId;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public Integer getIsRace() {
        return isRace;
    }

    public void setIsRace(Integer isRace) {
        this.isRace = isRace;
    }

    public Integer getIsSystemMessage() {
        return isSystemMessage;
    }

    public void setIsSystemMessage(Integer isSystemMessage) {
        this.isSystemMessage = isSystemMessage;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
