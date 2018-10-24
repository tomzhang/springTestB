package com.jk51.modules.promotions.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.TimeRuleForPromotionsRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.jk51.model.promotions.rule.TimeRuleForPromotionsRule.*;
import static com.jk51.modules.promotions.constants.PromotionsConstant.longFormatter;

/**
 * filename :com.jk51.modules.promotions.service.
 * author   :zw
 * date     :2017/8/11
 * Update   :
 */
@Service
public class VerifyRuleService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PromotionsRuleService service;

    /**
     * 是否过期
     *
     * @param promotionsRule
     * @return
     */
    public boolean verifyTimeRule(PromotionsRule promotionsRule) {
        String timeRule = promotionsRule.getTimeRule();
        try {
            TimeRuleForPromotionsRule rule = JacksonUtils.json2pojo(timeRule, TimeRuleForPromotionsRule.class);
            switch (rule.getValidity_type()) {
                case VALIDITY_TYPE_ABSOLUTE_TIME:
                    LocalDateTime endTime = LocalDateTime.parse(rule.getEndTime(), longFormatter);
                    return endTime.isBefore(LocalDateTime.now());
                case VALIDITY_TYPE_MONTH_SEPARATE:
                case VALIDITY_TYPE_WEEKLY_SEPARATE:
                    return false;
                default:
                    throw new RuntimeException("promitionsRule.timerule.validity_type 出现不该出现的类型");
            }
        } catch (Exception e) {
            logger.error("解析promotionsRule.timeRule出错", e);
        }

        return false;
    }

    /**
     * 是否没有库存了
     *
     * @param promotionsRule
     * @return
     */
    public boolean verifyAmount(PromotionsRule promotionsRule) {
        Integer amount = promotionsRule.getAmount();
        if (amount == null) {
            logger.error("promotionsRule.amount 不能为 null");
            return false;
        }

        return amount.equals(-1) ? false : amount.equals(0);
    }

    /**
     * 校验状态
     *
     * @param promotionsRule
     * @return
     */
    public boolean verifyStatus(PromotionsRule promotionsRule) {
        return service.autoChangeStatus(promotionsRule.getSiteId(), promotionsRule.getId());
    }
}
