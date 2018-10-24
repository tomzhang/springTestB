package com.jk51.modules.im.util;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 推送类型，1：个人，2：群组，默认为1
 * 作者: gaojie
 * 创建日期: 2017-06-12
 * 修改记录:
 */
public enum PushType {

    PERSON("个人",1),GROUP("群组",2);

    private String note;
    private int index;

    PushType(String note,int index){
        this.note = note;
        this.index = index;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
