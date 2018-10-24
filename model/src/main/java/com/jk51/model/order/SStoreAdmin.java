package com.jk51.model.order;

import java.io.Serializable;
import java.util.Date;

public class SStoreAdmin implements Serializable {
    private Integer id;

    private Integer site_id;

    private Integer store_id;

    private String user_name;

    private String user_pwd;

    private Integer user_type;

    private Boolean is_del;

    private Integer status;

    private Date last_login_time;

    private Integer login_count;

    private Date create_time;

    private Date update_time;

    private Double countIndex;

    private Integer chat;

    public Integer getChat() {
        return chat;
    }

    public void setChat(Integer chat) {
        this.chat = chat;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name == null ? null : user_name.trim();
    }

    public String getUser_pwd() {
        return user_pwd;
    }

    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd == null ? null : user_pwd.trim();
    }

    public Integer getUser_type() {
        return user_type;
    }

    public void setUser_type(Integer user_type) {
        this.user_type = user_type;
    }

    public Boolean getIs_del() {
        return is_del;
    }

    public void setIs_del(Boolean is_del) {
        this.is_del = is_del;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(Date last_login_time) {
        this.last_login_time = last_login_time;
    }

    public Integer getLogin_count() {
        return login_count;
    }

    public void setLogin_count(Integer login_count) {
        this.login_count = login_count;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Double getCountIndex() {
        return countIndex;
    }

    public void setCountIndex(Double countIndex) {
        this.countIndex = countIndex;
    }

    @Override
    public String toString() {
        return "StoreAdmin{" +
            "id=" + id +
            ", site_id=" + site_id +
            ", store_id=" + store_id +
            ", user_name='" + user_name + '\'' +
            ", user_pwd='" + user_pwd + '\'' +
            ", user_type=" + user_type +
            ", is_del=" + is_del +
            ", status=" + status +
            ", last_login_time=" + last_login_time +
            ", login_count=" + login_count +
            ", create_time=" + create_time +
            ", update_time=" + update_time +
            ", countIndex=" + countIndex +
            '}';
    }
}
