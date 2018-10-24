package com.jk51.modules.index.mapper;

import com.jk51.model.ClerkDetail;
import com.jk51.model.ClerkInfo;
import com.jk51.model.JKHashMap;
import com.jk51.model.StoreAdmin;
import com.jk51.model.packageEntity.StoreAdminIndex;
import com.jk51.modules.im.service.iMRecode.response.Clerk;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by gaojie on 2017/2/15.
 */
@Mapper
public interface StoreAdminMapper {

    //获取所有店员的指标类
    List<StoreAdminIndex> getAllStoreAdminIndexList();

    List<StoreAdmin> getStoreAdminList(@Param("storeAdminId") String storeAdminId, @Param("siteId")String siteId);

    //更新店员指标分
    void updateCountIndex(@Param("site_id")int site_id, @Param("storeadmin_id")int storeadmin_id, @Param("countIndex")double countIndex);

    //获取各门店店员数量
    List<Map<String,Integer>> getStoreClerkQuantityList();

    //查询店员根据商家ID，和他的手机号码
    List<StoreAdmin> findStoreAdminBySiteId(int site_id);

    StoreAdmin findUserAndPasswordByStoreId(Integer siteid, String username, String password2 );

    String getNameById(@Param("siteId") Integer siteId, @Param("id") Integer id);

    int batchUpdateCountIndex(List<StoreAdminIndex> storeAdminIndexList);


    List<String> getStoreAdminIdsByStore(@Param("siteId")Integer siteId, @Param("storeId")Integer storeId);

    List<StoreAdmin> selectStoreAdminByStoreId(int site_id,Integer storeId);

    List<Integer> selectStoreAdminIdsByStoreId(@Param("siteId") Integer siteId,@Param("storeId") Integer storeId);

    List<StoreAdmin> selectAllStoreAdmin(int site_id);

    int deleteByPrimaryKey(Integer id, Integer site_id);

    int insert(StoreAdmin record);

    int insertSelective(StoreAdmin record);

    StoreAdmin selectByPrimaryKey(@Param("id") Integer id, @Param("site_id") Integer site_id);

    int updateByPrimaryKeySelective(StoreAdmin record);

    int updateByPrimaryKey(StoreAdmin record);

    List<StoreAdmin> selectAll(@Param("site_id") Integer site_id, @Param("store_id") Integer store_id);

    int updateStoreId(Integer newStoreId);

    /**
     * 通过主键修改storeid
     *
     * @param siteId
     * @param id
     * @param newStoreId
     * @return
     */
    int updateStoreIdByPrimaryKey(@Param("site_id") Integer siteId, @Param("id") Integer id, @Param("new_store_id") Integer newStoreId ,@Param("clerk_invitation_code") String clerkInvitationCode);

    /**
     * 店员删除操作  改变is_del字段
     *
     * @param siteId
     * @param id
     * @return
     */
    int deleteClerkByPrimaryKey(@Param("site_id") Integer siteId, @Param("id") Integer id);

    List<StoreAdmin> findStoreAdmin(String phone);

    List<Map<String, Object>> findStoreAdminByPhoneAndPassword(@Param("phone") String phone, @Param("password") String password);

    int updatePassword(@Param("site_id") int site_id, @Param("id") int id, @Param("user_pwd") String user_pwd);

    ClerkDetail selectClerkDetail(@Param("site_id") Integer siteId,
                                  @Param("id") Integer id);

    StoreAdmin selectByName(@Param("site_id") Integer siteId, @Param("user_name") String user_name, @Param("password") String password);

    //获得门店超级管理员
    StoreAdmin selectbysiteandstore(@Param("site_id") Integer siteId, @Param("store_id") Integer store_id);

    List<ClerkInfo> selectClerkInfo(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("username") String username, @Param("realname") String realName, @Param("active") Integer active);

    List<StoreAdmin> selectByUsername(@Param("site_id") Integer siteId, @Param("mobile") String mobile);

    Integer changePwd(@Param("site_id") Integer site_id, @Param("id") Integer id, @Param("user_pwd") String user_pwd);

    String selectShopWxUrlBySiteId(@Param("merchant_id") Integer siteId);

    Integer updateLoginCount(@Param("site_id") Integer site_id, @Param("user_name") String user_name, @Param("user_pwd") String user_pwd);

    Map<String,Integer> selectStatue(@Param("siteId")Integer siteId,@Param("id")Integer id);

    StoreAdmin selectByMobile(String mobile);

    List<Clerk> findBySiteIdAndName(@Param("site_id")int site_id, @Param("name") String name);

    Integer findBySiteNum(Integer site_id);

    List<Map<String,Object>> queryAdminInfoAllByStore(@Param("siteId")Integer siteId, @Param("storeId")Integer storeId);

    Map<String,Object> selectStoreNameAndStoreIdByManagerId(@Param("siteId") Integer siteId,@Param("userId") String userId);

    JKHashMap<String, Object> selectSimpleInfo(@Param("siteId") Integer siteId,@Param("storeAdminId") Integer storeAdminId);

    List<Integer> queryStoreAdminIds(@Param("siteId") Integer siteId);
}
