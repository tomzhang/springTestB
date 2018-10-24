package com.jk51.modules.im.service.iMRecode.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018-01-18
 * 修改记录:
 */

public class Evaluatr {

    private Integer evaluate;
    private int imServiceId;

    public Integer getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(Integer evaluate) {
        this.evaluate = evaluate;
    }

    public int getImServiceId() {
        return imServiceId;
    }

    public void setImServiceId(Integer imServiceId) {
        this.imServiceId = imServiceId;
    }
}
