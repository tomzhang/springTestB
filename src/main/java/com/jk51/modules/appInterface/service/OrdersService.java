package com.jk51.modules.appInterface.service;

import com.jk51.model.ChOrderRemind;
import com.jk51.model.Goods;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.order.Stockup;
import com.jk51.model.order.Store;
import com.jk51.modules.appInterface.mapper.BGoodsPrebookMapper;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import com.jk51.modules.appInterface.mapper.BOrdersMapper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.im.mapper.ChOrderRemindMapper;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.trades.mapper.StockupMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yanglile
 * 创建日期: 2017-03-03
 * 修改记录:
 */
@Service
public class OrdersService {

    @Autowired
    private BMemberMapper bMemberMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private BOrdersMapper bOrdersMapper;
    @Autowired
    private StockupMapper bStockupMapper;
    @Autowired
    private ChOrderRemindMapper chOrderRemindMapper;
    @Autowired
    private BGoodsPrebookMapper bGoodsPrebookMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;

    public Map<String,Object> selectMemberMapByPhoneNum(String siteId, String mobile) {
        return bMemberMapper.selectMemberMapByPhoneNum(siteId, mobile);
    }

    public List<Map<String, Object>> getOtherOrderList(String siteId, String storeId, Integer pageNum, Integer pageSize) {
        return tradesMapper.getOtherOrderListMap(siteId, storeId);
    }

    public List<Map<String,Object>> getOrderList(String siteId, String storeId, Integer pageNum, Integer pageSize, String storeShippingClerkId) {
        return tradesMapper.getOrderListMap(siteId, storeId, storeShippingClerkId);
    }

    public Map<String,Object> getTradesInformation(String siteId, String tradesId, String storeId) {
        return tradesMapper.getTradesInformationMap(tradesId, siteId, storeId);
    }

    public Store getBStoresById(String siteId, String id) {
        return storesMapper.selectByPrimaryKey(Integer.valueOf(siteId), Integer.valueOf(id));
    }

    public List<Map<String,Object>> getTradesOrdersList(String tradesId, String siteId) {
        return bOrdersMapper.getTradesOrdersListMap(tradesId, siteId);
    }

    public Stockup getTradesStockup(String tradesId, String siteId, String storeId) {
        return bStockupMapper.getTradesStockup(tradesId, siteId, storeId);
    }

    public Map<String,Object> getOrderDetailByDeliveryCode(String deliveryCode, String siteId, String storeId) {
        return tradesMapper.getOrderDetailByDeliveryCodeMap(deliveryCode, siteId, storeId);
    }

    public List<Map<String,Object>> getOrderRemindList(String siteId, String storeId, Integer pageNum, Integer pageSize) {
        //return chOrderRemindMapper.getOrderRemindListMap(siteId, storeId);
        return chOrderRemindMapper.getOrderRemindListMap2(siteId, storeId);
    }

    public ChOrderRemind getOrderRemind(String id) {
        return chOrderRemindMapper.getOrderRemindById2(id);
    }

    public List<ChOrderRemind> getOrderReminds(String orderId) {
        return chOrderRemindMapper.getOrderReminds2(orderId);
    }

    public void setOrderRemindRead(String orderId, String storeAdminId) {
        chOrderRemindMapper.setOrderRemindRead(orderId, storeAdminId);
    }

    public List<Map<String,Object>> getBuyerTradesInformation(String memberId, String siteId, String storeId, Integer pageNum, Integer pageSize) {
        return tradesMapper.getBuyerTradesInformationMap(memberId, siteId, storeId);
    }

    public Map<String, Object> getPrebook(String siteId, String prebookId) {
        return bGoodsPrebookMapper.selectByPrimaryKeyMap(siteId, prebookId);
    }

    public Goods getGoods(String siteId, String goodsId) {
        return goodsMapper.getBySiteIdAndGoodsId(Integer.parseInt(siteId), Integer.parseInt(goodsId));
    }

    public void updataPrebook(String siteId, String prebookId, String prebookClerkId, int prebookState) {
        bGoodsPrebookMapper.updataPrebook(siteId, prebookId, prebookClerkId, prebookState);
    }

    public List<Map<String,Object>> getPrebookList(String siteId, String prebookClerkId, Integer pageNum, Integer pageSize) {
        return bGoodsPrebookMapper.getPrebookListMap(siteId, prebookClerkId);
    }

    public StoreAdminExt StoreAdminext(String id, String siteId) {
        return storeAdminExtMapper.selectByPrimaryKey(Integer.parseInt(id), Integer.parseInt(siteId));
    }

    public void acceptPrebook(String siteId, String prebookId, String prebookClerkId, String name) {
        bGoodsPrebookMapper.acceptPrebook(siteId, prebookId, prebookClerkId, name);
    }

    public long getOrderRemindListCount2(String siteId, String storeId) {
        return chOrderRemindMapper.getOrderRemindListCount2(siteId, storeId);
    }
    public List<Map<String,Object>> getOrderRemindList2(String siteId, String storeId, int startRow, int pageSize) {
        return chOrderRemindMapper.getOrderRemindList2(siteId, storeId, startRow, pageSize);
    }
}
