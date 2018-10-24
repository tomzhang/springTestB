package com.jk51.modules.im.mapper;

import com.jk51.model.ChUserPushInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-03-06
 * 修改记录:
 */
@Mapper
public interface ChUserPushInfoMapper {

    /**
     * 根据userId、app_name = "pharmacist"查询
     *@param userId
     *
     * */
    List<ChUserPushInfo> findByUserId(String userId);

    int deleteByCidAndUserIdAndAppName(@Param("cid") String cid, @Param("userId") String userId, @Param("app_name") String app_name);

    int insert(ChUserPushInfo chUserPushInfo);

    ChUserPushInfo findByStoreAdminId(Integer storeAdminId);

    /**
     * 根据userId、app_name = "pharmacist"查询@param userId
     *@param cid  */
    List<ChUserPushInfo> findByUserId( @Param("userId")String userId, @Param("cid")String cid);


    Integer deleteByPushId(@Param("pushId")String pushId,@Param("userId")String userId);
}
