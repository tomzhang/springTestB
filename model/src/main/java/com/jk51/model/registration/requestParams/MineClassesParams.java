package com.jk51.model.registration.requestParams;

import java.util.Date;
import java.util.List;

/**
 * filename :com.jk51.model.registration.requestParams.
 * author   :zw
 * date     :2017/4/7
 * Update   :
 */
public class MineClassesParams {
    private List<TemplateSonParams> templateRule;
    private Integer siteId;
    private Integer storeId;
    private Integer isDel;
    private String  templateNo;
    private long    useTime; //排班日期
    private Integer goodsId; //商品Id

    public List<TemplateSonParams> getTemplateRule() {
        return templateRule;
    }

    public void setTemplateRule(List<TemplateSonParams> templateRule) {
        this.templateRule = templateRule;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public String getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(String templateNo) {
        this.templateNo = templateNo;
    }

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }
}
