package com.jk51.modules.balance.mapper;

import com.jk51.model.balance.SmsFeeRule;
import com.jk51.model.balance.SmsFeeSet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/5/11
 * 修改记录:
 */
@Mapper
public interface SmsFeeMapper {

    SmsFeeSet getSmsFeeSet(Integer siteId);

    int addSmsFeeSet(SmsFeeSet record);

    int updSmsFeeSet(SmsFeeSet record);


    List<SmsFeeRule> getSmsFeeRuleLst(Integer siteId);

    int addSmsFeeRule(SmsFeeRule record);

    int updSmsFeeRule(SmsFeeRule record);

    int delSmsFeeRule(@Param("siteId") Integer siteId, @Param("id") Integer id);

}
