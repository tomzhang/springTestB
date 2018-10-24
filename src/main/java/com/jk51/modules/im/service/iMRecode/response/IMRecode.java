package com.jk51.modules.im.service.iMRecode.response;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:聊天记录查询结果
 * 作者: gaojie
 * 创建日期: 2017-06-23
 * 修改记录:
 */
public class IMRecode {

    private String name;
    private String msg;
    private String time;

    //app或者wechat 表示属于谁发送的
    private String sender;

    private Integer msg_type;

    private Integer siteId;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Integer getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(Integer msg_type) {
        this.msg_type = msg_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
}
