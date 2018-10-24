package com.jk51.model.task;

import java.io.Serializable;

/**
 * @author 
 */
public class TQuotagroup implements Serializable {
    private Integer id;

    /**
     * 指标分组名称
     */
    private String name;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}