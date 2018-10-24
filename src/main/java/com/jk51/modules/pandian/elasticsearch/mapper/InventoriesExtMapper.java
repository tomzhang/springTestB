package com.jk51.modules.pandian.elasticsearch.mapper;

import com.jk51.model.Goods;
import com.jk51.modules.pandian.param.SyncInventoryDataParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/5/28
 * 修改记录:
 */
@Mapper
public interface InventoriesExtMapper {

    Map<String,String> queryByGoodsCode (@Param("goods_code") String goods_code, @Param("site_id") String site_id);

    List<String> findGoodsCodeByBarCode(@Param("siteId")Integer siteId,@Param("barCode")String barCode);

    Map<String,String> findPandianDetailExt(@Param("storeId")Integer storeId,@Param("pandianNum")String pandianNum);

    List<Map<String,Object>> findCheckerName(@Param("siteId")Integer siteId, @Param("storeAdminIds")Set<Integer> storeAdminIds);

    void manualOperationIntenvories2Sql(SyncInventoryDataParam param);
}
