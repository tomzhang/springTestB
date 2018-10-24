package com.jk51.modules.im.netease.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jk51.model.netease.NeteaseAccid;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/1
 * 修改记录:
 */
@JsonIgnoreProperties(ignoreUnknown =true)
public class CreatAccidRes {

    private int code;
    private Info info;
    private String desc;

    public static class Info {

        private String token;
        private String accid;
        private String name;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getAccid() {
            return accid;
        }

        public void setAccid(String accid) {
            this.accid = accid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Info{" +
                "token='" + token + '\'' +
                ", accid='" + accid + '\'' +
                ", name='" + name + '\'' +
                '}';
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    @Override
    public String toString() {
        return "CreatAccidRes{" +
            "code=" + code +
            ", info=" + info +
            ", desc='" + desc + '\'' +
            '}';
    }

    public NeteaseAccid getNeteaseAccid(){
        NeteaseAccid result = new NeteaseAccid();
        result.setToken(getInfo().getToken());
        result.setAccid(getInfo().getAccid());
        return result;
    }
}
