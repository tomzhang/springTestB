package com.jk51.modules.coupon.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.Stores;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.*;
import com.jk51.modules.appInterface.mapper.BStoresMapper;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.service.CouponActivityService;
import com.jk51.modules.coupon.service.CouponProcessService;
import com.jk51.modules.coupon.service.CouponRuleService;
import com.jk51.modules.coupon.service.ParsingCouponRuleService;
import com.jk51.modules.goods.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: zw
 * 创建日期: 2017-03-06
 * 修改记录:
 */
@RestController
@RequestMapping("couponRule")
public class CouponRuleController {

    private static final Logger logger = LoggerFactory.getLogger(CouponRuleController.class);

    @Autowired
    private CouponRuleService couponRuleService;
    @Autowired
    private CouponProcessService couponProcessService;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private ParsingCouponRuleService service;
    @Autowired
    private CouponActivityService couponActivityService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;
    @Autowired
    private BStoresMapper bStoresMapper;


    @RequestMapping("/couponToolList")
    @ResponseBody
    public ReturnDto couponToolList(Integer siteId,@RequestParam(defaultValue = "1") Integer page
                                            ,@RequestParam(defaultValue = "10") Integer pageSize){
        Map<String, Object> result = couponRuleService.CouponCanUseToolList(siteId, page, pageSize);
        if(result ==null){
            return ReturnDto.buildFailedReturnDto("查询优惠券工具列表失败");
        }
        return ReturnDto.buildSuccessReturnDto(result);
    }



    @ResponseBody
    @PostMapping(value = "/createCouponRule", consumes = "application/json")
    public ReturnDto createCouponRule(@RequestBody BasisParams basisParams) {
        return couponRuleService.createCouponRule(basisParams);
    }

