package com.jk51.modules.goods.library;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.Goods;
import com.jk51.model.GoodsExtdWithBLOBs;
import com.jk51.model.goods.BIntegralGoods;
import com.jk51.modules.goods.request.GoodsData;

import java.util.Date;


public class GoodsProviders {

    private GoodsData data;

    public final static Integer INTEGRAL_GOODS_WAITING_STATUS = 0;////积分商品未开始类型

    public final static Integer INTEGRAL_GOODS_PROCESSIN_GSTATUS = 10;//积分商品进行中类型

    public final static Integer INTEGRAL_GOODS_ENDED_STATUS = 20;//积分商品截止类型

    public final static Integer INTEGRAL_GOODS_NOT_DELETE = 1;//积分商品有效状态

    public final static Integer INTEGRAL_GOODS_DELETE = 0;//积分商品无效状态（软删除）

    public GoodsProviders(GoodsData data) {
        this.data = data;
    }

    public void register() {

    }

    public Goods buildGoods() {
//        ObjectMapper objectMapper = JacksonUtils.getInstance();
        // 忽略未知字段
//        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 驼峰
//        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

//        GoodsInfo goods = JacksonUtils.map2pojo(request, GoodsInfo.class);

        Goods goods = (Goods)data;

        // 处理品牌、分类、模板字段

        return goods;
    }

    public BIntegralGoods buildBIntegralGoods(){

        if(INTEGRAL_GOODS_DELETE == data.getIntegralGoodsIsDel()){
            BIntegralGoods integralGoods = new BIntegralGoods();

            integralGoods.setSiteId(data.getSiteId());
            integralGoods.setStoreIds(data.getIntegralGoodsStoreIds());
            integralGoods.setGoodsId(data.getGoodsId());
            integralGoods.setIsDel(INTEGRAL_GOODS_DELETE);
            integralGoods.setLimitCount(data.getLimitCount());
            integralGoods.setLimitEach(data.getLimitEach());
            return integralGoods;

        }else if(INTEGRAL_GOODS_NOT_DELETE == data.getIntegralGoodsIsDel()) {

            final Date nowTime = new Date();

            BIntegralGoods integralGoods = new BIntegralGoods();

            integralGoods.setSiteId(data.getSiteId());
            integralGoods.setGoodsId(data.getGoodsId());
            integralGoods.setStoreIds(data.getIntegralGoodsStoreIds());
            integralGoods.setIsDel(INTEGRAL_GOODS_NOT_DELETE);
            integralGoods.setLimitCount(data.getLimitCount());
            integralGoods.setLimitEach(data.getLimitEach());
            integralGoods.setIntegralExchanges(data.getIntegralExchanges());
            integralGoods.setStartTime(data.getIntegralGoodsStartTime());
            integralGoods.setEndTime(data.getIntegralGoodsEndTime());

//            if(data.getIntegralGoodsStartTime() != null && data.getIntegralGoodsStartTime().getTime() > nowTime.getTime()){
//                integralGoods.setStatus(INTEGRAL_GOODS_WAITING_STATUS );
//            }
//
//            if (data.getIntegralGoodsStartTime() == null){
//                integralGoods.setStatus(INTEGRAL_GOODS_PROCESSIN_GSTATUS);
//            }
//
//            if(data.getIntegralGoodsStartTime() != null && data.getIntegralGoodsStartTime().getTime() <= nowTime.getTime()){
//                integralGoods.setStatus(INTEGRAL_GOODS_PROCESSIN_GSTATUS);
//            }
//
//            if(data.getIntegralGoodsEndTime() != null && data.getIntegralGoodsEndTime().getTime() <= nowTime.getTime()){
//                integralGoods.setStatus(INTEGRAL_GOODS_ENDED_STATUS);
//            }

            return integralGoods;
        }else {
            return null;
        }

    }

    public GoodsExtdWithBLOBs buildGoodsExtd() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        // 忽略未知字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 驼峰
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        String jsonStr = JacksonUtils.obj2json(data);

        GoodsExtdWithBLOBs goodsExtdWithBLOBs = objectMapper.readValue(jsonStr, GoodsExtdWithBLOBs.class);

        return goodsExtdWithBLOBs;
    }

    /**
     * 根据品牌名称查找品牌ID
     * @param brandName
     */
    public void getBrandIdByName(String brandName) {

    }
}
