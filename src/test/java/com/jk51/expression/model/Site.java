package com.jk51.expression.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件名:com.jk51.expression.model.
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-01-20
 * 修改记录:
 */
public class Site{

    private Long id;

    private String name;

    public Site(){

    }

    public Site(Long id,String name){
        this.id = id;
        this.name = name;
    }

    private List<Member> members = new ArrayList<Member>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Site{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", members=").append(members);
        sb.append('}');
        return sb.toString();
    }
}