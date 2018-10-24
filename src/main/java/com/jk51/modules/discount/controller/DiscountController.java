package com.jk51.modules.discount.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.discount.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/3.
 */
@Controller
@RequestMapping("/discount")
public class DiscountController {
    @Autowired
    private DiscountService discountService;

    /**
     * 阿里微信支付模块
     * @param request
     * @return
     */
    @RequestMapping(value="/getPayAliWx")
    @ResponseBody
    public Map<String,Object> getPayAliWx(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.getPayAliWx(params);
    }
    /**
     * 阿里微信支付模块:添加订单来源
     * @param request
     * @return
     */
    @RequestMapping(value="/insertTradesLine")
    @ResponseBody
    public Map<String,Object> insertTradesLine(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.insertTradesLine(params);
    }

    /**
     * 阿里微信支付模块:修改订单来源
     * @param request
     * @return
     */
    @RequestMapping(value="/updateTradesLine")
    @ResponseBody
    public Map<String,Object> updateTradesLine(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return null;
                //discountService.updateTradesLine(params);
    }

    /**
     * 查询打折记录
     * @param request
     * @return
     */
    @RequestMapping(value="/getDiscountRuleLine")
    @ResponseBody
    public Map<String,Object> getDiscountRuleLine(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.getDiscountRuleLine(params);
    }

    /**
     * 查询打折记录
     * @param request
     * @return
     */
    @RequestMapping(value="/getDiscountRuleLineBySiteId")
    @ResponseBody
    public Map<String,Object> getDiscountRuleLineBySiteId(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.getDiscountRuleLineBySiteId(params);
    }

    /**
     * 查询打折记录
     * @param request
     * @return
     */
    @RequestMapping(value="/updateDiscountRuleLine")
    @ResponseBody
    public Map<String,Object> updateDiscountRuleLine(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.updateDiscountRuleLine(params);
    }
    /**
     * 查询打折记录
     * @param request
     * @return
     */
    @RequestMapping(value="/getTradesLine")
    @ResponseBody
    public Map<String,Object> getTradesLine(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.getTradesLine(params);
    }

    /**
     * 获取红包记录
     * @param request
     * @return
     */
    @RequestMapping(value="/insertRedpacketLine")
    @ResponseBody
    public Map<String,Object> insertRedpacketLine(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.insertRedpacketLine(params);
    }

    /**
     * 获取指定会员所有未使用红包金额
     * @param request
     * @return
     */
    @RequestMapping(value="/getRedpacketTotalMoneyByUnused")
    @ResponseBody
    public Map<String,Object> getRedpacketTotalMoneyByUnused(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.getRedpacketTotalMoneyByUnused(params);
    }

    /**
     * 修改指定会员  所有未使用红包金额  为  已使用红包
     * @param request
     * @return
     */
    @RequestMapping(value="/updateRedpacketType")
    @ResponseBody
    public Map<String,Object> updateRedpacketType(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.updateRedpacketType(params);
    }

    /**
     * 获取支付优惠订单列表
     * @param request
     * @return
     */
    @RequestMapping(value="/getDiscountOrderList")
    @ResponseBody
    public Map<String,Object> getDiscountOrderList(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.getDiscountOrderList(params);
    }

    /**
     * 线下支付设置查询
     * @param request
     * @return
     */
    @RequestMapping(value="/getOfflineBySiteId")
    @ResponseBody
    public Map<String,Object> getOfflineBySiteId(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.getOfflineBySiteId(params);
    }

    /**
     * 线下支付设置--编辑
     * @param request
     * @return
     */
    @RequestMapping(value="/editOfflineBySiteId")
    @ResponseBody
    public Map<String,Object> editOfflineBySiteId(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.editOfflineBySiteId(params);
    }

    /**
     * 添加小票索要记录
     * @param request
     * @return
     */
    @RequestMapping(value="/insertTicketBlag")
    @ResponseBody
    public Map<String,Object> insertTicketBlag(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.insertTicketBlag(params);
    }

    /**
     * 查询小票索要记录
     * @param request
     * @return
     */
    @RequestMapping(value="/getTicketBlag")
    @ResponseBody
    public Map<String,Object> getTicketBlag(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.getTicketBlag(params);
    }

    /**
     * 查询指定会员获取了多少红包及红包总金额
     * @param request
     * @return
     */
    @RequestMapping(value="/getRedPacketAndAllTotalMoney")
    @ResponseBody
    public Map<String,Object> getRedPacketAndAllTotalMoney(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.getRedPacketAndAllTotalMoney(params);
    }

    /**
     * 余额提现
     * @param request
     * @return
     */
    @RequestMapping(value="/getTotalMoneyByBalance")
    @ResponseBody
    public Map<String,Object> getTotalMoneyByBalance(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.getTotalMoneyByBalance(params);
    }

    /**
     * 是否可抽红包
     * @param request
     * @return
     */
    @RequestMapping(value="/boolGetRedBao")
    @ResponseBody
    public Map<String,Object> boolGetRedBao(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.boolGetRedBao(params);
    }

    /**
     * 是否可抽红包
     * @param request
     * @return
     */
    @RequestMapping(value="/updateStatusById")
    @ResponseBody
    public Map<String,Object> updateStatusById(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.updateStatusById(params);
    }

    /**
     * 提现记录
     * @param request
     * @return
     */
    @RequestMapping(value="/getDiscountExtractList")
    @ResponseBody
    public Map<String,Object> getDiscountExtractList(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.getDiscountExtractList(params);
    }

    /**
     * 微信：商户反馈
     * @param request
     * @return
     */
    @RequestMapping(value="/getMerchantBack")
    @ResponseBody
    public Map<String,Object> getMerchantBack(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.getMerchantBack(params);
    }

    /**
     * 门店后台：商户号
     * @param request
     * @return
     */
    @RequestMapping(value="/getDeviceNumMap")
    @ResponseBody
    public Map<String,Object> getDeviceNumMap(HttpServletRequest request) {
        Map<String,Object> params = ParameterUtil.getParameterMap(request);
        return discountService.getDeviceNumMap(params);
    }



}
