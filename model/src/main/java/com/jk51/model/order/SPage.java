package com.jk51.model.order;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-03-20 12:40
 * 修改记录:
 */
public class SPage {

    @JsonProperty("pageIndex")
    private Long pageIndex;
    @JsonProperty("pageSize")
    private Long pageSize;
    @JsonProperty("pageCount")
    private Long pageCount;
    @JsonProperty("rowsIndex")
    private Long rowsIndex;
    @JsonProperty("count")
    private Long count;
    @JsonProperty("nextPageIndex")
    private Long nextPageIndex;
    @JsonProperty("prePageIndex")
    private Long prePageIndex;
    @JsonProperty("object")
    private Object object;

    public SPage() {
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
