package com.jk51.modules.promotions.controller;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.Stores;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.LimitRule;
import com.jk51.model.coupon.requestParams.TemplateInfoParams;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.modules.appInterface.mapper.BStoresMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.ParsingCouponRuleService;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.request.ProCouponRuleDto;
import com.jk51.modules.promotions.request.ProRuleMaxParam;
import com.jk51.modules.promotions.request.ProRuleMessageParam;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 活动规则相关的接口
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
@RestController
@RequestMapping("promotions/rule")
public class PromotionsRuleController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PromotionsRuleMapper promotionsRuleMapper;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private BStoresMapper bStoresMapper;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;
    @Autowired
    private PromotionsRuleService promotionsRuleService;

    /* -- 创建活动规则 开始 -- */
    @PostMapping("create")
    public ReturnDto createPromotionsRule(@RequestBody PromotionsRule promotionsRule) {
        return promotionsRuleService.create(promotionsRule);
    }



    /* -- 创建活动规则 结束 -- */

    /* -- 更新活动规则 开始 -- */

    /**
     * 该接口只开放给规则发放和手动停发，其他的状态变更不该通过该接口进行
     *
     * @param siteId
     * @param ruleId
     * @param status
     * @return
     */
    @PostMapping("changeStatus/{siteId}/{ruleId}/{status}")
    public ReturnDto changeStatus(@PathVariable("siteId") Integer siteId,
                                  @PathVariable("ruleId") Integer ruleId,
                                  @PathVariable("status") Integer status) {
        if (status.equals(2)) {
            return promotionsRuleService.stopRuleAndActivity(siteId, ruleId, status);
        } else {
            return promotionsRuleService.changeStatus(siteId, ruleId, status);
        }
    }

    /**
     * 用于页面的编辑
     *
     * @param promotionsRule
     * @return
     */
    @PostMapping("edit")
    public ReturnDto edit(@RequestBody PromotionsRule promotionsRule) {
        return promotionsRuleService.edit(promotionsRule);
    }

    @PostMapping("/editPromotionsRuleOneField")
    public ReturnDto editCouponRuleOneField(@RequestParam("siteId") Integer siteId,
                                            @RequestParam("ruleId") Integer ruleId,
                                            @RequestParam("field") String field,
                                            @RequestParam("goods") String goods) {
        return promotionsRuleService.editPromotionsRuleOneField(siteId, ruleId, field, goods);
    }
    /* -- 更新活动规则 结束 -- */


    /* -- 查询活动规则 开始 -- */
    @RequestMapping(name = "优惠活动列表接口", value = "/promRuleList", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryAllPromRuleList(@RequestBody ProCouponRuleDto proCouponRuleDto) {
        PageInfo<?> pageInfo = null;
        if (null == proCouponRuleDto.getSiteId()) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        pageInfo = promotionsRuleService.promRuleList(proCouponRuleDto);

        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /* -- 查询活动规则 开始 -- */
    @RequestMapping(name = "优惠活动列表接口", value = "/queryAllcouponRuleList", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryAllcouponRuleList(@RequestBody ProCouponRuleDto proCouponRuleDto) {
        PageInfo<?> pageInfo = null;
        if (null == proCouponRuleDto.getSiteId()) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        pageInfo = promotionsRuleService.couponRuleList(proCouponRuleDto);

        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }


    @GetMapping("queryById")
    public ReturnDto queryById(Integer promotionsId, Integer siteId) {
        if (promotionsId == null) return ReturnDto.buildFailedReturnDto("promotionsId不能为空");
        if (siteId == null) return ReturnDto.buildFailedReturnDto("siteId不能为空");

        PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRuleByIdAndSiteId(siteId, promotionsId);

        if (promotionsRule == null) return ReturnDto.buildFailedReturnDto("无法根据id查询到数据");

        return ReturnDto.buildSuccessReturnDto(promotionsRule);
    }

    @RequestMapping(name = "优惠活动详情获取数据接口", value = "/promRuleDetail", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto getPromRuleDetail(@RequestBody ProCouponRuleDto proCouponRuleDto) {
        if (null == proCouponRuleDto.getSiteId()) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        if (null == proCouponRuleDto.getId()) {
            return ReturnDto.buildFailedReturnDto("id不能为空");
        }

        if (null == proCouponRuleDto.getRuleType()) {
            return ReturnDto.buildFailedReturnDto("ruleType不能为空");
        }

        try {
            if (proCouponRuleDto.getRuleType() == 1) {
                CouponRule couponRule = null;
                couponRule = couponRuleMapper.findCouponRuleById(proCouponRuleDto.getId(), proCouponRuleDto.getSiteId());
                couponRule.setCouponView(parsingCouponRuleService.accountCoupon(couponRule));
                LimitRule limitRule = null;
                if (couponRule.getLimitRule() != null) {
                    limitRule = JacksonUtils.json2pojo(couponRule.getLimitRule(), LimitRule.class);
                }
                if (limitRule.getApply_store() == 1 && !StringUtils.isBlank(limitRule.getUse_stores())) {
                    String[] strs = limitRule.getUse_stores().split(",");
                    Set<String> set = new HashSet<String>(Arrays.asList(strs));
                    couponRule.setStoreList(bStoresMapper.getBStoresListByStoreIds(proCouponRuleDto.getSiteId(), set));
                }
                if (limitRule.getApply_store() == 2 && !StringUtils.isBlank(limitRule.getUse_stores())) {
                    couponRule.setStoreList(bStoresMapper.getStoreByCityIdAndSiteId(limitRule.getUse_stores(), proCouponRuleDto.getSiteId()));
                }
                return ReturnDto.buildSuccessReturnDto(ParameterUtil.ObjectConvertJson(couponRule));
            } else if (proCouponRuleDto.getRuleType() == 2) {
                PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRule(proCouponRuleDto);
                promotionsRule.setProCouponRuleView(promotionsRuleService.promotionsRuleForType(promotionsRule.getPromotionsType(),promotionsRule.getPromotionsRule()));
                if (promotionsRule.getUseStore().equals("2")) {
                    List<String> cityIds = Arrays.asList(promotionsRule.getUseArea().split(","));
                    List<Stores> list = bStoresMapper.getStoreByCityAndSiteId(cityIds, proCouponRuleDto.getSiteId());
                    String stores = JacksonUtils.obj2json(list);
                    promotionsRule.setUseArea(stores);
                }else if(promotionsRule.getUseStore().equals("1")){
                    List<String> storeIds = Arrays.asList(promotionsRule.getUseArea().split(","));
                    List<Stores> list = bStoresMapper.getStoreByStoreIds(storeIds, promotionsRule.getSiteId());
                    String stores = JacksonUtils.obj2json(list);
                    promotionsRule.setUseArea(stores);
                }
                return ReturnDto.buildSuccessReturnDto(ParameterUtil.ObjectConvertJson(promotionsRule));
            }
            return ReturnDto.buildFailedReturnDto("数据异常");
        } catch (Exception e) {
            logger.info("获取详情异常:" + e);
            return ReturnDto.buildFailedReturnDto("获取详情异常");
        }
    }


    @RequestMapping(name = "预下单页最优惠活动获取", value = "/proRuleUsableForMax")
    @ResponseBody
    public Map<String, Object> proRuleUsableForMax(@RequestBody ProRuleMaxParam proRuleMaxParam, HttpServletRequest request) {

        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (StringUtils.isEmpty(proRuleMaxParam.getSiteId())) {
            resultMap.put("code", "101");
            resultMap.put("errMsg", "siteId不能为空");
            return resultMap;
        }
        if (StringUtils.isEmpty(proRuleMaxParam.getUserId())) {
            resultMap.put("code", "101");
            resultMap.put("errMsg", "userId不能为空");
            return resultMap;
        }
        if (StringUtils.isEmpty(proRuleMaxParam.getOrderType())) {
            resultMap.put("code", "101");
            resultMap.put("errMsg", "orderType不能为空");
            return resultMap;
        }
        if (StringUtils.isEmpty(proRuleMaxParam.getApplyChannel())) {
            resultMap.put("code", "101");
            resultMap.put("errMsg", "applychannel不能为空");
            return resultMap;
        }
        if (StringUtils.isEmpty(proRuleMaxParam.getOrderFee())) {
            resultMap.put("code", "101");
            resultMap.put("errMsg", "orderFee不能为空");
            return resultMap;
        }
        if (StringUtils.isEmpty(proRuleMaxParam.getGoodsInfo())) {
            resultMap.put("code", "101");
            resultMap.put("errMsg", "goodsInfo不能为空");
            return resultMap;
        }

        try {
            ProRuleMessageParam proRuleMessageParam = new ProRuleMessageParam(proRuleMaxParam);
            Map<String, Object> map = promotionsRuleService.proRuleUsableForMax(proRuleMessageParam);
            if (map == null) {
                resultMap.put("code", "000");
                resultMap.put("proRuleList", null);
                resultMap.put("proRuleDeductionPrice", null);
            } else {
                resultMap = map;
                resultMap.put("code", "000");
            }

            return resultMap;
        } catch (Exception e) {
            logger.info("查询最优活动列表异常");
            resultMap.put("code", "101");
            resultMap.put("errMsg", "查询最优活动列表异常");
            return resultMap;
        }


    }
    /* -- 查询活动规则 结束 -- */

    /* -- 选择活动列表 开始 -- */
    @RequestMapping(name = "选择活动列表", value = "/choosePromList", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto chooseAllPromRuleList(@RequestBody ProCouponRuleDto proCouponRuleDto) {
        PageInfo<?> pageInfo = null;
        if (null == proCouponRuleDto.getSiteId()) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        pageInfo = promotionsRuleService.choosePromList(proCouponRuleDto);

        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    @RequestMapping(name = "延长活动规则时间", value = "/prolongPromValidity")
    @ResponseBody
    public Object prolongPromValidity(@RequestBody Map<String, Object> param) {
        Map rs = new HashMap<String, Object>();
        String ruleId = (String) param.get("ruleId");
        String act_id = (String) param.get("act_id");
        Object dayNum = param.get("dayNum");
        Integer siteId = (Integer) param.get("siteId");
        if (siteId == null) {
            rs.put("code", "-1");
            rs.put("message", "siteId不能为空");
        }
        if (ruleId == null) {
            rs.put("code", "-1");
            rs.put("message", "ruleId不能为空");
        }
        if (dayNum == null) {
            rs.put("code", "-1");
            rs.put("message", "dayNum不能为空");
        }
        if (act_id == null) {
            rs.put("code", "-1");
            rs.put("message", "promotionsId不能为空");
        }
        Integer days = Integer.parseInt(dayNum.toString());
        rs = promotionsRuleService.prolongPromValidity(act_id,siteId, ruleId, days);
        promotionsRuleService.autoChangeStatus(siteId, Integer.parseInt(ruleId));
        return rs;
    }

 /*   @RequestMapping(name = "延长活动发放时间", value = "/prolongPromValidity2")
    @ResponseBody
    public Object prolongPromValidity2(@RequestBody Map<String, Object> param) {
        Map rs = new HashMap<String, Object>();
        String ruleId = (String) param.get("ruleId");
        String promotionsId = (String)param.get("act_id");
        Object dayNum = param.get("dayNum");
        Integer siteId = (Integer) param.get("siteId");
        if (siteId == null) {
            rs.put("code", "-1");
            rs.put("message", "siteId不能为空");
        }
        if (ruleId == null) {
            rs.put("code", "-1");
            rs.put("message", "ruleId不能为空");
        }
        if (promotionsId == null) {
            rs.put("code", "-1");
            rs.put("message", "promotionsId不能为空");
        }
        if (dayNum == null) {
            rs.put("code", "-1");
            rs.put("message", "dayNum不能为空");
        }
        Integer days = Integer.parseInt(dayNum.toString());
        rs = promotionsRuleService.prolongPromValidity2(siteId, ruleId, days);
        promotionsRuleService.autoChangeStatus(siteId, Integer.parseInt(ruleId));
        return rs;
    }*/

    @RequestMapping(value = "/getCouponActivity", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto getCouponActivity(@RequestBody ProCouponRuleDto proCouponRuleDto) {
        PageInfo<?> pageInfo = null;
        if (null == proCouponRuleDto.getSiteId()) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }
        pageInfo = promotionsRuleService.findCouponActivity(proCouponRuleDto);

        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /**
     * 获取发布中的优惠活动的数量和使用的商品数量
     *
     * @param params Map的key有"promotionsType", "ruleType"
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getReleasePromotionsNumAndGoodsNum", consumes = "application/json")
    public ReturnDto getReleasePromotionsNumAndGoodsNum(@RequestBody TemplateInfoParams params) {
        return promotionsRuleService.getReleasePromotionsNumAndGoodsNum(params.getParams(), params.getSiteId());
    }
}
