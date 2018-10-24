package com.jk51.modules.appInterface.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.ChOrderRemind;
import com.jk51.model.Goods;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.order.Stockup;
import com.jk51.model.order.Store;
import com.jk51.modules.appInterface.service.OrdersService;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yanglile
 * 创建日期: 2017-03-03
 * 修改记录:
 */
@RequestMapping("/ordersAPP")
@Controller
public class OrdersController {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;

    @RequestMapping("/selectMember")
    @ResponseBody
    public Map<String, Object> selectMember(HttpServletRequest request){
        String siteId = request.getParameter("siteId");
        String mobile = request.getParameter("mobile");

        Map<String, Object> result = new HashMap<>();
        if(StringUtil.isEmpty(siteId) || StringUtil.isEmpty(mobile)){
            result.put("status", "ERROR");
            result.put("message", "缺少必填参数");
            return result;
        }

        try {
            Map<String, Object> member = ordersService.selectMemberMapByPhoneNum(siteId, mobile);
            result.put("status", "OK");
            result.put("member", member);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "ERROR");
            result.put("message", "服务异常");
        }
        return result;
    }

    /**
     * 获取门店直购订单
     * @param request
     * @return
     */
    @RequestMapping("/other")
    @ResponseBody
    public Map<String, Object> getOtherOrderList(HttpServletRequest request){
        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String page = request.getParameter("pageNum");
        String limit = request.getParameter("pageSize");

        Integer pageNum = null;
        Integer pageSize = null;
        if(StringUtil.isEmpty(page)){
            pageNum = 1;
        }else{
            pageNum = Integer.parseInt(page);
        }
        if(StringUtil.isEmpty(limit)){
            pageSize = 10;
        }else{
            pageSize = Integer.parseInt(limit);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Page<Map> pageBean = PageHelper.startPage(pageNum,pageSize);
            List<Map<String, Object>> tradeList = ordersService.getOtherOrderList(siteId, storeId, pageNum, pageSize);

            PageInfo pageInfo = new PageInfo(tradeList);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("items",tradeList!=null?tradeList:new ArrayList());
            responseMap.put("before",pageInfo.getPrePage());
            responseMap.put("current",pageInfo.getPageNum());
            responseMap.put("next",pageInfo.getNextPage());
            responseMap.put("totalItems",pageInfo.getTotal());
            responseMap.put("totalPages",pageInfo.getPages());

            result.put("status","OK");
            result.put("results",responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status","ERROR");
            result.put("message", "获取门店直购订单服务异常");
        }
        return result;
    }

    /**
     * 我的送货上门订单列表
     * @param request
     * @return
     */
    @RequestMapping("/orderList")
    @ResponseBody
    public Map<String, Object> getOrderList(HttpServletRequest request){
        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String page = request.getParameter("pageNum");
        String limit = request.getParameter("pageSize");
        String storeShippingClerkId = request.getParameter("storeShippingClerkId");

        Integer pageNum = null;
        Integer pageSize = null;
        if(StringUtil.isEmpty(page)){
            pageNum = 1;
        }else{
            pageNum = Integer.parseInt(page);
        }
        if(StringUtil.isEmpty(limit)){
            pageSize = 10;
        }else{
            pageSize = Integer.parseInt(limit);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Page<Map> pageBean = PageHelper.startPage(pageNum,pageSize);
            List<Map<String, Object>> tradeList = ordersService.getOrderList(siteId, storeId, pageNum, pageSize, storeShippingClerkId);

            PageInfo pageInfo = new PageInfo(tradeList);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("items", pageInfo.getList());
            responseMap.put("before",pageInfo.getPrePage());
            responseMap.put("current",pageInfo.getPageNum());
            responseMap.put("next",pageInfo.getNextPage());
            responseMap.put("totalItems",pageInfo.getTotal());
            responseMap.put("totalPages",pageInfo.getPages());

            result.put("status","OK");
            result.put("results",responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status","ERROR");
            result.put("message","获取送货上门订单失败");
        }
        return result;
    }

    /**
     * 订单详情
     * @param request
     * @return
     */
    @RequestMapping("/orderDetail")
    @ResponseBody
    public Map<String, Object> getTradesInformationMap(HttpServletRequest request){
        String siteId = request.getParameter("siteId");
        String tradesId = request.getParameter("tradesId");

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Map<String, Object> tradesInformationMap = new HashMap<String, Object>();
            Map<String, Object> trades_information = ordersService.getTradesInformation(siteId, tradesId, null);//获取交易 交易扩展表

            if(trades_information!=null && trades_information.size()!=0){
                //获取店员邀请码
                String clerkInvitationCode = "";
                if(trades_information.get("storeUserId")!=null){
                    List<StoreAdminExt> storeAdminExt = storeAdminExtMapper.selectBySiteIdAndStoreAdminId(Integer.parseInt(siteId), Integer.parseInt(String.valueOf(trades_information.get("storeUserId"))));
                    if(storeAdminExt!=null && storeAdminExt.size()!=0){
                        String clerkInvitationCodeStr = storeAdminExt.get(0).getClerk_invitation_code();
                        if(!StringUtil.isEmpty(clerkInvitationCodeStr)){
                            int index = clerkInvitationCodeStr.indexOf("_");
                            if(index!=-1){
                                clerkInvitationCode = clerkInvitationCodeStr.substring(index+1, clerkInvitationCodeStr.length());
                            }
                        }
                    }
                }
                trades_information.put("storeSalerCode", clerkInvitationCode);

                //积分抵扣的金额 ：分
                if(!StringUtil.isEmpty(trades_information.get("integralPrice")) && !"null".equals(String.valueOf(trades_information.get("integralPrice")))){
                    trades_information.put("integralPrice", Double.parseDouble(String.valueOf(trades_information.get("integralPrice"))));
                }

                //selfTakenStoreName 用户自选  自提门店 对应 b_stores.id
                if(!StringUtil.isEmpty(trades_information.get("selfTakenStoreName")) && !"null".equals(String.valueOf(trades_information.get("selfTakenStoreName")))){
                    Store bStores = ordersService.getBStoresById(siteId, String.valueOf(trades_information.get("selfTakenStoreName")));
                    if(bStores != null){
                        trades_information.put("selfTakenStoreName", bStores.getName());
                    }
                }
                //assignedStoresName 系统指定  指派送货的门店
                if(!StringUtil.isEmpty(trades_information.get("assignedStoresName")) && !"null".equals(String.valueOf(trades_information.get("assignedStoresName")))){
                    Store bStores = ordersService.getBStoresById(siteId, String.valueOf(trades_information.get("assignedStoresName")));
                    if(bStores != null){
                        trades_information.put("assignedStoresName", bStores.getName());
                    }
                }

                List<Map<String, Object>> order_list = ordersService.getTradesOrdersList(tradesId, siteId);//交易子订单
                Stockup stockup = ordersService.getTradesStockup(tradesId, siteId, null);//交易备货单

                if(!StringUtil.isEmpty(trades_information.get("userCouponId")) && !StringUtil.isEmpty(trades_information.get("userCouponAmount"))){//交易使用现金券
                    trades_information.put("userCouponName", "已使用现金券 "+ Integer.parseInt(String.valueOf(trades_information.get("userCouponAmount")))/100 + "元");
                }else{
                    trades_information.put("userCouponName", "");
                }

                if(StringUtil.isEmpty(trades_information.get("realPay"))){
                    trades_information.put("realPay", 0.0);
                }

                if(stockup!=null && stockup.getStockupStatus() == 120){//备货单状态为已备货
                    trades_information.put("stockupId", stockup.getStockupId());
                }

                if(order_list != null){
                    for(Map order : order_list){
                        Map<String, Object> imgInfo = new HashMap<>();
                        imgInfo.put("hostId", order.get("hostId"));
                        imgInfo.put("imageId", order.get("imageId"));
                        order.put("goodsImgurl", imgInfo);
                        order.remove("hostId");
                        order.remove("imageId");
                    }
                }

                tradesInformationMap.put("tradesInformation", trades_information);
                tradesInformationMap.put("orderList", order_list);
            }

            result.put("status","OK");
            result.put("results",tradesInformationMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status","ERROR");
            result.put("message","获取获取订单详情失败");
        }
        return result;
    }

    /**
     * 提货码订单详情
     * @param request
     * @return
     */
    @RequestMapping("/deliverycode")
    @ResponseBody
    public Map<String, Object> getOrderDetailByDeliveryCode(HttpServletRequest request){
        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String deliveryCode = request.getParameter("deliveryCode");

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Map<String, Object> tradesInformationMap = new HashMap<String, Object>();//没有判断提货码过期时间
            Map<String, Object> trades_information = ordersService.getOrderDetailByDeliveryCode(deliveryCode, siteId, storeId);//获取交易 交易扩展表

            if(trades_information!=null && trades_information.size()!=0 && !StringUtil.isEmpty(storeId) && storeId.equals(String.valueOf(trades_information.get("assignedStoresName")))){

                //获取店员邀请码
                String clerkInvitationCode = "";
                if(trades_information.get("storeUserId")!=null){
                    List<StoreAdminExt> storeAdminExt = storeAdminExtMapper.selectBySiteIdAndStoreAdminId(Integer.parseInt(siteId), Integer.parseInt(String.valueOf(trades_information.get("storeUserId"))));
                    if(storeAdminExt!=null && storeAdminExt.size()!=0){
                        String clerkInvitationCodeStr = storeAdminExt.get(0).getClerk_invitation_code();
                        if(!StringUtil.isEmpty(clerkInvitationCodeStr)){
                            int index = clerkInvitationCodeStr.indexOf("_");
                            if(index!=-1){
                                clerkInvitationCode = clerkInvitationCodeStr.substring(index+1, clerkInvitationCodeStr.length());
                            }
                        }
                    }
                }
                trades_information.put("storeSalerCode", clerkInvitationCode);

                //积分抵扣的金额 ：分
                if(!StringUtil.isEmpty(trades_information.get("integralPrice")) && !"null".equals(String.valueOf(trades_information.get("integralPrice")))){
                    trades_information.put("integralPrice", Double.parseDouble(String.valueOf(trades_information.get("integralPrice"))));
                }

                //selfTakenStoreName 用户自选  自提门店 对应 b_stores.id
                if(!StringUtil.isEmpty(trades_information.get("selfTakenStoreName")) && !"null".equals(String.valueOf(trades_information.get("selfTakenStoreName")))){
                    Store bStores = ordersService.getBStoresById(siteId, String.valueOf(trades_information.get("selfTakenStoreName")));
                    if(bStores != null){
                        trades_information.put("selfTakenStoreName", bStores.getName());
                    }
                }
                //assignedStoresName 系统指定  指派送货的门店
                if(!StringUtil.isEmpty(trades_information.get("assignedStoresName")) && !"null".equals(String.valueOf(trades_information.get("assignedStoresName")))){
                    Store bStores = ordersService.getBStoresById(siteId, String.valueOf(trades_information.get("assignedStoresName")));
                    if(bStores != null){
                        trades_information.put("assignedStoresName", bStores.getName());
                    }
                }

                List<Map<String, Object>> order_list = ordersService.getTradesOrdersList(String.valueOf(trades_information.get("tradesId")), siteId);//交易子订单
                Stockup stockup = ordersService.getTradesStockup(String.valueOf(trades_information.get("tradesId")), siteId, null);//交易备货单

                if(!StringUtil.isEmpty(trades_information.get("userCouponId")) && !StringUtil.isEmpty(trades_information.get("userCouponAmount"))){//交易使用现金券
                    trades_information.put("userCouponName", "已使用现金券 "+ Integer.parseInt(String.valueOf(trades_information.get("userCouponAmount")))/100 + "元");
                }else{
                    trades_information.put("userCouponName", "");
                }

                if(StringUtil.isEmpty(trades_information.get("realPay"))){
                    trades_information.put("realPay", 0.0);
                }

                if(stockup!=null && stockup.getStockupStatus() == 120){//备货单状态为已备货
                    trades_information.put("stockupId", stockup.getStockupId());
                }

                if(order_list != null){
                    for(Map order : order_list){
                        Map<String, Object> imgInfo = new HashMap<>();
                        imgInfo.put("hostId", order.get("hostId"));
                        imgInfo.put("imageId", order.get("imageId"));
                        order.put("goodsImgurl", imgInfo);
                        order.remove("hostId");
                        order.remove("imageId");
                    }
                }

                tradesInformationMap.put("tradesInformation", trades_information);
                tradesInformationMap.put("orderList", order_list);
            }

            result.put("status","OK");
            result.put("results",tradesInformationMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status","ERROR");
            result.put("message","获取获取订单详情失败");
        }
        return result;
    }

    @RequestMapping("/reminds")
    @ResponseBody
    public Map<String, Object> getRemindsOrderList(HttpServletRequest request){
        String siteId = request.getParameter("siteId");
        String storeId = request.getParameter("storeId");
        String page = request.getParameter("pageNum");
        String limit = request.getParameter("pageSize");

        Integer pageNum = null;
        Integer pageSize = null;
        if(StringUtil.isEmpty(page)){
            pageNum = 1;
        }else{
            pageNum = Integer.parseInt(page);
        }
        if(StringUtil.isEmpty(limit)){
            pageSize = 10;
        }else{
            pageSize = Integer.parseInt(limit);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            //Page<Map> pageBean = PageHelper.startPage(pageNum,pageSize);
            //List<Map<String, Object>> order_reminds = ordersService.getOrderRemindList(siteId, storeId, pageNum, pageSize);
            long total = ordersService.getOrderRemindListCount2(siteId, storeId);

            com.github.pagehelper.Page pageBean = new com.github.pagehelper.Page(pageNum, pageSize);//Page(int pageNum, int pageSize, boolean count, Boolean reasonable)
            pageBean.setReasonable(true);
            pageBean.setTotal(total);

            List<Map<String, Object>> order_reminds = ordersService.getOrderRemindList2(siteId, storeId, pageBean.getStartRow(), pageBean.getPageSize());
            PageInfo pageInfo = new PageInfo<>(pageBean);
            pageInfo.setList(order_reminds);

            if(order_reminds != null){
                for(Map<String, Object> map : order_reminds){
                    String type = "";
                    if("150".equals(String.valueOf(map.get("postStyle")))){
                        type = "送货";
                    }else if("160".equals(String.valueOf(map.get("postStyle")))){
                        type = "自提";
                    }else{
                        type = "直购";
                    }
                    String content = "门店有新的"+type+"订单,请尽快处理";

                    //这里pharmacistId 用 storeAdminId
                    if(!StringUtil.isEmpty(map.get("pharmacistId")) && !"0".equals(String.valueOf(map.get("pharmacistId")))){
                        String storeAdminextName = storeAdminExtMapper.getStoreAdminNameById(siteId, String.valueOf(map.get("pharmacistId")));
                        if(!StringUtil.isEmpty(storeAdminextName)){
                            map.put("realName", storeAdminextName);
                        }else{
                            map.put("realName", "");
                        }
                    }else{
                        map.put("realName", "");
                    }
                    map.put("content", content);
                }
            }

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("items",order_reminds!=null?order_reminds:new ArrayList());
            responseMap.put("before",pageInfo.getPrePage());
            responseMap.put("current",pageInfo.getPageNum());
            responseMap.put("next",pageInfo.getNextPage());
            responseMap.put("totalItems",pageInfo.getTotal());
            responseMap.put("totalPages",pageInfo.getPages());

            result.put("status","OK");
            result.put("results",responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status","ERROR");
            result.put("message","获取订单提醒列表失败");
        }
        return result;
    }

    @RequestMapping("/remindread")
    @ResponseBody
    public Map<String, Object> setOrderRemindRead(HttpServletRequest request){
        String id = request.getParameter("id");
        String storeAdminId = request.getParameter("storeAdminId");

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            ChOrderRemind orderRemind = ordersService.getOrderRemind(id);
            if(orderRemind != null && !StringUtil.isEmpty(storeAdminId)){
                List<ChOrderRemind> orderRemindList = ordersService.getOrderReminds(orderRemind.getOrderId());//is_readed=1 已读
                if(orderRemindList!=null && orderRemindList.size()!=0){
                    result.put("status","ERROR");
                    result.put("message","已有药师处理，请重新刷新。");
                }else{
                    ordersService.setOrderRemindRead(orderRemind.getOrderId(), storeAdminId);
                    result.put("status","OK");
                    result.put("results",null);
                }
            }else{
                result.put("status","ERROR");
                result.put("message","已有药师处理，请重新刷新。");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status","ERROR");
            result.put("message","已有药师处理，请重新刷新。");
        }
        return result;
    }

    @RequestMapping("/buyer")
    @ResponseBody
    public Map<String, Object> getBuyerTrades(HttpServletRequest request){
        String siteId = request.getParameter("siteId");
        String memberId = request.getParameter("memberId");
        String page = request.getParameter("pageNum");
        String limit = request.getParameter("pageSize");

        Integer pageNum = null;
        Integer pageSize = null;
        if(StringUtil.isEmpty(page)){
            pageNum = 1;
        }else{
            pageNum = Integer.parseInt(page);
        }
        if(StringUtil.isEmpty(limit)){
            pageSize = 10;
        }else{
            pageSize = Integer.parseInt(limit);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Page<Map> pageBean = PageHelper.startPage(pageNum,pageSize);
            List<Map<String, Object>> trades_informations = ordersService.getBuyerTradesInformation(memberId, siteId, null, pageNum, pageSize);//获取交易 交易扩展表
            List<Map<String, Object>> items = new ArrayList<>();
            if(trades_informations != null){
                for(Map<String, Object> trades_information : trades_informations){
                    Map<String, Object> tradesInformationMap = new HashMap<String, Object>();
                    if(trades_information!=null && trades_information.size()!=0){

                        //获取店员邀请码
                        String clerkInvitationCode = "";
                        if(trades_information.get("storeUserId")!=null){
                            List<StoreAdminExt> storeAdminExt = storeAdminExtMapper.selectBySiteIdAndStoreAdminId(Integer.parseInt(siteId), Integer.parseInt(String.valueOf(trades_information.get("storeUserId"))));
                            if(storeAdminExt!=null && storeAdminExt.size()!=0){
                                String clerkInvitationCodeStr = storeAdminExt.get(0).getClerk_invitation_code();
                                if(!StringUtil.isEmpty(clerkInvitationCodeStr)){
                                    int index = clerkInvitationCodeStr.indexOf("_");
                                    if(index!=-1){
                                        clerkInvitationCode = clerkInvitationCodeStr.substring(index+1, clerkInvitationCodeStr.length());
                                    }
                                }
                            }
                        }
                        trades_information.put("storeSalerCode", clerkInvitationCode);

                        //订单类型;1:门店直购(170),2:送货上门(150)
                        if("150".equals(String.valueOf(trades_information.get("postStyle")))){
                            trades_information.put("postStyleName", "送货上门");
                        }else if("170".equals(String.valueOf(trades_information.get("postStyle")))){
                            trades_information.put("postStyleName", "门店直购");
                        }
                        //交易状态:110(等侍买家付款), 120(等待卖家发货),130(等侍买家确认收货),140(买家已签收，货到付款专用)，150(交易成功)，160(用户未付款主动关闭)，
                        // 170(超时未付款，系统关闭)，180(商家关闭订单)，200( 待取货|待自提，直购和自提专用),210（ 已取货|已自提 直购和自提专用），900（已退款），220(用户确认收货)，230(门店确认收货)，800（系统确认收货）
                        //付款状态;已付款;未付款

                        //积分抵扣的金额 ：分
                        if(!StringUtil.isEmpty(trades_information.get("integralPrice")) && !"null".equals(String.valueOf(trades_information.get("integralPrice")))){
                            trades_information.put("integralPrice", Double.parseDouble(String.valueOf(trades_information.get("integralPrice")))/100);
                        }

                        //selfTakenStoreName 用户自选  自提门店 对应 b_stores.id
                        if(!StringUtil.isEmpty(trades_information.get("selfTakenStoreName")) && !"null".equals(String.valueOf(trades_information.get("selfTakenStoreName")))){
                            Store bStores = ordersService.getBStoresById(siteId, String.valueOf(trades_information.get("selfTakenStoreName")));
                            if(bStores != null){
                                trades_information.put("selfTakenStoreName", bStores.getName());
                            }
                        }
                        //assignedStoresName 系统指定  指派送货的门店
                        if(!StringUtil.isEmpty(trades_information.get("assignedStoresName")) && !"null".equals(String.valueOf(trades_information.get("assignedStoresName")))){
                            Store bStores = ordersService.getBStoresById(siteId, String.valueOf(trades_information.get("assignedStoresName")));
                            if(bStores != null){
                                trades_information.put("assignedStoresName", bStores.getName());
                            }
                        }

                        List<Map<String, Object>> order_list = ordersService.getTradesOrdersList(String.valueOf(trades_information.get("tradesId")), siteId);//交易子订单
                        Stockup stockup = ordersService.getTradesStockup(String.valueOf(trades_information.get("tradesId")), siteId, null);//交易备货单
                        if(!StringUtil.isEmpty(trades_information.get("userCouponId")) && !StringUtil.isEmpty(trades_information.get("userCouponAmount"))){//交易使用现金券
                            trades_information.put("userCouponName", "已使用现金券 "+ Integer.parseInt(String.valueOf(trades_information.get("userCouponAmount")))/100 + "元");
                        }else{
                            trades_information.put("userCouponName", "");
                        }
                        if(StringUtil.isEmpty(trades_information.get("realPay"))){
                            trades_information.put("realPay", 0.0);
                        }
                        if(stockup!=null && stockup.getStockupStatus() == 120){//备货单状态为已备货
                            trades_information.put("stockupId", stockup.getStockupId());
                        }

                        if(order_list != null){
                            for(Map order : order_list){
                                Map<String, Object> imgInfo = new HashMap<>();
                                imgInfo.put("hostId", order.get("hostId"));
                                imgInfo.put("imageId", order.get("imageId"));
                                order.put("goodsImgurl", imgInfo);
                                order.remove("hostId");
                                order.remove("imageId");
                            }
                        }

                        tradesInformationMap.put("tradesId", trades_information.get("tradesId"));
                        tradesInformationMap.put("postStyle", trades_information.get("postStyle"));
                        tradesInformationMap.put("createTime", trades_information.get("createTime"));
                        tradesInformationMap.put("orderList", order_list);
                        items.add(tradesInformationMap);
                    }
                }
            }

            PageInfo pageInfo = new PageInfo(trades_informations);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("items",(items!=null&&items.size()!=0)?items:new ArrayList());
            responseMap.put("before",pageInfo.getPrePage());
            responseMap.put("current",pageInfo.getPageNum());
            responseMap.put("next",pageInfo.getNextPage());
            responseMap.put("totalItems",pageInfo.getTotal());
            responseMap.put("totalPages",pageInfo.getPages());

            result.put("status","OK");
            result.put("results",responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status","ERROR");
            result.put("message","获取指定会员的所有订单失败");
        }
        return result;
    }

    @RequestMapping("/prebookid")
    @ResponseBody
    public Map<String, Object> getPreBookByIdMap(HttpServletRequest request){
        String siteId = request.getParameter("siteId");
        String prebookId = request.getParameter("prebookId");

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Map<String, Object> prebook = ordersService.getPrebook(siteId, prebookId);
            if(prebook!=null && prebook.size()!=0){
                String goods_ids = String.valueOf(prebook.get("prebook_goods_id"));
                if(!StringUtil.isEmpty(goods_ids)){
                    Goods goods = ordersService.getGoods(siteId, goods_ids);
                    if(goods != null){
                        prebook.put("specifCation", goods.getSpecifCation());
                    }
                }
                prebook.remove("prebook_goods_id");

                Map<String, Object> prebookMap = new HashMap<>();
                prebookMap.put("prebook", prebook);

                result.put("status","OK");
                result.put("results",prebookMap);
            }else{
                result.put("status","OK");
                result.put("results","没有查询到数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status","ERROR");
            result.put("message","预约单获取失败。");
        }
        return result;
    }

    @RequestMapping("/closeprebook")
    @ResponseBody
    public Map<String, Object> updataPrebook(HttpServletRequest request){
        String siteId = request.getParameter("siteId");
        String prebookId = request.getParameter("prebookId");
        String prebookClerkId = request.getParameter("prebookClerkId");
        String prebookState = request.getParameter("prebookState");

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            ordersService.updataPrebook(siteId, prebookId, prebookClerkId, Integer.parseInt(prebookState));
            result.put("status","OK");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status","ERROR");
            result.put("message","更改预约单状态失败。");
        }
        return result;
    }

    @RequestMapping("/prebook")
    @ResponseBody
    public Map<String, Object> getPrebookList(HttpServletRequest request){
        String siteId = request.getParameter("siteId");
        String prebookClerkId = request.getParameter("prebookClerkId");
        String page = request.getParameter("pageNum");
        String limit = request.getParameter("pageSize");

        Integer pageNum = null;
        Integer pageSize = null;
        if(StringUtil.isEmpty(page)){
            pageNum = 1;
        }else{
            pageNum = Integer.parseInt(page);
        }
        if(StringUtil.isEmpty(limit)){
            pageSize = 10;
        }else{
            pageSize = Integer.parseInt(limit);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Page<Map> pageBean = PageHelper.startPage(pageNum,pageSize);
            List<Map<String, Object>> prebookList = ordersService.getPrebookList(siteId, prebookClerkId, pageNum, pageSize);

            PageInfo pageInfo = new PageInfo(prebookList);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("items",(prebookList!=null)?prebookList:new ArrayList());
            responseMap.put("before",pageInfo.getPrePage());
            responseMap.put("current",pageInfo.getPageNum());
            responseMap.put("next",pageInfo.getNextPage());
            responseMap.put("totalItems",pageInfo.getTotal());
            responseMap.put("totalPages",pageInfo.getPages());

            result.put("status","OK");
            result.put("results",responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status","ERROR");
            result.put("message","获取送货上门订单失败");
        }
        return result;
    }

    @RequestMapping("/acceptprebook")
    @ResponseBody
    public Map<String, Object> acceptPrebook(HttpServletRequest request){
        String siteId = request.getParameter("siteId");
        String prebookId = request.getParameter("prebookId");
        String prebookClerkId = request.getParameter("prebookClerkId");
        String storeUserId = request.getParameter("storeUserId");

        Map<String, Object> result = new HashMap<String, Object>();
        String name = "";
        try {
            if(!StringUtil.isEmpty(prebookId) && !StringUtil.isEmpty(storeUserId) && !StringUtil.isEmpty(siteId)){
                StoreAdminExt adminext = ordersService.StoreAdminext(storeUserId, siteId);
                if(adminext!=null && !StringUtil.isEmpty(adminext.getName())){
                    name = adminext.getName();
                }
            }else{
                result.put("status","ERROR");
                result.put("message","店员信息不正确");
                return result;
            }

            ordersService.acceptPrebook(siteId, prebookId, prebookClerkId, name);
            result.put("status","OK");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status","ERROR");
            result.put("message","预约接单失败。");
        }
        return result;
    }


}
