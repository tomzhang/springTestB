package com.jk51.modules.balance.service;

import com.jk51.model.balance.SmsFeeRule;
import com.jk51.model.balance.SmsFeeSet;
import com.jk51.modules.balance.mapper.SmsFeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/5/11
 * 修改记录:
 */
@Service
public class SmsFeeService {

    @Autowired
    private SmsFeeMapper smsFeeMapper;


    public SmsFeeSet getSmsFeeSet(Integer siteId) {
        SmsFeeSet smsFeeSet = smsFeeMapper.getSmsFeeSet(siteId);
        //如果首次无数据则启用默认规则
//        if(smsFeeSet==null){
//            smsFeeSet = smsFeeMapper.getSmsFeeSet(0);
//        }
        return smsFeeSet;
    }

    public Integer addSmsFeeSet(SmsFeeSet record) {
        return smsFeeMapper.addSmsFeeSet(record);
    }

    public Integer updSmsFeeSet(SmsFeeSet record) {
        return smsFeeMapper.updSmsFeeSet(record);
    }



    public List<SmsFeeRule> getSmsFeeRuleLst(Integer siteId) {
        return smsFeeMapper.getSmsFeeRuleLst(siteId);
    }

    public Integer addSmsFeeRule(SmsFeeRule record) {
        return smsFeeMapper.addSmsFeeRule(record);
    }

    public Integer updSmsFeeRule(SmsFeeRule record) {
        return smsFeeMapper.updSmsFeeRule(record);
    }

    public Integer delSmsFeeRule(Integer siteId, Integer id) {
        return smsFeeMapper.delSmsFeeRule(siteId,id);
    }
}