    /**
     * 创建可发放的优惠券，返回主键
     *
     * @param basisParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/createReleasedCouponRuleReturnKey", consumes = "application/json")
    public ReturnDto createReleasedCouponRuleReturnKey(@RequestBody BasisParams basisParams) {
        return couponRuleService.createReleasedCouponRuleReturnKey(basisParams);
    }

    /**
     * 获取发布的优惠券的数量和参加的商品数量
     *
     * @param params Map的key有"couponType", "ruleType"
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/getActiveCouponNumAndGoodsNum", consumes = "application/json")
    public ReturnDto getActiveCouponNumAndGoodsNum(@RequestBody TemplateInfoParams params) {
        return couponRuleService.getReleaseCouponNumAndGoodsNum(params.getParams(), params.getSiteId());
    }

    /**
     * 编辑优惠券规则
     *
     * @param basisParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/editCouponRule", consumes = "application/json")
    public ReturnDto editCouponRule(@RequestBody BasisParams basisParams) {
        return couponRuleService.editCouponRule(basisParams);
    }

    /**
     * 编辑优惠券规则返回ID
     *
     * @param basisParams
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/editCouponRuleAndRelease", consumes = "application/json")
    public ReturnDto editCouponRuleAndRelease(@RequestBody BasisParams basisParams) {
        ReturnDto returnDto = couponRuleService.editCouponRuleAndRelease(basisParams);
        return returnDto;
    }


    @PostMapping("/editCouponRuleOneField")
    public ReturnDto editCouponRuleOneField(@RequestParam("siteId") Integer siteId,
                                            @RequestParam("ruleId") Integer ruleId,
                                            @RequestParam("field") String field,
                                            @RequestParam("goods") String goods) {
        return couponRuleService.editCouponRuleOneField(siteId, ruleId, field, goods);
    }

    @ResponseBody
    @PostMapping(value = "/couponDownDetail", consumes = "application/json")
    public ReturnDto couponDownDetail(@RequestBody BasisParams basisParams) {
        return couponRuleService.couponDownDetail(basisParams);
    }

    @ResponseBody
    @PostMapping(value = "/updateCouponRule", consumes = "application/json")
    public ReturnDto updateCouponRule(@RequestBody BasisParams basisParams) {
        return couponRuleService.updateCouponRule(basisParams);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ReturnDto findCouponRuleList(Integer siteId) {
        if (siteId == null) {
            logger.error("siteId：[{}]不能为空", siteId);
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }

        return ReturnDto.buildSuccessReturnDto(couponRuleMapper.findCouponRuleBySiteId(siteId));
    }


    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryCouponRule(HttpServletRequest request,
                                     @RequestParam(value = "page", defaultValue = "1", required = true) Integer page,
                                     @RequestParam(value = "pageSize", defaultValue = "15", required = true) Integer pageSize) throws Exception {

        return queryCouponRule(request, pageSize, page, false);
    }

    @GetMapping("queryUsedNumAndUnusedNum")
    @ResponseBody
    public ReturnDto queryUsedNumAndUnusedNum(String ids, Integer siteId) {
        if (ids == null || ids.length() == 0) {
            return ReturnDto.buildFailedReturnDto("id不能为空");
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
            List<Map<String, Integer>> result = couponDetailMapper.useAmountBySiteIdAndRuleIdForRuleList(siteId, idList);
            return ReturnDto.buildSuccessReturnDto(result);
        } catch (Exception e) {
            logger.error("数据错误导致的查询失败");
            return ReturnDto.buildFailedReturnDto("数据错误导致的查询失败");
        }
    }

    @RequestMapping(value = "/sendcouponquery", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto querysendcouponquery(HttpServletRequest request,
                                          @RequestParam(value = "page", defaultValue = "1") Integer page,
                                          @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize) {

        return queryCouponRule(request, pageSize, page, true);
    }

    /**
     * 根据一系列条件分页查询优惠券规则表
     *
     * @param request
     * @param pageSize
     * @param page
     * @param checkCouponSendable 是否检查优惠券已发完
     * @return
     */
    private ReturnDto queryCouponRule(HttpServletRequest request, Integer pageSize, Integer page, Boolean checkCouponSendable) {
        String ruleName = request.getParameter("ruleName");
        String siteId = request.getParameter("siteId");
        String ruleId = request.getParameter("ruleId");
        String couponType = request.getParameter("couponType");// 100现金券 200打折券 300现价券 400包邮券
        String status = request.getParameter("status");// 0正常  1结束 2终止
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTtime");

        if (!StringUtils.isNotBlank(siteId))
            return ReturnDto.buildFailedReturnDto("siteId为空");

        Map<String, Object> params = new HashMap<>();
        params.put("ruleName", ruleName);
        params.put("siteId", siteId);
        params.put("couponType", couponType);
        params.put("status", status);
        params.put("ruleId", ruleId);
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        if (checkCouponSendable)
            params.put("checkCouponSendable", 1);

        PageHelper.startPage(page, pageSize);

        List<CouponRule> list = couponRuleMapper.queryCouponRule(params);

        list.stream().forEach(item -> item.setCouponView(service.accountCoupon(item.getAimAt(),
            item.getCouponType(), item.getOrderRule(), item.getGoodsRule())));

        PageInfo<?> pageInfo = new PageInfo<>(list);

        Map<String, Object> map = new HashMap<>();
        map.put("items", pageInfo.getList());
        map.put("page", pageInfo.getPageNum());
        map.put("pageSize", pageInfo.getPageSize());
        map.put("total", pageInfo.getTotal());

        return ReturnDto.buildSuccessReturnDto(map);
    }

    @ResponseBody
    @PostMapping(value = "/useCouponAccountRule", consumes = "application/json")
    public ReturnDto useCouponRule(OrderMessageParams orderMessageParams) {
        return couponProcessService.accountCoupon(orderMessageParams);
    }


