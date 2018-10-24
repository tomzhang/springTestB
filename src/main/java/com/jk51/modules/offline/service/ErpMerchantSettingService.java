package com.jk51.modules.offline.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.offline.mapper.BFaultStatisticsMapper;
import com.jk51.modules.offline.mapper.MerchantERPMapper;
import com.jk51.modules.offline.utils.ErpMerchantUtils;
import com.jk51.modules.offline.utils.FaultERPParams;
import com.jk51.modules.offline.utils.FaultStaticsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-08-02
 * 修改记录:
 */
@Service
public class ErpMerchantSettingService {
    @Autowired
    private MerchantERPMapper merchantERPMapper;
    @Autowired
    private BFaultStatisticsMapper faultStatisticsMapper;
    @Autowired
    private ErpToolsService erpToolsService;

    /**
     * 获取erp对接商户的信息
     *
     * @param siteId
     * @param status
     * @return
     */
    public List<Map<String, Object>> getErpMerchantInfo(Integer siteId, Integer status) {
        List<Map<String, Object>> merchantList = merchantERPMapper.selectMerchantByStatus(siteId, status);
        return merchantList;
    }

    @Transactional
    public int updateErpAppliStatus(ErpMerchantUtils merchantUtils) {
        return merchantERPMapper.updateErpAppliStatus(merchantUtils);
    }

    public List<Map<String, Object>> selectErpMerchantName(Integer siteId, String merchantName, Integer status) {
        return merchantERPMapper.selectErpMerchantName(siteId, merchantName, status);
    }

    public List<Map<String, Object>> selectFaultInfoById(String ids) {
        List<Integer> faultIds = StringUtil.convertToIdsList(ids).get();
        return faultStatisticsMapper.selectFaultInfoById(faultIds);
    }

    @Transactional
    public int insertFaultStatics(Integer siteId, String appli, Integer type, Integer faultType, String reason,
                                  String faultDetails, String pushInfo, Integer isPush) {
        if (type == 1) {
            Integer id = faultStatisticsMapper.findByPushInfo(siteId, type, pushInfo);
            if (StringUtil.isEmpty(id)) {
                return insertIntoFaultStatics(siteId, appli, type, faultType, reason, faultDetails, pushInfo, isPush);
            } else {
                return updateFaultStaticsType(id, siteId, appli, type, faultType, reason, faultDetails, isPush);
            }
        } else {
            return insertIntoFaultStatics(siteId, appli, type, faultType, reason, faultDetails, pushInfo, isPush);
        }
    }

    public int insertIntoFaultStatics(Integer siteId, String appli, Integer type, Integer faultType, String reason,
                                      String faultDetails, String pushInfo, Integer isPush) {
        if (faultType != 200) {
            erpToolsService.sendError(siteId, appli, reason + "pushInfo:" + pushInfo, faultDetails, type);
        }
        FaultStaticsUtils faultStaticsUtils = new FaultStaticsUtils();
        faultStaticsUtils.setSiteId(siteId);
        faultStaticsUtils.setType(type);
        faultStaticsUtils.setFaultType(faultType);
        faultStaticsUtils.setFaultDetails(faultDetails);
        faultStaticsUtils.setPushInfo(pushInfo);
        faultStaticsUtils.setIsPush(isPush);
        return faultStatisticsMapper.insertStatics(faultStaticsUtils);
    }

    @Transactional
    public int updateFaultStaticsType(Integer id, Integer siteId, String appli, Integer type, Integer faultType,
                                      String reason, String faultDetails, Integer isPush) {
        if (faultType != 200) {
            erpToolsService.sendError(siteId, appli, reason, faultDetails, type);
        }
        return faultStatisticsMapper.updateStaticsStatus(id, siteId, faultType, faultDetails, isPush);
    }

    @Transactional
    public PageInfo selectErpFault(Integer siteId, Integer type, String merchantName, String startTime, String endTime,
                                   Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);//开启分页
        List<Map<String, Object>> faultList = faultStatisticsMapper.findFaultStatics(siteId, merchantName, type, startTime, endTime);
        PageInfo pageInfo = new PageInfo<>(faultList);
        for (Object info : pageInfo.getList()) {
            Map<String, Object> faultInfo = (Map<String, Object>) info;
            Integer nowtype = Integer.parseInt(((Map<String, Object>) info).get("type").toString());
            Integer faultType = Integer.parseInt(((Map<String, Object>) info).get("faultType").toString());
            faultInfo.put("appliTypeName", FaultERPParams.getERPProperty(nowtype));
            faultInfo.put("faultPheno", FaultERPParams.getERPFaultPheno(nowtype));
            faultInfo.put("faultReason", FaultERPParams.getERPFaultReason(nowtype, faultType));
        }
        return pageInfo;
    }

    public Integer insertERpMerchantInfo(ErpMerchantUtils erpMerchantUtils) {
        return merchantERPMapper.insertERPMerchantInfo(erpMerchantUtils);
    }


}
