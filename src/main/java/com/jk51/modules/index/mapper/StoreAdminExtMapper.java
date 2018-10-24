package com.jk51.modules.index.mapper;

import com.jk51.model.ClerkDetail;
import com.jk51.model.MerchantClerkInfo;
import com.jk51.model.StoreAdminExt;
import com.jk51.modules.appInterface.util.Page;
import com.jk51.modules.pandian.Response.JoinInventoryUserResp;
import com.jk51.modules.task.domain.FollowTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Mapper
public interface StoreAdminExtMapper {

    List<StoreAdminExt> getStoreAdminExtBySiteIdAndStoreAdminId(@Param("site_id") int site_id, @Param("storeadmin_id") int storeadmin_id);

    //查询店员的门店名称，店员名称，店员职称
    List<Map<String, String>> getStoreAdminInfo(@Param("site_id") String site_id, @Param("store_admin_id") String store_admin_id);

    String getNameById(@Param("siteId") Integer siteId, @Param("id") Integer id);

    StoreAdminExt selectByIvCode(@Param("siteId") Integer siteId, @Param("ivcode") String ivCode);

    Map<String, Object> getStoreIdAndClerkInvitationCode(@Param("siteId") Integer siteId, @Param("soteAdminId") Integer soteAdminId);

    String getNameById2(@Param("siteId") Integer siteId, @Param("storeAdminId") Integer storeAdminId);

    List<Map<String, Object>> getStoreAdminInfoList(Map<String, Object> params);


    int deleteByPrimaryKey(Integer id, Integer site_id);

    int insert(StoreAdminExt record);

    int insertSelective(StoreAdminExt record);

    List<String> selectInviteCodeMax(@Param("siteId") Integer siteId);

    StoreAdminExt selectByPrimaryKey(@Param("id") int id, @Param("site_id") int site_id);

    int updateByPrimaryKeySelective(StoreAdminExt record);

    int updateByPrimaryKeyWithBLOBs(StoreAdminExt record);

    int updateByPrimaryKey(StoreAdminExt record);

    int deleleBySiteIdAndStoreAdminId(Integer site_id, Integer storeadmin_id);

    List<StoreAdminExt> selectAll(Integer site_id, Integer store_id);

    List<StoreAdminExt> selectAllByUsed(@Param("siteId") Integer siteId,@Param("storeId") Integer storeId);

    List<ClerkDetail> selectSelectiv(@Param("site_id") Integer siteId,
                                     @Param("store_id") Integer storeId,
                                     @Param("mobile") String mobile,
                                     @Param("start") Date start,
                                     @Param("end") Date end);

    List<StoreAdminExt> selectSelective(@Param("site_id") Integer siteId,
                                        @Param("store_id") Integer storeId,
                                        @Param("mobile") String mobile,
                                        @Param("start") Date start,
                                        @Param("end") Date end);

    StoreAdminExt selectByStoreAdminKey(@Param("site_id") Integer siteId, @Param("storeadmin_id") Integer storeadminId);

    List<StoreAdminExt> selectBySiteId(@Param("site_id") Integer siteId);

    List<MerchantClerkInfo> selectClerkInfo(@Param("site_id") Integer siteId,
                                            @Param("mobile") String mobile,
                                            @Param("name") String name,
                                            @Param("ivcode") String id,
                                            @Param("store_id") Integer storeId,
                                            @Param("status") Integer status);

    List<StoreAdminExt> findStoreAdminextList(@Param("mobile") String mobile, @Param("site_id") int store_dd);

    List<StoreAdminExt> selectBySiteIdAndStoreAdminId(@Param("siteId") int siteId, @Param("storeAdminId") int storeAdminId);

    StoreAdminExt getStoreAdminExtById(@Param("siteId") String siteId, @Param("storeadminId") String storeadminId);

    List<StoreAdminExt> selectClerkList(@Param("storeAdminext") StoreAdminExt storeAdminext, @Param("page") Page page);

    Long selectClerkCount(@Param("storeAdminext") StoreAdminExt storeAdminext);

    List<StoreAdminExt> selectClerkListByInviteCode(@Param("siteId") Integer siteId, @Param("inviteCode") String inviteCode);

    List<StoreAdminExt> selectClerkListLikeByInviteCode(@Param("siteId") Integer siteId, @Param("inviteCode") String inviteCode);

    List<MerchantClerkInfo> selectClerkChat(@Param("site_id") Integer siteId,
                                            @Param("mobile") String mobile,
                                            @Param("name") String name,
                                            @Param("storeName") String storeName,
                                            @Param("chat") Integer chat,
                                            @Param("status") Integer status);

    /**
     * 更改部分店员聊天开关权限
     *
     * @param map
     * @return
     */
    Integer updateClerkChatType(Map map);

    /**
     * 更改所有店员聊天开关权限
     *
     * @param siteId
     * @param chat
     * @return
     */
    Integer updateAllclerkChatType(@Param("siteId") Integer siteId, @Param("chat") Integer chat);

    String getStoreAdminNameById(@Param("siteId") String siteId, @Param("storeAdminId") String storeAdminId);

    StoreAdminExt selectClerkByInviteCode(@Param("siteId") Integer siteId, @Param("inviteCode") String inviteCode);

    List<Map<String, Object>> getEnergyClerksNum(@Param("siteId") Integer siteId, @Param("start") String startDate, @Param("end") String endDate);

    Map<String,Object> getStoreAdminExt(Map<String, Object> params);

    List<FollowTask> selectAdminByIds (@Param("siteId") Integer siteId, @Param("ids") int[] ids, @Param("objectName") String objectName);

    Map<String,Object> queryStoreId(@Param(value = "siteId") Integer siteId,@Param(value = "storeAdminId")Integer storeAdminId);

    String findClerkInvitationCode(@Param("siteId") int siteId,@Param("storeAdminId") String storeAdminId);

    Map<String,Object> queryAdminForReward (Map<String,Object> param);

    String selectNameByStoreAdminId(@Param("siteId") Integer siteId, @Param("storeadminId") Integer storeadminId);

    Integer getDeviceFlag(@Param("siteId")Integer site_id);

    Set<Integer> findStoreAdminIdsByUserName(@Param("siteId")Integer siteId, @Param("storeId")Integer storeId, @Param("name") String name);

    List<JoinInventoryUserResp> findUserNameByStoreAdminIds(@Param("siteId")Integer siteId, @Param("storeId") Integer storeId, @Param("storeAdminIds")Set storeAdminIds);
}
