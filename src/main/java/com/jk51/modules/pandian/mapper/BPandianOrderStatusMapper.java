package com.jk51.modules.pandian.mapper;

import com.jk51.model.BPandianOrderStatus;
import com.jk51.modules.pandian.param.StatusParam;
import com.jk51.modules.pandian.param.StoreOrderStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-06
 * 修改记录:
 */
@Mapper
public interface BPandianOrderStatusMapper {
    int insertSelective(BPandianOrderStatus record);
    int updateStatusByAdd(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("orderId") Integer orderId, @Param("status") Integer status);
    int insertByList(List<BPandianOrderStatus> orderStatusList);
    List<Map<String,Object>> getCanUpStoreNumList(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("orderId") Integer orderId);





    Integer updateStatus2Confirm(@Param("storeId") Integer storeId, @Param("storeAdminId")Integer storeAdminId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);



    Integer getStatus(StatusParam s);

    Integer updateStatus2Repeat(@Param("storeId") Integer storeId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);

    //-------------审核---------------
    Integer updateStatus2AuditByAdmin(@Param("storeId")Integer storeId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);

    Integer updateStatus2AuditByManager(@Param("storeId") Integer storeId, @Param("storeAdminId")Integer storeAdminId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);

    Integer updateStatus2AuditByStoreAdmin(@Param("storeId") Integer storeId, @Param("storeAdminId")Integer storeAdminId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);

    //---------------下发--------------
    Integer updateStatus2StartByAdmin(@Param("storeId")Integer storeId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);

    Integer updateStatus2StartByManager(@Param("storeId") Integer storeId, @Param("storeAdminId")Integer storeAdminId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);

    Integer updateStatus2StartByStoreAdmin(@Param("storeId") Integer storeId, @Param("storeAdminId")Integer storeAdminId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);

    //-------------监盘确认----------------
    Integer updateStatus2ConfirmByAdmin(@Param("storeId")Integer storeId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);

    Integer updateStatus2ConfirmByManager(@Param("storeId") Integer storeId, @Param("storeAdminId")Integer storeAdminId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);

    Integer updateStatus2ConfirmByStoreAdmin(@Param("storeId") Integer storeId, @Param("storeAdminId")Integer storeAdminId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);

    List<StoreOrderStatus> getPDAllStoreStatus(@Param("siteId") Integer siteId, @Param("planId") Integer planId, @Param("orderId") Integer orderId);

    StoreOrderStatus getPDStoreStatus(@Param("siteId") Integer siteId, @Param("planId") Integer planId, @Param("orderId") Integer orderId, @Param("storeId") Integer storeId);

    int updateStatusByStoreIdList(@Param("siteId") Integer siteId, @Param("orderId") Integer orderId, @Param("list") List<Integer> storeIdList, @Param("status") int status);
    int updateBillidStatusByStoreId(@Param("siteId") Integer siteId, @Param("orderId") Integer orderId, @Param("storeId") Integer storeId, @Param("status") int status, @Param("billid") String billid);

    /*//-------------复盘---------------- ps:由于不需要显示复盘操作的人员，也就不许要去记录复盘操作人员的信息，后续如果需要的时候在加
    Integer updateStatus2RepeatByAdmin(@Param("storeId")Integer storeId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);

    Integer updateStatus2RepeatByManager(@Param("storeId") Integer storeId, @Param("storeAdminId")Integer storeAdminId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);

    Integer updateStatus2RepeatByStoreAdmin(@Param("storeId") Integer storeId, @Param("storeAdminId")Integer storeAdminId,@Param("pandian_num") String pandian_num,@Param("toStatus") int toStatus);*/
}
