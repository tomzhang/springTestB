package com.jk51.modules.pandian.mapper;

import com.jk51.model.Inventories;
import com.jk51.modules.pandian.Response.PandianOrderDetailResponse;
import com.jk51.modules.pandian.dto.PandianSortRedisDto;
import com.jk51.modules.pandian.param.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:盘点表Mapper
 * 作者: gaojie
 * 创建日期: 2017-10-18
 * 修改记录:
 */
@Mapper
public interface InventoriesMapper {



    List<Inventories> getInventories(InventoryParam param);

    List<Inventories> getInventories2(InventoryParam param);

    List<Inventories> getHasNotCheckInventoriesList(@Param("pandian_num") String pandian_num, @Param("storeId") int storeId, @Param("drug_name") String drug_name, @Param("goods_code") String goods_code);

    int insertInventory(Inventories inventories);

    int updateInventory(Inventories inventories);

    List<Inventories> getHasDifferenceInventories(@Param("pandian_num")String pandian_num,@Param("storeAdminId")int storeAdminId);


    Inventories getInventoryByPrimaryKey(int inventoryId);

    List<PandianOrderDetailResponse> getPandianOrderDetail(PandianOrderDetailParam param);

    Integer storeAdminConfirm(StoreAdminConfirmParam param);

    Integer getHasNotStoreAdminConfirmNum(@Param("pandian_num")String pandian_num,@Param("storeId") Integer storeId);

    Integer restInventoryonfirm(@Param("storeId")Integer storeId, @Param("pandian_num")String pandian_num);

    Integer getPandianDetailCount(@Param("pandian_num") String pandian_num,@Param("storeId") Integer storeId);


    List<Inventories> repeatInventoryForCondition(RepeatInventoryForConditionParam param);


    Integer getHasNotCheckInventoriesNum(@Param("pandian_num") String pandian_num,@Param("storeId") Integer storeId);

    List<String> findStoreAdminByPandianNum(StatusParam param);

    List<Inventories> getNextNotCheckerInventories(NextNotCheckerParam param);

    int updateInventoryByEsId(Inventories inventories);

    List<Inventories> findByPandianNum(@Param("pandian_num") String pandian_num);


    int updateScore(@Param("pandianNum")String pandianNum, @Param("storeId")Integer storeId,@Param("goodsCode")String goodsCode, @Param("score")Double score);
}
