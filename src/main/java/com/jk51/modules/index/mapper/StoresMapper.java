package com.jk51.modules.index.mapper;

import com.jk51.model.CityHasStores;
import com.jk51.model.Stores;
import com.jk51.model.order.Store;
import com.jk51.modules.task.domain.FollowTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yanglile
 * 创建日期: 2017-02-15
 * 修改记录:
 */
@Mapper
public interface StoresMapper {


    Stores getStore(@Param("storeId") int storeId, @Param("siteId") int siteId);

    //获取所有商家状态为未删除且启用的旗舰店 quantity：是否旗舰店 1,0

    List<Map<String, Integer>> getStoresIsQJDList();

    /**
     * @param stores 新增门店
     * @return 1成功0失败
     */
    int insertStores(Stores stores);

    /**
     * 修改门店信息
     *
     * @param stores
     * @return
     */
    int updateStores(Stores stores);

    List<Map> selectServiceSupport(@Param("site_id") Integer site_id);

    String getStorePhone(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId);

    int updateOriginStoreId(Stores stores);

    List<CityHasStores> GroupStoresByCityAndServiceSupport(@Param("site_id") Integer siteId, @Param("serviceSupport") String serviceSupport);

    List<Store> selectStoresByCityAndServiceSupport(@Param("site_id") Integer siteId, @Param("cityId") Integer cityId, @Param("serviceSupport") String serviceSupport);

    List<Integer> selectByOwnPricingTypeAndSiteId(Integer siteId);

    List<Integer> selectByUpPricingTypeAndSiteId(Integer siteId);

    Store selectByOwnPricingTypeAndSiteIdAndStoreId(@Param("siteId") Integer siteId, @Param("id") Integer id);

    List<Integer> selectByOwnPromotionTypeAndSiteId(Integer siteId);

    Store selectByOwnPromotionTypeAndSiteIdAndStoreId(@Param("siteId") Integer siteId, @Param("id") Integer id);

    /**
     * 显示相关商家的所有门店信息
     *
     * @param site_id
     * @return
     */
    List<Store> selectAllStore(@Param("site_id") Integer site_id, @Param("name") String name, @Param("storesNumber") String storeNumber, @Param("storeId") Integer storeId);

    /**
     * 显示相关商家的所有门店信息
     *
     * @param site_id
     * @return
     */
    List<Store> selectAllStoreByStoresstatus(@Param("site_id") Integer site_id, @Param("name") String name, @Param("storesNumber") String storeNumber, @Param("storeId") Integer storeId, @Param("storesStatus") Integer storesStatus);


    List<Store> selectAllStoreByStatus(@Param("site_id") Integer site_id, @Param("stores_status") Integer stores_status);

    /**
     * 查看指定的门店信息
     *
     * @return
     */
    Store selectByPrimaryKey(@Param("siteId") Integer siteId, @Param("id") Integer id);

    /**
     * 新增门店信息
     *
     * @param map
     * @return
     */
    int insertstores(Map map);

    /**
     * 修改门店信息
     *
     * @return
     */
    int updatestorebymap(Map map);

    Integer updatestoreStatusBystoreId(Map<String, Object> map);

    /**
     * 模糊查询门店信息
     *
     * @param siteId
     * @param name
     * @param storeNumber
     * @param type
     * @param storesStatus
     * @param isQjd
     * @return
     */

    List<Store> selectByFuzzy(@Param("siteId") Integer siteId, @Param("name") String name,
                              @Param("storeNumber") String storeNumber, @Param("type") Integer type,
                              @Param("storesStatus") Integer storesStatus, @Param("isQjd") Integer isQjd,
                              @Param("service_support") String service_support);

    Integer initOwnPricingTypeBySiteId(@Param("initPricingType") Integer ownPricingType, @Param("siteId") Integer siteId);

    Integer updateOwnPricingType(Map<String, Object> map);

    Integer updateUpPricingType(Map<String, Object> map);

    /**
     * 微信端查询某城市门店信息
     */
    List<Store> findShopbyCity(@Param("site_id") Integer siteId, @Param("city") String city);

    Integer selectMaxId(@Param("siteId") Integer siteId);

