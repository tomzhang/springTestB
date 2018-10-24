package com.jk51.modules.marketing.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.jk51.model.BMarketingPlan;
import com.jk51.model.BMarketingPlanExt;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.marketing.request.PlanExtListParam;
import com.jk51.modules.marketing.request.RandomParams;
import com.jk51.modules.marketing.service.MarketingService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("marketing")
public class MarketingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketingController.class);

    @Autowired
    private MarketingService marketingService;

    @RequestMapping("create")
    public Result createPrimary(@RequestBody BMarketingPlan bMarketingPlan) {
        Result result = null;
        try {
            bMarketingPlan.setId(null);
            result = marketingService.createPrimary(bMarketingPlan);
        } catch (Exception e) {
            LOGGER.error("营销计划添加异常", e);
            result = Result.fail("营销计划添加异常。");
        }
        return result;
    }

    @RequestMapping("edit")
    public Result editPrimary(@RequestBody BMarketingPlan bMarketingPlan) {
        Result result = null;
        try {
            result = marketingService.editPrimary(bMarketingPlan);
        } catch (Exception e) {
            LOGGER.error("营销计划更新异常", e);
            result = Result.fail("营销计划更新异常。");
        }
        return result;
    }

    @RequestMapping("del")
    public Result delPrimary(@RequestBody BMarketingPlan bMarketingPlan) {
        Result result = null;
        try {
            result = marketingService.delPrimary(bMarketingPlan);
        } catch (Exception e) {
            LOGGER.error("营销计划删除异常", e);
            result = Result.fail("营销计划删除异常。");
        }
        return result;
    }

    @RequestMapping("detail")
    public Result detail(Integer siteId, Integer id) {
        Result result = null;
        try {
            BMarketingPlan bMarketingPlan = marketingService.detail(siteId, id);
            if (bMarketingPlan == null) {
                result = Result.fail("查无信息。");
            } else {
                result = Result.success(bMarketingPlan);
            }
        } catch (Exception e) {
            LOGGER.error("营销计划查询异常", e);
            result = Result.fail("营销计划查询异常。");
        }
        return result;
    }

    @RequestMapping("list")
    public Result list(@RequestBody BMarketingPlan bMarketingPlan) {
        Result result = null;
        try {
            if (bMarketingPlan.getSiteId() == null) return Result.fail("缺少必要参数。");
            if (bMarketingPlan.getPageNum() == null) bMarketingPlan.setPageNum(1);
            if (bMarketingPlan.getPageSize() == null) bMarketingPlan.setPageSize(15);
            List<BMarketingPlan> planList = marketingService.list(bMarketingPlan, bMarketingPlan.getPageNum(), bMarketingPlan.getPageSize());
            result = Result.success(new PageInfo<>(CollectionUtils.isNotEmpty(planList)?planList:null));
        } catch (Exception e) {
            LOGGER.error("营销列表查询异常", e);
            result = Result.fail("营销列表查询异常。");
        }
        return result;
    }

    @RequestMapping("setting")
    public Result setting(@RequestBody PlanExtListParam planExtList) {
        Result result = null;
        try {
            result = marketingService.setting(planExtList.getList(), planExtList.getSiteId(), planExtList.getPlanId(), planExtList.getOperatorType(), planExtList.getOperatorId());
        } catch (Exception e) {
            LOGGER.error("营销计划奖品添加异常", e);
            result = Result.fail("营销计划奖品添加异常。");
        }
        return result;
    }

    @RequestMapping("editSetting")
    public Result editSetting(@RequestBody BMarketingPlanExt planExt) {
        Result result = null;
        try {
            result = marketingService.editSetting(planExt, planExt.getOperatorType(), planExt.getOperatorId());
        } catch (Exception e) {
            LOGGER.error("营销计划奖品更新异常", e);
            result = Result.fail("营销计划奖品更新异常。");
        }
        return result;
    }

    @RequestMapping("editChances")
    public Result editChances(@RequestBody PlanExtListParam planExtList) {
        Result result = null;
        try {
            result = marketingService.editChances(planExtList.getList(), planExtList.getSiteId(), planExtList.getPlanId(), planExtList.getOperatorType(), planExtList.getOperatorId());
        } catch (Exception e) {
            LOGGER.error("奖品批量更新异常", e);
            result = Result.fail("奖品批量更新异常。");
        }
        return result;
    }

    @RequestMapping("delSetting")
    public Result delSetting(@RequestBody BMarketingPlanExt planExt) {
        Result result = null;
        try {
            result = marketingService.delSetting(planExt, planExt.getOperatorType(), planExt.getOperatorId());
        } catch (Exception e) {
            LOGGER.error("营销计划奖品删除异常", e);
            result = Result.fail("营销计划奖品删除异常。");
        }
        return result;
    }

    @RequestMapping("editFlag")
    public Result startOrStop(@RequestBody BMarketingPlan bMarketingPlan) {
        Result result = null;
        try {
            result = marketingService.startOrStop(bMarketingPlan);
        } catch (Exception e) {
            LOGGER.error("营销计划状态启停异常", e);
            result = Result.fail("营销计划状态启停异常。");
        }
        return result;
    }

    @RequestMapping("editType")
    public Result editType(@RequestBody BMarketingPlan bMarketingPlan) {
        Result result = null;
        try {
            result = marketingService.editType(bMarketingPlan);
        } catch (Exception e) {
            LOGGER.error("营销计划类型更新异常", e);
            result = Result.fail("营销计划类型更新异常。");
        }
        return result;
    }

    @RequestMapping("settingComplete")
    public Result settingComplete(@RequestBody BMarketingPlan bMarketingPlan) {
        Result result = null;
        try {
            result = marketingService.settingComplete(bMarketingPlan);
        } catch (Exception e) {
            LOGGER.error("营销计划完成发布异常", e);
            result = Result.fail("营销计划完成发布异常。");
        }
        return result;
    }

    @RequestMapping("prizesRandom")
    public Result prizesRandom(@RequestBody RandomParams randomParams) {
        Result result = null;
        try {
            result = marketingService.prizesRandom(randomParams);
        } catch (Exception e) {
            LOGGER.error("抽取异常", e);
            result = Result.fail("抽取异常。");
        }
        marketingService.addLog(randomParams.getSiteId(), 2, randomParams.getBuyerId(), "prizesRandom", JSON.toJSONString(result), "抽奖品(活动ID)：" + randomParams.getPlanId());
        return result;
    }

    @RequestMapping("statusDetail")
    public Result statusDetail(@RequestBody RandomParams randomParams) {
        Result result = null;
        try {
            result = marketingService.statusDetail(randomParams);
        } catch (Exception e) {
            LOGGER.error("活动详情异常", e);
            result = Result.fail("活动详情异常。");
        }
        return result;
    }

}
