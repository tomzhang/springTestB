package com.jk51.modules.pay.controller;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.pay.service.uni.UniPayApi;
import com.jk51.modules.trades.service.TradesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-02-21
 * 修改记录:
 */
@RestController
@RequestMapping("/pay/uni")
public class UniPayController {
    private static final Logger log = LoggerFactory.getLogger(UniPayController.class);
    @Autowired
    UniPayApi uniPayApi;
    @Autowired
    TradesService tradesService;

    @RequestMapping("/into")
    public String into(String orderId, String money) {
        if(StringUtil.isEmpty(orderId) || StringUtil.isEmpty(money))
            return "orderId或者money不能为空";
        try {
            String html = uniPayApi.getFrontConsumeData(orderId, money);
            log.info(html);
            return html;
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return "error";
    }

    @RequestMapping("/frontrec")
    public String frontRec(@RequestParam Map<String, String> map) {
        log.info("请求参数：\n" + JacksonUtils.mapToJson(map));
        try {
            if(uniPayApi.validate(map, map.get("encoding"))) {
                return "success";
            }
        } catch (Exception e) {
            return "error";
        }
        return "fail";
    }

    @RequestMapping("/backrec")
    public String backRec(@RequestParam Map map) {
        log.info("请求参数：\n" + JacksonUtils.mapToJson(map));
        return "success";
    }

    @RequestMapping("/undo")
    public String undo(String origQryId, String txnAmt) {
        String orderId = DateUtils.formatDate("yyyyMMddHHmmss");
        try {
            if(uniPayApi.consumeUndo(orderId,origQryId,txnAmt) != null)
                return "success";
            return "fail";
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return "error";
        }
    }

    @RequestMapping("/query")
    public String query(String orderId) {
        String txnAmt = DateUtils.formatDate("yyyyMMddHHmmss");
        try {
            if(uniPayApi.query(orderId,txnAmt) != null)
                return "success";
            return "fail";
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return "error";
        }
    }

    @RequestMapping("/refund")
    public String refund(String origQryId, String txnAmt) {
        try {
            if(uniPayApi.refund(origQryId,txnAmt) != null)
                return "success";
            return "fail";
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return "error";
        }
    }

}
