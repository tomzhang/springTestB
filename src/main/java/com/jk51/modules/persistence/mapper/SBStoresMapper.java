package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SBStores;
import com.jk51.model.order.SCityHasStores;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SBStoresMapper {

    List<SBStores> getBStoresListByName(@Param("siteId") String siteId, @Param("storeName") String storeName);

    /**
     * 新增门店信息
     *
     * @param store
     * @return
     */
    int insertStore(SBStores store);

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
    int updateStores(SBStores store);

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
    List<SBStores> selectAllStore(@Param("site_id") Integer site_id, @Param("name") String name, @Param("storesNumber") String storeNumber, @Param("storeId") Integer storeId);

    List<SBStores> selectAllStoreByStatus(@Param("site_id") Integer site_id, @Param("stores_status") Integer stores_status);

    /**
     * 查看指定的门店信息
     *
     * @return
     */
    SBStores selectByPrimaryKey(@Param("siteId") Integer siteId, @Param("id") Integer id);

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

    List<SBStores> selectByFuzzy(@Param("siteId") Integer siteId, @Param("name") String name,
                                @Param("storeNumber") String storeNumber, @Param("type") Integer type,
                                @Param("storesStatus") Integer storesStatus, @Param("isQjd") Integer isQjd,
                                @Param("service_support") String service_support);


    /**
     * 微信端查询某城市门店信息
     */
    List<SBStores> findShopbyCity(@Param("site_id") Integer siteId, @Param("city") String city);

    List<SCityHasStores> GroupStoresBycity(@Param("site_id") Integer siteId);

    List<SCityHasStores> GroupStoresByCityAndServiceSupport(@Param("site_id") Integer siteId, @Param("serviceSupport") String serviceSupport);

    List<SBStores> selectStoresByCityAndServiceSupport(@Param("site_id") Integer siteId, @Param("cityId") Integer cityId, @Param("serviceSupport") String serviceSupport);

    SBStores selectByPrimaryKey(@Param("siteId") String siteId, @Param("id") String id);

    Integer selectMaxId(@Param("siteId") Integer siteId);

    Integer initOwnPricingTypeBySiteId(@Param("initPricingType") Integer ownPricingType, @Param("siteId") Integer siteId);

    Integer updateOwnPricingType(Map<String, Object> map);

    Integer updatestoreStatusBystoreId(Map<String, Object> map);

    List<Integer> selectByOwnPricingTypeAndSiteId(Integer siteId);

    SBStores selectByOwnPricingTypeAndSiteIdAndStoreId(@Param("siteId") Integer siteId, @Param("id") Integer id);

    List<Integer> selectByOwnPromotionTypeAndSiteId(Integer siteId);

    SBStores selectByOwnPromotionTypeAndSiteIdAndStoreId(@Param("siteId") Integer siteId, @Param("id") Integer id);

    List<SBStores> selectBySiteIdAndCityAndStoreName(@Param("siteId") Integer siteId, @Param("city") String city,
                                                    @Param("storeName") String storeName);

    List<SBStores> findShopbyCityID(@Param("siteId") Integer siteId, @Param("cityId") Integer cityId);

    int getStoreCount(@Param("siteId") int siteId);

    SBStores getRecomm(@Param("siteId") int siteId, @Param("mobile") String mobile);
    List<Map<Object,Object>> getCityLabelBySiteId(Integer siteId);

    List<Map<Object,Object>> getAreaLabelByCityId(Integer city_id, Integer siteId);

    List<SBStores> getStore(@Param("siteId") Integer siteId, @Param("name") String name);
}
