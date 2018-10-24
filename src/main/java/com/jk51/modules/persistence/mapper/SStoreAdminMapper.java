package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SClerkDetail;
import com.jk51.model.order.SClerkInfo;
import com.jk51.model.order.SStoreAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SStoreAdminMapper {
    int deleteByPrimaryKey(Integer id, Integer site_id);

    int insert(SStoreAdmin record);

    int insertSelective(SStoreAdmin record);

    SStoreAdmin selectByPrimaryKey(@Param("id") Integer id, @Param("site_id") Integer site_id);

    int updateByPrimaryKeySelective(SStoreAdmin record);

    int updateByPrimaryKey(SStoreAdmin record);

    List<SStoreAdmin> selectAll(@Param("site_id") Integer site_id, @Param("store_id") Integer store_id);

    int updateStoreId(Integer newStoreId);

    /**
     * 通过主键修改storeid
     *
     * @param siteId
     * @param id
     * @param newStoreId
     * @return
     */
    int updateStoreIdByPrimaryKey(@Param("site_id") Integer siteId, @Param("id") Integer id, @Param("new_store_id") Integer newStoreId, @Param("clerk_invitation_code") String clerkInvitationCode);

    /**
     * 店员删除操作  改变is_del字段
     *
     * @param siteId
     * @param id
     * @return
     */
    int deleteClerkByPrimaryKey(@Param("site_id") Integer siteId, @Param("id") Integer id);

    List<SStoreAdmin> findStoreAdmin(String phone);

    List<Map<String, Object>> findStoreAdminByPhoneAndPassword(@Param("phone") String phone, @Param("password") String password);

    int updatePassword(@Param("site_id") int site_id, @Param("id") int id, @Param("user_pwd") String user_pwd);

    SClerkDetail selectClerkDetail(@Param("site_id") Integer siteId,
                                   @Param("id") Integer id);

    SStoreAdmin selectByName(@Param("site_id") Integer siteId, @Param("user_name") String user_name, @Param("password") String password);

    SStoreAdmin selectAdminByUserTypeOrStoreId(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId);

    //获得门店超级管理员
    SStoreAdmin selectbysiteandstore(@Param("site_id") Integer siteId, @Param("store_id") Integer store_id);

    List<SClerkInfo> selectClerkInfo(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("username") String username, @Param("realname") String realName, @Param("active") Integer active);

    List<SStoreAdmin> selectByUsername(@Param("site_id") Integer siteId, @Param("mobile") String mobile);

    Integer changePwd(@Param("site_id") Integer site_id, @Param("id") Integer id, @Param("user_pwd") String user_pwd);

    String selectShopWxUrlBySiteId(@Param("merchant_id") Integer siteId);

    Integer updateLoginCount(@Param("site_id") Integer site_id, @Param("user_name") String user_name, @Param("user_pwd") String user_pwd);

    Map<String,Integer> selectStatue(@Param("siteId") Integer siteId, @Param("id") Integer id);

    SStoreAdmin selectByMobile(String mobile);

    SStoreAdmin selectByMobileAndSiteId(@Param("mobile")String mobile, @Param("siteId")Integer siteId);

    int updateClerkDel(@Param("siteId") Integer siteId,@Param("storeId") Integer storeId,@Param("id") Integer id);
    int updateInsertClerk(@Param("siteId") Integer siteId,@Param("storeId") Integer storeId,@Param("id") Integer id);
}
