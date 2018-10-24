package com.jk51.modules.coupon.service;

import com.gexin.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.encode.Base64Coder;
import com.jk51.model.coupon.requestParams.CouponView;
import com.jk51.model.coupon.requestParams.GoodsRule;
import com.jk51.modules.coupon.mapper.BCouponDetailExtraLogMapper;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jk51.modules.coupon.constants.CouponConstant.COUPON_TYPE;
import static java.util.stream.Collectors.toList;

/**
 * Created by javen73 on 2018/4/12.
 */
@Service
public class CouponDetailExtraLogService {
    @Autowired
    private BCouponDetailExtraLogMapper couponDetailExtraLogMapper;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;
    @Autowired
    private PromotionsFilterService promotionsFilterService;
    @Autowired
    private CouponNoEncodingService couponNoEncodingService;

    private Logger logger = LoggerFactory.getLogger(CouponDetailExtraLogService.class);

    public ReturnDto findDetailExtraLogList(Map<String, Object> param) {
        Integer page = Integer.parseInt(param.get("page").toString());
        Integer pageSize = Integer.parseInt(param.get("pageSize").toString());
        Object start = param.get("start");
        Object end = param.get("end");
        if (Objects.nonNull(start) && Objects.nonNull(end)) {
            String start_time = String.valueOf(start) + " 00:00:00";
            String end_time = String.valueOf(end) + " 23:59:59";
            param.put("start", start_time);
            param.put("end", end_time);
        }
        PageHelper.startPage(page, pageSize);
        List<Map<String, Object>> logsList = couponDetailExtraLogMapper.findLogsList(param);
        PageInfo<Map<String, Object>> info = new PageInfo<>(logsList);
        List<Map<String, Object>> list = info.getList();
        list = processLogList(list);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("rows", list);
        //总记录数
        result.put("total", info.getTotal());
        result.put("pages", info.getPages());
        result.put("page", info.getPageNum());
        return ReturnDto.buildSuccessReturnDto(result);
    }

    private List<Map<String, Object>> processLogList(List<Map<String, Object>> list) {
        String regex = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern pattern = Pattern.compile(regex);
        return list.stream().map(map -> {
            String goods_rule = (String) map.get("goods_rule");
            GoodsRule goodsRule = JSON.parseObject(goods_rule, GoodsRule.class);
            Integer coupon_type = (Integer) map.get("coupon_type");
            String denomination = "--";
            denomination = getDenomination(goodsRule, coupon_type, denomination);
            map.put("denomination", denomination);
            return map;
        }).map(map -> {
            String goods_rule = (String) map.get("goods_rule");
            Integer coupon_type = (Integer) map.get("coupon_type");
            String type = COUPON_TYPE.get(coupon_type);
            map.put("coupon_type", type);
            GoodsRule goodsRule = JSON.parseObject(goods_rule, GoodsRule.class);
            CouponView couponView = parsingCouponRuleService.getGoodsRule(coupon_type, goodsRule);
            String ruleDetail = couponView.getRuleDetail();
            map.put("goods_rule", ruleDetail);
            return map;
        }).map(map -> {
            Object nick = map.get("buyer_nick");
            if (nick == null || "".equals(nick.toString().trim())) {
                return map;
            }
            Matcher matcher = pattern.matcher(nick.toString());
            if (matcher.matches()) {
                //是Base64
                try {
                    map.put("buyer_nick", Base64Coder.decode(nick.toString()));
                } catch (UnsupportedEncodingException e) {
                    logger.error("Base64 Nick Name 解码失败:{}", e);
                }
            }
            return map;
        }).map(map -> {
            Date create_time = (Date) map.get("create_time");
            String date = DateUtils.formatDate(create_time, "yyyy-MM-dd HH:mm:ss");
            map.put("create_time", date);
            return map;
        }).map(map->{
            String coupon_no = String.valueOf(map.get("coupon_no"));
            String couponNo = couponNoEncodingService.encryptionCouponNo(coupon_no);
            map.put("coupon_no",couponNo);
            String operation_code = String.valueOf(map.get("operation_code"));
            String code = operation_code.split("_")[1];
            map.put("operation_code",code);
            return map;
        }).collect(toList());
    }

    private String getDenomination(GoodsRule goodsRule, Integer coupon_type, String denomination) {
        if (coupon_type == 100) {
            if (goodsRule.getRule_type() == 4) {
                Map<String, Integer> rule_4 = (Map<String, Integer>) goodsRule.getRule();
                denomination = promotionsFilterService.save2Digit(rule_4.get("direct_money") / 100.00d);
            } else if (goodsRule.getRule_type() == 1) {
                List<Map<String, Integer>> rule_1 = (List<Map<String, Integer>>) goodsRule.getRule();
                if (rule_1.size() == 1) {
                    Map<String, Integer> ruleSigle = rule_1.get(0);
                    denomination = promotionsFilterService.save2Digit(ruleSigle.get("reduce_price") / 100.00d);
                }
            }
        }
        return denomination;
    }
}
