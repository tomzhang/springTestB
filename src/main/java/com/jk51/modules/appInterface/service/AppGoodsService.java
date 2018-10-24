package com.jk51.modules.appInterface.service;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.Goods;
import com.jk51.model.coupon.tags.TagsGoodsPromotions;
import com.jk51.model.coupon.tags.TagsParam;
import com.jk51.model.goods.YbStoresGoodsPrice;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import com.jk51.modules.appInterface.util.ResultMap;
import com.jk51.modules.coupon.tags.PromotionsTimeTagsFilter;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.treat.mapper.MerchantExtTreatMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yeah
 * 创建日期: 2017-03-08
 * 修改记录:
 */
@Service
public class AppGoodsService {
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    MerchantExtTreatMapper merchantExtTreatMapper;
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private BMemberMapper bMemberMapper;
    Logger logger = LoggerFactory.getLogger(AppGoodsService.class);

    public Map<String, Object> queryGoodsDetailByGoodId(Integer goods_id, Integer site_id, Integer store_id, Map<String, Object> body) {
        Map<String, Object> results = new HashMap<>();
        try {
            Map items = this.goodsMapper.queryGoodsDetailByGoodId(goods_id, site_id);
            //MerchantExtTreat merchantExt = merchantExtTreatMapper.selectByMerchantId(site_id);
            //未开启erp价格对接
            /*if(!StringUtil.isEmpty(merchantExt.getHas_erp_price())&&merchantExt.getHas_erp_price().equals(1)&&!StringUtil.isEmpty(items.get("erpPrice"))&&Integer.parseInt(items.get("erpPrice")+"")!=0 ){
                items.put("shopPrice",items.get("erpPrice"));
            }*/
            YbStoresGoodsPrice ybStoresGoodsPrice = this.goodsMapper.queryGoodStorePrice(goods_id, site_id, store_id);
            if (null != ybStoresGoodsPrice && 0 != ybStoresGoodsPrice.getDiscountPrice()) {
                //m.put("shopPrice",ybStoresGoodsPrice.getDiscountPrice());
                items.put("shopPrice", ybStoresGoodsPrice.getDiscountPrice());
            } else {//门店商品价格不存在，读取总部价格
                items.put("shopPrice", goodsMapper.getBySiteIdAndGoodsId(goods_id, site_id).getShopPrice());
            }
//            List<Integer> goodsIds = new ArrayList<Integer>();
//            goodsIds.add((Integer) items.get("goodsId"));
//            Map<Integer, BGoodsErp> bGoodsErpMap = erpPriceService.selectERPPrice(site_id, goodsIds, store_id);
//            if (Objects.nonNull(bGoodsErpMap.get(items.get("goodsId")))) {
//                items.put("shopPrice", bGoodsErpMap.get(items.get("goodsId")).getPrice());
//            }
            // 添加tag ----start
            PromotionsTimeTagsFilter promotionsTagsFilter = null;
            Object mobileObj = body.get("mobile");
            //若手机号不为null，则包含memberId查询；否则，根据memberId为null查询
            if (Objects.nonNull(body.get("mobile")) && body.get("mobile") != "" ){
                Map<String,Object> memberMap = bMemberMapper.selectMemberMapByPhoneNum(site_id.toString(), mobileObj.toString());
                if (null != memberMap || memberMap.size() > 0)
                    promotionsTagsFilter = new PromotionsTimeTagsFilter(servletContext, new TagsParam(Integer.parseInt(site_id.toString()), Integer.parseInt(memberMap.get("member_id").toString()), goods_id.toString(), 3));
                else
                    promotionsTagsFilter = new PromotionsTimeTagsFilter(servletContext, new TagsParam(Integer.parseInt(site_id.toString()), null, goods_id.toString(), 3));
            }else
                promotionsTagsFilter = new PromotionsTimeTagsFilter(servletContext, new TagsParam(Integer.parseInt(site_id.toString()), null, goods_id.toString(), 3));

            promotionsTagsFilter.collection();
            try {
                promotionsTagsFilter.sorted().resolve();
            } catch (Exception e) {
                logger.error("处理标签异常:{}",e);
            }
            List<TagsGoodsPromotions> tags = promotionsTagsFilter.getTags();
            tags.forEach(tg->{
                if(items.get("goods_id").toString().equals(tg.getGoodsId()))
                    items.put("tag", tg.getTags());
            });
            // 添加tag ----end
            //------添加是否购买过、是否有任务标签------start
            items.put("shoppingRecord", 0);
            items.put("haveTaskPlan", 0);
            //购买记录
            Object goodsIdObj = items.get("goodsId");
            if (Objects.nonNull(goodsIdObj) && Objects.nonNull(body.get("mobile")) && StringUtils.isNotBlank(body.get("mobile").toString())){
                List<Integer> buyerRecordGoodsIds = goodsMapper.getBuyerRecordBySiteIdAndStoreIdOrStoreAdminId(body);
                if(CollectionUtils.isNotEmpty(buyerRecordGoodsIds)){
                    if (buyerRecordGoodsIds.contains(Integer.parseInt(items.get("goodsId").toString()))){
                        items.put("shoppingRecord", 1);
                    }
                }
            }
            //任务考核
            if (Objects.nonNull(body.get("store_id")) && Objects.nonNull(body.get("storeAdminId"))){
                List<String> planGoodsIds = goodsMapper.getKaoHeBySiteIdAndStoreIdOrStoreAdminId(Integer.valueOf(body.get("site_id").toString()), Integer.valueOf(body.get("store_id").toString()), body.get("storeAdminId").toString());
                List<String> splitList = new ArrayList<>();
                boolean isAll = false;
                if(CollectionUtils.isNotEmpty(planGoodsIds)){
                    Long all=planGoodsIds.stream().filter(s -> Objects.equals("all",s)).count();
                    if(all > 0L){
                        isAll =true;
                    }else{
                        splitList =Arrays.asList(StringUtil.join(planGoodsIds,",").split(","));
                    }
                }
                List<Integer> taskGoodsIdsList = splitList.stream().map(Integer::parseInt).collect(Collectors.toList());
                if (isAll || taskGoodsIdsList.contains(items.get("goods_id").toString())){
                    items.put("haveTaskPlan", 1);
                }
            }
            //------添加是否购买过、是否有任务标签------end
            List list = new ArrayList();
            list.add(items);
            results.put("items", list);
            return ResultMap.successResult(results);
        } catch (Exception e) {
            logger.error("根据商品id[{}]获取商品信息失败,错误信息" + e, goods_id);
            e.printStackTrace();
            return ResultMap.errorResult("根据商品id获取商品信息失败");
        }
    }

