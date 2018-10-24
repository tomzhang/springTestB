package com.jk51.modules.im.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:容联消息格式，用于转成json
 * 作者: gaojie
 * 创建日期: 2017-03-08
 * 修改记录:
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RLMessageContent {

    //1抢答,0表示正常发送
    private int is_question;

    //一键呼叫时为手机号
    private String msg_content;
    private int msg_id;

    //消息类型
    private int type;
    //聊天服务表ID
    @JsonProperty("imServiceId")
    private Integer imServiceId;
    //聊天记录时间
    private String create_time;

    private String clerkInvitationCode;

    public String getClerkInvitationCode() {
        return clerkInvitationCode;
    }

    public void setClerkInvitationCode(String clerkInvitationCode) {
        this.clerkInvitationCode = clerkInvitationCode;
    }

    public int getIs_question() {
        return is_question;
    }

    public void setIs_question(int is_question) {
        this.is_question = is_question;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public int getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getImServiceId() {
        return imServiceId;
    }

    public void setImServiceId(Integer imServiceId) {
        this.imServiceId = imServiceId;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
