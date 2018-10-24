package com.jk51.modules.task.domain.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

public class QueryBTaskDTO {
    @Max(100)
    @Min(0)
    private Byte status;

    private Integer id;

    private List<Integer> idList;

    private Integer pageNum;

    private Integer pageSize;

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Integer> getIdList() {
        return idList;
    }

    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
