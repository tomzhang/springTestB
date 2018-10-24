package com.jk51.model.coupon.requestParams;

import java.util.List;
import java.util.Map;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/10/26                                <br/>
 * 修改记录:                                         <br/>
 */
public class TemplateInfoParams {
    private List<Map<String, Object>> params;
    private Integer siteId;

    public List<Map<String, Object>> getParams() {
        return params;
    }

    public void setParams(List<Map<String, Object>> params) {
        this.params = params;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
}
