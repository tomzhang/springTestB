package com.jk51.modules.store.web;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.order.Refund;
import com.jk51.model.order.response.RefundQueryReq;
import com.jk51.modules.store.service.BTradesService;
import com.jk51.modules.store.service.SRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: zhangkuncheng
 * 创建日期: 2017-05-16
 * 修改记录:
 */
@Controller
@RequestMapping("/store")
public class BTradesController {

    @Autowired
    private BTradesService bTradesService;

    /**
     * 查询门店后台各订单量
     * @param
     * @return
     */

    @RequestMapping(value = "/bt/orderCount")
    @ResponseBody
    public Map<String, Object> orderCount(HttpServletRequest request) {
        Map<String,Object> result = new HashMap<String,Object>();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        String siteId = null;
        try {
            siteId = param.get("siteId").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Integer storeId = Integer.parseInt(String.valueOf(param.get("storeId")));
        return bTradesService.orderCount(siteId,storeId);
    }



}
