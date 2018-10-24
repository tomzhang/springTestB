package com.jk51.modules.treat.service;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.treat.DeliveryMethodTreat;
import com.jk51.model.treat.DeliveryTemplate;
import com.jk51.model.treat.O2OMeta;
import com.jk51.modules.merchant.mapper.YbMerchantMapper;
import com.jk51.modules.treat.mapper.DeliveryMapper;
import com.jk51.modules.treat.mapper.MerchantExtTreatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: wangcheng
 * 创建日期: 2017-3-4.
 */
@Service
public class DeliveryService {

    @Autowired
    private DeliveryMapper deliveryMapper;
    @Autowired
    private MerchantExtTreatMapper merchantExtTreatMapper;
    @Autowired
    private YbMerchantMapper ybMerchantMapper;

    private Logger logger = LoggerFactory.getLogger(DeliveryService.class);

    public List<DeliveryMethodTreat> findDeliveryMethods(DeliveryMethodTreat deliveryMethodTreat) {
        return deliveryMapper.findDeliveryMethods(deliveryMethodTreat);
    }

    @Transactional
    public void updateDeliveryState(DeliveryMethodTreat deliveryMethodTreat, int isActivation) {
        deliveryMethodTreat.setIsActivation(isActivation);
        deliveryMapper.updateDeliveryState(deliveryMethodTreat);
    }

    @Transactional
    public void updateDlivaryDetail(DeliveryMethodTreat deliveryMethodTreat) {
        //deliveryMapper.updateDeliveryDetail(deliveryMethodTreat);
        if (deliveryMethodTreat.getPostStyleId() == 130) {
            deliveryMapper.updateDeliveryDetailForTemplate(deliveryMethodTreat);
        }

    }

    public List<DeliveryTemplate> queryDeliveryTemplate(DeliveryTemplate deliveryTemplate) {
        return deliveryMapper.queryDeliveryTemplate(deliveryTemplate);
    }

    public Map findO2oList(O2OMeta o2OMeta) {

        Map<String,Object> o2oMap = deliveryMapper.findO2oList(o2OMeta);
        Map<String,Object> o2oTimeGapMap = deliveryMapper.findO2oTimeGap(o2OMeta);

        if(StringUtil.isEmpty(o2oMap)){
            o2oMap = new HashMap();
        }
        o2oMap.put("o2oTimeGapMap",o2oTimeGapMap);
        return o2oMap;
    }

    @Transactional
    public void defaltDelivery(DeliveryMethodTreat deliveryMethodTreat) {
        deliveryMapper.defaltDelivery(deliveryMethodTreat);
        deliveryMapper.cancledefaltDelivery(deliveryMethodTreat);
    }

    @Transactional
    public void updateO2OState(O2OMeta o2OMeta) {
        deliveryMapper.updateO2OState(o2OMeta);
    }

    @Transactional
    public void updateRule(O2OMeta o2OMeta) {
        deliveryMapper.updateRule(o2OMeta);
    }

    @Transactional
    public void updateDeliverydefault(DeliveryMethodTreat deliveryMethodTreat) {
        //deliveryMapper.updateDeliveryIsActivation(deliveryMethodTreat);
        deliveryMapper.cancleUpdateDeliveryIsActivation(deliveryMethodTreat);
    }

    public void o2oUpdate(O2OMeta meta) {
        Map map = deliveryMapper.findO2oList(meta);

        Map timeGapMap = deliveryMapper.findO2oTimeGap(meta);

        if(StringUtil.isEmpty(meta.getMetaVal())){
            meta.setMetaVal("[]");
        }

        if (StringUtil.isEmpty(map)) {
            deliveryMapper.o2oIns(meta);
        } else  {
            deliveryMapper.o2oUpdate(meta);
        }

        //物流时间段更新
        if(StringUtil.isEmpty(timeGapMap)){
            deliveryMapper.O2oTimeGapIns(meta);
        }else{
            deliveryMapper.O2oTimeGapUpdate(meta);
        }
    }

    @Transactional
    public List<Map> logisticsList(Map map) {
        List<Map> result = deliveryMapper.logisticsList(map);
        if (!StringUtil.isEmpty(result) && result.size()>0) return result;
        String[] styles = { "120","130", "140"};
        String[] names = { "平邮","快递", "EMS"};
        String[] defaults = { "0","1", "0"};
        for (int i = 0; i < styles.length; i++) {
            Map param = new HashMap();
            param.put("siteId", getMapValue(map, "siteId"));
            param.put("name", names[i]);
            param.put("style", styles[i]);
            param.put("defaultFlag", defaults[i]);
            deliveryMapper.initDelivery(param);
        }
        return deliveryMapper.logisticsList(map);
    }

    public List<Map> queryAreaByArgs(Map map) {
        if (map.containsKey("areaId") || map.containsKey("parentId") || map.containsKey("type")) {
            return deliveryMapper.queryAreaByArgs(map);
        }
        return null;
    }

    public Map queryParentCity(Integer areaId) {
        return deliveryMapper.queryParentCity(areaId);
    }

    /**
     * 设置启用暂停
     */
    public void updateIsActivation(DeliveryMethodTreat item) {
        deliveryMapper.updateIsActivation(item);
    }

    public void o2oIns(O2OMeta item) {
        deliveryMapper.o2oIns(item);
    }

    public void updateDelivery(DeliveryMethodTreat item) {
        deliveryMapper.updateDelivery(item);
    }

    public List<Map> queryCommonData(Map map) {
        return deliveryMapper.queryCommonData(map);
    }

