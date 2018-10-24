package com.jk51.modules.pc.mapper;

import com.jk51.model.pc.Notice;
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
public interface NoticeMapper {
    Integer add(Notice notice);

    Integer upd(Notice notice);

    Integer del(@Param("siteId") Integer siteId,@Param("id") Integer id);

    List<Notice> getLst(Integer siteId);

    Notice getById(@Param("siteId") Integer siteId,@Param("id") Integer id);

    Notice getUpById(@Param("siteId") Integer siteId,@Param("id") Integer id);

    Notice getDownById(@Param("siteId") Integer siteId,@Param("id") Integer id);
}
