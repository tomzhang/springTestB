package com.jk51.modules.offline.mapper;

import com.jk51.modules.offline.utils.ErpMerchantUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-11-02
 * 修改记录:erp信息对接表
 */
@Mapper
public interface MerchantERPMapper {

    Map<String, Object> selectMerchantERPInfo(@Param("siteId") Integer merchantId);

    List<Map<String, Object>> selectMerchantByStatus(@Param("siteId") Integer siteId, @Param("status") Integer status);

    int updateErpAppliStatus(ErpMerchantUtils erpMerchantUtils);

    List<Map<String, Object>> selectErpMerchantName(@Param("siteId") Integer siteId, @Param("merchantName") String merchantName,
                                                    @Param("status") Integer status);


    Map<String, Object> selectMerchantAppli(@Param("siteId") Integer siteId);

    int insertERPMerchantInfo(ErpMerchantUtils erpMerchantUtils);

    int getGoodsYibao(@Param("siteId") String siteId,@Param("goodsCode") String goodsCode);
    int getStoresYibao(@Param("siteId") String siteId,@Param("storesNumber") String storesNumber);

}
