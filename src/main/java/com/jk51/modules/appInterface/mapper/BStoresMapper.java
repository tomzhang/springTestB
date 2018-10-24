package com.jk51.modules.appInterface.mapper;

import com.jk51.model.BStores;
import com.jk51.model.CityHasStores;
import com.jk51.model.Stores;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface BStoresMapper {

    List<BStores> getBStoresListByName(@Param("siteId") String siteId, @Param("storeName") String storeName);


    List<Map<String,String>> getBStoresListByStoreIds(@Param("siteId") Integer siteId, @Param("ids") Set<String> ids);



    /**
     * 新增门店信息
     *
     * @param store
     * @return
     */
    int insertStore(BStores store);

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
     * @param store
     * @return
     */
    int updateStores(BStores store);

    /**
     * 修改门店信息
     *
     * @return
     */
    int updatestorebymap(Map map);


    /**
     * 显示相关商家的所有门店信息
     *
     * @param site_id
     * @return
     */
    List<BStores> selectAllStore(@Param("site_id") Integer site_id, @Param("name") String name, @Param("storesNumber") String storeNumber, @Param("storeId") Integer storeId);

    List<BStores> selectAllStoreByStatus(@Param("site_id") Integer site_id, @Param("stores_status") Integer stores_status);

    /**
     * 查看指定的门店信息
     *
     * @return
     */
    BStores selectByPrimaryKey(@Param("siteId") Integer siteId, @Param("id") Integer id);

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

    List<BStores> selectByFuzzy(@Param("siteId") Integer siteId, @Param("name") String name,
                                @Param("storeNumber") String storeNumber, @Param("type") Integer type,
                                @Param("storesStatus") Integer storesStatus, @Param("isQjd") Integer isQjd,
                                @Param("service_support") String service_support);


    /**
     * 微信端查询某城市门店信息
     */
    List<BStores> findShopbyCity(@Param("site_id") Integer siteId, @Param("city") String city);

    List<CityHasStores> GroupStoresBycity(@Param("site_id") Integer siteId);

    List<CityHasStores> GroupStoresByCityAndServiceSupport(@Param("site_id") Integer siteId, @Param("serviceSupport") String serviceSupport);

    List<BStores> selectStoresByCityAndServiceSupport(@Param("site_id") Integer siteId, @Param("cityId") Integer cityId, @Param("serviceSupport") String serviceSupport);

    BStores selectByPrimaryKey(@Param("siteId") String siteId, @Param("id") String id);

    Integer selectMaxId(@Param("siteId") Integer siteId);

    Integer initOwnPricingTypeBySiteId(@Param("initPricingType") Integer ownPricingType, @Param("siteId") Integer siteId);

    Integer updateOwnPricingType(Map<String, Object> map);

    Integer updatestoreStatusBystoreId(Map<String, Object> map);

    List<Integer> selectByOwnPricingTypeAndSiteId(Integer siteId);

    BStores selectByOwnPricingTypeAndSiteIdAndStoreId(@Param("siteId") Integer siteId, @Param("id") Integer id);

    List<Integer> selectByOwnPromotionTypeAndSiteId(Integer siteId);

    BStores selectByOwnPromotionTypeAndSiteIdAndStoreId(@Param("siteId") Integer siteId, @Param("id") Integer id);

    List<BStores> selectBySiteIdAndCityAndStoreName(@Param("siteId") Integer siteId, @Param("city") String city,
                                                    @Param("storeName") String storeName);

    List<BStores> findShopbyCityID(@Param("siteId") Integer siteId, @Param("cityId") Integer cityId);

    int getStoreCount(@Param("siteId") int siteId);

    BStores getRecomm(@Param("siteId") int siteId, @Param("mobile") String mobile);

    List<Map<String,String>> findBySiteId(Integer site_id);
    List<Stores> getStoreByCityIdAndSiteId(@Param("use_stores") String use_stores, @Param("siteId") Integer siteId);

    List<Stores> getStoreByStoreId(@Param("storeIds") List<String> storeIds,@Param("siteId") Integer siteId);
    List<Stores> getStoreByCityAndSiteId(@Param("cityIds") List<String> cityIds,@Param("siteId") Integer siteId);

    List<Stores> getStoreByStoreIds(@Param("storeIds") List<String> storeIds,@Param("siteId") Integer siteId);

    List<Integer> findStoreIdByCityAndSiteId(@Param("siteId")Integer siteId,@Param("cityIds") List<String> cityIds);

    List<String> storeAreaBySiteId (@Param("siteId") Integer siteId, @Param("type") Integer type);

    List<Map<String,Object>> getStoresByCityId(@Param("cityIds") List<String> cityIds,@Param("siteId") Integer siteId);
}
