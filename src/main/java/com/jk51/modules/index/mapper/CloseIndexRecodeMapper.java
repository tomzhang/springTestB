package com.jk51.modules.index.mapper;

import com.jk51.model.CloseIndexRecode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-21
 * 修改记录:
 */
@Mapper
public interface CloseIndexRecodeMapper {

    //批量插入
    int batchInsert(List<CloseIndexRecode> closeIndexList);

    List<CloseIndexRecode> findCloseIndexRecode(@Param("sender")String sender, @Param("site_id")int site_id, @Param("storeAdminId") int storeAdminId);

    List<CloseIndexRecode> findCloseIndexRecodeBySenderAndSiteId(@Param("sender") String sender, @Param("site_id") int site_id, @Param("now")Date now, @Param("before")Date before);
}
