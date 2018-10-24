package com.jk51.modules.goods.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Administrator on 2017/5/10.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JoinCateDto {
    /**
     * site_id : 100030
     * cate_code : 1004100410004
     */

    @JsonProperty("site_id")
    private int siteId;
    @JsonProperty("cate_code")
    private long cateCode;

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public long getCateCode() {
        return cateCode;
    }

    public void setCateCode(long cateCode) {
        this.cateCode = cateCode;
    }
}
