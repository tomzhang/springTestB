package com.jk51.modules.promotions.controller;

import com.jk51.modules.promotions.request.ProRuleMaxParam;
import com.jk51.modules.promotions.service.PromotionsRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by mqq on 2017/10/27.
 */
@RestController
@RequestMapping(name = "优惠活动拆弹", value = "proRule/splitOrder")
public class PromotionsSplitOrderController {
    @Autowired
    PromotionsRuleService promotionsRuleService;

    @RequestMapping(name = "活动计算金额", value = "proRuleForSplitOrder")
    @ResponseBody
    public Map<String, Object> proRuleForSpitOrder (@RequestBody ProRuleMaxParam proRuleMaxParam, HttpServletRequest request) {
        return null;
    }
}
