package com.jk51.modules.appInterface.util;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-03-20 12:40
 * 修改记录:
 */
public class Page {

    private Long pageIndex;
    private Long pageSize;
    private Long pageCount;
    private Long rowsIndex;
    private Long count;
    private Long nextPageIndex;
    private Long prePageIndex;

    private Object object;

    public Page() {
    }

    public Long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getPageCount() {
        return pageCount;
    }

    public void setPageCount(Long pageCount) {
        this.pageCount = pageCount;
    }

    public Long getRowsIndex() {
        return rowsIndex;
    }

    public void setRowsIndex(Long rowsIndex) {
        this.rowsIndex = rowsIndex;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getNextPageIndex() {
        return nextPageIndex;
    }

    public void setNextPageIndex(Long nextPageIndex) {
        this.nextPageIndex = nextPageIndex;
    }

    public Long getPrePageIndex() {
        return prePageIndex;
    }

    public void setPrePageIndex(Long prePageIndex) {
        this.prePageIndex = prePageIndex;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
