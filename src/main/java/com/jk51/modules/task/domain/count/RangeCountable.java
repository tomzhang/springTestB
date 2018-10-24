package com.jk51.modules.task.domain.count;

import com.jk51.model.task.TCounttype;

import java.util.List;
import java.util.Map;

/**
 * 范围统计
 */
public interface RangeCountable {
    Map<Integer, Long> count(List<TCounttype> tCounttypeList, CountRangeTime countRangeTime);
}
