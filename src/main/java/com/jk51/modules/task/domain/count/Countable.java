package com.jk51.modules.task.domain.count;

import com.jk51.model.task.BTask;
import com.jk51.model.task.BTaskExecute;
import com.jk51.model.task.BTaskplan;

import java.util.Map;

/**
 * 任务指标统计
 */
public interface Countable {
    Map<Integer, Long> count(BTask task, BTaskplan bTaskplan, BTaskExecute bTaskExecute);
}
