package com.jk51.modules.trades.service;

import com.github.pagehelper.PageHelper;
import com.jk51.commons.date.DateUtils;
import com.jk51.model.order.Stockup;
import com.jk51.modules.integral.mapper.IntegrallogMapper;
import com.jk51.modules.offline.service.TianROfflineService;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.trades.mapper.StockupMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-23
 * 修改记录:
 */
@Service
public class StockupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockupService.class);
    @Autowired
    TradesService tradesService;
    @Autowired
    StockupMapper stockupMapper;
    @Autowired
    TradesMapper tradesMapper;
    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    TianROfflineService tianROfflineService;
    @Value("${erp.service_url}")
    private String erp_service_url;
    @Autowired
    private IntegrallogMapper integrallogMapper;

    /**
     * 订单备货，插入备货数据
     *
     * @param tradesId
     * @param siteId
     * @param storeId
     * @param clerkId
     * @return
     */
    public int commitStockup(long tradesId, int siteId, int storeId, int clerkId) {
        try {
            PageHelper.startPage(1, 1, false);
            String stockupId = stockupMapper.getStockupId(siteId, storeId, new Timestamp(DateUtils.parse(DateUtils.getToday(), "yyyy-MM-dd").getTime()));
            if (stockupId == null) {
                stockupId = "0";
            }
            DecimalFormat df = new DecimalFormat("000");
            Stockup stockup = this.findByTradesId(tradesId);
            if (stockup == null)
                stockup = new Stockup();
            stockup.setTradesId(tradesId);
            stockup.setSiteId(siteId);
            stockup.setStoreId(storeId);
            stockup.setStockupStatus(120);
            stockup.setClerkId(clerkId);
            stockup.setStockupTime(new Timestamp(System.currentTimeMillis()));
            stockup.setStockupId(df.format(Integer.parseInt(stockupId) + 1));
            //erp推送订单减erp库存
//            sendStockup(tradesId);
            if (stockup.getId() == null)
                return stockupMapper.insert(stockup);
            else {
                stockupMapper.update(stockup);
                return stockup.getId();
            }
        } catch (ParseException e) {
        }
        return -1;
    }

    /**
     * 根据订单id查询备货数据
     *
     * @param tradesId
     * @return
     */
    public Stockup findByTradesId(Long tradesId) {
        if (tradesId == null) {
            return null;
        }
        return stockupMapper.findByTradesId(tradesId);
    }

    /**
     * 订单发货，修改发货状态
     *
     * @param tradesId
     * @param clerkId
     * @return
     */
    public boolean commitShipping(Long tradesId, Integer clerkId) {
        if (tradesId == null) {
            return false;
        }
        Stockup stockup = stockupMapper.findByTradesId(tradesId);
        if (stockup == null) {
            return false;
        }
        stockup.setShippingStatus(120);
        stockup.setClerkId(clerkId);
        stockup.setShippingTime(new Timestamp(System.currentTimeMillis()));
        if (stockupMapper.update(stockup) > 0)
            return true;
        return false;
    }
}
