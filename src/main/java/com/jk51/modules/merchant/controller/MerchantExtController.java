package com.jk51.modules.merchant.controller;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.merchant.MerchantExt;
import com.jk51.model.order.Refund;
import com.jk51.model.order.response.RefundQueryReq;
import com.jk51.modules.merchant.service.MerchantExtService;
import com.jk51.modules.store.service.BStoreService;
import com.jk51.modules.store.service.SRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-14
 * 修改记录:
 */
@Controller
public class MerchantExtController {
    @Autowired
    MerchantExtService merchantExtService;
    @Autowired
    private SRefundService sRefundService;

    @RequestMapping("/merchant/orderRemind")
    @ResponseBody
    public ReturnDto saveRemindTipsSetting(Integer merchant_id, @RequestParam(defaultValue = "0") Integer order_pc_alert,
                                           @RequestParam(defaultValue = "0") Integer order_voice_alert, @RequestParam(defaultValue = "0") Integer order_phone_lert,
                                           String order_lert, String order_remind_phones) {
        if (merchant_id == null)
            return ReturnDto.buildFailedReturnDto("merchant_id不能为空");
        MerchantExt merchantExt = merchantExtService.findByMerchantId(merchant_id);
        if (merchantExt == null)
            return ReturnDto.buildFailedReturnDto("找不到商户");
        merchantExt.setOrder_pc_alert(order_pc_alert);
        merchantExt.setOrder_voice_alert(order_voice_alert);
        merchantExt.setOrder_phone_lert(order_phone_lert);
        merchantExt.setOrder_remind_phones(order_remind_phones);
        merchantExt.setOrder_lert(order_lert);
        if (merchantExtService.updateByMerchantId(merchantExt) == 1) {
            return ReturnDto.buildSuccessReturnDto("更新成功");
        }
        return ReturnDto.buildFailedReturnDto("更新失败");
    }

    @RequestMapping("/merchant/getOrderRemind")
    @ResponseBody
    public ReturnDto getRemindTipsSetting(Integer merchant_id) {
        if (merchant_id == null)
            return ReturnDto.buildFailedReturnDto("merchant_id不能为空");
        MerchantExt merchantExt = merchantExtService.findByMerchantId(merchant_id);
        if (merchantExt == null)
            return ReturnDto.buildFailedReturnDto("找不到商户");
        return ReturnDto.buildSuccessReturnDto(merchantExt);
    }

    @RequestMapping("/merchant/getStoreByServiceSupport")
    @ResponseBody
    public Map getStoreByServiceSupport(HttpServletRequest request) {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        Integer siteId = null;
        Integer serviceSupport = null;
        if (!StringUtil.isEmpty(map.get("siteId"))) {
            siteId = Integer.valueOf(map.get("siteId").toString());
        }
        if (!StringUtil.isEmpty(map.get("serviceSupport"))) {
            serviceSupport = Integer.valueOf(map.get("serviceSupport").toString());
        }
        if (siteId == null) {
            return null;
        } else if (serviceSupport == null) {
            return null;
        } else if (serviceSupport == 160) {
            return merchantExtService.selectToself(siteId);
        } else if (serviceSupport == 150) {
            return merchantExtService.selectassign(siteId);
        }
        return null;
    }

    @RequestMapping(value = "/refund/getRefundList2")
    @ResponseBody
    public Map<String, Object> getRefundList(RefundQueryReq req) {
        Map<String, Object> refundMap = new HashMap<>();
        try {
            if (!StringUtil.isEmpty(req.getCreateTimeStart())) {
                req.setCreateTimeStart(req.getCreateTimeStart() + " 00:00:00");
            }
            if (!StringUtil.isEmpty(req.getCreateTimeEnd())) {
                req.setCreateTimeEnd(req.getCreateTimeEnd() + " 23:59:59");
            }

            Page<Refund> page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
            List<Refund> refundList = sRefundService.getRefundList(req);

            refundMap.put("code", "0000");
            refundMap.put("message", "success");
            refundMap.put("result", refundList);
            refundMap.put("pageInfo", page.toPageInfo());
        } catch (Exception e) {
            refundMap.put("code", "9999");
            refundMap.put("message", "failed");
            refundMap.put("result", new ArrayList<>());
            refundMap.put("pageInfo", null);
        }
        return refundMap;
    }

    /**
     *
     */
    @ResponseBody
    @RequestMapping("/merchant/selectsByserviceSupport")
    public Map<String, Object> getServiceSupport(Integer siteId, String serviceSupport) {
        Map<String, Object> responseParams = new HashMap<>();
        if (siteId == null) {
            responseParams.put("storeList", null);
        } else if (serviceSupport == null) {
            responseParams.put("storeList", null);
        } else {
            responseParams.put("storeList", merchantExtService.selectStoresBystoreSupport(siteId, serviceSupport));
        }
        return responseParams;
    }
}
