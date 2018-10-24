package com.jk51.model.coupon.requestParams;

import java.util.Map;

/**
 * filename :com.jk51.model.coupon.requestParams.
 * author   :zw
 * date     :2017/3/6
 * Update   :
 */
public class AreaRule {
    private Integer post_area;//0包邮区域  1不包邮区域
    private Map<String,String> rule; //规则

    public Integer getPost_area() {
        return post_area;
    }

    public void setPost_area(Integer post_area) {
        this.post_area = post_area;
    }

    public Map<String, String> getRule() {
        return rule;
    }

    public void setRule(Map<String, String> rule) {
        this.rule = rule;
    }
}
