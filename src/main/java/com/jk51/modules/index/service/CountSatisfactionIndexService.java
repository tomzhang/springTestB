package com.jk51.modules.index.service;

import com.jk51.model.packageEntity.StoreAdminIndex;
import com.jk51.model.Target;
import com.jk51.model.packageEntity.TargetName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-17
 * 修改记录:
 * 满意度指标
 */
@Service
public class CountSatisfactionIndexService {

    @Autowired
    private CountIndexService countIndexService;

    /**
     *满意度指标计算----------暂时没有评价的功能，没有数据查（先返回初始值）
     *
     * */
    public int countSatisfactionIndexOne(StoreAdminIndex index){

        Target target = countIndexService.getTarget(index.getTargetList(), TargetName.SATISFACTION_INDEX);
        int initialValue = target.getInitial_value();
        return initialValue;
    }



}
