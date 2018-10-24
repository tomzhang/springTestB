package com.jk51.modules.goods.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.jk51.commons.string.StringUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/27.
 */
@JsonIgnoreProperties(ignoreUnknown =true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BatchJoinIntegralGoods {
    @NotNull(message = "商品编号不能为空")
    protected String goodsIds;
    @NotNull(message = "门店编号不能为空")
    protected Integer siteId;
    @NotNull(message = "汇率不能为空")
    protected Integer parities;//汇率
    protected Integer limitEach;//兑换上限

    //添加有效期 开始时间 和 截止时间 ----start
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date endTime;
    //添加有效期 开始时间 和 截止时间
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date startTime;
    //添加有效期 开始时间 和 截止时间 ----end

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date integralGoodsStartTime;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date integralGoodsEndTime;

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    protected Integer joinType;//参与类型:单个：20;批量10

    public Integer getLimitEach() {return limitEach;}

    public void setLimitEach(Integer limitEach) {this.limitEach = limitEach;}

    public Integer getParities() {
        return parities;
    }

    public void setParities(Integer parities) {
        this.parities = parities;
    }

    public Date getIntegralGoodsStartTime() {
        return integralGoodsStartTime;
    }

    public void setIntegralGoodsStartTime(Date integralGoodsStartTime) {
        this.integralGoodsStartTime = integralGoodsStartTime;
    }

    public Date getIntegralGoodsEndTime() {
        return integralGoodsEndTime;
    }

    public void setIntegralGoodsEndTime(Date integralGoodsEndTime) {
        this.integralGoodsEndTime = integralGoodsEndTime;
    }

    public void setGoodsIds(String goodsIds) {
        this.goodsIds = goodsIds;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
    public int[] getGoodsIdList() {
        String[] t = goodsIds.split(",");
        int[] ids = new int[t.length];
        for (int i = 0; i < t.length; i++) {
            ids[i] = StringUtil.convertToInt(t[i]);
        }

        return ids;
    }


    public String getGoodsIds() {
        return goodsIds;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public Integer getJoinType() {
        return joinType;
    }

    public void setJoinType(Integer joinType) {
        this.joinType = joinType;
    }
}
