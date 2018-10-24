package com.jk51.modules.treat.mapper;

import com.jk51.model.treat.DeliveryMethodTreat;
import com.jk51.model.treat.DeliveryTemplate;
import com.jk51.model.treat.O2OMeta;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: wangcheng
 * 创建日期: 2017-3-4.
 */
@Mapper
public interface DeliveryMapper {
    List<DeliveryMethodTreat> findDeliveryMethods(DeliveryMethodTreat deliveryMethodTreat);

    void updateDeliveryState(DeliveryMethodTreat deliveryMethodTreat);

    void defaltDelivery(DeliveryMethodTreat deliveryMethodTreat);

    void cancledefaltDelivery(DeliveryMethodTreat deliveryMethodTreat);

    List<DeliveryTemplate> queryDeliveryTemplate(DeliveryTemplate deliveryTemplate);

    Map<String,Object> findO2oList(O2OMeta o2OMeta);

    void updateO2OState(O2OMeta o2OMeta);

    void updateRule(O2OMeta o2OMeta);

    void cancleUpdateDeliveryIsActivation(DeliveryMethodTreat deliveryMethodTreat);

    void updateDeliveryDetailForTemplate(DeliveryMethodTreat deliveryMethodTreat);

    void o2oUpdate(O2OMeta meta);

    List<Map> logisticsList(Map map);

    List<Map> queryAreaByArgs(Map map);

    Map queryParentCity(Integer araId);

    void o2oIns(O2OMeta item);

    void updateDelivery(DeliveryMethodTreat item);

    void updateIsActivation(DeliveryMethodTreat deliveryMethodTreat);

    List<Map> queryCommonData(Map map);

    List<Map> deliverySelect(Map map);

    Map findCommonList(Map map);

    int commonIns(Map map);

    int commonUpdate(Map map);

    int commonAdd(Map map);

    int initDelivery(Map map);

    Map<String,Object> findO2oTimeGap(O2OMeta meta);

    void O2oTimeGapIns(O2OMeta meta);

    void O2oTimeGapUpdate(O2OMeta meta);

    String queryDeliveryArrivalTime(Integer siteId);

    List<Map> queryDeliveryBasicFee(@Param("type") String type);
}
