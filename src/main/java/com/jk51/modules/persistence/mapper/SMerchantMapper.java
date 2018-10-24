package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SMerchant;
import com.jk51.model.treat.MerchantVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface SMerchantMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SMerchant record);

    int insertSelective(SMerchant record);

    SMerchant selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SMerchant record);

    int updateByPrimaryKey(SMerchant record);

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

    SMerchant selectByMerchantId(Integer merchantId);

    int updateByMerchantId(SMerchant merchant);

    SMerchant getMerchant(String merchant_id);

    SMerchant selectBySiteId(@Param("merchantId") Integer merchantId);

    List<SMerchant> getMerchants(@Param("merchantId") Integer siteId, @Param("username") String username, @Param("password") String password);

    int updatePassword(@Param("merchantId") Integer merchantId, @Param("id") Integer id, @Param("password") String password);

    int updateInvoice(@Param("merchant_id") Integer merchantId, @Param("invoice_is") Integer invoice_is);

    int updateSeller(Map param);

    int insSeller(Map param);

    Map<String,Object> querySeller(Map param);

    Map<String,String> queryByUrl(String url);

    Integer selectStatus(@Param("siteId") Integer siteId);

    String queryMerchantName(@Param("siteId") Integer siteId);
}
