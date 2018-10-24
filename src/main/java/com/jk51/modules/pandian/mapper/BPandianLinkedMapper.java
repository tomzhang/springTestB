package com.jk51.modules.pandian.mapper;

import com.jk51.model.PandianLinkedNode;
import com.jk51.modules.pandian.dto.PandianLinkedDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/4/25
 * 修改记录:
 */
@Mapper
public interface BPandianLinkedMapper {

    PandianLinkedNode findNodeByUniqueIndex(@Param("linkedDto")PandianLinkedDto linkedDto);

    PandianLinkedNode findnextIsNullNode(@Param("linkedDto")PandianLinkedDto linkedDto);

    PandianLinkedNode selectByPrimaryKey(Integer id);

    Integer insert(PandianLinkedNode linkedDto);

    Integer update(@Param("linkedDto")PandianLinkedNode linkedDto);

    Integer deleteByPrimaryKey(Integer id);
}