    List<String> getStoreNamesByStoreIds(@Param("siteId") Integer siteId, @Param("storeIds") List<Integer> storeIds);

    List<String> getstoreidsByStoreNumber(@Param("siteId") Integer siteId, @Param("uids") String uids);

    List<Map<String, Object>> selectStoreIdbyServiceSupport(@Param("siteId") Integer siteId, @Param("storesStatus") Integer storeStatus, @Param("service_support") String serviceSupport);

    List<Store> selectStoreByIds(@Param("siteId") Integer siteId, @Param("ids") String ids, @Param("storesStatus") Integer storesStatus);

    List<Store> selectStoreByCityIds(@Param("siteId") Integer siteId, @Param("cityIds") String cityIds, @Param("storesStatus") Integer storesStatus);

    List<Map> searchStores(@Param("siteId") String siteId, @Param("keyword") String keyword, @Param("city") String city, @Param("storeIds") List<String> storeIds);

    List<Map> queryClicks(@Param("siteId") String siteId, @Param("storeId") String storeId);

    List<Map> queryClickDiss(@Param("siteId") String siteId, @Param("id") String id);

    List<Map> searchStoresPro(@Param("siteId") String siteId, @Param("keyword") String keyword,
                              @Param("storesNumber") String storesNumber, @Param("isQjd") String isQjd,
                              @Param("type") String type, @Param("storesStatus") String storesStatus,
                              @Param("name") String name, @Param("dStatus") String dStatus,@Param("implementationPhase")String implementationPhase);

    int updateStoreEleStatus(Stores stores);

    List<String> selectAllStoresBySiteId(@Param("siteId") Integer siteId);

    List<FollowTask> queryStoreByIds(@Param("siteId") Integer siteId, @Param("ids") int[] ids, @Param("objectName") String objectName);

    List<Map<String, Object>> searchStoresPro(Map<String, Object> param);

    Store selectByStoreId(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("status") Integer storesStatus);

    Integer initUpPricingTypeBySiteId(@Param("initPricingType") Integer upPricingType, @Param("siteId") Integer siteId);

    Map queryTradesTime(@Param("siteId") String siteId, @Param("tradesId") String tradesId);

    List<Map<String, Object>> queryStoresBySiteId(@Param("siteId") int siteId);

    String queryClickOnline(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("storeUserId") String storeUserId);

    List<String> queryStoreCities(@Param("siteId") String siteId);

    String queryStoreName(@Param("siteId") String siteId, @Param("tradesId") String tradesId);

    /**
     * 查询门店编码，多个用逗号隔开
     *
     * @param siteId
     * @param storeIds
     * @return
     */
    String selectStoreNumbers(@Param("siteId") Integer siteId, @Param("ids") String storeIds);

    String selectAllStoreNumbers(@Param("siteId") Integer siteId);

    Map<String, Object> queryStoreAndAdminCount(Map<String, Object> param);

    List<Map<String, Object>> getStoreBySiteId(@Param("siteId") Integer siteId);

    List<Map<String, Object>> getStoreIdBySiteId(@Param("siteId") Integer siteId);

    /**
     * 去除选中的门店ids后的id
     */
    String selectStoreIds(@Param("siteId") Integer siteId, @Param("storeIds") String storeIds);

    String queryStoreQrcodeWx(@Param("siteId") String siteId, @Param("id") String id);

    int updateStoreQrcodeWx(@Param("siteId") String siteId, @Param("id") String id, @Param("store_qrcode_wx") String store_qrcode_wx);

    List<Map<String, Object>> selectStoreByAreaIds(@Param("siteId") Integer siteId, @Param("cityId") Integer cityId,
                                                   @Param("regionId") Integer regionId);

    Integer queryEleStatus(@Param("siteId") String siteId, @Param("id") String id);

    Integer queryEleStatusMeituan(@Param("siteId") String siteId, @Param("id") String id);
    
    List<Integer> queryStoreNumbers(@Param("siteId") Integer siteId);

    String getStoreName(@Param("siteId") Integer siteId,@Param("deviceNum") String deviceNum);

    List<Store> selectStoreAll(@Param("siteId") Integer siteId);
}
