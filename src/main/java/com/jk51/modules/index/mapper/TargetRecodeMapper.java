package com.jk51.modules.index.mapper;

import com.jk51.model.TargetRecode;
import com.jk51.model.packageEntity.StoreAdminIndex;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-17
 * 修改记录:
 */
@Mapper
public interface TargetRecodeMapper {

    //查询时间最大的记录
    List<TargetRecode> getTargetRecodeByStoreadminIdAndSiteId(@Param("storeadmin_id")int storeadmin_id, @Param("site_id")int site_id);

    int insertSelective(@Param("site_id")int site_id, @Param("storeadmin_id")int storeadmin_id,@Param("indexJson") String indexJson);

    //查询每个店员时间最大的一笔指标记录
    List<TargetRecode> getTargetRecodeforMaxCreatTime();

    int batchInsertSelective(List<StoreAdminIndex> storeAdminIndexList);
}
