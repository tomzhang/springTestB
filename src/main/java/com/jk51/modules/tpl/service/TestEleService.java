package com.jk51.modules.tpl.service;

import com.jk51.model.Stores;
import com.jk51.model.order.Trades;
import com.jk51.modules.tpl.request.ElemeCreateOrderRequest;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Service
public class TestEleService {
    private static final Log logger = LogFactory.getLog(TestEleService.class);
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private EleService eleService;
    public  Map<String,Object> test() {
        Trades trades=  tradesMapper.getTradesByTradesId(1000301492755632963L);
        /*trades.setSellerMobile("18302154581");
        trades.setRecevierName("李先生");
        trades.setSellerName("大药房");
        trades.setReceiverPhone("18356154581");*/

        Stores stores=new Stores();
        stores.setAddress("上海市浦东新区碧波路49弄一号");
        stores.setGaode_lng("122.124271");
        stores.setGaode_lat("31.521305");
        stores.setRemind_mobile("13817708743");
        ElemeCreateOrderRequest.ItemsJson[] itemsJsons = new ElemeCreateOrderRequest.ItemsJson[2];
        ElemeCreateOrderRequest.ItemsJson item = new ElemeCreateOrderRequest.ItemsJson();
        item.setItem_name("香蕉");
        item.setItem_quantity(5);
        item.setItem_actual_price(new BigDecimal(9.50));
        item.setItem_price(new BigDecimal(10.00));
        item.setIs_agent_purchase(1);
        item.setAgent_purchase_price(new BigDecimal(10.00));
        item.setIs_need_package(1);

        ElemeCreateOrderRequest.ItemsJson item1 = new ElemeCreateOrderRequest.ItemsJson();
        item1.setItem_name("苹果");
        item1.setItem_quantity(5);
        item1.setItem_actual_price(new BigDecimal(9.50));
        item1.setItem_price(new BigDecimal(10.00));
        item1.setIs_agent_purchase(1);
        item1.setAgent_purchase_price(new BigDecimal(10.00));
        item1.setIs_need_package(1);

        itemsJsons[0] = item;
        itemsJsons[1] = item1;
        logger.info("测试蜂鸟");
        eleService.MycreateOrder(trades,stores,itemsJsons);
        //imdadaService.createOrder(trades,stores);
        return  null;
    }
}
