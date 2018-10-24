package com.jk51.modules.im.service.iMRecode.response;

import com.jk51.modules.im.service.wechatUtil.WechatInfo;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 查询会员订单统计返回信息
 * 作者: chen_pt
 * 创建日期: 2017/6/28
 * 修改记录:
 */
public class StatMemberTrades {

    private Integer totalNum;//订单总数
    private String totalFee;//总金额
    private String avgFee;  //客单价
    private String maxTime; //时间最大的订单时间

    private WechatInfo wechatInfo;

    private Map<String,Object> labels;

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getAvgFee() {
        return avgFee;
    }

    public void setAvgFee(String avgFee) {
        this.avgFee = avgFee;
    }

    public WechatInfo getWechatInfo() {
        return wechatInfo;
    }

    public void setWechatInfo(WechatInfo wechatInfo) {
        this.wechatInfo = wechatInfo;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    public Map<String, Object> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, Object> labels) {
        this.labels = labels;
    }
}
