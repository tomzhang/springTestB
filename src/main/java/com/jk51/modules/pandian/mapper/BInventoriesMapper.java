package com.jk51.modules.pandian.mapper;

import com.jk51.model.BInventories;
import com.jk51.model.Inventories;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BInventoriesMapper {

    int insertSelective(BInventories record);

    int updateByAdd(BInventories bInventories);

    int deleteByAdd(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("orderId") Integer orderId);

    List<Map<String, Object>> getInventoriesList(@Param("siteId") Integer siteId, @Param("storeIdList") List<Integer> storeIdList, @Param("orderId") Integer orderId, @Param("cond") Map<String, Object> cond);

    //erp对接使用
    List<Map<String, Object>> selectInventoriesListToErp(@Param("siteId") Integer siteId, @Param("uid") String uid,
                                                         @Param("pandianNum") String pandianNum, @Param("itemIds") List<Integer> itemIds);

    int insertByList(@Param("list") List<Inventories> list);

    int deleteByStoreIdList(@Param("siteId") Integer siteId, @Param("orderId") Integer orderId, @Param("storeIds") List<Integer> storeIds);
}
