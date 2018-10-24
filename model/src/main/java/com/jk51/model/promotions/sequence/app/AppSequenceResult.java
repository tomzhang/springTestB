package com.jk51.model.promotions.sequence.app;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jk51.model.promotions.sequence.SequenceResult;

/**
 * Created by javen on 2018/5/9.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppSequenceResult extends SequenceResult{

    //总商品页数
    private Integer pages;
    //总商品数量
    private Long totals;
    //当前页码
    private Integer page;

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Long getTotals() {
        return totals;
    }

    public void setTotals(Long totals) {
        this.totals = totals;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
