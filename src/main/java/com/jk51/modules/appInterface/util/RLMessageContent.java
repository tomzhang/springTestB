package com.jk51.modules.appInterface.util;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:容联消息格式，用于转成json
 * 作者: gaojie
 * 创建日期: 2017-03-08
 * 修改记录:
 */
public class RLMessageContent {

    //1抢答,0表示正常发送
    private int is_question;

    //一键呼叫时为手机号
    private String msg_content;
    private int msg_id;

    //8是一键呼叫的抢答
    private int type;

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
}
