package com.jk51.model.order;

import java.sql.Timestamp;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-12
 * 修改记录:
 */
public class SBMember {

    private Timestamp last_time;
    private Timestamp create_time;
    private Timestamp update_time;
    private Integer site_id;
    private Integer member_id;
    private Integer buyer_id;
    private Integer sex;
    private Integer order_num;
    private Integer order_fee;
    private Integer register_stores;
    private Long register_clerks;
    private Long integrate;
    private Long total_get_integrate;
    private Long total_consume_integrate;
    private Integer mem_source;
    private Integer is_activated;
    private Integer ban_status;
    private String buyer_nick;
    private String mobile;
    private String email;
    private String passwd;
    private String idcard_number;
    private String memo;
    private String name;
    private String last_ipaddr;
    private Integer first_erp;
    private String open_id;

    public SBMember() {
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public Integer getFirst_erp() {
        return first_erp;
    }

    public void setFirst_erp(Integer first_erp) {
        this.first_erp = first_erp;
    }

    public Timestamp getLast_time() {
        return last_time;
    }

    public void setLast_time(Timestamp last_time) {
        this.last_time = last_time;
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

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getMember_id() {
        return member_id;
    }

    public void setMember_id(Integer member_id) {
        this.member_id = member_id;
    }

    public Integer getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(Integer buyer_id) {
        this.buyer_id = buyer_id;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getOrder_num() {
        return order_num;
    }

    public void setOrder_num(Integer order_num) {
        this.order_num = order_num;
    }

    public Integer getOrder_fee() {
        return order_fee;
    }

    public void setOrder_fee(Integer order_fee) {
        this.order_fee = order_fee;
    }

    public Integer getRegister_stores() {
        return register_stores;
    }

    public void setRegister_stores(Integer register_stores) {
        this.register_stores = register_stores;
    }

    public Long getRegister_clerks() {
        return register_clerks;
    }

    public void setRegister_clerks(Long register_clerks) {
        this.register_clerks = register_clerks;
    }

    public Long getIntegrate() {
        return integrate;
    }

    public void setIntegrate(Long integrate) {
        this.integrate = integrate;
    }

    public Long getTotal_get_integrate() {
        return total_get_integrate;
    }

    public void setTotal_get_integrate(Long total_get_integrate) {
        this.total_get_integrate = total_get_integrate;
    }

    public Long getTotal_consume_integrate() {
        return total_consume_integrate;
    }

    public void setTotal_consume_integrate(Long total_consume_integrate) {
        this.total_consume_integrate = total_consume_integrate;
    }

    public Integer getMem_source() {
        return mem_source;
    }

    public void setMem_source(Integer mem_source) {
        this.mem_source = mem_source;
    }

    public Integer getIs_activated() {
        return is_activated;
    }

    public void setIs_activated(Integer is_activated) {
        this.is_activated = is_activated;
    }

    public Integer getBan_status() {
        return ban_status;
    }

    public void setBan_status(Integer ban_status) {
        this.ban_status = ban_status;
    }

    public String getBuyer_nick() {
        return buyer_nick;
    }

    public void setBuyer_nick(String buyer_nick) {
        this.buyer_nick = buyer_nick;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getIdcard_number() {
        return idcard_number;
    }

    public void setIdcard_number(String idcard_number) {
        this.idcard_number = idcard_number;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_ipaddr() {
        return last_ipaddr;
    }

    public void setLast_ipaddr(String last_ipaddr) {
        this.last_ipaddr = last_ipaddr;
    }

    @Override
    public String toString() {
        return "BMember{" +
            "last_time=" + last_time +
            ", create_time=" + create_time +
            ", update_time=" + update_time +
            ", site_id=" + site_id +
            ", member_id=" + member_id +
            ", buyer_id=" + buyer_id +
            ", sex=" + sex +
            ", order_num=" + order_num +
            ", order_fee=" + order_fee +
            ", register_stores=" + register_stores +
            ", register_clerks=" + register_clerks +
            ", integrate=" + integrate +
            ", total_get_integrate=" + total_get_integrate +
            ", total_consume_integrate=" + total_consume_integrate +
            ", mem_source=" + mem_source +
            ", is_activated=" + is_activated +
            ", ban_status=" + ban_status +
            ", buyer_nick='" + buyer_nick + '\'' +
            ", mobile='" + mobile + '\'' +
            ", email='" + email + '\'' +
            ", passwd='" + passwd + '\'' +
            ", idcard_number='" + idcard_number + '\'' +
            ", memo='" + memo + '\'' +
            ", name='" + name + '\'' +
            ", last_ipaddr='" + last_ipaddr + '\'' +
            '}';
    }
}
