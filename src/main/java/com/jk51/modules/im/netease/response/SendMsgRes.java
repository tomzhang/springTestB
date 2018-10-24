package com.jk51.modules.im.netease.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/1
 * 修改记录:
 */
@JsonIgnoreProperties(ignoreUnknown =true)
public class SendMsgRes {

    private int code;
    private Data Data;
    private String desc;
    private String from;
    private String to;
    public static class Data{
        private String msgid;
        private boolean antispam;

        public String getMsgid() {
            return msgid;
        }

        public void setMsgid(String msgid) {
            this.msgid = msgid;
        }

        public boolean isAntispam() {
            return antispam;
        }

        public void setAntispam(boolean antispam) {
            this.antispam = antispam;
        }

        @Override
        public String toString() {
            return "Data{" +
                "msgid='" + msgid + '\'' +
                ", antispam=" + antispam +
                '}';
        }
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public SendMsgRes.Data getData() {
        return Data;
    }

    public void setData(SendMsgRes.Data data) {
        Data = data;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "SendMsgRes{" +
            "code=" + code +
            ", Data=" + Data +
            ", desc='" + desc + '\'' +
            ", from='" + from + '\'' +
            ", to='" + to + '\'' +
            '}';
    }
}
