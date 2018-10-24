package com.jk51.modules.merchant.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.merchant.MerchantExt;
import com.jk51.model.order.Meta;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.merchant.mapper.MerchantExtMapper;
import com.jk51.modules.merchant.mapper.MetaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-14
 * 修改记录:
 */
@Service
public class MerchantExtService {


    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantExtService.class);
    @Autowired
    MerchantExtMapper merchantExtMapper;

    @Autowired
    MetaMapper metaMapper;

    @Autowired
    StoresMapper storesMapper;

    public MerchantExt findByMerchantId(Integer merchantId) {
        return merchantExtMapper.selectByMerchantId(merchantId);
    }

    @Transactional
    public int updateByMerchantId(MerchantExt merchantExt) {
        int result = merchantExtMapper.updateByMerchantId(merchantExt);
        if (result != 1)
            throw new RuntimeException("更新订单提醒设置失败");
        return result;
    }

    /**
     * 获取门店分单权限
     *
     * @param siteId
     * @return
     */
    @Transactional
    public Map selectassignAll(Integer siteId) {
        Map map = new HashMap();
        Object order_assign_type = merchantExtMapper.selectByMerchantId(siteId).getOrder_assign_type();
        Meta meta = metaMapper.selectByMetaTypeAndKey(siteId, "auto_assign_type", "assign_store_ids");
        String storeidss = "";
        String ids = "";
        if (!StringUtil.isEmpty(meta) && !meta.getMetaVal().equals("NULL") && !StringUtil.isEmpty(meta.getMetaVal())) {
            ids = meta.getMetaVal();
            if (ids.equals("all")) {
                List<Map> mapList = storesMapper.selectServiceSupport(siteId);
                for (Map m : mapList) {
                    storeidss += Integer.parseInt(m.get("id").toString()) + ",";
                }
            } else {
                storeidss = ids;
            }
        }
        if (!StringUtil.isEmpty(order_assign_type)) {
            map.put("orderType", order_assign_type);
            Integer order_type = Integer.parseInt(order_assign_type.toString());
            if (order_type == 130) {//定时分单模式
                String time = metaMapper.selectBysiteIdAndMetaType(siteId, "order_assign_type").getMetaVal();
                String[] timein = time.split("--");
                try {
                    DateFormat format = new SimpleDateFormat("HH:mm");
                    Date minDate = format.parse(timein[0]);
                    Date now = format.parse(format.format(new Date()));
                    Date maxDate = format.parse(timein[1]);
                    if (!(now.getTime() >= minDate.getTime() && now.getTime() <= maxDate.getTime())) {//当天;
                        storeidss = "";
                    }
                } catch (Exception e) {

                }
            }
            map.put("storeIds", storeidss);
        }
        return map;
    }


    /**
     * 获取门店是否具有分单权限（送货上门）
     *
     * @param siteId
     * @return map orderType:分单类型，storeIds：可分单门店
     */
    public Map selectassign(Integer siteId) {
        Map<String, Object> map = new HashMap();
        String storeidss = "";
        map = selectassignAll(siteId);
        if (!StringUtil.isEmpty(map.get("storeIds"))) {
            String ids = map.get("storeIds").toString();
            LOGGER.info("具有分单权限的门店id" + ids);
            if (!"".equals(ids) && ids != null) {
                Map<String, List<Integer>> maplist = selectServiceSupport(siteId);
                List<Integer> idlist = maplist.get("todoor");
                String[] id = ids.split(",");
                for (String i : id) {
                    if (idlist.contains(Integer.parseInt(i))) {
                        storeidss += i + ",";
                    }
                }
            }
        }
        LOGGER.info("具有分单权限的送货上门门店id" + storeidss);
        map.put("storeIds", storeidss);
        return map;
    }

    /**
     * @param siteId
     * @return since:上门自提门店ids；todoor：送货上门门店ids
     */
    public Map selectServiceSupport(Integer siteId) {
        List<Map> mapList = storesMapper.selectServiceSupport(siteId);
        Map<String, List<Integer>> map = new HashMap<>();
        List<Integer> selfList = new ArrayList<>();//上门自提160
        List<Integer> toDoorList = new ArrayList<>();//送货上门150
        for (Map m : mapList) {
            if (!StringUtil.isEmpty(m.get("service_support"))) {
                if (m.get("service_support").toString().indexOf("160") > -1) {
                    selfList.add(Integer.parseInt(m.get("id").toString()));
                }
                if (m.get("service_support").toString().indexOf("150") > -1) {
                    toDoorList.add(Integer.parseInt(m.get("id").toString()));
                }
            }
        }
        map.put("since", selfList);
        map.put("todoor", toDoorList);
        return map;
    }

    //获取门店是否具有门店自提(160)
    public Map selectToself(Integer siteId) {
        Map<String, Object> map = new HashMap();
        String storeidss = "";
        map = selectassignAll(siteId);
        if (!StringUtil.isEmpty(map.get("storeIds"))) {
            String ids = map.get("storeIds").toString();
            LOGGER.info("具有分单权限的门店id" + ids);
            if (!"".equals(ids) && ids != null) {
                Map<String, List<Integer>> maplist = selectServiceSupport(siteId);
                List<Integer> idlist = maplist.get("since");
                if (idlist.size() > 0) {
                    String[] id = ids.split(",");
                    for (String i : id) {
                        if (idlist.contains(Integer.parseInt(i))) {
                            storeidss += i + ",";
                        }
                    }
                }
            }
        }
        LOGGER.info("具有分单权限的门店自提门店id" + storeidss);
        map.put("storeIds", storeidss);
        return map;
    }

    /**
     * 获取具有具有总仓权限的门店ids
     *
     * @param siteId
     * @return
     * @metaType site_general_warehouse_config
     */
    public String selectidsBywarehouseFromMeta(Integer siteId) {
        String ids = "";
        Meta meta = metaMapper.selectByMetaTypeAndKey(siteId, "site_general_warehouse_config",
                "site_general_warehouse_config");
        if (!StringUtil.isEmpty(meta) && !meta.getMetaVal().equals("NULL") && !StringUtil.isEmpty(meta.getMetaVal())) {
            ids = meta.getMetaVal();
        }
        return ids;
    }

    /**
     * 获取门店相关信息列表
     */
    public List<Map<String, Object>> selectStoresBystoreSupport(Integer siteId, String serviceSupport) {
        List<Map<String, Object>> storeList = storesMapper.selectStoreIdbyServiceSupport(siteId, 1, serviceSupport);
        LOGGER.info("获取的门店信息{}", storeList.toString());
        return storeList;
    }

    public int integralShopManger(Map<String,Object> map){
        return merchantExtMapper.integralShopManger(map);
    }

    public Map<String, Object> getIntegralShopStatus(int siteId){
        return merchantExtMapper.getIntegralShopStatus(siteId);
    }
}
