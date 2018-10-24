package com.jk51.modules.concession.constants;

import com.jk51.model.concession.ConcessionDesc;

/**
 * Created by ztq on 2018/1/9
 * Description:
 */
public class ConcessionConstant {
    /**
     * 优惠中优惠券类型是1
     */
    public static final int COUPON = 1;

    /**
     * 优惠中活动类型是2
     */
    public static final int PROMOTIONS = 2;

    /**
     * 用于{@link ConcessionDesc#concessionType}
     */
    public static final Integer TYPE_COUPON_DETAIL = 1;

    /**
     * 用于{@link ConcessionDesc#concessionType}
     */
    public static final Integer TYPE_PROMOTIONS_ACTIVITY = 2;

    /**
     * 用于{@link ConcessionDesc#concessionType}
     */
    public static final Integer TYPE_PROMOTIONS_DETAIL = 3;
}
