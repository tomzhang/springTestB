package com.jk51.modules.coupon.tags;

import com.jk51.modules.promotions.constants.PromotionsConstant;
import org.springframework.beans.factory.annotation.Autowired;

import static com.jk51.modules.promotions.constants.PromotionsConstant.*;

/**
 * @author javen73
 * @date 2018/6/25 15:16
 */
public class TagsGroupUtils {
    public static int groupByIncludes(String tags){
        if(tags.contains("团"))
            return PROMOTIONS_RULE_TYPE_GROUP_BOOKING;
        else if (tags.contains("特价"))
            return PROMOTIONS_RULE_TYPE_LIMIT_PRICE;
        else if (tags.contains("减"))
            return PROMOTIONS_RULE_TYPE_MONEY_OFF;
        else if(tags.contains("折"))
            return PROMOTIONS_RULE_TYPE_DISCOUNT;
        else if(tags.contains("包邮"))
            return PROMOTIONS_RULE_TYPE_FREE_POST;
        else if(tags.contains("送"))
            return PROMOTIONS_RULE_TYPE_GIFT;
        return 0;
    }
}
