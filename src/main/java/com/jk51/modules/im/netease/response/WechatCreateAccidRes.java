package com.jk51.modules.im.netease.response;

import com.jk51.model.netease.NeteaseAccid;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/1
 * 修改记录:
 */
public class WechatCreateAccidRes {

    private boolean neteaseActive;
    private NeteaseAccid neteaseAccid;
    private String receiver;

    public boolean isNeteaseActive() {
        return neteaseActive;
    }

    public void setNeteaseActive(boolean neteaseActive) {
        this.neteaseActive = neteaseActive;
    }

    public NeteaseAccid getNeteaseAccid() {
        return neteaseAccid;
    }

    public void setNeteaseAccid(NeteaseAccid neteaseAccid) {
        this.neteaseAccid = neteaseAccid;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
