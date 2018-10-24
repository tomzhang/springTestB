package com.jk51.modules.task.domain.count;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.BMember;
import com.jk51.model.task.TCounttype;
import com.jk51.modules.task.domain.StoreAndAdminCombId;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;

/**
 * 会员注册量统计
 */
public class RegisterMemberNumCounter extends AbstractCounter {
    List<StoreAndAdminCombId> storeAndAdminCombIds;
    List<Integer> joinIds;

    @Override
    public Map<Integer, Long> count(List<TCounttype> tCounttypeList, CountRangeTime countRangeTime) {
        int siteId = bTaskplan.getSiteId();

        storeAndAdminCombIds = selectStoreAdmid(siteId);
        joinIds = StringUtil.split(bTaskplan.getJoinIds(), ",", Integer::parseInt);
        if (CollectionUtils.isEmpty(joinIds)) {
            return null;
        }

        // 任务统计口径
        List<BMember> bMembers = new ArrayList<>();
        for (TCounttype tCounttype : tCounttypeList) {
            List<BMember> temp = getBmembers(siteId, tCounttype.getFilterCondition(), countRangeTime.getStartTime(), countRangeTime.getEndTime());
            bMembers.addAll(temp);
        }

        return groupByTypeCount(bMembers);
    }

    public List<BMember> getBmembers(int siteId, String filterCondition, LocalDateTime startTime, LocalDateTime endTime) {
        return taskPlanCountMapper.selectBmemberBySiteIdAndCreateTime(siteId,
                filterCondition,
                startTime,
                endTime);
    }

    public Map<Integer, Long> groupByTypeCount(List<BMember> bMembers) {
        Map<Integer, Long> rewardCountMap = new HashMap<>();
        Map<Integer, Integer> adminIdOfStoreId = getAdminIdOfStoreId(storeAndAdminCombIds);
        for (BMember bMember : bMembers) {
            int registerStores = bMember.getRegisterStores();
            int registerClerks = NumberUtils.toInt(bMember.getRegisterClerks());
            // 忽略注册门店为空并且邀请店员为空的数据
            if (registerStores == 0 && registerClerks == 0) {
                continue;
            }
            // 注册门店字段为空的根据店员所在门店补齐
            if (bMember.getRegisterStores() == 0) {
                registerStores = adminIdOfStoreId.get(registerClerks);
            }
            // 当前数据的注册门店或者邀请店员不在计划发放对象内
            if (bTaskplan.getJoinType() == 10 && !joinIds.contains(registerStores)
                    || (bTaskplan.getJoinType() == 20 && !joinIds.contains(registerClerks))) {
                continue;
            }
            BiFunction<Integer, Long, Long> remappingFunction = (key, oldValue) -> Objects.isNull(oldValue) ? 1L : oldValue + 1L;
            rewardCountMap.compute(task.getObject() == 10 ? registerStores : registerClerks, remappingFunction);
        }

        return rewardCountMap;
    }
}
