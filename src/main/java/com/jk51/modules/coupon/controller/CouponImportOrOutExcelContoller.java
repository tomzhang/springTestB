package com.jk51.modules.coupon.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.coupon.requestParams.Import;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.service.GoodsService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.apache.bcel.generic.RET;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Created by Administrator on 2017/7/12.
 */
@RestController
@RequestMapping("couponExcel")
public class CouponImportOrOutExcelContoller {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private MemberMapper memberMapper;

    /**
     * @param port 创建优惠券批量导入商品
     * @return
     */
    @RequestMapping("importGoods")
    @ResponseBody
    public ReturnDto importGoodsCvs(@RequestBody Import port) {
        try {
            if (port.getSiteId() == null) return ReturnDto.buildFailedReturnDto("siteId不能为空");
            if (StringUtils.isBlank(port.getGoodsList())) return ReturnDto.buildFailedReturnDto("商品编码不能为空");

            String[] goodsCodesSplit = port.getGoodsList().split(",");

            // 不重复的商品编码集合
            Set<String> goodsCodes = Stream.of(goodsCodesSplit)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

            // 用于帮助统计cvs中商品编码重复问题
            Map<String, Integer> countForExcel = new HashMap<String, Integer>() {{
                goodsCodes.forEach(s -> put(s, 0));
            }};

            // 查询，并按照商品编码分组
            List<Map<String, Object>> goodsList = goodsMapper.getGoodsByGoodsCodesAndStatusIs1Or2(port.getSiteId(), goodsCodes);
            Map<Object, List<Map<String, Object>>> goodsListGroupedByGoodsCode = goodsList.stream()
                .collect(groupingBy(map -> map.get("goods_code")));

            // 结果集初始化
            List<Map<String, Object>> resultToShow = new ArrayList<>();
            Set<Integer> resultGoodsIds = new HashSet<>();

            // 数据分析处理
            for (int i = 0 ; i < goodsCodesSplit.length; i++) {
                String goodsCode = goodsCodesSplit[i];
                Map<String, Object> tempMap = new HashMap<>();
                tempMap.put("goods_code", goodsCode);
                resultToShow.add(tempMap);

                countForExcel.put(goodsCode, countForExcel.get(goodsCode) + 1);
                if (countForExcel.get(goodsCode).equals(1)) { // cvs文件中商编重复性校验
                    if (goodsListGroupedByGoodsCode.get(goodsCode) == null) { // 上下架商品中不存在该商编
                        tempMap.put("import_success", false);
                        tempMap.put("msg", "上下架商品中不存在该商品编码");
                    } else if (goodsListGroupedByGoodsCode.get(goodsCode).size() == 1) {
                        Map<String, Object> goods = goodsListGroupedByGoodsCode.get(goodsCode).get(0);
                        if ("1".equals(goods.get("goods_status").toString()) || "1".equals(goods.get("app_goods_status").toString())) {
                            tempMap.put("import_success", true);
                            resultGoodsIds.add((int) goods.get("goods_id"));
                        } else {
                            tempMap.put("import_success", false);
                            tempMap.put("msg", "该商品已下架");
                        }
                        tempMap.put("goods_id", goods.get("goods_id"));
                        tempMap.put("goods_status", goods.get("goods_status"));
                        tempMap.put("goods_title", goods.get("goods_title"));
                        tempMap.put("specif_cation", goods.get("specif_cation"));
                        tempMap.put("shop_price", goods.get("shop_price"));
                    } else { // 上下架商品中该商编重复
                        tempMap.put("import_success", false);
                        tempMap.put("msg", "上下架商品中该商品编码重复");
                    }
                } else {
                    tempMap.put("import_success", false);
                    tempMap.put("msg", "cvs文件中该编码出现第" + countForExcel.get(goodsCode) + "次");
                }
            }

            return ReturnDto.buildSuccessReturnDto(new HashMap<String, Object>() {{
                put("resultToShow", resultToShow);
                put("goodsIds", resultGoodsIds);
            }});
        } catch (Exception e) {
            logger.error(e.getMessage() + ", {}", e);
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
    }

    @RequestMapping("importPhoneForActivity")
    @ResponseBody
    public ReturnDto importMemberCvs(String memberPhones, String siteId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String[] phones = memberPhones.split(",");
        List<String> phoneList = Arrays.stream(phones)
            .distinct()
            .collect(toList());

        Map<String, Map<String, Object>> map = memberMapper.queryForImportByPhoneAndSiteId(phoneList, Integer.parseInt(siteId));

        List<Map<String, String>> result = Arrays.stream(phones)
            .map(phone -> {
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("phone", phone);
                logger.info("phone: {}", phone);

                Map<String, Object> tempMap = map.get(phone);
                logger.info("tempMap: {}", tempMap);
                if (tempMap != null) {
                    Integer count = ((Integer) tempMap.get("count")); // 判断重复
                    if (count == null) { // 第一次查询
                        resultMap.put("memberId", tempMap.get("memberId").toString());
                        String storeName = Optional.ofNullable(tempMap.get("storeName")).orElse("---").toString();
                        resultMap.put("storeName", storeName);
                        Object createTime_ = tempMap.get("createTime");
                        if (createTime_ == null) {
                            resultMap.put("createTime", "---");
                        } else {
                            String createTime = sdf.format((Date) createTime_);
                            resultMap.put("createTime", createTime);
                        }
                        resultMap.put("status", tempMap.get("status").toString());
                        if (resultMap.get("status").equals("0"))
                            resultMap.put("remark", "0");
                        else if (resultMap.get("status").equals("-1"))
                            resultMap.put("remark", "-1");
                        else
                            resultMap.put("remark", "-9");
                    } else { // 重复查询
                        resultMap.put("storeName", "---");
                        resultMap.put("createTime", "---");
                        resultMap.put("status", "---");
                        resultMap.put("remark", "11");
                    }
                    tempMap.put("count", count == null ? 1 : count + 1);
                } else {
                    resultMap.put("storeName", "---");
                    resultMap.put("createTime", "---");
                    resultMap.put("status", "---");
                    resultMap.put("remark", "13");
                }
                return resultMap;
            }).collect(toList());

        return ReturnDto.buildSuccessReturnDto(result);
    }


    /**
     * @param type       追加的map的数据格式（未找到或者重复）
     * @param good       编码所对应的商品
     * @param AliveGoods 数据库中商品编码存在的商品
     * @return
     */
    public Map<String, Object> returnMap(Integer type, String good, Map<String, Object> AliveGoods) {
        Map<String, Object> emptyMap = new HashMap<String, Object>();
        Map<String, Object> repeatMap = new HashMap<String, Object>();
        try {
            if (type == 1) {
                emptyMap.put("goods_id", null);
                emptyMap.put("goods_status", null);
                emptyMap.put("import_status_num", 0);
                emptyMap.put("specif_cation", null);
                emptyMap.put("shop_price", null);
                emptyMap.put("goods_title", null);
                emptyMap.put("goods_code", good);
                return emptyMap;
            }
            if (type == 2) {
                repeatMap.put("goods_id", AliveGoods.get("goods_id"));
                repeatMap.put("goods_status", AliveGoods.get("goods_status"));
                repeatMap.put("import_status_num", 2);
                repeatMap.put("specif_cation", AliveGoods.get("specif_cation"));
                repeatMap.put("shop_price", AliveGoods.get("shop_price"));
                repeatMap.put("goods_title", AliveGoods.get("goods_title"));
                repeatMap.put("goods_code", good);
                return repeatMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
