package com.jk51.modules.pc.mapper;

import com.jk51.model.pc.HomePage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/11/16
 * 修改记录:
 */
@Mapper
public interface HomePageMapper {
    Integer add(HomePage homePage);

    Integer upd(HomePage homePage);

    List<HomePage> getLst(@Param("siteId") Integer siteId,@Param("bfrom") Integer bfrom);
}