    @RequestMapping(value = "/getCouponRuleDetail/{siteId}/{ruleId}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnDto getCouponRuleDetail(@PathVariable("siteId") Integer siteId,
                                         @PathVariable("ruleId") Integer ruleId) throws Exception {
        CouponRule couponRule;
        try {
            couponRule = couponRuleMapper.findCouponRuleById(ruleId, siteId);
            couponRule.setCouponView(parsingCouponRuleService.accountCoupon(ruleId, siteId));
            LimitRule limitRule = null;
            if (couponRule.getLimitRule() != null) {
                limitRule = JacksonUtils.json2pojo(couponRule.getLimitRule(), LimitRule.class);
            }
            if (limitRule.getApply_store() == 1 && !StringUtils.isBlank(limitRule.getUse_stores())) {
                String[] strs = limitRule.getUse_stores().split(",");
                Set<String> set = new HashSet<String>(Arrays.asList(strs));
                couponRule.setStoreList(bStoresMapper.getBStoresListByStoreIds(siteId, set));
            }
            if (limitRule.getApply_store() == 2 && !StringUtils.isBlank(limitRule.getUse_stores())) {
                List<String> cityIds = Arrays.asList(limitRule.getUse_stores().split(","));
                List<Stores> storesList = bStoresMapper.getStoreByCityAndSiteId(cityIds, siteId);
                couponRule.setStoreList(storesList);
            }

        } catch (RuntimeException e) {
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }

        return ReturnDto.buildSuccessReturnDto(couponRuleService.ObjectConvertJson(couponRule));
    }


    @RequestMapping(value = "/getStoresListByStoreIds")
    @ResponseBody
    public Object getStoresListByStoreIds(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Map<String, Object> param = ParameterUtil.getParameterMap(request);
            String ids = param.get("ids").toString();
            Integer siteId = Integer.parseInt(param.get("siteId").toString());
            String[] id_arr = ids.split(",");
            Set<String> set = new HashSet<String>(Arrays.asList(id_arr));
            List<Map<String, String>> list = bStoresMapper.getBStoresListByStoreIds(siteId, set);
            result.put("code", "000");
            result.put("value", list);
        } catch (Exception e) {
            logger.error("查询门店出错{}", e);
            result.put("code", "-1");
            result.put("value", "-1");
        }
        return result;
    }


    /**
     * 撤销优惠券（修改status=2）
     *
     * @param siteId
     * @param ruleId
     * @param status
     * @return
     */
    @RequestMapping(value = "/revampStatus/{siteId}/{ruleId}/{status}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnDto revampCouponRuleStatus(@PathVariable("siteId") Integer siteId, @PathVariable("ruleId") Integer ruleId,
                                            @PathVariable("status") Integer status) {
        if (siteId == null || ruleId == null)
            return ReturnDto.buildFailedReturnDto("siteId或ruleId为空");
        if (status == null || status != 2)
            return ReturnDto.buildFailedReturnDto("撤销状态错误");

        try {
            couponRuleMapper.revampCouponRuleStatus(ruleId, siteId, status);
            couponRuleService.checkRuleAndActivityStatusByRuleId(siteId, ruleId);
        } catch (RuntimeException e) {
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }

        return ReturnDto.buildSuccessReturnDto("撤销成功");
    }

    /**
     * 修改优惠券规则的状态
     *
     * @param couponRuleUpdateStatus
     * @return
     */
    @PostMapping(value = "/updateRuleStatus", consumes = "application/json")
    @ResponseBody
    public ReturnDto updateRuleStatus(@RequestBody CouponRuleUpdateStatus couponRuleUpdateStatus) {
        if (null == couponRuleUpdateStatus.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        if (null == couponRuleUpdateStatus.getPreStatus())
            return ReturnDto.buildFailedReturnDto("规则初始状态不能为空");
        if (null == couponRuleUpdateStatus.getToUpdateStatus())
            return ReturnDto.buildFailedReturnDto("规则状态不能为空");
        if (null == couponRuleUpdateStatus.getRuleId())
            return ReturnDto.buildFailedReturnDto("规则主键不能为空");

        CouponRule rule = couponRuleMapper.findCouponRuleById(couponRuleUpdateStatus.getRuleId(), couponRuleUpdateStatus.getSiteId());
        if (null == rule) {
            return ReturnDto.buildFailedReturnDto("没有查到该优惠券信息");
        }

        if (rule.getStatus() != couponRuleUpdateStatus.getPreStatus()) {
            return ReturnDto.buildFailedReturnDto("规则状态已被修改,请刷新页面确认");
        }

        return couponRuleService.updateCouponRuleStatus(couponRuleUpdateStatus);
    }
}



