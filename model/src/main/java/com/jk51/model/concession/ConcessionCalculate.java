package com.jk51.model.concession;

import com.jk51.model.concession.result.BaseResult;

import java.util.Optional;

/**
 * Created by ztq on 2017/12/25
 * Description: 优惠最优算法
 */
public interface ConcessionCalculate {
    /**
     * 不同类型之间的计算规则
     *
     * @return
     */
    Optional<? extends BaseResult> calculateInAllRule();
}
