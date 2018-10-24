package com.jk51.modules.merchant.mapper;

import com.jk51.model.merchant.MerchantApplyDto;
import com.jk51.model.merchant.YbMerchant;
import com.jk51.model.treat.MerchantVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface YbMerchantMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(YbMerchant record);

    int insertSelective(YbMerchant record);

    YbMerchant selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(YbMerchant record);

    int updateByPrimaryKey(YbMerchant record);

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

    YbMerchant selectByMerchantId(Integer merchantId);

    int updateByMerchantId(YbMerchant merchant);

    YbMerchant getMerchant(String merchant_id);

    YbMerchant selectBySiteId(@Param("merchantId") Integer merchantId);

    List<YbMerchant> getMerchants(@Param("merchantId") Integer siteId, @Param("username") String username, @Param("password") String password);

    int updatePassword(@Param("merchantId") Integer merchantId, @Param("id") Integer id, @Param("password") String password);

    int updateInvoice(@Param("merchant_id") Integer merchantId, @Param("invoice_is") Integer invoice_is);

    int updateSeller(Map param);

    int insSeller(Map param);

    Map<String, Object> querySeller(Map param);

    Map<String, String> selectAreaByAreaId(Integer areaId);

    Map<String, String> queryByUrl(String url);

    Integer selectStatus(@Param("siteId") Integer siteId);

    Map queryAliPayInfo(@Param("siteId") String siteId);

    Map queryAliPayInfoByAppId(@Param("appId") String appId);

    Map queryDefaulto2o(@Param("siteId") String siteId);

    int insertDefaulto2o(@Param("siteId") String siteId, @Param("metaVal") String metaVal);

    int updateDefaulto2o(@Param("siteId") String siteId, @Param("metaVal") String metaVal);

    List<MerchantApplyDto> getUntreatedApplyList();

    List<MerchantApplyDto> getAllApplyList();

    List<MerchantApplyDto> selectApplys(@Param("company_name") String company_name,@Param("legal_name") String legal_name,
    @Param("legal_mobile") String legal_mobile,@Param("approval_status") String approval_status);

    void approvalAllow(@Param("merchant_id")Integer merchant_id, @Param("approval_status")Integer approval_status,@Param("seller_pwd")String seller_pwd,@Param("site_status")Integer site_status);

    void signContract(@Param("merchant_id")Integer merchant_id, @Param("is_sign")Integer is_sign,@Param("sign_contract")String sign_contract);
}
