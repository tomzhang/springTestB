package com.jk51.modules.task.domain.count;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.task.BTask;
import com.jk51.model.task.BTaskBlob;
import com.jk51.model.task.BTaskExecute;
import com.jk51.model.task.BTaskplan;
import com.jk51.modules.goods.library.SpringContextUtil;
import com.jk51.modules.task.domain.StoreAndAdminCombId;
import com.jk51.modules.task.mapper.BTaskBlobMapper;
import com.jk51.modules.task.mapper.BTaskExecuteMapper;
import com.jk51.modules.task.mapper.BTaskcountMapper;
import com.jk51.modules.task.mapper.TaskPlanCountMapper;
import com.jk51.modules.task.service.TaskPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public abstract class AbstractCounter implements RangeCountable {
    protected BTaskplan bTaskplan;

    protected BTask task;

    protected BTaskExecute bTaskExecute;

    @Autowired
    protected RedisTemplate<String, Long> redisTemplate;

    @Autowired
    protected TaskPlanService taskPlanService;

    @Autowired
    protected BTaskcountMapper bTaskcountMapper;

    @Autowired
    BTaskBlobMapper bTaskBlobMapper;

    @Autowired
    TaskPlanCountMapper taskPlanCountMapper;

    @Autowired
    BTaskExecuteMapper bTaskExecuteMapper;

    public AbstractCounter() {
        getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);
    }

    public AbstractCounter withBTask(BTask bTask) {
        this.task = bTask;

        return this;
    }

    public AbstractCounter withBTaskplan(BTaskplan bTaskplan) {
        this.bTaskplan = bTaskplan;

        return this;
    }

    public AbstractCounter withBTaskExecute(BTaskExecute bTaskExecute) {
        this.bTaskExecute = bTaskExecute;

        return this;
    }

    public static ApplicationContext getApplicationContext() {
        return SpringContextUtil.getApplicationContext();
    }

    public List<Integer> getGoodsIds() {
        BTaskBlob bTaskBlob = bTaskBlobMapper.selectByTaskId(task.getId());
        String goodsIds = "";
        if (Objects.nonNull(bTaskBlob)) {
            goodsIds = bTaskBlob.getGoodsId();
        }

        return StringUtil.split(goodsIds, ",", Integer::parseInt);
    }

    public List<StoreAndAdminCombId> selectStoreAdmid(int siteId) {
        return taskPlanCountMapper.selectAdminId(siteId);
    }

    /**
     * 获取门店->店员
     * @param list
     * @return
     */
    public Map<Integer, List<Integer>> getStoreIdOfadminList(List<StoreAndAdminCombId> list) {
        return list.stream()
                .collect(Collectors.groupingBy(StoreAndAdminCombId::getStoreId, Collectors.mapping(StoreAndAdminCombId::getAdminId, toList())));
    }

    /**
     * 获取店员->门店
     * @param list
     * @return
     */
    public Map<Integer, Integer> getAdminIdOfStoreId(List<StoreAndAdminCombId> list) {
        return list.stream().collect(toMap(item -> item.getAdminId(), item -> item.getStoreId()));
    }
}
