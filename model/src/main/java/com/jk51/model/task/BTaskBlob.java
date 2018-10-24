package com.jk51.model.task;

import java.io.Serializable;

/**
 * @author 
 */
public class BTaskBlob implements Serializable {
    private Integer id;

    /**
     * 任务id
     */
    private Integer taskId;

    /**
     * 商品id列表用,分隔
     */
    private String goodsId;

    //该商户下商品是否全选
    private Byte isAll;

    //试卷id
    private Integer examinationId;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public Byte getIsAll() {
        return isAll;
    }

    public void setIsAll(Byte isAll) {
        this.isAll = isAll;
    }

    public Integer getExaminationId() {
        return examinationId;
    }

    public void setExaminationId(Integer examinationId) {
        this.examinationId = examinationId;
    }
}
