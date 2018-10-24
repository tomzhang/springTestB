package com.jk51.modules.smallTicket.service;

import com.jk51.model.TradesInvoice;
import com.jk51.model.order.BMember;
import com.jk51.modules.smallTicket.mapper.SmallTicketMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/6/5.
 */
@Service
public class SmallTicketService {
    private static final Logger logger = LoggerFactory.getLogger(SmallTicketService.class);
    @Autowired
    private SmallTicketMapper smallTicketMapper;
    /**
     * 支付成功回调，生成小票记录
     * @param member
     * @param tradesId
     */
    public void callBack(BMember member, Long tradesId) {
        logger.info("开始生成电子小票记录");
        String mobile = member.getMobile();
        Integer siteId = member.getSiteId();
        String openId = member.getOpenId();
        //会员手机号和openId至少有一个
        if ((StringUtils.isNotBlank(mobile) || StringUtils.isNotBlank(openId)) && null != siteId ) {
            TradesInvoice tradesInvoice = new TradesInvoice();
            tradesInvoice.setMobile(mobile);
            tradesInvoice.setSiteId(siteId);
            tradesInvoice.setStatus(0);
            tradesInvoice.setTradesId(tradesId);
            tradesInvoice.setOpenId(openId);
            smallTicketMapper.insertOfTradesInvoice(tradesInvoice);
        }
    }
    /**
     * 电子小票详情信息
     * @param siteId
     * @param openId
     * @param mobile
     * @param dateTime dateTime为空，说明不是通过日期进行小票的查询
     * @return
     */
    public List<Map<String, Object>> getBradesInvoiceBySiteIdAndOpenIdOrMobile(Integer siteId, String openId, String mobile, String dateTime, String status) {
        //存储小票日期 (前端日期下拉框)
        List<Map<String, Object>> ticketMap = new ArrayList<>();
        List<Map<String, Object>> ticketMap2 = new ArrayList<>();
        //status 为空，则是通过扫二维码进入小票详情页，不为空则为通过个人中心进入小票详情页
        if (StringUtils.isBlank(status)){
            //获取小票信息 （不根据status查，查出若status为0，则进行状态更新，若为1，则执行扫码查询操作）
            List<Map<String, Object>> ticketList = smallTicketMapper.getBradesInvoiceBySiteIdAndOpenIdOrMobile(siteId, openId, mobile);
            //处理：扫码获取了openId，但是输入的是其他的手机号(一般情况下是走不到该判断的)
            if (CollectionUtils.isEmpty(ticketList)) {
                //存储只有openId，传入参数为mobile处理
                if (StringUtils.isNotBlank(mobile)){
                    String openIdStr = smallTicketMapper.getOpenIdOfMemberBySiteIdAndMobile(siteId, mobile);
                    if (StringUtils.isNotBlank(openIdStr)){
                        ticketList = smallTicketMapper.getBradesInvoiceBySiteIdAndOpenIdOrMobile(siteId, openIdStr, null);
                        //再次判断ticketList是否为空，不可去除此次判断，此次判断是对ticketList最终判断
                        if (CollectionUtils.isEmpty(ticketList))
                            return null;
                    }else
                        return null;
                }else
                    return null;
            }
            Object statusObj = ticketList.get(0).get("status");
            //满足此条件为首次扫码 获取小票信息
            if (Objects.nonNull(statusObj) && 0 == Integer.parseInt(statusObj.toString())){
                List<Integer> ticketIdsList = ticketList.stream().filter(Objects::nonNull).map(m -> Integer.parseInt(m.get("id").toString())).collect(Collectors.toList());
                //更改小票状态
                smallTicketMapper.updateBrandesInvoiceBySiteIdAndOpenIdOrMobile(siteId, ticketIdsList);
            }
            //获取小票的各个日期  (必须重新查询获取小票日期，例如：6月15号之前，该会员扫码查询了小票信息，
            // 6月15号之后，该会员再次下单，但是没有扫码，导致隐藏状态的小票也被查询处理，使得后面根据日期查询导致错误)
            List<String> dateList = smallTicketMapper.getDateOfBradesInvoiceBySiteIdAndOpenIdOrMobile(siteId, openId, mobile);
            /*List<String> dateList = getSmallTicketDateTime(ticketList);*/
            if (CollectionUtils.isEmpty(dateList))
                return null;
            Map<String, Object> dateMap = new HashMap<>();
            dateMap.put("dateList", dateList.size() <= 9 ? dateList : dateList.subList(0, 9));
            ticketMap.add(dateMap);
            if (StringUtils.isNotBlank(dateTime))
                ticketMap2 = handleSmallTicket(siteId, openId, mobile, dateTime, ticketMap);
            else{
                String date = dateList.get(0);
                ticketMap2 = handleSmallTicket(siteId, openId, mobile, date, ticketMap);
            }
            /*String date = dateList.get(0);
            ticketMap2 = handleSmallTicket(siteId, openId, mobile, date, ticketMap);*/
        }else {
            //获取小票的各个日期
            List<String> dateList = smallTicketMapper.getDateOfBradesInvoiceBySiteIdAndOpenIdOrMobile(siteId, openId, mobile);
            if (CollectionUtils.isEmpty(dateList))
                return null;
            Map<String, Object> dateMap = new HashMap<>();
            dateMap.put("dateList", dateList.size() <= 9 ? dateList : dateList.subList(0, 9));
            ticketMap.add(dateMap);
            if (StringUtils.isNotBlank(dateTime))
                ticketMap2 = handleSmallTicket(siteId, openId, mobile, dateTime, ticketMap);
            else{
                String date = dateList.get(0);
                ticketMap2 = handleSmallTicket(siteId, openId, mobile, date, ticketMap);
            }
        }
        return ticketMap2;
    }
    //获取小票的各个日期
    private List<String> getSmallTicketDateTime(List<Map<String, Object>> ticketList) {
        return ticketList.stream().filter(m -> {
                    if (Objects.nonNull(m.get("createTime")) && StringUtils.isNotBlank(m.get("createTime").toString()))
                        return true;
                    return false;
                }).map(m -> {
                    String createTime = m.get("createTime").toString();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date date = format.parse(createTime);
                        String format1 = format.format(date);
                        return format1;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList());
    }

    //处理小票详情信息
    private List<Map<String, Object>> handleSmallTicket(Integer siteId, String openId, String mobile, String date, List<Map<String, Object>> ticketMap) {
        //获取最近时间点的小票信息（正常情况下，只会查询出一条小票记录）
        List<Map<String, Object>> tickets = smallTicketMapper.getBradesInvoiceFreshBySiteIdAndDateAndOpenIdOrMobile(siteId, date, openId, mobile);
        if (CollectionUtils.isEmpty(tickets))
            return null;
        for (Map map : tickets) {
            Object tradesIdObj = map.get("tradesId");
            if (Objects.nonNull(tradesIdObj) && StringUtils.isNotBlank(tradesIdObj.toString())) {
                String tradesId = tradesIdObj.toString();
                //根据订单号获取b_trades订单的详情信息
                Map<String, Object> tradesMap = smallTicketMapper.getTradesBySiteIdAndTradesIdOrMobile(siteId, tradesId, mobile);
                if (null == tradesMap)
                    return null;
                //根据订单号获取b_orders订单信息中商品信息 获取药厂名称
                List<Map<String, Object>> goodsList = smallTicketMapper.getOrdersBySiteIdAndTradesId(siteId, tradesId);
                goodsList.stream().forEach(g ->{
                    Object goodsPriceObj = g.get("goodsPrice");
                    Object goodsTotalPriceObj = g.get("goodsTotalPrice");
                    if (Objects.nonNull(goodsPriceObj))
                        g.put("goodsPrice", String.format("%.2f", Integer.parseInt(goodsPriceObj.toString()) / 100f));
                    if (Objects.nonNull(goodsTotalPriceObj))
                        g.put("goodsTotalPrice", String.format("%.2f", Integer.parseInt(goodsTotalPriceObj.toString()) / 100f));
                });
                tradesMap.put("goodsList", goodsList);
                //优惠金额
                Object totalFeeObj = tradesMap.get("totalFee");
                Object realPayObj = tradesMap.get("realPay");
                Object postFeeObj = tradesMap.get("postFee");
                Integer totalFee = totalFeeObj == null ? 0 : Integer.parseInt(totalFeeObj.toString());
                Integer realPay = realPayObj == null ? 0 : Integer.parseInt(realPayObj.toString());
                Integer postFee = postFeeObj == null ? 0 : Integer.parseInt(postFeeObj.toString());
                if (totalFee + postFee - realPay > 0){
                    tradesMap.put("discountAmount", String.format("%.2f", (totalFee + postFee - realPay) / 100f));
                }
                else if (totalFee + postFee - realPay < 0){
                    tradesMap.put("discountAmount", String.format("%.2f", (realPay - totalFee - postFee) / 100f));
                }
                else
                    tradesMap.put("discountAmount", String.format("%.2f", 0.00f));
                tradesMap.put("totalFee", String.format("%.2f", totalFee / 100f));
                tradesMap.put("realPay", String.format("%.2f", realPay / 100f));
                tradesMap.put("postFee", String.format("%.2f", postFee / 100f));
                ticketMap.add(tradesMap);
            }
        }
        return ticketMap;
    }

/*    //根据手机号查询
    public List<Map<String, Object>> getTradesInvoiceCondition(Integer siteId,String mobile) {

        //获取小票信息  (时间*****)
        List<Map<String, Object>> ticketList = smallTicketMapper.getTradesInvoiceCondition(siteId, mobile);
        List<Map<String, Object>> invoiceMap = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ticketList)) {
            for (Map map : ticketList) {
                Object tradesIdObj = map.get("tradesId");
                if (Objects.nonNull(tradesIdObj) && StringUtils.isNotBlank(tradesIdObj.toString())) {
                    String tradesId = tradesIdObj.toString();
                    //根据订单号获取b_trades订单的详情信息
                    Map<String, Object> tradesMap = smallTicketMapper.getTradesBySiteIdAndTradesIdOrMobile(siteId, tradesId, mobile);
                    if (null == tradesMap)
                        return null;
                    //根据订单号获取b_orders订单信息中商品信息
                    List<Map<String, Object>> goodsList1 = smallTicketMapper.getOrdersBySiteIdAndTradesId(siteId, tradesId);
                    tradesMap.put("goodsList1", goodsList1);
                    //优惠金额
                    Object totalFeeObj = tradesMap.get("totalFee");
                    Object realPayObj = tradesMap.get("realPay");
                    Object postFeeObj = tradesMap.get("postFee");
                    Integer totalFee = totalFeeObj == null ? 0 : Integer.parseInt(totalFeeObj.toString());
                    Integer realPay = realPayObj == null ? 0 : Integer.parseInt(realPayObj.toString());
                    Integer postFee = postFeeObj == null ? 0 : Integer.parseInt(postFeeObj.toString());
                    if (totalFee + postFee - realPay > 0)
                        tradesMap.put("discountAmount", new BigDecimal((totalFee + postFee - realPay) / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    else if (totalFee + postFee - realPay < 0)
                        tradesMap.put("discountAmount", new BigDecimal((realPay - totalFee - postFee) / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    else
                        tradesMap.put("discountAmount", 0.00);
                    tradesMap.put("totalFee", totalFee / 100);
                    tradesMap.put("realPay", realPay / 100);
                    tradesMap.put("postFee", postFee / 100);
                    //获取门店地址  通过b_trades表中的trades_store和siteId获取表b_stores中的门店记录
                    Object tradesStoreObj = tradesMap.get("tradesStore");
                    Map<String, Object> storeMap = null;
                    if (Objects.nonNull(tradesStoreObj) && StringUtils.isNotBlank(tradesStoreObj.toString())) {
                        storeMap = smallTicketMapper.getStoresBySiteIdAndTradesStore(siteId, Integer.parseInt(tradesStoreObj.toString()));
                    }
                    tradesMap.put("storeMap", storeMap);
                    //获取门店名称
                    String shopTitle = smallTicketMapper.getYBMerchantBySiteId(siteId);
                    tradesMap.put("shopTitle", shopTitle);
                    invoiceMap.add(tradesMap);
                }
            }
        }
        return invoiceMap;
    }*/


}
