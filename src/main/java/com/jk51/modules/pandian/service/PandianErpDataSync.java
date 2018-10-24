package com.jk51.modules.pandian.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.BInventories;
import com.jk51.model.BPandianOrder;
import com.jk51.model.Inventories;
import com.jk51.model.erp.PanDianHead;
import com.jk51.modules.offline.service.OfflineCheckService;
import com.jk51.modules.pandian.elasticsearch.repositories.InventoryRepository;
import com.jk51.modules.pandian.job.PandianErpSyncMessage;
import com.jk51.modules.pandian.mapper.BInventoriesMapper;
import com.jk51.modules.pandian.mapper.BPandianOrderMapper;
import com.jk51.modules.pandian.mapper.BPandianOrderStatusMapper;
import com.jk51.modules.pandian.param.PDMessage;
import com.jk51.modules.pandian.param.StoreOrderStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PandianErpDataSync {
    private static final Logger LOGGER = LoggerFactory.getLogger(PandianErpDataSync.class);

    public static final List<Integer> pdErpSiteIdList = Arrays.asList(100166);

    @Autowired
    private PandianErpSyncMessage pandianErpSyncMessage;
    @Autowired
    private OfflineCheckService offlineCheckService;
    @Autowired
    private BPandianOrderMapper bPandianOrderMapper;
    @Autowired
    private BPandianOrderStatusMapper bPandianOrderStatusMapper;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private PandianAsyncService pandianAsyncService;

    @Async
    public void sendMessageAsync(PDMessage data) {
        if (pdErpSiteIdList.contains(data.getSiteId())) {
            try {
                Thread.sleep(2000);
                pandianErpSyncMessage.sendMessage(PDMessage.MESSAGE_TYPE, data);
            } catch (Exception e) {
                LOGGER.error("sendMessageAsync 异常 {}", e);
            }
        } else {
            LOGGER.error("siteId无对应erp");
        }
    }

    public Map<String, Object> pdErpSync(Integer siteId, Integer orderId, List<Integer> storeIds) throws Exception {
        Map<String, Object> result = new HashMap() {{
            put("status", "OK");
        }};
        BPandianOrder order = bPandianOrderMapper.selectByPrimaryKey(siteId, orderId);
        List<StoreOrderStatus> storeStatusList = bPandianOrderStatusMapper.getPDAllStoreStatus(siteId, order.getPlanId(), orderId);
        PanDianHead panDianHead = offlineCheckService.getPanDianStatusFromErp(siteId, order.getPandianNum());
        if (order == null) {
            result.put("status", "ERROR");
            result.put("message", "查无盘点单");
        } else if (!pdErpSiteIdList.contains(siteId)) {
            result.put("status", "ERROR");
            result.put("message", "未对接erp");
        } else if (CollectionUtils.isEmpty(storeStatusList)) {
            result.put("status", "ERROR");
            result.put("message", "无门店盘点单");
        } else if (panDianHead == null || CollectionUtils.isEmpty(panDianHead.getStoreOrderNums())) {
            result.put("status", "ERROR");
            result.put("message", "无ERP门店盘点单");
        } else {
            Map<String, String> erpStoreOrderNumMap = panDianHead.getStoreOrderNums().stream().collect(Collectors.toMap(o -> o.getUnit_no(), o -> o.getBillid(), (v1, v2) -> v2));
            if (CollectionUtils.isNotEmpty(storeIds)) {
                List<Integer> storeIdList = storeIds;
                storeStatusList = storeStatusList.stream()
                    .filter(o -> storeIdList.contains(o.getStoreId()))
                    .filter(o -> o.getStatus() == 0 || o.getStatus() == 100)
                    .filter(o -> erpStoreOrderNumMap.containsKey(o.getStoresNumber()))
                    .collect(Collectors.toList());
            } else {
                storeIds = null;
            }
            if (storeIds != null && storeIds.size() != storeStatusList.size()) {
                result.put("status", "ERROR");
                result.put("message", "ERP未找到对应的盘点作业单，请在ERP操作后再来点击ERP库存读取");
            } else {

                deleteByStoreIdList(siteId, orderId, storeIds);
                List<Integer> storeIdList = storeIds;
                pandianErpSyncMessage.sendPDMessage(new PDMessage() {{
                    setSiteId(siteId);
                    setType(order.getType());
                    setPlanId(order.getPlanId());
                    setOrderId(orderId);
                    setOrderNum(order.getPandianNum());
                    setInsertType(1);
                    setStoreIdList(storeIdList);
                }});
            }
        }
        return result;
    }



    private void deleteByStoreIdList(Integer siteId,Integer orderId,List<Integer> storeIds){

        long start = System.currentTimeMillis();
        inventoryRepository.deleteByStoreIdList(siteId, orderId, storeIds);
        long finishEsDelete = System.currentTimeMillis();
        pandianAsyncService.asyncDeleteByStoreIdList(siteId, orderId, storeIds);
        long finishAsyncDeleteDBDelete = System.currentTimeMillis();

        LOGGER.info("deleteByStoreIdList finishEsDeleteTime:{},finishAsyncDeleteDBDelete:{}",(finishEsDelete-start),(finishAsyncDeleteDBDelete-finishEsDelete));

    }




}
