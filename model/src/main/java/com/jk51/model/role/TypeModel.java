package com.jk51.model.role;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:permissiontype 精简版  下拉框显示
 * 作者: chen
 * 创建日期: 2017-03-20
 * 修改记录:
 */
public class TypeModel {

    private Integer id;
    private String name;
    private Integer platform;

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

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
