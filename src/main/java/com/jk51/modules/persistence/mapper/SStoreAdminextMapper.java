package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SClerkDetail;
import com.jk51.model.order.SMerchantClerkInfo;
import com.jk51.model.order.SPage;
import com.jk51.model.order.SStoreAdminext;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.quartz.impl.matchers.StringMatcher;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface SStoreAdminextMapper {
    int deleteByPrimaryKey(Integer id, Integer site_id);

    int insert(SStoreAdminext record);

    int insertSelective(SStoreAdminext record);

    List<String> selectInviteCodeMax(@Param("siteId") Integer siteId);

    SStoreAdminext selectByPrimaryKey(@Param("id") int id, @Param("site_id") int site_id);

    int updateByPrimaryKeySelective(SStoreAdminext record);

    int updateByPrimaryKeyWithBLOBs(SStoreAdminext record);

    int updateByPrimaryKey(SStoreAdminext record);

    int deleleBySiteIdAndStoreAdminId(Integer site_id, Integer storeadmin_id);

    List<SStoreAdminext> selectAll(Integer site_id, Integer store_id);

    List<SClerkDetail> selectSelectiv(@Param("site_id") Integer siteId,
                                      @Param("store_id") Integer storeId,
                                      @Param("mobile") String mobile,
                                      @Param("start") Date start,
                                      @Param("end") Date end);

    List<SStoreAdminext> selectSelective(@Param("site_id") Integer siteId,
                                        @Param("store_id") Integer storeId,
                                        @Param("mobile") String mobile,
                                        @Param("start") Date start,
                                        @Param("end") Date end);

    SStoreAdminext selectByStoreAdminKey(@Param("site_id") Integer siteId, @Param("storeadmin_id") Integer storeadminId);

    List<SStoreAdminext> selectBySiteId(@Param("site_id") Integer siteId);

    List<SMerchantClerkInfo> selectClerkInfo(@Param("site_id") Integer siteId,
                                             @Param("mobile") String mobile,
                                             @Param("name") String name,
                                             @Param("ivcode") String id,
                                             @Param("store_id") Integer storeId,
                                             @Param("status") Integer status);

    List<SStoreAdminext> findStoreAdminextList(@Param("mobile") String mobile, @Param("site_id") int store_dd);

    List<SStoreAdminext> selectBySiteIdAndStoreAdminId(@Param("siteId") int siteId, @Param("storeAdminId") int storeAdminId);

    SStoreAdminext getStoreAdminExtById(@Param("siteId") String siteId, @Param("storeadminId") String storeadminId);

    List<SStoreAdminext> selectClerkList(@Param("storeAdminext") SStoreAdminext storeAdminext, @Param("page") SPage page);

    Long selectClerkCount(@Param("storeAdminext") SStoreAdminext storeAdminext);

    List<SStoreAdminext> selectClerkListByInviteCode(@Param("siteId") Integer siteId, @Param("inviteCode") String inviteCode);

    List<SStoreAdminext> selectClerkListLikeByInviteCode(@Param("siteId") Integer siteId, @Param("inviteCode") String inviteCode);

    List<SMerchantClerkInfo> selectClerkChat(@Param("site_id") Integer siteId,
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
    SStoreAdminext selectByMobileExt(String mobile);
    SStoreAdminext selectByMobileExtToInsert(String mobile);
    int updateClerkDel(@Param("siteId") Integer siteId,@Param("storeId") Integer storeId,@Param("id") Integer id);

    int editAvatar(@Param("siteId") Integer siteId, @Param("id") Integer id, @Param("avatar") String avatar);
    Map queryItem(@Param("siteId") Integer siteId, @Param("id") Integer id);

    int forgetPwd(@Param("siteId") Integer siteId, @Param("phone") String phone, @Param("pwd")String pwd);

    List<Map<String, Object>> storeAdminReport (Map<String, Object> map);

    List<Map<String,Object>> getClerksList(Map<String,Object> map);

    List<Map<String,Object>> getadminInfo(Map<String,Object> map);

    void addChangeClerkLog(Map<String,Object> map);

    int changeClerk(Map<String,Object> map);
}
