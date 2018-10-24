package com.jk51.modules.im.util;

import javax.validation.constraints.NotNull;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-06-21
 * 修改记录:
 */
public class IMIndexRequestBody {

    @NotNull
    private Integer site_id;

    //时间类型(7:7天，30:30天)
    @NotNull
    private Integer time_gap_type;

    //指标类型
    @NotNull
    private Integer Index_type;
    @NotNull
    private String end_day;

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getTime_gap_type() {
        return time_gap_type;
    }

    public void setTime_gap_type(Integer time_gap_type) {
        this.time_gap_type = time_gap_type;
    }

    public Integer getIndex_type() {
        return Index_type;
    }

    public void setIndex_type(Integer index_type) {
        Index_type = index_type;
    }

    public String getEnd_day() {
        return end_day;
    }

    public void setEnd_day(String end_day) {
        this.end_day = end_day;
    }
}
