package com.jk51.modules.im.mapper;

import com.jk51.model.NoBodyAnswerIMRecode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-27
 * 修改记录:
 */
@Mapper
public interface NoBodyAnswerIMRecodeMapper {
    int insertSelective(NoBodyAnswerIMRecode recode);
}
