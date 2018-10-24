package com.jk51.modules.pandian.Response;

import com.github.pagehelper.PageInfo;
import com.jk51.model.Inventories;

import java.io.Serializable;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/5/4
 * 修改记录:
 */
public class HasNotCheckInventoriesRes implements Serializable {

    private static final long serialVersionUID = -7540417995482066539L;
    private Object pageInfo;
    private long waitInventoryNum;
    private long total;

    public Object getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(Object pageInfo) {
        this.pageInfo = pageInfo;
    }

    public long getWaitInventoryNum() {
        return waitInventoryNum;
    }

    public void setWaitInventoryNum(long waitInventoryNum) {
        this.waitInventoryNum = waitInventoryNum;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
