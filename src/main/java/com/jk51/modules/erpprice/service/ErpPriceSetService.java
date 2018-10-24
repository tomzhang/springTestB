package com.jk51.modules.erpprice.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.configuration.PathUrlConfig;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.erpprice.BErpSetting;
import com.jk51.model.erpprice.BErpSettingExample;
import com.jk51.model.erpprice.ErpPriceSetting;
import com.jk51.model.erpprice.ErpPriceSettingExample;
import com.jk51.modules.erpprice.domain.dto.BErpSettingDTO;
import com.jk51.modules.erpprice.domain.pojo.ErpSettingPO;
import com.jk51.modules.erpprice.domain.pojo.ErpStorePO;
import com.jk51.modules.erpprice.mapper.BErpSettingMapper;
import com.jk51.modules.erpprice.mapper.ErpPriceSettingMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.offline.service.GoodsERPServices;
import com.jk51.modules.persistence.mapper.SYbStoresGoodsPriceMapper;
import org.apache.mina.util.ExpiringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron
 * 创建日期: 2017-10-30 16:34
 * 修改记录:
 */
@Service
public class ErpPriceSetService {

    @Autowired
    StoresMapper storesMapper;
    @Autowired
    BErpSettingMapper bErpSettingMapper;
    @Autowired
    ErpPriceSettingMapper erpPriceSettingMapper;
    @Autowired
    SYbStoresGoodsPriceMapper storesGoodsPriceMapper;

    private final static Logger LOGGER = LoggerFactory.getLogger(ErpPriceSetService.class);


    public List<Map<String, Object>> getStoreBySiteId(Integer siteId) {
        return storesMapper.getStoreBySiteId(siteId);
    }

    /**
     * 插入商户区域指定价格的门店
     *
     * @param bErpSettingDTO
     * @return
     * @throws BusinessLogicException
     */
    @Transactional(rollbackFor = BusinessLogicException.class)
    public Boolean saveErpPriceSetting(BErpSettingDTO bErpSettingDTO) throws BusinessLogicException {
        try {
            ObjectMapper objectMapper = JacksonUtils.getInstance().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            //如果已經有指定区域价格了就删除
            ErpPriceSettingExample erpPriceSettingExample = new ErpPriceSettingExample();
            erpPriceSettingExample.createCriteria().andSiteIdEqualTo(bErpSettingDTO.getSiteId()).andStatusEqualTo((byte) 10);
            if (erpPriceSettingMapper.selectByExample(erpPriceSettingExample).size() > 0) {
                ErpPriceSetting erpPriceSetting = new ErpPriceSetting();
                erpPriceSetting.setStatus((byte) 20);
                if (erpPriceSettingMapper.updateByExampleSelective(erpPriceSetting, erpPriceSettingExample) < 0)
                    throw new BusinessLogicException("商户区域指定价格的门店删除失败");
            }

            //获取数据
            BErpSetting bErpSetting = objectMapper.convertValue(bErpSettingDTO, BErpSetting.class);

            //设置区域价格的门店
            if (bErpSettingMapper.insertOrUpdate(bErpSetting) > 0)
                if (bErpSettingDTO.getType() != (byte) 40) {
                    ErpPriceSetting[] erpPriceSettings = objectMapper.convertValue(bErpSettingDTO.getErpPriceSettingDTOs(), ErpPriceSetting[].class);
                    if (erpPriceSettingMapper.insertBatch(Arrays.asList(erpPriceSettings), bErpSettingDTO.getSiteId()) > 0)
                        return true;
                    else throw new BusinessLogicException("商户区域指定价格的门店插入失败");
                } else return true;
            else throw new BusinessLogicException("商户区域价格类型插入失败");
        } catch (Exception e) {
            LOGGER.error("ERP对接设置失败 -> {}", e);
            throw new BusinessLogicException("ERP对接设置失败 -> " + e.getMessage());
        }
    }

    /**
     * @param siteId
     * @return
     */
    public Map<String, Object> getSettingDetailBySiteId(Integer siteId) throws BusinessLogicException {
        ErpStorePO erpStorePO = bErpSettingMapper.selectErpStore(siteId);

        if (erpStorePO == null) {
            return new HashMap<String, Object>() {{
                put("type", -1);
                put("isJoint", 20);
                put("data", null);
            }};
        }

        List<ErpSettingPO> erpSettingPOs = erpStorePO.getErpSettings();

        if (erpStorePO.getType() != null && erpStorePO.getType() != 40) {
            if (erpSettingPOs == null && erpSettingPOs.size() <= 0) {
                return new HashMap<String, Object>() {
                    {
                        put("type", erpStorePO.getType());
                        put("isJoint", erpStorePO.getIsJoint());
                        put("data", new ArrayList<ErpSettingPO>());
                    }
                };
            } else {
                return new HashMap<String, Object>() {{
                    put("type", erpStorePO.getType());
                    put("isJoint", erpStorePO.getIsJoint());
                    put("data", erpSettingPOs
                        .stream()
                        .filter(e -> e.getStoresStatus() == 1)
                        .map(erpSettingPO -> {
                            ErpPriceSetting erpPriceSetting = new ErpPriceSetting();
                            erpPriceSetting.setSiteId(siteId);
                            erpPriceSetting.setType(erpStorePO.getType());
                            erpPriceSetting.setStoreId(erpSettingPO.getStoreId());
                            erpPriceSetting.setAreaCode(erpSettingPO.getAreaCode());
                            erpPriceSetting.setPriority((byte) erpSettingPO.getPriority().intValue());
                            return erpPriceSetting;
                        })
                        .collect(groupingBy(ErpPriceSetting::getAreaCode)));
                }};
            }

        } else if (erpStorePO != null && erpStorePO.getType() != null && erpStorePO.getType() == 40) {
            return new HashMap<String, Object>() {{
                put("type", erpStorePO.getType());
                put("isJoint", erpStorePO.getIsJoint());
                put("data", new HashMap<Integer, List<ErpPriceSetting>>() {{
                    put((int) erpStorePO.getType(), new ArrayList<ErpPriceSetting>() {{
                        add(new ErpPriceSetting(siteId, (byte) 40));
                    }});
                }});
            }};
        } else
            throw new BusinessLogicException("获取erp对接设置的价格失败");
    }

    //重置多价格设置
    public int cancelSetting(Integer siteId) {
        //之前的价格方案设置失效
        int priceSetting = erpPriceSettingMapper.updateSettingStatus(20, 0, siteId);
        int setting = bErpSettingMapper.updateType(0, siteId);
        //同时门店助手的价格根据是否修改的状态进行操作，selfFag字段为0的商品价格清除为null。修改过的价格不变
        storesGoodsPriceMapper.updateFlag2(null, siteId, null, null, 0);
        return priceSetting;
    }
}
