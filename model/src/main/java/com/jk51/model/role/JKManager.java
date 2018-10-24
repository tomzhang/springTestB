package com.jk51.model.role;

import java.sql.Timestamp;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     : 51jk
 * 作者     : zhangduoduo
 * 创建日期 : 2017/2/27-02-27
 * 修改记录 :
 */
public class JKManager {
    private Integer site_id;
    private Integer id;
    private Integer store_id;
    private String username;
    private String user_pwd;
    private Integer user_type;
    private Integer is_del;
    private Integer status;
    private Timestamp last_login_time;
    private Integer login_count;
    private Timestamp create_time;
    private Timestamp update_time;
    private Double countIndex;

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_pwd() {
        return user_pwd;
    }

    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }

    public Integer getUser_type() {
        return user_type;
    }

    public void setUser_type(Integer user_type) {
        this.user_type = user_type;
    }

    public Integer getIs_del() {
        return is_del;
    }

    public void setIs_del(Integer is_del) {
        this.is_del = is_del;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Timestamp getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(Timestamp last_login_time) {
        this.last_login_time = last_login_time;
    }

    public Integer getLogin_count() {
        return login_count;
    }

    public void setLogin_count(Integer login_count) {
        this.login_count = login_count;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public Double getCountIndex() {
        return countIndex;
    }

    public void setCountIndex(Double countIndex) {
        this.countIndex = countIndex;
    }
}
