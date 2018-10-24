package com.jk51.modules.im.util;

import com.jk51.model.ImRecodeIdAndServiceIds;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 容联发送消息的参数
 * 作者: gaojie
 * 创建日期: 2017-06-12
 * 修改记录:
 */
public class RLMessageParameter {

    @NotNull
    private String appId;
    private String receiver;
    @NotNull
    private String sender;

    //b_im_recode.id
    private Integer msg_id;

    //消息内容
    private String msg_content;

    //消息类型
    @NotNull
    private Integer msg_type;

    //聊天一次服务的id
    private Integer imServiceId;

    @NotNull
    private Integer site_id;

    //推送类型，1：个人，2：群组，默认为1
    private Integer push_type = 1;

    //json封装发送消息格式
    private String msgContent;

    private Integer isRace;
    private Integer isSystemMessage;
    private List<ImRecodeIdAndServiceIds> imRecodeIdAndServiceIds;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public Integer getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(Integer msg_type) {
        this.msg_type = msg_type;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getPush_type() {
        return push_type;
    }

    public void setPush_type(Integer push_type) {
        this.push_type = push_type;
    }

    public Integer getImServiceId() {
        return imServiceId;
    }

    public void setImServiceId(Integer imServiceId) {
        this.imServiceId = imServiceId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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

    public Integer getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(Integer msg_id) {
        this.msg_id = msg_id;
    }

    public List<ImRecodeIdAndServiceIds> getImRecodeIdAndServiceIds() {
        return imRecodeIdAndServiceIds;
    }

    public void setImRecodeIdAndServiceIds(List<ImRecodeIdAndServiceIds> imRecodeIdAndServiceIds) {
        this.imRecodeIdAndServiceIds = imRecodeIdAndServiceIds;
    }

    @Override
    public String toString() {
        return "RLMessageParameter{" +
                "appId='" + appId + '\'' +
                ", receiver='" + receiver + '\'' +
                ", sender='" + sender + '\'' +
                ", msg_id=" + msg_id +
                ", msg_content='" + msg_content + '\'' +
                ", msg_type=" + msg_type +
                ", imServiceId=" + imServiceId +
                ", site_id=" + site_id +
                ", push_type=" + push_type +
                ", msgContent='" + msgContent + '\'' +
                ", isRace=" + isRace +
                ", isSystemMessage=" + isSystemMessage +
                ", imRecodeIdAndServiceIds=" + imRecodeIdAndServiceIds +
                '}';
    }
}