    /**
     * 查询物流接口
     */
    public List<Map> queryCommonDataService(Map map) {

        List<Map> data = deliveryMapper.queryCommonData(map);//全部
        Map meta = deliveryMapper.findCommonList(map);//常用
        if (!StringUtil.isEmpty(meta)) {
            List sortList = new ArrayList();
            List<Map> result = new ArrayList<>();
            String metaVal = getMapValue(meta, "meta_val");

            for (int i = 0; i < data.size(); i++) {
                for (String str : metaVal.split(",")) {
                    if (str.equals(getMapValue(data.get(i), "id"))) {
                        sortList.add(str);
                    }
                }
            }

            for (int m = 0; m < data.size(); m++) {
                for (Object id : sortList) {
                    if (getMapValue(data.get(m), "id").equals(id))
                        result.add(data.get(m));
                }
            }

            for (int n = 0; n < data.size(); n++) {
                boolean flag = true;
                for (Object id : sortList) {
                    if (getMapValue(data.get(n), "id").equals(id))
                        flag = false;
                }
                if (flag)
                    result.add(data.get(n));
            }

            return result;
        }

        return data;
    }

    public String getMapValue(Map map, String key) {
        if (StringUtil.isEmpty(map)) return "";
        if (map.containsKey(key) && !StringUtil.isEmpty(map.get(key))) {
            return map.get(key).toString().toUpperCase();
        }
        return "";
    }

    public List<Map> deliverySelect(Map map) {
        return deliveryMapper.deliverySelect(map);
    }

    public Map findCommonList(Map map) {
        return deliveryMapper.findCommonList(map);
    }

    public void commonIns(Map map) {
        deliveryMapper.commonIns(map);
    }

    public void commonUpdateService(Map map) {
        if (StringUtil.isEmpty(deliveryMapper.findCommonList(map))) {
            deliveryMapper.commonIns(map);
            return;
        }
        deliveryMapper.commonUpdate(map);
    }

    public void commonAdd(Map map) {
        deliveryMapper.commonAdd(map);
    }


    /**
     * 获取第三方物流收费规则
     * */
    public String getDeliveryConf(O2OMeta o2OMeta){

        String result = "";

        try{
            Map o2oTimeGapMap = deliveryMapper.findO2oTimeGap(o2OMeta);
            Map status = merchantExtTreatMapper.queryLogisticsFlag(o2OMeta.getSiteId().toString());
            Map meta = ybMerchantMapper.queryDefaulto2o(o2OMeta.getSiteId().toString());


            // 如果'51jk后台默认物流设置（0：关闭，1：启用）关闭
            if(status.get("logistics_flag_jk").equals(0)){
                return result;
            }


            boolean isThirdParty = status.get("logistics_flag_mode").equals(0);
            boolean isMerchant = status.get("logistics_flag_mode").equals(1);

            boolean timeGapIsEmpty = StringUtil.isEmpty(o2oTimeGapMap);

            if(!StringUtil.isEmpty(meta)&&StringUtil.isEmpty(o2oTimeGapMap)){
                return meta.get("meta_val").toString();
            }
            boolean isInTimeGap = inTimeGap(o2OMeta,o2oTimeGapMap.get("meta_val").toString());

            //设置为第三方物流收费规则,且没有时间段设置
            if(isThirdParty&&timeGapIsEmpty){
                return meta.get("meta_val").toString();
            }

            //设置为第三方物流收费规则,有时间段设置,下单时间在时间段内
            if(isThirdParty&&!timeGapIsEmpty&&isInTimeGap){
                return meta.get("meta_val").toString();
            }

            //设置为商户收费规则,有时间段设置,下单时间不在时间段设置内
            if(isMerchant&&!timeGapIsEmpty&&!isInTimeGap){
                return meta.get("meta_val").toString();
            }
        }catch (Exception e) {
            logger.info("物流错误：{}", e);
        }


      return result;

    }

    //判断下单时间是否在设置的时间段内
    private boolean inTimeGap(O2OMeta o2OMeta, String timeGapStr){

        if(StringUtil.isEmpty(timeGapStr)){
            return false;
        }

        Map<String,Object> timeGap = null;

        try {
            timeGap =  JacksonUtils.json2map(timeGapStr) ;
        } catch (Exception e) {

            logger.error("timeGapStr,{} :json转换失败",timeGapStr);
            return false;
        }

        Date o2oStartTime = null;
        Date o2oEndTime = null;
        try {
            o2oStartTime = DateUtils.parse(timeGap.get("o2oStartTime").toString(),"HH:mm");
            o2oEndTime =  DateUtils.parse(timeGap.get("o2oEndTime").toString(),"HH:mm");
        } catch (ParseException e) {

            logger.error("o2oStartTime,{};o2oEndTime,{} :Date转换失败",timeGap.get("o2oStartTime"),timeGap.get("o2oEndTime"));
            return false;
        }

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(o2oStartTime);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(o2oEndTime);

        Calendar orderTimeCalendar = Calendar.getInstance();
        orderTimeCalendar.setTime(o2OMeta.getOrderTime());
        orderTimeCalendar.set(Calendar.YEAR,1970);
        orderTimeCalendar.set(Calendar.MONTH,Calendar.JANUARY);
        orderTimeCalendar.set(Calendar.DAY_OF_MONTH,01);

        boolean afterStart =  orderTimeCalendar.after(startCalendar);
        boolean beforeEnd = orderTimeCalendar.before(endCalendar);

        if(afterStart&&beforeEnd){
            return true;
        }else {
            return false;
        }

    }

    public String queryDeliveryArrivalTime(Integer siteId){
        String arrivalTime = deliveryMapper.queryDeliveryArrivalTime(siteId);
        /*if(StringUtil.isEmpty(arrivalTime)){
            throw new RuntimeException("启用的默认快递的到货时间为空");
        }*/
        return arrivalTime;
    }
}
