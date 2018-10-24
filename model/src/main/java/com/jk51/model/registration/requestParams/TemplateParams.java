package com.jk51.model.registration.requestParams;

import java.util.List;

/**
 * filename :com.jk51.model.registration.requestParams.
 * author   :zw
 * date     :2017/4/7
 * Update   :
 */
public class TemplateParams {
    private List<TemplateSonParams> templateRule;
    private Integer siteId;
    private Integer storeId;
    private Integer isDel;

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
}
