package com.jk51.modules.treat.mapper;

import com.jk51.model.treat.MerchantTreat;
import com.jk51.model.treat.MerchantVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface MerchantMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MerchantTreat record);

    int insertSelective(MerchantTreat record);

    MerchantTreat selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MerchantTreat record);

    int updateByPrimaryKey(MerchantTreat record);

    List<MerchantVO> selectSeletive(@Param("name") String name,
                                    @Param("merchantId") Integer merchantId,
                                    @Param("status") Integer status,
                                    @Param("from") Date from,
                                    @Param("to") Date to,
                                    @Param("type") Integer setType,
                                    @Param("lastTime") Date thelastTime,
                                    @Param("valuea") Integer valuea,
                                    @Param("valueb") Integer valueb);

    Integer selectMerchantId();

    MerchantTreat selectByMerchantId(Integer merchantId);

    int updateByMerchantId(MerchantTreat MerchantTreat);

    MerchantTreat getMerchant(String merchant_id);

    String getMerchantTitle(String merchant_id);

    MerchantTreat selectBySiteId(@Param("merchantId") Integer merchantId);

    List<MerchantTreat> getMerchants(@Param("merchantId") Integer siteId, @Param("username") String username, @Param("password") String password);

    int updatePassword(@Param("merchantId") Integer merchantId, @Param("id") Integer id, @Param("password") String password);

    int updateInvoice(@Param("merchant_id") Integer merchantId, @Param("invoice_is") Integer invoice_is);

    int updateSeller(Map param);

    int insSeller(Map param);

    Map<String, Object> querySeller(Map param);

    Map<String, String> queryByUrl(String url);

    Integer selectStatus(@Param("siteId") Integer siteId);

    List<Map> queryStoreDelivery(@Param("siteId") String siteId);

    int addStoreDelivery(@Param("siteId") String siteId, @Param("storeId") String storeId);

    int updateStoreDelivery(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("postStyle") Integer postStyle);

    Map isExist(@Param("siteId") String siteId, @Param("storeId") String storeId);

    Map<String,Object> selectInfoByMerchantId(@Param("merchantId") int merchantId);
    List<Integer> selectSiteids();

    List<Map<String,Object>> getWXStoreQRUrl(@Param("siteId") Integer siteId, @Param("storeName") String storeName);

    int addStoreDeliveryMeituan(@Param("siteId") String siteId, @Param("storeId") String storeId);

    int updateDeliveryMeituanFun(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("flag") Integer flag);

    int addStoreDeliveryShi(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("flag") Integer flag);

    int updateDeliveryShiFun(@Param("siteId") String siteId, @Param("storeId") String storeId, @Param("flag") Integer flag);
}
