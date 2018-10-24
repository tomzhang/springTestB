package com.jk51.modules.task.domain.dto;

import com.jk51.model.task.TAnswer;
import com.jk51.modules.task.domain.AddGroup;
import com.jk51.modules.task.domain.UpdateGroup;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public class QuestionDTO {
    private Integer id;

    /**
     * 试卷编号
     */
    private Integer examId;

    /**
     * 第多少题
     */
    @NotNull(groups = {UpdateGroup.class, AddGroup.class}, message = "题目编号不能为空")
    private Byte num;

    /**
     * 题干
     */
    @NotNull(groups = {UpdateGroup.class, AddGroup.class}, message = "题目内容不能为空")
    private String content;

    /**
     * 讲解
     */
    private String expound;

    @NotEmpty(groups = {UpdateGroup.class, AddGroup.class}, message = "答案选项不能为空")
    private List<TAnswer> answers;

    /**
     * 状态 10 有效 20 无效
     */
    private Byte status;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExamId() {
        return examId;
    }

    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public Byte getNum() {
        return num;
    }

    public void setNum(Byte num) {
        this.num = num;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExpound() {
        return expound;
    }

    public void setExpound(String expound) {
        this.expound = expound;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<TAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<TAnswer> answers) {
        this.answers = answers;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
