package com.jk51.modules.im.util;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 消息类型
 * 作者: gaojie
 * 创建日期: 2017-06-12
 * 修改记录:
 */
public enum MsgType {

    TEXT("文本消息",1),EVALUATR("评价消息",23),SYSTEM_REMIND("系统消息",3),SEND_GOODS("推荐商品",12),SEND_TRADES("推荐订单",14),IMAGE("文本消息",4);

    private String note;
    private int index;

    MsgType(String note,int index){
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
