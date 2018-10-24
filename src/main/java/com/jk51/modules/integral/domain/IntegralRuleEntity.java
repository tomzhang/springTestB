package com.jk51.modules.integral.domain;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

/**
 * 积分规则实体
 *
 * @auhter zy
 * @create 2017-06-01 10:24
 */
//忽略未知字段
@JsonIgnoreProperties(ignoreUnknown = true)
// 驼峰命名
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class IntegralRuleEntity {

    private int id;


    private Integer siteId; //商家id

    private String name;    //规则名称

    private String desc;    //规则描述

    private Integer type;   //规则类型(10 注册送 20 签到送 30 完善信息送 40 下单满额送 50 咨询评价送 60 订单评价送)

    private String rule;    //规则 json串

    private int limit;      //每日赠送上限

    private Integer status; //规则状态(0  1 开启)

//    private T ruleEntity;   //rule字段对应的实体

    @DateTimeFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    private Date createTime;//创建时间

    @DateTimeFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    private Date updateTime;//更新时间

    public int getId() {
        return id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getType() {
        return type;
    }

    public String getRule() {
        return rule;
    }

    public int getLimit() {
        return limit;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setId(int id) {

        this.id = id;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setType(Integer type) {
        this.type = type;
        /*if(type != null && type == 10) {    //注册送
            IntegralRule.RegisterRule registerRule1 = JSON.parseObject(this.getRule(), IntegralRule.RegisterRule.class);
            this.ruleEntity = registerRule1;
        }else if(type != null && type == 20) {  //签到送
            IntegralRule.SignRule registerRule2 = JSON.parseObject(this.getRule(), IntegralRule.SignRule.class);
            this.ruleEntity = registerRule2;
        }else if(type != null && type == 30) {
            //
        }else if(type != null && type == 40) {  //下单满额送
            Map map = JSON.parseObject(this.getRule(), Map.class);
            String type1 = (String) map.get("type");
            //满额送固定积分
            if(type1 != null && type1.equals("1")) {
                IntegralRule.ShoppingRule registerRule3 = JSON.parseObject(this.getRule(), IntegralRule.ShoppingRule.class);
                this.ruleEntity = registerRule3;
            }
            //满额送累计积分
            if(type1 != null && type1.equals("2")) {
                IntegralRule.ShoppingRuleAdd registerRule4 = JSON.parseObject(this.getRule(), IntegralRule.ShoppingRuleAdd.class);
                this.ruleEntity = registerRule4;
            }
        }else if(type != null && type == 50) {
            //
        } else {
            //
        }*/

    }

    //rule转换成对应的对象
    public void setRule(String rule) {
        this.rule = rule;
        /*Class<T> t = (Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        T t1 = JSON.parseObject(rule, t);
        this.ruleEntity = t1;*/
    }
    //获取这个对象
    /*public Object getRuleEntity() {
        return ruleEntity;
    }*/

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "IntegralRuleEntity{" +
                "id=" + id +
                ", siteId=" + siteId +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", type=" + type +
                ", rule='" + rule + '\'' +
                ", limit=" + limit +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
