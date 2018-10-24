package com.jk51.model;



import com.jk51.commons.json.JacksonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:个推content类
 * 作者: gaojie
 * 创建日期: 2017-03-07
 * 修改记录:
 */
public class GeTuiNoticeMessage {

    private String anchor;
    private ArrayList<String> extras;
    private String msg;

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public ArrayList<String> getExtras() {
        return extras;
    }

    public void setExtras(ArrayList<String> extras) {
        this.extras = extras;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public  String getContent(){

        Map<String,Object> content = new HashMap<String,Object>();
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("anchor",getAnchor());
        data.put("msg",getMsg());
        data.put("extras",getExtras());
        content.put("type","push");
        content.put("data",data);

        return JacksonUtils.mapToJson(content);
    }
}
