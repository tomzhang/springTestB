package com.jk51.modules.store.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.erpprice.BGoodsErp;
import com.jk51.model.goods.YbStoresGoodsPrice;
import com.jk51.modules.erpprice.mapper.BGoodsErpMapper;
import com.jk51.modules.persistence.mapper.SGoodsMapper;
import com.jk51.modules.persistence.mapper.SYbStoresGoodsPriceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SGoodsPriceService {

    private static final Logger logger = LoggerFactory.getLogger(SGoodsPriceService.class);

    @Autowired
    private SYbStoresGoodsPriceMapper ybStoresGoodsPriceMapper;

    @Autowired
    private SGoodsMapper goodsMapper;

    @Autowired
    private BGoodsErpMapper bGoodsErpMapper;

    public Map<String, Object> findByGoodsList(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtil.isEmpty(param.get("siteId"))) {
            result.put("msg", "商户ID不能为空！");
        } else {
            int currentPage = Integer.parseInt(StringUtils.isEmpty(param.get("currentPage")) ? "0" : param.get("currentPage").toString());
            int pageSize = Integer.parseInt(StringUtils.isEmpty(param.get("pageSize")) ? "10" : param.get("pageSize").toString());
            PageHelper.startPage(currentPage, pageSize);//开启分页
            logger.info(param.toString());
            List list = ybStoresGoodsPriceMapper.findByGoodsListPrice(param);
            result.put("goodsPage", new PageInfo<>(list));
        }
        return result;
    }

    public int updateYBPrice(Integer storeId, Integer siteId, Integer goodsPrice, Integer goodsId, Integer erpPrice) {
        int result = 0;
        YbStoresGoodsPrice ybStoresGoodsPrice = ybStoresGoodsPriceMapper.findByGoodsId(storeId, siteId, goodsId);
        Map goodsInfo = this.goodsMapper.queryGoodsDetailByGoodId(goodsId, siteId);
        Integer shop_price = (Integer) goodsInfo.get("shopPrice");//药房价格
        if (StringUtil.isEmpty(shop_price)) {
            shop_price = 0;
        }
        if (StringUtil.isEmpty(ybStoresGoodsPrice)) {
            YbStoresGoodsPrice ybStoresGoodsPricenew = new YbStoresGoodsPrice();
            ybStoresGoodsPricenew.setCreateTime(new Date());
            ybStoresGoodsPricenew.setUpdateTime(new Date());
            ybStoresGoodsPricenew.setGoodsId(goodsId);
            ybStoresGoodsPricenew.setSiteId(siteId);
            ybStoresGoodsPricenew.setStoreId(storeId);
            ybStoresGoodsPricenew.setGoodsPrice(shop_price);
            ybStoresGoodsPricenew.setDiscountPrice(goodsPrice);
            result = ybStoresGoodsPriceMapper.insertYBPrice(ybStoresGoodsPricenew);
        } else {
            result = ybStoresGoodsPriceMapper.updateYBPrice(goodsPrice, goodsId, siteId, storeId);
        }
        ybStoresGoodsPriceMapper.updateSelfFlag(null, 1, siteId, StringUtil.toList(String.valueOf(goodsId), ","),
            StringUtil.toList(String.valueOf(storeId), ","));
        if (erpPrice != null) {
            BGoodsErp bGoodsErp = bGoodsErpMapper.selectByStroeidAndGoodsid(siteId, storeId, goodsId);
            if (bGoodsErp != null) {
                result = bGoodsErpMapper.updateByStroeidAndGoodsid(siteId, storeId, goodsId, erpPrice);
            }
        }
        return result;
    }

    public int refreshYBTime(Map<String, Object> param) {
        int result = 0;
        result = ybStoresGoodsPriceMapper.refreshYBTime(param);
        return result;
    }

    public int resumeStorePriceAction(Integer storeId, Integer siteId, Integer type, Integer goodsId) {
        int result = 0;
        if (type == 100) {
            result = ybStoresGoodsPriceMapper.delYBPriceAll(storeId, siteId, goodsId);
        } else {
            result = ybStoresGoodsPriceMapper.delYBPriceAll(storeId, siteId, null);
        }
        return result;
    }

    public YbStoresGoodsPrice getGoodsInfoPriceByStore(Integer siteId, Integer storeId, Integer goodsId) {
        return ybStoresGoodsPriceMapper.findByGoodsId(storeId, siteId, goodsId);
    }

    public ReturnDto updateStorePriceFlag(Integer siteId, Integer storeId, String goodsIds, Integer delFlag) {
        try {
            if (delFlag == 1) {//禁用商品价格
                ybStoresGoodsPriceMapper.updateFlag(1, null, siteId, StringUtil.toList(goodsIds, ","),
                    StringUtil.toList(String.valueOf(storeId), ","), null);
            } else {//启用商品的门店价格
                ybStoresGoodsPriceMapper.updateFlag(0, -1, siteId, StringUtil.toList(goodsIds, ","),
                    StringUtil.toList(String.valueOf(storeId), ","), null);
            }
            ybStoresGoodsPriceMapper.updateSelfFlag(null, 0, siteId, StringUtil.toList(goodsIds, ","),
                StringUtil.toList(String.valueOf(storeId), ","));
            return ReturnDto.buildSuccessReturnDto();
        } catch (Exception e) {
            logger.info("更新门店商品价格状态失败" + e.getMessage());
            return ReturnDto.buildFailedReturnDto("更新门店商品价格状态失败" + e.getMessage());
        }
    }
}
