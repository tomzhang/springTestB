package com.jk51.modules.im.netease.response;

import java.util.ArrayList;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/3
 * 修改记录:
 */
public class BatchSendMsgRes {

    private List<Info> success;
    private List<Info> fail;


    public List<Info> getSuccess() {
        return success;
    }

    public List<Info> getFail() {
        return fail;
    }

    public BatchSendMsgRes(){
        success = new ArrayList<>();
        fail = new ArrayList<>();
    }

    public BatchSendMsgRes addSuccess(String from,String to){
        Info info = new Info();
        info.setFrom(from);
        info.setTo(to);
        success.add(info);
        return this;
    }

    public BatchSendMsgRes addFail(String from,String to,String desc){

        Info info = new Info();
        info.setFrom(from);
        info.setTo(to);
        info.setDesc(desc);

        fail.add(info);
        return this;
    }

    public static class Info{
        private String to;
        private String desc;
        private String from;

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

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        @Override
        public String toString() {
            return "Info{" +
                "to='" + to + '\'' +
                ", desc='" + desc + '\'' +
                ", from='" + from + '\'' +
                '}';
        }
    }

    @Override
    public String toString() {
        return "BatchSendMsgRes{" +
            "success=" + success +
            ", failt=" + fail +
            '}';
    }
}