    public Map<String, Object> queryGoodsListByCondition(Map map) {
        Map<String, Object> results = new HashMap<>();
        if (Integer.parseInt(map.get("drug_type").toString()) == 3 || Integer.parseInt(map.get("drug_type").toString()) == 0) {
            map.put("drug_name", "%" + map.get("drug_name") + "%");
        }
        if (Integer.parseInt(map.get("drug_type").toString()) == 1 && map.get("drug_name").toString().length() == 13 && map.get("drug_name").toString().substring(0, 1).equals("0")) {
            map.put("drug_namenew", map.get("drug_name").toString().substring(1, map.get("drug_name").toString().length()));
        }
        try {

            List<Map> items = this.goodsMapper.queryGoodsListByConditions(map);
            //MerchantExtTreat merchantExt = merchantExtTreatMapper.selectByMerchantId((Integer) map.get("site_id"));
            items.stream().forEach(m -> {
                //未开启erp价格对接
                /*if(!StringUtil.isEmpty(merchantExt.getHas_erp_price())&&merchantExt.getHas_erp_price().equals(1)&&!StringUtil.isEmpty(m.get("erpPrice"))&&Integer.parseInt(m.get("erpPrice")+"")!=0 ){
                    m.put("shopPrice",m.get("erpPrice"));
                }*/
                YbStoresGoodsPrice ybStoresGoodsPrice = this.goodsMapper.queryGoodStorePrice((Integer) m.get("goodsId"), (Integer) map.get("site_id"), (Integer) map.get("store_id"));
                if (null != ybStoresGoodsPrice && 0 != ybStoresGoodsPrice.getDiscountPrice()) {//门店商品价格存在
                    //m.put("shopPrice",ybStoresGoodsPrice.getDiscountPrice());
                    m.put("shopPrice", ybStoresGoodsPrice.getDiscountPrice());
                } else {//门店商品价格不存在，读取总部价格
                    m.put("shopPrice", goodsMapper.getBySiteIdAndGoodsId((Integer) m.get("goodsId"), (Integer) map.get("site_id")).getShopPrice());
                }
//                List<Integer> goodsIds=new ArrayList<Integer>();
//                goodsIds.add((Integer)m.get("goodsId"));
//                Map<Integer, BGoodsErp> bGoodsErpMap = erpPriceService.selectERPPrice((Integer) map.get("site_id"), goodsIds,(Integer) map.get("store_id"));
//                if (Objects.nonNull(bGoodsErpMap.get(m.get("goodsId")))) {
//                    m.put("shopPrice", bGoodsErpMap.get(m.get("goodsId")).getPrice());
//                }
            });
            // 添加tag ----starts
            PromotionsTimeTagsFilter promotionsTagsFilter = null;
            String goodsIds = items.stream()
                .map(gs -> {
                    return gs.get("goodsId").toString();
                }).collect(Collectors.joining(","));
            //若手机号不为null，则包含memberId查询；否则，根据memberId为null查询
            System.err.println(map.get("mobile"));
            if (Objects.nonNull(map.get("mobile")) && map.get("mobile") != "" && !("false".equals(map.get("mobile")))){
                Map<String,Object> memberMap = bMemberMapper.selectMemberMapByPhoneNum(map.get("site_id").toString(), map.get("mobile").toString());
                if (null != memberMap && memberMap.size() > 0) {
                    promotionsTagsFilter = new PromotionsTimeTagsFilter(servletContext, new TagsParam(Integer.parseInt(map.get("site_id").toString()), Integer.parseInt(memberMap.get("member_id").toString()), goodsIds, 3));
                }else{
                    promotionsTagsFilter = new PromotionsTimeTagsFilter(servletContext, new TagsParam(Integer.parseInt(map.get("site_id").toString()), null, goodsIds, 3));
                }
            }else{
                promotionsTagsFilter = new PromotionsTimeTagsFilter(servletContext, new TagsParam(Integer.parseInt(map.get("site_id").toString()), null, goodsIds, 3));
            }

            promotionsTagsFilter.collection();
            try {
                promotionsTagsFilter.sorted().resolve();
            } catch (Exception e) {
                logger.error("处理标签异常:{}",e);
            }
            List<TagsGoodsPromotions> tags = promotionsTagsFilter.getTags();
            items.forEach(gs->{
                tags.forEach(tg->{
                    if(gs.get("goodsId").toString().equals(tg.getGoodsId()))
                        gs.put("tag", tg.getTags());
                });
            });
            // 添加tag ----end
            //------添加是否购买过、是否有任务标签------start
            items.stream().filter(m -> {
                if (Objects.isNull(m.get("goodsId"))) return false;
                return true;
            }).forEach(m -> {m.put("shoppingRecord", 0);m.put("haveTaskPlan", 0);});
            //购买记录
            if (Objects.nonNull(map.get("mobile")) && StringUtils.isNotBlank(map.get("mobile").toString())){
                List<Integer> buyerRecordGoodsIds = goodsMapper.getBuyerRecordBySiteIdAndStoreIdOrStoreAdminId(map);
                if (CollectionUtils.isNotEmpty(buyerRecordGoodsIds)){
                    List<Integer> goodsIdsList = items.stream().filter(m -> {
                        if (Objects.isNull(m.get("goodsId"))) return false;
                        return true;
                    }).map(m -> {
                        return Integer.valueOf(m.get("goodsId").toString());
                    }).collect(Collectors.toList());
                    buyerRecordGoodsIds.retainAll(goodsIdsList);
                    if(CollectionUtils.isNotEmpty(buyerRecordGoodsIds)){
                        items.stream().filter(m -> {
                            if (Objects.isNull(m.get("goodsId"))) return false;
                            return true;
                        }).forEach(m -> {
                            if (buyerRecordGoodsIds.contains(Integer.parseInt(m.get("goodsId").toString()))){
                                m.put("shoppingRecord", 1);
                            }
                        });
                    }
                }
            }
            //任务考核
            if (Objects.nonNull(map.get("store_id")) && Objects.nonNull(map.get("storeAdminId"))){
                List<String> planGoodsIds = goodsMapper.getKaoHeBySiteIdAndStoreIdOrStoreAdminId(Integer.valueOf(map.get("site_id").toString()), Integer.valueOf(map.get("store_id").toString()), map.get("storeAdminId").toString());
                List<String> splitList = new ArrayList<>();
                boolean isAll = false;
                if(CollectionUtils.isNotEmpty(planGoodsIds)){
                    Long all=planGoodsIds.stream().filter(s -> Objects.equals("all",s)).count();
                    if(all > 0L){
                        isAll =true;
                    }else{
                        splitList =Arrays.asList(StringUtil.join(planGoodsIds,",").split(","));
                    }
                }
                if(isAll){
                    items.stream().forEach(m -> {
                        m.put("haveTaskPlan", 1);
                    });
                }else{
                    List<String> taskPlanGoodsId = new ArrayList<>(splitList);
                    items.stream().filter(m -> {
                        if (Objects.isNull(m.get("goodsId"))) return false;
                        return true;
                    }).forEach(m -> {
                        if (taskPlanGoodsId.contains(m.get("goodsId").toString())){
                            m.put("haveTaskPlan", 1);
                        }
                    });
                }
            }
            //------添加是否购买过、是否有任务标签------end
            results.put("items", items);
            results.put("totalItems", items.size());
            return ResultMap.successResult(results);
        } catch (Exception e) {
            logger.error("根据商品名条件查询获取商品信息失败,错误信息" + e);
            return ResultMap.errorResult("根据商品名条件查询获取商品信息失败");
        }
    }

