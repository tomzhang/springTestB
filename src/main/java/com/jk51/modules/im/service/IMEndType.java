package com.jk51.modules.im.service;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:聊天结束类型
 * 作者: gaojie
 * 创建日期: 2017-06-12
 * 修改记录:
 */
public enum IMEndType {


    evaluate("会员评价结束",1),clerkTimeout("店员超时",2),memberTimeout("会员超时",3);

    private String note;
    private Integer index;
    IMEndType(String note,Integer index){
        this.note = note;
        this.index = index;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
