package com.jk51.model.distribute;

import java.sql.Timestamp;

/**
 * Created by admin on 2017/2/9.
 */
public class RecruitRecord {

    /**
     *招募书修改记录ID
     */
    private long id;

    /**
     * 商家ID
     */
    private long owner;

    /**
     * 是否需要保证金：0-不需要 1-需要
     */
    private int is_diposit;

    /**
     * 保证金
     */
    private long deposit;

    /**
     * 推荐规则
     */
    private String rule;

    /**
     * 招募总人数
     */
    private long total_recruit;

    /**
     * 审核方式：0-手动审核 1-自动审核
     */
    private int audit_mode;

    /**
     * 招募模板
     */
    private String template;

    /**
     * 更新时间
     */
    private Timestamp create_time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public long getDeposit() {
        return deposit;
    }

    public void setDeposit(long deposit) {
        this.deposit = deposit;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public long getTotal_recruit() {
        return total_recruit;
    }

    public void setTotal_recruit(long total_recruit) {
        this.total_recruit = total_recruit;
    }

    public int getAudit_mode() {
        return audit_mode;
    }

    public void setAudit_mode(int audit_mode) {
        this.audit_mode = audit_mode;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int getIs_diposit() {
        return is_diposit;
    }

    public void setIs_diposit(int is_diposit) {
        this.is_diposit = is_diposit;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

}
