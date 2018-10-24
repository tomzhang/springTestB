package com.jk51.modules.pc.mapper;

import com.jk51.model.pc.Help;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/11/15
 * 修改记录:
 */
@Mapper
public interface HelpMapper {
    List<Help> getLst(Integer siteId);

    Integer upd(Help help);

    Integer add(Help help);

    Help getBySecTitle(@Param("siteId") Integer siteId,@Param("firTitle") String firTitle,@Param("secTitle") String secTitle);

    List<Help> getLstByFirTitle(@Param("siteId") Integer siteId,@Param("firTitle") String firTitle);
}
