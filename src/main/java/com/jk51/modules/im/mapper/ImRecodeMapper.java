package com.jk51.modules.im.mapper;

import com.jk51.model.packageEntity.StoreAdminCloseIndex;
import com.jk51.modules.im.controller.request.IMRelationRequest;
import com.jk51.modules.im.service.iMRecode.response.Clerk;
import com.jk51.modules.im.service.iMRecode.response.IMRecode;
import com.jk51.modules.im.service.iMRecode.response.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-16
 * 修改记录:
 */
@Mapper
public interface ImRecodeMapper {

    Integer insertSelective(com.jk51.model.IMRecode imRecode);

    Integer findIMRecodeByTiemScope(@Param("now")Date now,@Param("beforeDate") Date beforeDate,@Param("user_name") String user_name);

    /**
     *根据发送用户名和接收用户名查询时间最小的聊天记录
     *@param sender
     * @param receiver
     * */
    com.jk51.model.IMRecode findImRecodeByReceiverAndSender(@Param("sender")String sender, @Param("receiver")String receiver);

    //查询90聊天记录，封装成StoreAdminCloseIndex
    List<StoreAdminCloseIndex> findStoreAdminCloseIndex(@Param("now")Date now, @Param("before")Date before, @Param("userNameList")List<String> userNameList);

    List<Map<String, String>> findIMRecodeByTiemScope2(@Param("now") Date now, @Param("before") Date before);

    List<Map<String, Object>> getServiceSatisfactionMap(@Param("siteId") Integer siteId, @Param("start") String startTime, @Param("end") String endTime);


    Integer updateStoreAdminIdByPrimaryKey(@Param("storeAdminId")Integer storeAdminId,@Param("msg_id")Integer msg_id);

    List<IMRecode> findByStoreAdminIdAndBuyerId(@Param("site_id")Integer site_id,@Param("store_admin_id")int store_admin_id, @Param("buyer_id") int buyer_id);

    List<Clerk> findClerkList(IMRelationRequest imRelationRequest);

    List<Member> findMemberList(IMRelationRequest imRelationRequest);

    List<IMRecode> queryIMRecodeTop10(@Param("site_id")Integer site_id, @Param("store_admin_id")Integer store_admin_id,@Param("buyer_id") Integer buyer_id, @Param("create_time")String create_time);
}
