package com.jk51.modules.goods.service;


import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.*;
import com.jk51.model.coupon.requestParams.Import;
import com.jk51.model.goods.BIntegralGoods;
import com.jk51.model.goods.GoodsUpdateInfo;
import com.jk51.model.goods.PageData;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.GiftRule;
import com.jk51.model.promotions.rule.SelectGiftByGoodsIdParms;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.esn.service.GoodsEsService;
import com.jk51.modules.goods.event.GoodsEvent;
import com.jk51.modules.goods.library.BatchResult;
import com.jk51.modules.goods.library.GoodsProviders;
import com.jk51.modules.goods.library.GoodsStatusConv;
import com.jk51.modules.goods.mapper.*;
import com.jk51.modules.goods.request.BatchJoinIntegralGoods;
import com.jk51.modules.goods.request.GoodsData;
import com.jk51.modules.merchant.mapper.MerchantExtMapper;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.request.ProCouponRuleDto;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class GoodsService {
    private static final Logger logger = LoggerFactory.getLogger(GoodsService.class);
    @Autowired
    private GoodsMapper goodsMapper;


    @Autowired
    private GoodsExtdMapper goodsExtdMapper;

    @Autowired
    private GoodsmMapper goodsmMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BarndMapper barndMapper;

    @Autowired
    private BIntegralGoodsMapper integralGoodsMapper;

    @Autowired
    private YbConfigGoodsSyncMapper ybConfigGoodsSyncMapper;

    @Autowired
    private PromotionsRuleMapper promotionsRuleMapper;

    @Autowired
    private PromotionsRuleService promotionsRuleService;

    @Autowired
    private PromotionsActivityMapper promotionsActivityMapper;
    @Autowired
    private MerchantExtMapper merchantExtMapper;

    @Transactional
    public void updateGoods(String id, String name) {

        goodsMapper.update(id, name);

        throw new RuntimeException("测试回滚");

    }

    public List<Goods> find(Map<String, Object> param) {
        return goodsMapper.find(param);
    }

    public List<Map<String, Object>> getByApprovalNumber(Map<String, Object> param) {
        return goodsMapper.getByApprovalNumber(param);
    }

    @Transactional
    public int insertOne(GoodsData data) throws Exception {
        GoodsProviders providers = new GoodsProviders(data);
        Goods goods = providers.buildGoods();
        GoodsExtdWithBLOBs goodsExtdWithBLOBs = providers.buildGoodsExtd();
        BIntegralGoods integralGoods = providers.buildBIntegralGoods();

        if (StringUtil.isNotEmpty(data.getBarndName())) {
            int barndId = getBarndId(data.getBarndName(), data.getSiteId());
            goods.setBarndId(barndId);
        }

        int insertNum = goodsMapper.add(goods);

        if (goods.getDetailTpl() == 150) {
            // 向b_images_attr中添加数据
            if (goods.getImgHash() != null) {
                String imgString = goods.getImgHash().toString();
                String[] imgs = imgString.split(",");
                Map<Integer, Object> imgMap = new HashMap<>();
                Map<String, Object> imgMainMap = new HashMap<String, Object>();
                imgMainMap.put("siteId", goods.getSiteId());
                imgMainMap.put("hash", imgs[0]);
                imgMainMap.put("width", 1);
                imgMainMap.put("height", 1);
                imgMainMap.put("size", goods.getSize());
                imgMainMap.put("type", 10);
                imgMainMap.put("host_id", "a");
                imgMainMap.put("flag", 0);
                imgMainMap.put("goodsId", goods.getGoodsId());
                imgMainMap.put("isDefault", 1);
                goodsmMapper.insert(imgMainMap);
                for (int i = 1; i < imgs.length; i++) {
                    imgMap.put(i, imgs[i]);
                }
                if (imgMap.size() != 0) {
                    Map<String, Object> imgParmMap = new HashMap<String, Object>();
                    imgParmMap.put("siteId", goods.getSiteId());
                    imgParmMap.put("hash", imgMap);
                    imgParmMap.put("width", 1);
                    imgParmMap.put("height", 1);
                    imgParmMap.put("size", goods.getSize());
                    imgParmMap.put("type", 10);
                    imgParmMap.put("host_id", "a");
                    imgParmMap.put("flag", 0);
                    imgParmMap.put("goodsId", goods.getGoodsId());
                    imgParmMap.put("isDefault", 0);
                    goodsmMapper.insertGoodsImgBatch(imgParmMap, imgMap);
                }
            }
        }
        if (insertNum == 0) {
            // 什么情况下会发生不抛异常且没有写入成功】
            throw new RuntimeException("SAVE_FAIL");
        }
        // 通过goods_id关联
        goodsExtdWithBLOBs.setGoodsId(goods.getGoodsId());

        int goodsExtdInsertNum = goodsExtdMapper.add(goodsExtdWithBLOBs);
        if (goodsExtdInsertNum == 0) {
            throw new RuntimeException("SAVE_FAIL");
        }

        //插入积分商品表
        if (integralGoods != null && GoodsProviders.INTEGRAL_GOODS_NOT_DELETE == integralGoods.getIsDel()) {

            integralGoods.setGoodsId(goods.getGoodsId());

            int integralGoodsInsertNum = integralGoodsMapper.insertIntegralGoods(integralGoods);

            if (0 == integralGoodsInsertNum) {
                throw new RuntimeException("INTTEGRAL_GOODS_SAVE_FAIL");
            }

        }

        return goods.getGoodsId().intValue();
    }

    public Integer addIntegralGoods(BIntegralGoods integralGoods) {
        return integralGoodsMapper.insertIntegralGoods(integralGoods);
    }

    public Integer updateByGoodsId(BIntegralGoods integralGoods) {
        return integralGoodsMapper.updateByGoodsId(integralGoods);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean updateOne(GoodsData data) throws Exception {
        GoodsProviders providers = new GoodsProviders(data);
        Goods goods = providers.buildGoods();
        GoodsExtdWithBLOBs goodsExtdWithBLOBs = providers.buildGoodsExtd();
        if (StringUtil.isNotEmpty(data.getBarndName())) {
            int barndId = getBarndId(data.getBarndName(), data.getSiteId());
            goods.setBarndId(barndId);
        }
        //如果类型是礼品，还需要更新b_images_attr表
        try {
            if (Objects.nonNull(data.getDetailTpl()))
            if (data.getDetailTpl() == 150) {
                //接下来插入新的数据
                Map<String, Object> map = new HashMap<>();

                map.put("siteId", goods.getSiteId());
                map.put("goodsId", goods.getGoodsId());
                goodsMapper.updateImg(map);
                if (goods.getImgHash() != null) {
                    String imgString = goods.getImgHash().toString();
                    String[] imgs = imgString.split(",");
                    String[] sizes = goods.getPicSize().split(",");
                    Map<String, Object> imgMainMap = new HashMap<String, Object>();
                    imgMainMap.put("siteId", goods.getSiteId());
                    imgMainMap.put("hash", imgs[0]);
                    imgMainMap.put("width", 1);
                    imgMainMap.put("height", 1);
                    imgMainMap.put("picSize", sizes[0]);
                    imgMainMap.put("type", 10);
                    imgMainMap.put("host_id", "a");
                    imgMainMap.put("flag", 0);
                    imgMainMap.put("goodsId", goods.getGoodsId());
                    imgMainMap.put("isDefault", 1);
                    goodsmMapper.insert2(imgMainMap);
                    for (int i = 1; i < imgs.length; i++) {
                        Map<String, Object> imgParmMap = new HashMap<String, Object>();
                        imgParmMap.put("siteId", goods.getSiteId());
                        imgParmMap.put("hash", imgs[i]);
                        imgParmMap.put("width", 1);
                        imgParmMap.put("height", 1);
                        imgParmMap.put("picSize", sizes[i]);
                        imgParmMap.put("type", 10);
                        imgParmMap.put("host_id", "a");
                        imgParmMap.put("flag", 0);
                        imgParmMap.put("goodsId", goods.getGoodsId());
                        imgParmMap.put("isDefault", 0);
                        goodsmMapper.insert2(imgParmMap);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isUpdated = goodsMapper.updateByGoodsId(goods);

        BIntegralGoods integralGoods = providers.buildBIntegralGoods();

        //插入积分商品表
        if (null != integralGoods) {

            BIntegralGoods existIntegralGoods = integralGoodsMapper.getBIntegralGoodsByGoodsId(integralGoods.getSiteId(), integralGoods.getGoodsId());

            if (null != existIntegralGoods) {

                int integralGoodsUpdateNum;

                if (GoodsProviders.INTEGRAL_GOODS_DELETE == integralGoods.getIsDel()) {
                    integralGoodsUpdateNum = integralGoodsMapper.deleteIntegralGoods(existIntegralGoods.getId(), integralGoods.getIsDel());
                } else if (GoodsProviders.INTEGRAL_GOODS_NOT_DELETE == integralGoods.getIsDel()) {
                    integralGoodsUpdateNum = integralGoodsMapper.updateByGoodsId(integralGoods);
                } else {
                    integralGoodsUpdateNum = 0;
                }

                if (0 == integralGoodsUpdateNum) {
                    throw new RuntimeException("INTTEGRAL_GOODS_UPDATE_FAIL");
                }

            } else {

                if (GoodsProviders.INTEGRAL_GOODS_NOT_DELETE == integralGoods.getIsDel()) {
                    int integralGoodsInsertNum = integralGoodsMapper.insertIntegralGoods(integralGoods);

                    if (0 == integralGoodsInsertNum) {
                        throw new RuntimeException("INTTEGRAL_GOODS_SAVE_FAIL");
                    }
                }

            }

        }

        // 更新扩展表
        return goodsExtdMapper.updateByGoodsId(goodsExtdWithBLOBs);

    }

    @Transactional
    public int create(GoodsData data) throws Exception {
        int goodsId = insertOne(data);

        // 商品同步
        GoodsProviders providers = new GoodsProviders(data);
        Goods goods = providers.buildGoods();
        GoodsEvent goodsEvent = new GoodsEvent(this, goods);
        //applicationContext.publishEvent(goodsEvent);
        updateYbGoods(data, goodsEvent);

        return goodsId;
    }


    @Transactional
    public Map<String, Object> joinIntegralGoods(BatchJoinIntegralGoods integralGoods) {
        Map<String, Object> param = new HashMap<String, Object>();
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> mapList = new ArrayList<>();
        int[] goods_ids = integralGoods.getGoodsIdList();
        int siteId = integralGoods.getSiteId();
        int failNum = 0;
        int successNum = 0;
        param.put("siteId", siteId);
        param.put("hasImage", 1);
        for (Integer goodsId : goods_ids) {
            param.put("goodsId", goodsId);
            Map<String, String> goods = goodsMapper.getGoodsD(param);
            Map<String, Object> record = new HashMap<>();
            //int exChange=/integralGoods.getParities();
            if (null != goods) {
                Object price = goods.get("shopPrice");
                int exChange = 0;
                if (integralGoods.getJoinType() == 20) {
                    if (Integer.valueOf(price.toString()) != 0) {
                        price = Math.ceil(Double.parseDouble(price.toString()));
                        double integral = (double) price * integralGoods.getParities() / 100;
                        exChange = (int) Math.ceil(integral);
                    }
                } else {
                    exChange = integralGoods.getParities();
                }

                BIntegralGoods existIntegralGoods = integralGoodsMapper.getBIntegralGoodsByGoodsId(integralGoods.getSiteId(), goodsId);
                BIntegralGoods bIntegralGoods = new BIntegralGoods();
                bIntegralGoods.setGoodsId(goodsId);
                bIntegralGoods.setSiteId(siteId);
                bIntegralGoods.setIntegralExchanges(exChange);
                bIntegralGoods.setStartTime(integralGoods.getStartTime());
                bIntegralGoods.setEndTime(integralGoods.getEndTime());
                bIntegralGoods.setLimitEach(integralGoods.getLimitEach());
                if (null != existIntegralGoods) {
                    bIntegralGoods.setIsDel(1);//取消删除状态
                    int ss = integralGoodsMapper.updateByGoodsId(bIntegralGoods);
                    successNum++;
                } else {
                    int integralGoodsInsertNum = integralGoodsMapper.insertIntegralGoods(bIntegralGoods);
                    if (0 == integralGoodsInsertNum) {
                        throw new RuntimeException("INTTEGRAL_GOODS_SAVE_FAIL");
                    }
                    successNum++;
                }
            } else {
                Goods goods1 = goodsMapper.getBySiteIdAndGoodsId(goodsId, siteId);
                failNum++;
                record.put("goodsName", goods1.getDrugName());
                record.put("reason", "未添加图片或未设置主图");
                mapList.add(record);
            }
        }
        result.put("recode", mapList);
        result.put("failNum", failNum);
        result.put("successNum", successNum);
        return result;
    }


    /**
     * 没什么卵用的方法
     *
     * @param goodsDatas
     * @param siteId
     * @return
     * @throws Exception
     * @deprecated
     */
    @Transactional
    public BatchResult create(GoodsData[] goodsDatas, int siteId) throws Exception {
        int successNum = 0;
        int failNum = 0;
        BatchResult batchResult = new BatchResult();

        for (GoodsData goodsData : goodsDatas) {
            goodsData.setSiteId(siteId);
            // 单条插入 返回失败数和成功数
            try {
                int goodsId = create(goodsData);
                successNum++;
            } catch (Exception e) {
                batchResult.addError("approval_number", goodsData.getApprovalNumber(), "添加失败");
                failNum++;
            }
        }

        batchResult.setSuccessNum(successNum);
        batchResult.setFailNum(failNum);

        return batchResult;
    }

    /**
     * 获取品牌
     *
     * @param barndName
     * @param siteId
     * @return
     */
    public int getBarndId(String barndName, int siteId) {
        // 处理品牌
        if (StringUtil.isNotEmpty(barndName)) {
            Barnd barnd = barndMapper.findByName(barndName, siteId);
            if (barnd == null) {
                barnd = new Barnd();
                barnd.setSiteId(siteId);
                barnd.setBarndName(barndName);
                barndMapper.insert(barnd);
            }

            return barnd.getBarndId();
        }

        return 0;
    }

    @Transactional
    public boolean updateGoods(GoodsData data) throws Exception {
        boolean isUpdated = updateOne(data);

        // 商品同步
        GoodsProviders providers = new GoodsProviders(data);
        Goods goods = providers.buildGoods();
        GoodsEvent goodsEvent = new GoodsEvent(this, goods);
        updateYbGoods(data, goodsEvent);

        //applicationContext.publishEvent(goodsEvent);


        return isUpdated;
    }

    @Transactional
    public void updateGoodsOnFail(GoodsData goodsData) throws Exception {
        boolean isUpdate = updateGoods(goodsData);
        if (!isUpdate) {
            throw new Exception("更新失败");
        }
    }

    //51商品同步
    public void updateYbGoods(GoodsData goodsData, GoodsEvent goodsEvent) {
        Integer goodsId = goodsData.getGoodsId();
        Integer siteId = goodsData.getSiteId();
        GoodsUpdateInfo goodsBefore = goodsMapper.queryBGoodsBeforeUpdate(goodsId, siteId);
        Integer detailTpl = goodsBefore.getDetailTpl();
        YbConfigGoodsSync ybConfigGoodsSync = ybConfigGoodsSyncMapper.findByDetailTplFirst(detailTpl);
        List<GoodsUpdateInfo> ybGoods;

        String[] rule = ybConfigGoodsSync.getFields().split(",");
        Map<String, Object> beforeMap = new HashMap();
        Map<String, Object> ybMap = new HashMap();

        if (goodsBefore.getYbGoodsId() == 0) {
            logger.info("51库不存在该商品");
            if (ybConfigGoodsSync.getAllowAdd()) {
                logger.info("模板允许新增");
                ybGoods = goodsMapper.queryYbGoods(null, goodsBefore.getApprovalNumber(), goodsBefore.getBarCode());
                if (ybGoods.size() == 0) {
                    applicationContext.publishEvent(goodsEvent);

                } else {
                    if (!ybConfigGoodsSync.getAllowUpdate()) {
                        logger.info("模板不允许更新");
                        return;
                    }
                    for (int j = 0; j < ybGoods.size(); j++) {
                        if (ybGoods.get(j).getUpdateStatus() == 0) {
                            logger.info("商品被锁定，无法更新");
                            continue;
                        }
                        // 判断允许更新字段不一致
                        try {
                            beforeMap = JacksonUtils.json2map(JacksonUtils.obj2json(goodsBefore));
                            ybMap = JacksonUtils.json2map(JacksonUtils.obj2json(ybGoods.get(j)));
                            for (int i = 0; i < rule.length; i++) {
                                if (beforeMap.containsKey(rule[i])) {
                                    if (!beforeMap.get(rule[i]).equals(ybMap.get(rule[i]))) {
                                        if ((beforeMap.get(rule[i]).equals("") || beforeMap.get(rule[i]) == null) && (ybMap.get(rule[i]).equals("") || ybMap.get(rule[i]) == null) || (beforeMap.get(rule[i]).equals(0) && ybMap.get(rule[i]).equals(9999))) {
                                            continue;
                                        } else {
                                            applicationContext.publishEvent(goodsEvent);
                                            break;
                                        }
                                    }
                                }
                            }
                            logger.info("允许更新字段未更新");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

            } else {
                logger.info("模板不允许新增");
                if (!ybConfigGoodsSync.getAllowUpdate()) {
                    logger.info("模板不允许更新");
                    return;
                }
                ybGoods = goodsMapper.queryYbGoods(null, goodsBefore.getApprovalNumber(), goodsBefore.getBarCode());
                for (int j = 0; j < ybGoods.size(); j++) {
                    if (ybGoods.get(j).getUpdateStatus() == 0) {
                        logger.info("商品被锁定，无法更新");
                        continue;
                    }
                    // 判断允许更新字段不一致
                    try {
                        beforeMap = JacksonUtils.json2map(JacksonUtils.obj2json(goodsBefore));
                        ybMap = JacksonUtils.json2map(JacksonUtils.obj2json(ybGoods.get(j)));
                        for (int i = 0; i < rule.length; i++) {
                            if (beforeMap.containsKey(rule[i])) {
                                if (!beforeMap.get(rule[i]).equals(ybMap.get(rule[i]))) {
                                    if ((beforeMap.get(rule[i]).equals("") || beforeMap.get(rule[i]) == null) && (ybMap.get(rule[i]).equals("") || ybMap.get(rule[i]) == null) || (beforeMap.get(rule[i]).equals(0) && ybMap.get(rule[i]).equals(9999))) {
                                        continue;
                                    } else {
                                        applicationContext.publishEvent(goodsEvent);
                                        break;
                                    }
                                }
                            }
                        }
                        logger.info("允许更新字段未更新");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
        } else {
            logger.info("51库存在该商品，goods_id为{}", goodsBefore.getYbGoodsId());
            ybGoods = goodsMapper.queryYbGoods(goodsBefore.getYbGoodsId(), null, null);
            if (!ybConfigGoodsSync.getAllowUpdate()) {
                logger.info("模板不允许更新");
                return;
            }
            for (int j = 0; j < ybGoods.size(); j++) {
                if (ybGoods.get(j).getUpdateStatus() == 0) {
                    logger.info("商品被锁定，无法更新");
                    continue;
                }
                // 判断允许更新字段不一致
                try {
                    beforeMap = JacksonUtils.json2map(JacksonUtils.obj2json(goodsBefore));
                    ybMap = JacksonUtils.json2map(JacksonUtils.obj2json(ybGoods.get(j)));
                    for (int i = 0; i < rule.length; i++) {
                        if (beforeMap.containsKey(rule[i])) {
                            if (!beforeMap.get(rule[i]).equals(ybMap.get(rule[i]))) {
                                if ((beforeMap.get(rule[i]).equals("") || beforeMap.get(rule[i]) == null) && (ybMap.get(rule[i]).equals("") || ybMap.get(rule[i]) == null) || (beforeMap.get(rule[i]).equals(0) && ybMap.get(rule[i]).equals(9999))) {
                                    continue;
                                } else {
                                    applicationContext.publishEvent(goodsEvent);
                                    break;
                                }
                            }
                        }
                    }
                    logger.info("允许更新字段未更新");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }
    }

    @Transactional
    public BatchResult updateGoods(GoodsData[] goodsDatas, int siteId) throws Exception {
        int successNum = 0;
        int failNum = 0;
        BatchResult batchResult = new BatchResult();

        for (GoodsData goodsData : goodsDatas) {
            goodsData.setSiteId(siteId);
            // 单条插入 返回失败数和成功数
            try {
                boolean isUpdated = updateGoods(goodsData);
                successNum += isUpdated ? 1 : 0;
//                goods_ids.add(goods_id);
            } catch (Exception e) {
                batchResult.addError("approval_number", goodsData.getApprovalNumber(), "更新失败");
                failNum++;
            }
        }

        batchResult.setSuccessNum(successNum);
        batchResult.setFailNum(failNum);

        return batchResult;
    }

    private BatchResult batchChangeStatus(Function<Integer, Boolean> function, int[] goods_ids,int siteId) {
        int successNum = 0;
        int failNum = 0;
        BatchResult batchResult = new BatchResult();
        /*AtomicInteger ai = new AtomicInteger();
        List<Map> errorLists = Arrays.stream(goods_ids).parallel().mapToObj(goodsId -> {
            Map<String, String> errorMap = new HashMap();
            try {
                boolean isUpdated = function.apply(goodsId);
                if (! isUpdated) {
                    // 记录失败原因
                    batchResult.addError(goodsId, GoodsStatusConv.getLastErrorMessage());
                    errorMap.put("goodsId", String.valueOf(goodsId));
                    errorMap.put("reason", GoodsStatusConv.getLastErrorMessage());
                } else {
                    ai.incrementAndGet();
                }
            } catch (Exception e) {
                logger.debug("批量操作商品状态错误 {}", e.getMessage());
                errorMap.put("goodsId", String.valueOf(goodsId));
                errorMap.put("reason", "系统发生异常");
            }

            return errorMap;
        }).collect(Collectors.toList());*/

        for (int goodsId : goods_ids) {
            try {
                boolean isUpdated = function.apply(goodsId);
                if (!isUpdated) {
                    // 记录失败原因
                    batchResult.addError(goodsId, GoodsStatusConv.getLastErrorMessage());
                    failNum++;
                }
                successNum += isUpdated ? 1 : 0;
            } catch (Exception e) {
                e.printStackTrace();
                batchResult.addError(goodsId, "系统发生异常");
                failNum++;
            }
        }
        batchResult.setSuccessNum(successNum);
        batchResult.setFailNum(failNum);
//        redisTemplate.delete("appChecked" + "_" + siteId);
//        redisTemplate.delete("appCheckedValue" + "_" + siteId);
//        redisTemplate.delete("onLineShopChecked" + "_" + siteId);
//        redisTemplate.delete("onLineShopCheckedValue" + "_" + siteId);
//        redisTemplate.delete("status" + "_" + siteId);

        return batchResult;
    }
    //------添加------start
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GoodsEsService goodsEsService;
    //------添加------end
    @Transactional
    private boolean changeStatus(int goodsId, int siteId, int goodsStatus,Map<String,Object> map) {
        Map<String, Object> param = new HashMap();
        param.put("goodsId", goodsId);
        param.put("siteId", siteId);
        //------添加参数------start
        String status= null;
        String appChecked= null;
        String appCheckedValue= null;
        String onLineShopChecked= null;
        String onLineShopCheckedValue= null;
        if(Objects.nonNull(map.get("status"))){
            status = map.get("status").toString();
        }
        if(Objects.nonNull(map.get("appChecked"))){
            appChecked = map.get("appChecked").toString();
        }
        if(Objects.nonNull(map.get("appCheckedValue"))){
            appCheckedValue = map.get("appCheckedValue").toString();
        }
        if(Objects.nonNull(map.get("onLineShopChecked"))){
            onLineShopChecked = map.get("onLineShopChecked").toString();
        }
        if(Objects.nonNull(map.get("onLineShopCheckedValue"))){
            onLineShopCheckedValue = map.get("onLineShopCheckedValue").toString();
        }

        Map<String, Object> para = new HashMap<>();
        para.put("siteId", siteId);
        para.put("goodsId", goodsId);
        para.put("status", status == null ? null : status);
        para.put("goodsStatus", goodsStatus);
        //------添加参数------end

//        Map<String, String> goods = goodsMapper.getGoodsD(param);
        Map<String, Object> goods = goodsMapper.getGoodsD2(param);
        if (Objects.isNull(goods)){
            GoodsStatusConv.setLastErrorMessage("未找到该商品");
            return false;
        }
//        List<Map<String, Object>> goodsList = goodsMapper.getGoodsD2(param);
        /*// 检查上架条件是否满足
        if (1 == goodsStatus){
            if (!GoodsStatusConv.allowChangeStatusToValue(goods, goodsStatus, onLineShopChecked, status)) {
                //app上架条件，若都满足，则更新app渠道上架状态，否则，不更新
                if (StringUtils.isNotBlank(appChecked) && (Objects.nonNull(goods.get("goodsCode")) && StringUtils.isNotBlank(goods.get("goodsCode").toString())) && (Objects.nonNull(goods.get("drugName")) && StringUtils.isNotBlank(goods.get("drugName").toString()))
                    && (Objects.nonNull(goods.get("goodsTitle")) && StringUtils.isNotBlank(goods.get("goodsTitle").toString())) && (Objects.nonNull(goods.get("specifCation")) && StringUtils.isNotBlank(goods.get("specifCation").toString()))
                    && (Objects.nonNull(goods.get("goodsCompany")) && StringUtils.isNotBlank(goods.get("goodsCompany").toString()))
                    && (Objects.nonNull(goods.get("shopPrice")) && 0 < Integer.parseInt(goods.get("shopPrice").toString()))){
                    Object appGoodsStatusObj = goods.get("appGoodsStatus");
                    if (Objects.nonNull(appChecked) && "1".equals(appChecked) && Objects.nonNull(appGoodsStatusObj) && 1 != Integer.parseInt(appGoodsStatusObj.toString())){
                        //满足，则进行app商品上架更新
                        para.put("appChecked", appChecked);
                        para.put("appCheckedValue", appCheckedValue);
                        Integer flag = goodsMapper.updateGoodsBySiteIdAndAppGoodsStatus(para);
                        if (0 < flag){
                            //更新商品索引操作
                            updateIndex(siteId);
                        }
                    }else {
                        GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":app该商品已上架，不能重复上架");
                        return false;
                    }
                }
                GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":" + GoodsStatusConv.getLastErrorMessage());
                return false;
            }
            // 检查上架条件是否满足(是否有图片)
            *//*if (goodsStatus == GoodsStatusConv.STATUS_LISTING && !("2".equals(status) && !("1".equals(appChecked)))) {
                Map goodsImgParam = new HashMap();
                goodsImgParam.put("siteId", siteId);
                goodsImgParam.put("goodsId", goodsId);
                if (StringUtil.isEmpty(goodsMapper.getGoodsImg(goodsImgParam))) {
                    GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":商品没有图片不能上架");
                    return false;
                }
            }*//*
            // 检查上架条件是否满足(是否有已经有其他商品编码已经上架)
            *//*if (goodsStatus == GoodsStatusConv.STATUS_LISTING) {
                Goods goodsqi=goodsMapper.queryByGoodsCodeSYS(goods.get("goodsCode")+"", siteId+"");
                if (!StringUtil.isEmpty(goodsqi)) {
                    Integer appGoodsStatus = goodsqi.getAppGoodsStatus();
                    Integer goodsStatus1 = goodsqi.getGoodsStatus();
                    if ((Objects.nonNull(appGoodsStatus) && goodsStatus == appGoodsStatus && Objects.nonNull(appChecked) && Integer.parseInt(appChecked.toString()) == appGoodsStatus)
                        && (Objects.nonNull(goodsStatus1) && goodsStatus1 == goodsStatus && Objects.nonNull(onLineShopChecked) && Integer.parseInt(onLineShopChecked.toString()) == goodsStatus1)){
                        GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":同一商品编码不能重复上架");
                        return false;
                    }
                }
            }*//*
        }*/
        para.put("appC", StringUtil.isEmpty(appChecked) ? null : Integer.parseInt(appChecked));
        para.put("appCV", StringUtil.isEmpty(appCheckedValue) ? null : Integer.parseInt(appCheckedValue));
        para.put("onLineSC",StringUtil.isEmpty(onLineShopChecked) ? null : Integer.parseInt(onLineShopChecked));
        para.put("onLineSCV",StringUtil.isEmpty(onLineShopCheckedValue) ? null : Integer.parseInt(onLineShopCheckedValue));
        //批量操作
        //app商品上架，若appGoodsStatus已是上架状态，则不可再次上架 appChecked = 1
        //线上商城商品上架，若goodsStatus已是上架状态，则不可再次上架
        //单个操作
        // status = 1：表线上商城渠道；status = 2：表app渠道
        Object appGoodsStatusObj = goods.get("appGoodsStatus");
        Object goodsStatusObj = goods.get("goodsStatus");
        //批量上下架
        if (1 == goodsStatus){ //即将上架状态
            //即将上架状态时，批量上架，该商品当前状态appGoodsStatus和goodsStatus是否也均为上架状态，若有某个不为上架状态或都不为上架状态，则进行对应的更新；否则，不可更新
            if (Objects.nonNull(appChecked) && "1".equals(appChecked) && Objects.nonNull(onLineShopChecked) && "1".equals(onLineShopChecked) && Objects.isNull(status)){//两渠道均选择了
                if (Objects.nonNull(appGoodsStatusObj) && 1 == Integer.parseInt(appGoodsStatusObj.toString()) && Objects.nonNull(goodsStatusObj) && 1 == Integer.parseInt(goodsStatusObj.toString())){
                    GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":app、线上商城，该商品均已上架，不可再次上架");
                    return false;
                }else if (Objects.nonNull(appGoodsStatusObj) && 1 == Integer.parseInt(appGoodsStatusObj.toString())
                    && ((Objects.nonNull(goodsStatusObj) && 1 != Integer.parseInt(goodsStatusObj.toString())) || Objects.isNull(goodsStatusObj))){
                    Boolean codeFlag = judgeGoodsCode(goods);
                    Boolean nameFlag = judgeGoodsName(goods);
                    Boolean titleFlag = judgeGoodsTitle(goods);
                    Boolean companyFlag = judgeGoodsCompany(goods);
                    Boolean specifCationFlag = judgeGoodsSpecifCation(goods);
                    Boolean priceFlag = judgeGoodsShopPrice(goods);
                    Boolean isDefaultFlag = judgeGoodsIsDefault(goods);
                    if (codeFlag && titleFlag && nameFlag && companyFlag && specifCationFlag && priceFlag){
                        if (isDefaultFlag){
                            para.put("appC", null);
                            para.put("appCV", null);
                            goodsMapper.changeStatus(para);
                            GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":app该商品已上架，不可再次上架");
                            return false;
                        }else {
                            GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":app该商品已上架，不可再次上架；且线上商城主图为空，不可上架");
                            return false;
                        }
                    }else {
                        return false;
                    }
                }else if (Objects.nonNull(goodsStatusObj) && 1 == Integer.parseInt(goodsStatusObj.toString())
                    && ((Objects.nonNull(appGoodsStatusObj) && 1 != Integer.parseInt(appGoodsStatusObj.toString()) || Objects.isNull(appGoodsStatusObj)))){
                    Boolean codeFlag = judgeGoodsCode(goods);
                    Boolean nameFlag = judgeGoodsName(goods);
                    Boolean titleFlag = judgeGoodsTitle(goods);
                    Boolean companyFlag = judgeGoodsCompany(goods);
                    Boolean specifCationFlag = judgeGoodsSpecifCation(goods);
                    Boolean priceFlag = judgeGoodsShopPrice(goods);
                    Boolean isDefaultFlag = judgeGoodsIsDefault(goods);
                    if (codeFlag && titleFlag && nameFlag && companyFlag && specifCationFlag && priceFlag && isDefaultFlag){
                        para.put("onLineSC", null);
                        para.put("onLineSCV", null);
                        goodsMapper.changeStatus(para);
                        GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":线上商城，该商品均已上架，不可再次上架");
                        return false;
                    }else {
                        return false;
                    }
                }else{//否则，表明该商品原本的状态均是下架（两渠道都是），即将更改为上架状态
                    Boolean codeFlag = judgeGoodsCode(goods);
                    Boolean nameFlag = judgeGoodsName(goods);
                    Boolean titleFlag = judgeGoodsTitle(goods);
                    Boolean companyFlag = judgeGoodsCompany(goods);
                    Boolean specifCationFlag = judgeGoodsSpecifCation(goods);
                    Boolean priceFlag = judgeGoodsShopPrice(goods);
                    Boolean isDefaultFlag = judgeGoodsIsDefault(goods);
                    if (codeFlag && titleFlag && nameFlag && companyFlag && specifCationFlag && priceFlag){
                        if (isDefaultFlag){
                            return goodsMapper.changeStatus(para);
                        }else {
                            para.put("onLineSC", null);
                            para.put("onLineSCV", null);
                            goodsMapper.changeStatus(para);
                            GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":该商品主图为空或无效，故线上商城该商品不可上架");
                            return false;
                        }
                    }else {
                        return false;
                    }
                }
            }else if (Objects.nonNull(appChecked) && "1".equals(appChecked) && Objects.isNull(onLineShopChecked)){//只选择了APP上架渠道，只对app渠道做相应操作
                if (Objects.nonNull(appGoodsStatusObj) && 1 == Integer.parseInt(appGoodsStatusObj.toString())){
                    GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":app该商品已上架，不可再次上架");
                    return false;
                }else {
                    Boolean codeFlag = judgeGoodsCode(goods);
                    Boolean nameFlag = judgeGoodsName(goods);
                    Boolean titleFlag = judgeGoodsTitle(goods);
                    Boolean companyFlag = judgeGoodsCompany(goods);
                    Boolean specifCationFlag = judgeGoodsSpecifCation(goods);
                    Boolean priceFlag = judgeGoodsShopPrice(goods);
                    if (codeFlag && titleFlag && nameFlag && companyFlag && specifCationFlag && priceFlag){
                        return goodsMapper.changeStatus(para);
                    }else {
                        return false;
                    }
                }
            }else if (Objects.nonNull(onLineShopChecked) && "1".equals(onLineShopChecked) && Objects.isNull(appChecked)){//只选择了线上商城渠道，只对线上商城渠道做相应的操作
                if (Objects.nonNull(goodsStatusObj) && 1 == Integer.parseInt(goodsStatusObj.toString())){
                    GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":线上商城，该商品均已上架，不可再次上架");
                    return false;
                }else {
                    Boolean codeFlag = judgeGoodsCode(goods);
                    Boolean nameFlag = judgeGoodsName(goods);
                    Boolean titleFlag = judgeGoodsTitle(goods);
                    Boolean companyFlag = judgeGoodsCompany(goods);
                    Boolean specifCationFlag = judgeGoodsSpecifCation(goods);
                    Boolean priceFlag = judgeGoodsShopPrice(goods);
                    Boolean isDefaultFlag = judgeGoodsIsDefault(goods);
                    if (codeFlag && titleFlag && nameFlag && companyFlag && specifCationFlag && priceFlag && isDefaultFlag){
                        return goodsMapper.changeStatus(para);
                    }else {
                        return false;
                    }
                }
            }
            //即将上架状态时，单个上架，即一次只能操作一个渠道，status：1 线上商城、2 app
            if (Objects.nonNull(status) && "1".equals(status)){
                if (Objects.nonNull(goodsStatusObj) && 1 == Integer.parseInt(goodsStatusObj.toString())){
                    GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":线上商城，该商品均已上架，不可再次上架");
                    return false;
                }else {
                    Boolean codeFlag = judgeGoodsCode(goods);
                    Boolean nameFlag = judgeGoodsName(goods);
                    Boolean titleFlag = judgeGoodsTitle(goods);
                    Boolean companyFlag = judgeGoodsCompany(goods);
                    Boolean specifCationFlag = judgeGoodsSpecifCation(goods);
                    Boolean priceFlag = judgeGoodsShopPrice(goods);
                    Boolean isDefaultFlag = judgeGoodsIsDefault(goods);
                    if (codeFlag && titleFlag && nameFlag && companyFlag && specifCationFlag && priceFlag && isDefaultFlag){
                        return goodsMapper.changeStatus(para);
                    }else {
                        return false;
                    }
                }
            }else if (Objects.nonNull(status) && "2".equals(status)){
                if (Objects.nonNull(appGoodsStatusObj) && 1 == Integer.parseInt(appGoodsStatusObj.toString())){
                    GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":app该商品已上架，不可再次上架");
                    return false;
                }else {
                    Boolean codeFlag = judgeGoodsCode(goods);
                    Boolean nameFlag = judgeGoodsName(goods);
                    Boolean titleFlag = judgeGoodsTitle(goods);
                    Boolean companyFlag = judgeGoodsCompany(goods);
                    Boolean specifCationFlag = judgeGoodsSpecifCation(goods);
                    Boolean priceFlag = judgeGoodsShopPrice(goods);
                    if (codeFlag && titleFlag && nameFlag && companyFlag && specifCationFlag && priceFlag){
                        return goodsMapper.changeStatus(para);
                    }else {
                        return false;
                    }
                }
            }
        }else if (2 == goodsStatus){//即将下架状态
            //即将下架状态时，批量下架，该商品当前状态appGoodsStatus和goodsStatus是否也均为下架状态，若有某个不为下架状态或都不为下架状态，则进行对应的更新；否则，不可更新
            if (Objects.nonNull(appChecked) && "2".equals(appChecked) && Objects.nonNull(onLineShopChecked) && "2".equals(onLineShopChecked)){//两渠道均选择下架
                if (Objects.nonNull(appGoodsStatusObj) && 2 == Integer.parseInt(appGoodsStatusObj.toString()) && Objects.nonNull(goodsStatusObj) && 2 ==Integer.parseInt(goodsStatusObj.toString())){
                    GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":app、线上商城，该商品均已下架，不可再次下架");
                    return false;
                }else if (Objects.nonNull(appGoodsStatusObj) && 2 == Integer.parseInt(appGoodsStatusObj.toString())
                    && ((Objects.nonNull(goodsStatusObj) && 2 != Integer.parseInt(goodsStatusObj.toString())) || Objects.isNull(goodsStatusObj))){
                    goodsMapper.changeStatus(para);
                    GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":app该商品已下架，不可再次下架");
                    return false;
                }else if (Objects.nonNull(goodsStatusObj) && 2 == Integer.parseInt(goodsStatusObj.toString())
                    && ((Objects.nonNull(appGoodsStatusObj) && 2 != Integer.parseInt(appGoodsStatusObj.toString())) || Objects.isNull(appGoodsStatusObj))){
                    goodsMapper.changeStatus(para);
                    GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":线上商城，该商品已下架，不可再次下架");
                    return false;
                }else {
                    return goodsMapper.changeStatus(para);
                }
            }else if (Objects.nonNull(appChecked) && "2".equals(appChecked) && Objects.isNull(onLineShopChecked)){//只选择了app下架渠道，只针对app渠道做相应操作
                if (Objects.nonNull(appGoodsStatusObj) && 2 == Integer.parseInt(appGoodsStatusObj.toString())){
                    GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":app该商品已下架，不可再次下架");
                    return false;
                }else {
                    return goodsMapper.changeStatus(para);
                }
            }else if (Objects.nonNull(onLineShopChecked) && "2".equals(onLineShopChecked) && Objects.isNull(appChecked)){//只选择了线上商城下架渠道，只针对线上商城渠道做相应的操作
                if (Objects.nonNull(goodsStatusObj) && 2 == Integer.parseInt(goodsStatusObj.toString())){
                    GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":线上商城，该商品已下架，不可再次下架");
                    return false;
                }else {
                    return goodsMapper.changeStatus(para);
                }
            }
            //即将下架状态时，单个下架，即一次只能操作一个渠道，status：1 线上商城、2 app
            if (Objects.nonNull(status) && "1".equals(status)){
                if (Objects.nonNull(goodsStatusObj) && 2 == Integer.parseInt(goodsStatusObj.toString())){
                    GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":线上商城，该商品已下架，不可再次下架");
                    return false;
                }else {
                    return goodsMapper.changeStatus(para);
                }
            }else if (Objects.nonNull(status) && "2".equals(status)){
                if (Objects.nonNull(appGoodsStatusObj) && 2 == Integer.parseInt(appGoodsStatusObj.toString())){
                    GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":app该商品已下架，不可再次下架");
                    return false;
                }else {
                    return goodsMapper.changeStatus(para);
                }
            }
        }
        ///-------------------------------------------------------------------------------------------------------------------------------------
        return goodsMapper.changeStatus(para);
