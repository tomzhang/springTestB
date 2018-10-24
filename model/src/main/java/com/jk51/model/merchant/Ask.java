package com.jk51.model.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/19.
 */
public class Ask {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("siteId")
    private Integer siteId;
    @JsonProperty("questions")
    private String questions;
    @JsonProperty("answers")
    private String answers;
    @JsonProperty("createtime")
    private Date createtime;
    @JsonProperty("updatetime")
    private Date updatetime;
    @JsonProperty("isDel")
    private Integer isDel;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }
}
