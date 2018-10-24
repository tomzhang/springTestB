package com.jk51.modules.im.util;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-06-12
 * 修改记录:
 */
public enum ExpireType {

    clerkTimeout("店员超时","clerkTimeout"),memberTimeoutEvaluate("会员超时发送评价给会员","memberTimeoutEvaluate"),
    memberTimeOutOverConversation("会员超时断开聊天关系","memberTimeOutOverConversation"),memberCall("一键呼叫","call")
    ,clerkTimeoutRemind("店员超时提醒","clerkTimeoutRemind");


    private String note;
    private String expireType;
    ExpireType(String note,String expireType){
        this.note = note;
        this.expireType = expireType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getExpireType() {
        return expireType;
    }

    public void setExpireType(String expireType) {
        this.expireType = expireType;
    }
}