//        return goodsMapper.changeStatus(goodsId, siteId, goodsStatus);
    }

    private void updateIndex(int siteId) {
        //更新商品索引操作
        Integer hasErpPrice = goodsMapper.getHasErpPriceOfMerchantExtBySiteId(siteId);
        if (Objects.nonNull(hasErpPrice)){
            goodsEsService.batchUpdateGoods(String.valueOf(siteId), String.valueOf(hasErpPrice));
            goodsEsService.updateSuggestByBrandId(String.valueOf(siteId));
        }
    }

    //判断商品编码是否为空
    private Boolean judgeGoodsCode(Map<String, Object> goods){
        Object goodsCodeObj = goods.get("goodsCode");
        if (Objects.isNull(goodsCodeObj) || StringUtils.isBlank(goodsCodeObj.toString())){
            GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":商品编码为空不能上架");
            return false;
        }
        return true;
    }
    //判断商品名称是否为空
    private Boolean judgeGoodsName(Map<String, Object> goods){
        Object drugNameObj = goods.get("drugName");
        if (Objects.isNull(drugNameObj) || StringUtils.isBlank(drugNameObj.toString())){
            GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":商品名称为空不能上架");
            return false;
        }
        return true;
    }
    //判断商品标题是否为空
    private Boolean judgeGoodsTitle(Map<String, Object> goods){
        Object goodsTitleObj = goods.get("goodsTitle");
        if (Objects.isNull(goodsTitleObj) || StringUtils.isBlank(goodsTitleObj.toString())){
            GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":商品标题为空不能上架");
            return false;
        }
        return true;
    }
    //判断商品规格是否为空
    private Boolean judgeGoodsSpecifCation(Map<String, Object> goods){
        Object specifCationObj = goods.get("specifCation");
        if (Objects.isNull(specifCationObj) || StringUtils.isBlank(specifCationObj.toString())){
            GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":商品规格为空不能上架");
            return false;
        }
        return true;
    }
    //判断商品生成厂家是否为空
    private Boolean judgeGoodsCompany(Map<String, Object> goods){
        Object goodsCompanyObj = goods.get("goodsCompany");
        if (Objects.isNull(goodsCompanyObj) || StringUtils.isBlank(goodsCompanyObj.toString())){
            GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":商品生成厂家为空不能上架");
            return false;
        }
        return true;
    }
    //判断商品现价是否为空
    private Boolean judgeGoodsShopPrice(Map<String, Object> goods){
        Object shopPriceObj = goods.get("shopPrice");
        if (Objects.isNull(shopPriceObj) || 0 >= Integer.parseInt(shopPriceObj.toString())){
            GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":商品现价为空或小于等于0不能上架");
            return false;
        }
        return true;
    }
    //判断商品主图是否为空
    private Boolean judgeGoodsIsDefault(Map<String, Object> goods){
        Object isDefaultObj = goods.get("isDefault");
        Object flagObj = goods.get("flag");
        if (Objects.isNull(isDefaultObj) || StringUtils.isBlank(isDefaultObj.toString()) || (Objects.isNull(flagObj) || (Objects.nonNull(flagObj) && 1 == Integer.parseInt(flagObj.toString())))){
            GoodsStatusConv.setLastErrorMessage(goods.get("goodsTitle") + ":商品主图为空或无效，故线上商城该商品不能上架");
            return false;
        }
        return true;
    }
    public Map<String, String> getGoodsD(Map<String, Object> param) {

        return goodsMapper.getGoodsD(param);

    }

    public boolean delete(int goodsId, int siteId, Map<String, Object> param) {
        return changeStatus(goodsId, siteId, GoodsStatusConv.STATUS_SOFTDEL,param);
    }

    /**
     * 删除商品 失败会抛出异常
     */
    public void deleteOnFail(int goodsId, int siteId, Map<String, Object> param) throws Exception {
        boolean isDelete = delete(goodsId, siteId, param);
        if (!isDelete) {
            throw new Exception(GoodsStatusConv.getLastErrorMessage());
        }

        if (goodsId > 0) {
            //软删除 0  默认1
            integralGoodsMapper.deleteIntegralGoods(Long.valueOf(goodsId), 0);
        }
    }

    public BatchResult delete(int[] goodsIds, int siteId) {
        return batchChangeStatus((goods_id) -> delete(goods_id, siteId, null), goodsIds, siteId);
    }

    public boolean listing(int goodsId, int siteId,Map<String,Object> map) {
        return changeStatus(goodsId, siteId, GoodsStatusConv.STATUS_LISTING,map);
    }

    public boolean listingOnFail(int goodsId, int siteId) throws Exception {
        boolean isChange = listing(goodsId, siteId,null);
        if (!isChange) {
            throw new Exception(GoodsStatusConv.getLastErrorMessage());
        }

        return isChange;
    }

    public BatchResult listing(int[] goodsIds, int site_id,Map<String,Object> map) {
        return batchChangeStatus((goods_id) -> listing(goods_id, site_id,map), goodsIds, site_id);
    }

    public String join(int[] goodsIds, int site_id) {
        return "";
    }

    public boolean delisting(int goodsId, int siteId,Map<String,Object> map) {
        return changeStatus(goodsId, siteId, GoodsStatusConv.STATUS_DELISTING,map);
    }

    public boolean delistingOnFail(int goodsId, int siteId) throws Exception {
        boolean isChange = delisting(goodsId, siteId,null);
        if (!isChange) {
            throw new Exception(GoodsStatusConv.getLastErrorMessage());
        }

        return isChange;
    }

    public BatchResult delisting(int[] goodsIds, int siteId,Map<String,Object> map) {
        return batchChangeStatus((goodsId) -> delisting(goodsId, siteId,map), goodsIds, siteId);
    }


    public List<String> getGoodsCode(int siteId, List<Integer> goodsIds) {
        return goodsMapper.getGoodsCode(siteId, goodsIds);
    }

    public Map<String, Object> queryByBarCodeSYS(String bar_code, String site_id) {

        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtil.isEmpty(bar_code)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "bar_code 为空");
            return result;
        }

        Map<String, String> goods = goodsMapper.queryByBarCodeSYS(bar_code, site_id);
        result.put("goods", goods);
        if (StringUtil.isEmpty(goods)) {
            result.put("status", "OK");
            return result;
        } else {
            result.put("status", "have");
            return result;
        }
    }

    public Map<String, Object> queryByBarCode(String bar_code, String site_id, String goods_id) {

        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtil.isEmpty(bar_code)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "bar_code 为空");
            return result;
        }

        Goods goods = goodsMapper.queryByBarCode(bar_code, site_id, goods_id);
        if (StringUtil.isEmpty(goods)) {
            result.put("status", "OK");
            return result;
        } else {
            result.put("status", "have");
            return result;
        }
    }

    public Map<String, Object> queryByGoodsCode(String goods_code, String site_id) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (StringUtil.isEmpty(goods_code)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "goods_code 为空");
            return result;
        }

        Goods goods = goodsMapper.queryByGoodsCode(goods_code, site_id);
        if (StringUtil.isEmpty(goods)) {
            result.put("status", "OK");
            return result;
        } else {
            result.put("status", "have");
            return result;
        }
    }

    /**
     * 根据id查询需要的商品字段
     *
     * @param siteId
     * @param goodsIds 用逗号分隔的商品ids
     * @return
     */
    public ReturnDto queryGoodsInfoByIds(Integer siteId, String goodsIds) {
        List<Map<String, Object>> goodsList = null;
        try {
            String[] split = goodsIds.split(",");
            List<Integer> idList = new ArrayList<>();
            for (String s : split) {
                idList.add(Integer.parseInt(s));
            }

            goodsList = goodsMapper.queryGoodsInfoByIds(siteId, idList);
        } catch (Exception e) {
            logger.error("查询商品信息服务器异常", e);
            return ReturnDto.buildFailedReturnDto("查询商品信息失败");
        }

        return ReturnDto.buildSuccessReturnDto(goodsList);
    }

    /**
     * 根据id查询需要的商品字段
     *
     * @param siteId
     * @param goodsIds 用逗号分隔的商品ids
     * @return
     */
    public ReturnDto queryGoodsInfoByIds2(Integer page, Integer pageSize, Integer siteId, String goodsIds) {
        Map<String, Object> result = new HashMap<>();
        PageHelper.startPage(page, pageSize);
        List<Map<String, Object>> goodsList = null;
        List<Map<String, Object>> returnList = null;
        try {
            String[] split = goodsIds.split(",");
            List<Integer> idList = new ArrayList<>();
            for (String s : split) {
                idList.add(Integer.parseInt(s));
            }
            goodsList = goodsMapper.queryGoodsInfoByIds2(siteId, idList);
            PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(goodsList);
            result.put("list", goodsList);
            result.put("total", pageInfo.getTotal());
            result.put("pages", pageInfo.getPages());
        } catch (Exception e) {
            logger.error("查询商品信息服务器异常", e);
            return ReturnDto.buildFailedReturnDto("查询商品信息失败");
        }

        return ReturnDto.buildSuccessReturnDto(result);
    }

    /**
     * 商家更改商品会员价格
     *
     * @return
     */
    public Map<String, Object> updateGoodsDisPrice(@RequestBody Map<String, Object> params) {
        Map<String, Object> objectMap = new HashMap<>();
        logger.info("更新商品价格接口参数{}", params.toString());
        if (StringUtil.isEmpty(params.get("siteId"))) {
            logger.info("Method{}，商家站点为空", "updateGoodsPrice");
            objectMap.put("code", -1);
            objectMap.put("msg", "通讯异常，请稍后重试");
        } else if (StringUtil.isEmpty(params.get("goodlist"))) {
            logger.info("Method{},商品信息为空", "updateGoodsPrice");
            objectMap.put("code", -1);
            objectMap.put("msg", "需要修改的商品信息不能为空");
        } else {
            Integer siteId = Integer.parseInt(params.get("siteId").toString());
            try {
                List goodsList = JSONArray.parseArray(JacksonUtils.obj2json(params.get("goodlist")), Map.class);
                Integer has_erp_price = merchantExtMapper.selectErpGoodsPriceQu(siteId);
                if ((!StringUtil.isEmpty(has_erp_price)) && (has_erp_price == 0)) {
                    objectMap.put("code", -1);
                    objectMap.put("msg", "商户未开通erp价格同步");
                    return objectMap;
                }
                Map<String, Object> requestparams = new HashMap<>();
                requestparams.put("siteId", siteId);
                requestparams.put("goodsList", goodsList);
                int result = goodsMapper.updatedisPrice(requestparams);
                if (result == goodsList.size()) {
                    objectMap.put("code", 0);
                    objectMap.put("msg", "更改商品会员价格成功");
                } else {
                    objectMap.put("code", 0);
                    objectMap.put("msg", "部分商品更改会员价格成功");
                }
            } catch (Exception e) {
                logger.info("类型转换异常{}", e);
                objectMap.put("code", -1);
                objectMap.put("msg", "商品信息异常");
            }
        }
        return objectMap;
    }

    public List<String> getGoodCodeBySiteId(Integer siteId) {
        List<String> codeList = goodsMapper.getGoodCodeBySiteId(siteId, null);
        logger.info("该商户{}下商品编码{}", siteId, codeList.toString());
        return codeList;
    }

    public Integer batchDeleteGoods(Map<String, Object> param) {
        String[] goodId = param.get("goods_ids").toString().split(",");
        List goodsId = new ArrayList();
        for (int i = 0; i < goodId.length; i++) {
            goodsId.add(goodId[i]);
        }
        param.put("goodsId", goodId);
        return goodsMapper.batchDeleteGoods(param);
    }

    public String updateGoodByPrice(Map param) {
        String siteId = param.get("siteId").toString();
        String goodId = param.get("goodId").toString();
        Integer price = Integer.parseInt(param.get("price").toString());
        Integer marketprice = Integer.parseInt(param.get("marketPrice").toString());
        int i = 0;
        if (price > marketprice) {
            i = goodsMapper.updateGoodByPrice(siteId, goodId, price);
        } else {
            i = goodsMapper.updateGoodByPriceOther(siteId, goodId, price);
        }

        if (i != 0)
            return "200";
        return "500";
    }

    public List<Map> getRecommendGoodsByIds(Integer siteId, List<Integer> goodIds) {
        return goodsMapper.getRecommendGoodsByIds(siteId, goodIds);
    }

    public PageInfo<?> proActivityList(GoodsParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<PageData> list = goodsMapper.getGiftList(param);
        return new PageInfo<>(list);

    }

    /**
     * 优惠券根据活动ID查询所有选择的赠品（不是满赠活动返回NULL）
     *
     * @return
     */
    public Map<String, Object> selectGiftByGoodsIdParms(SelectGiftByGoodsIdParms selectGiftByGoodsIdParms) {
        logger.info("rMap-------------selectGiftByGoodsIdParms-----{}", ParameterUtil.ObjectConvertJson(selectGiftByGoodsIdParms));
        Map<String, Object> resultMap = new HashMap<>();
        ProCouponRuleDto proCouponRuleDto = new ProCouponRuleDto();
        Integer siteId = selectGiftByGoodsIdParms.getSiteId();
        proCouponRuleDto.setId(selectGiftByGoodsIdParms.getId());
        PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRule(proCouponRuleDto);
        try {
            if (promotionsRule.getPromotionsType() == 10) {
                List<Map<String, Object>> list = new ArrayList<>();
                GiftRule giftRule = JSON.parseObject(promotionsRule.getPromotionsRule(), GiftRule.class);
                if (giftRule.getSendType() == 3) {
                    List<GiftRule.sendGifts> sendGifts = giftRule.getSendGifts();
                    logger.info("rMap-------------sendGifts-----{}", ParameterUtil.ObjectConvertJson(sendGifts));
                    for (int i = 0; i < sendGifts.size(); i++) {
                        Integer giftId = sendGifts.get(i).getGiftId();
                        logger.info("rMap-------------giftId-----{}", giftId);
                        Map<String, Object> rMap = goodsMapper.getGiftById(siteId, giftId);
                        if (rMap == null || rMap.size() <= 0) {
                            continue;
                        }
                        logger.info("rMap--------------rMap----{}", ParameterUtil.ObjectConvertJson(rMap));
                        Integer goodsId = rMap.get("goods_id") == null ? 0 : Integer.parseInt(rMap.get("goods_id").toString());
                        Map<String, Object> imgMap = goodsmMapper.selectImgById(siteId, goodsId);
                        rMap.put("sendNum", sendGifts.get(i).getSendNum());
                        if (imgMap != null) {
                            rMap.put("hash", imgMap.get("hash"));
                        } else {
                            rMap.put("hash", "");
                        }
                        list.add(rMap);
                    }
                } else if (giftRule.getSendType() == 2) {
                    list = getGiftListForeSendTypeSecond(giftRule, selectGiftByGoodsIdParms);
                }

                resultMap.put("giftList", list);
                resultMap.put("proCouponRuleView", promotionsRuleService.getProCouponRuleViewForProRule(promotionsRule.getSiteId(), promotionsRule.getId()));
                resultMap.put("maxSenNum", maxSenNumFroBeforeOrder(giftRule, selectGiftByGoodsIdParms.getGoodsInfo()));
                return resultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Map<String, Object>> getGiftListForeSendTypeSecond(GiftRule giftRule,
                                                                    SelectGiftByGoodsIdParms selectGiftByGoodsIdParms) {
        List<Map<String, Object>> listGift = new ArrayList<Map<String, Object>>();

        JSONArray objects = JSON.parseArray(selectGiftByGoodsIdParms.getGoodsInfo());
        List<Map> goodsInfoList = JSONArray.parseArray(objects.toJSONString(), Map.class);

        for (int i = 0; i < goodsInfoList.size(); i++) {
            Map map = goodsInfoList.get(i);
            String thegoodsId = map.get("goodsId").toString();
            if (giftRule.getCalculateBase() == 1) {
                HashSet<String> set = new HashSet<String>(Arrays.asList(giftRule.getGoodsIds().split(",")));
                if (set.contains(thegoodsId)) {

                    if (giftRule.getRuleType() == 1) {
                        if (!giftRule.getRuleConditions().stream().anyMatch(cond -> cond.getMeetNum() <= Integer.parseInt(map.get("num").toString())))
                            continue;
                    }

                    if (giftRule.getRuleType() == 2) {
                        if (!giftRule.getRuleConditions().stream().anyMatch(cond -> cond.getMeetMoney() <= Integer.parseInt(map.get("num").toString()) * Integer.parseInt(map.get("goodsPrice").toString())))
                            continue;
                    }

                    Map<String, Object> rMap = goodsMapper.getGiftById(selectGiftByGoodsIdParms.getSiteId(), Integer.parseInt(thegoodsId));
                    if (rMap == null || rMap.size() <= 0) {
                        continue;
                    }
                    logger.info("rMap--------------rMap----{}", ParameterUtil.ObjectConvertJson(rMap));
                    Integer goodsId = rMap.get("goods_id") == null ? 0 : Integer.parseInt(rMap.get("goods_id").toString());
                    Map<String, Object> imgMap = goodsmMapper.selectImgById(selectGiftByGoodsIdParms.getSiteId(), goodsId);
                    rMap.put("sendNum", giftRule.getSendGifts().stream().filter(sendGifts -> goodsId.equals(sendGifts.getGiftId())).collect(Collectors.toList()).get(0).getSendNum());
                    if (imgMap != null) {
                        rMap.put("hash", imgMap.get("hash"));
                    } else {
                        rMap.put("hash", "");
                    }
                    listGift.add(rMap);
                }
            }

        }


        return listGift;
    }


    private Integer maxSenNumFroBeforeOrder(GiftRule giftRule, String goodsInfo) {
        JSONArray objects = JSON.parseArray(goodsInfo);
        List<Map> goodsInfoList = JSONArray.parseArray(objects.toJSONString(), Map.class);
        List<Integer> sendNumList = new ArrayList<Integer>();
        sendNumList.add(0);
        if (giftRule.getRuleType() == 1) {

            if (giftRule.getCalculateBase() == 1)
                return Collections.max(giftRule.getRuleConditions()
                    .stream()
                    .filter(ruleCondition -> goodsInfoList.stream()
                        .filter(goodsMap -> new HashSet<String>(Arrays.asList(giftRule.getGoodsIds().split(",")))
                            .contains(goodsMap.get("goodsId").toString()))
                        .anyMatch(map -> Integer.parseInt(map.get("num").toString()) >= ruleCondition.getMeetNum()))
                    .map(map -> map.getSendNum())
                    .collect(Collectors.toList()));

            if (giftRule.getCalculateBase() == 2)
                return Collections.max(giftRule.getRuleConditions()
                    .stream()
                    .filter(ruleCondition ->
                        goodsInfoList.stream()
                            .filter(goodsMap -> new HashSet<String>(Arrays.asList(giftRule.getGoodsIds().split(",")))
                                .contains(goodsMap.get("goodsId").toString()))
                            .mapToInt(goodsMap -> Integer.parseInt(goodsMap.get("num").toString())).sum() >= ruleCondition.getMeetNum())
                    .map(map -> map.getSendNum())
                    .collect(Collectors.toList()));

        } else if (giftRule.getRuleType() == 2) {

            if (giftRule.getCalculateBase() == 1)
                return Collections.max(giftRule.getRuleConditions()
                    .stream()
                    .filter(ruleCondition -> goodsInfoList.stream()
                        .filter(goodsMap -> new HashSet<String>(Arrays.asList(giftRule.getGoodsIds().split(",")))
                            .contains(goodsMap.get("goodsId").toString()))
                        .anyMatch(map -> Integer.parseInt(map.get("num").toString()) * Integer.parseInt(map.get("goodsPrice").toString()) >= ruleCondition.getMeetMoney()))
                    .map(map -> map.getSendNum())
                    .collect(Collectors.toList()));


            if (giftRule.getCalculateBase() == 2)
                return Collections.max(giftRule.getRuleConditions()
                    .stream()
                    .filter(ruleCondition ->
                        goodsInfoList.stream()
                            .filter(goodsMap -> new HashSet<String>(Arrays.asList(giftRule.getGoodsIds().split(",")))
                                .contains(goodsMap.get("goodsId").toString()))
                            .mapToInt(goodsMap -> Integer.parseInt(goodsMap.get("num").toString())
                                * Integer.parseInt(goodsMap.get("goodsPrice").toString())).sum()
                            >= ruleCondition.getMeetMoney())
                    .map(map -> map.getSendNum())
                    .collect(Collectors.toList()));


        }
        return Collections.max(sendNumList);

    }

    public boolean checkFlag(String flag) {
        boolean check = false;
        if ("1".equals(flag)) {
            check = true;
        } else {
            check = false;
        }
        return check;
    }

    public Optional<Goods> queryById(Integer siteId, Integer goodsId) {
        Goods goods = goodsMapper.getBySiteIdAndGoodsId(goodsId, siteId);
        return Optional.ofNullable(goods);
    }

    @Transactional
    public Result addDefaultImage(Integer siteId, String goodsCode) throws Exception {
        if (siteId==null || StringUtils.isBlank(goodsCode)) return Result.fail("缺少必要参数：?siteId=100xxx&goodsCode=100xxx");
        Map<String, Object> image = goodsMapper.getDefaultImgByGoodsCode(siteId, goodsCode);
        if (MapUtils.isEmpty(image) || image.get("hash") == null || image.get("size") == null) return Result.fail("商品无符合主图信息");
        int total = goodsMapper.addDefaultImage(siteId, String.valueOf(image.get("hash")), Integer.parseInt(String.valueOf(image.get("size"))));
        return Result.success(new HashMap(){{
            put("影响商品个数", total);
        }});
    }
}
