package com.jk51.modules.distribution.result;

import java.io.Serializable;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-14 10:12
 * 修改记录:
 */
public class Page implements Serializable {

    //当前页
    private Long pageIndex;

    //下一页
    private Long nextPageIndex;

    //上一页
    private Long prePageIndex;

    //总页数
    private Long pageCount;

    //页个数
    private Long pageSize;

    //当前页的行号
    private Long rowsIndex;

    //总数量
    private Long count;

    //页面数据
    private Object data;

    public Page() {
    }

    public Long getPageIndex() {
        return this.pageIndex;
    }

    public void setPageIndex(Long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Long getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(Long pageCount) {
        this.pageCount = pageCount;
    }

    public Long getRowsIndex() {
        return this.rowsIndex;
    }

    public void setRowsIndex(Long rowsIndex) {
        this.rowsIndex = rowsIndex;
    }

    public Long getCount() {
        return this.count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getNextPageIndex() {
        return this.nextPageIndex;
    }

    public void setNextPageIndex(Long nextPageIndex) {
        this.nextPageIndex = nextPageIndex;
    }

    public Long getPrePageIndex() {
        return this.prePageIndex;
    }

    public void setPrePageIndex(Long prePageIndex) {
        this.prePageIndex = prePageIndex;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

