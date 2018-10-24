package com.jk51.modules.order.controller;

import com.jk51.model.order.HomeDeliveryAndStoresInvite;
import com.jk51.model.order.response.OrderResponse;
import com.jk51.modules.order.service.PaOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:订单创建、查询controller
 * 作者: baixiongfei
 * 创建日期: 2017/2/15
 * 修改记录:
 */
@Controller
@RequestMapping("/PaOrder")
public class PaOrderController {

    public static final Logger logger = LoggerFactory.getLogger(PaOrderController.class);

    @Autowired
    private PaOrderService orderService;

    /**
     * 创建订单，送货上门订单及门店自提订单
     *
     * @param homeDeliveryAndStoresInvite
     * @return
     */
    @RequestMapping("/create")
    @ResponseBody
    public OrderResponse createOrder (@RequestBody HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite) {
        return orderService.createOrders(homeDeliveryAndStoresInvite);
    }
}