    public Map<String, Object> queryNullBarCode(Map map) {
        Map<String, Object> results = new HashMap();
        List<Map> items = this.goodsMapper.queryNullBarCode(map);
        results.put("items", items);
        results.put("totalItems", items.size());
        if (Objects.nonNull(items) && items.size() > 0) {
            results.put("results", new PageInfo(items));
            return ResultMap.successResult(results);
        } else {
            results.put("results", null);
            return ResultMap.errorResult("没有查询到记录!");
        }

    }


    public Map<String, Object> updateBarCode(Integer siteId, Map<String, Object> body) {
        Map<String, Object> results = new HashMap<>();
        try {
            Goods goods = goodsMapper.findByGoodsIdAndBarcode(siteId, (String) body.get("bar_code"), Integer.parseInt((String) body.get("goodId")));
            if (!StringUtil.isEmpty(goods)) {
                return ResultMap.errorResult("修改商品二维码已经存在");
            }
            Integer num = this.goodsMapper.updateBarCode(siteId, Integer.parseInt((String) body.get("goodId")), (String) body.get("bar_code"));
            results.put("status", "OK");
            results.put("results", "修改成功");
            return ResultMap.successResult(results);
        } catch (Exception e) {
            logger.error("根据商品名更新商品条形码失败,错误信息" + e);
            return ResultMap.errorResult("修改商品二维码信息失败");
        }
    }

    public int updateGoodsBarCode(Map<String, Object> map) {
        return goodsMapper.updateGoodsBarCode(map);
    }

}
