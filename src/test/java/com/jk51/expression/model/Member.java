package com.jk51.expression.model;

/**
 * 文件名:com.jk51.expression.model.Member
 * 描述:
 * 作者: wangzhengfei
 * 创建日期: 2017-01-20
 * 修改记录:
 */
public class Member {

    private Long id;

    private String name;

    private Long mobile;

    public Member(){

    }

    public Member(Long id,String name,Long mobile){
        this.id = id;
        this.name = name;
        this.mobile = mobile;
    }

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

    public Long getMobile() {
        return mobile;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Member{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", mobile=").append(mobile);
        sb.append('}');
        return sb.toString();
    }
}
