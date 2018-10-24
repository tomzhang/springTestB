package com.jk51.modules.offline.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-10-12
 * 修改记录:
 */
@Mapper
public interface BExcelTimeMapper {
    int insert(@Param("site_id") Integer siteId, @Param("file_size") String file_size, @Param("file_num") String file_num,
               @Param("cost_time") String cost_time, @Param("file_desc") String file_desc);
}
