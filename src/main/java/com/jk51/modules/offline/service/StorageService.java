package com.jk51.modules.offline.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.commons.xml.XmlUtils;
import com.jk51.erpDataConfig.DataSourceConfig_RuiSen;
import com.jk51.erpDataConfig.foxin.DataSourceConfig_FoXin;
import com.jk51.erpDataConfig.zhonglian.DataSourceConfig_ZhongLian;
import com.jk51.model.order.GoodsInfo;
import com.jk51.model.order.Store;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.offline.mapper.MerchantERPMapper;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-06-21
 * 修改记录:
 */
@Service
public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);
    @Autowired
    private MerchantERPMapper merchantERPMapper;
    @Autowired
    private BaoDaoOfflineService baoDaoOfflineService;
    @Autowired
    private ErpToolsService erpToolsService;
    @Resource
    private DataSourceConfig_ZhongLian dataSoureConfig_zhongLian;
    @Resource
    private DataSourceConfig_FoXin dataSourceConfig_foXin;
    @Resource
    private DataSourceConfig_RuiSen dataSourceConfig_ruiSen;
    @Autowired
    private StoresService storesService;
    @Autowired
    private ErpMerchantSettingService merchantSettingService;

//    @TimeRequired
    public Map<String, Object> getStorageBysiteId(String siteId, String goodsNo, String uid) {
        Map<String, Object> erpMap = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(siteId));
        if (!erpMap.containsKey("status") || !erpMap.get("status").toString().equals("1")) {
            return null;
        }
        if (!erpMap.containsKey("erpUrl")) {
            return null;
        }
        String baseUrl = erpMap.get("erpUrl").toString();//erp请求地址
        Map<String, Object> reMap = new HashMap<>();
        try {
            if (StringUtil.isEmpty(uid) || StringUtil.isEmpty(goodsNo)) {
                reMap.put("code", 1);
                reMap.put("info", new ArrayList<>());
                return reMap;
            }
            if ((!StringUtil.isEmpty(goodsNo)) && goodsNo.endsWith(",")) {
                goodsNo = goodsNo.substring(0, goodsNo.length() - 1);
            }
            if ((!StringUtil.isEmpty(uid)) && uid.endsWith(",")) {
                uid = uid.substring(0, uid.length() - 1);
            }
            if (siteId.equals("100166")) {
                String url = baseUrl + "/orders/storages?GOODSNO=" + goodsNo + "&UNIT_NO=" + uid;
                logger.info("查询九洲库存=====" + url);
                reMap = JacksonUtils.json2map(OkHttpUtil.get(url));
            } else if (siteId.equals("1001901")) {
                String url = baseUrl + "/getinventory/" + uid + "/" + goodsNo;
                logger.info("查询天润库存=====" + url);
                reMap = JacksonUtils.json2map(OkHttpUtil.get(url));
            } else if (siteId.equals("100180")) {
                String url = baseUrl + "/wscitystockservlet?GOODSNO=" + goodsNo + "&UID=" + uid;
                logger.info("##库存信息##请求地址:[{}],商品编码:[{}],门店编号:[{}]", url, goodsNo, uid);
                logger.info("查询千金库存=====" + url);
                reMap = JacksonUtils.json2map(OkHttpUtil.get(url));
            } else if (siteId.equals("100173")) {
                reMap = ZHONGLIAN_storage(goodsNo, uid, baseUrl);
            } else if (siteId.equals("100030")) {
                reMap = baoDaoOfflineService.getStorageList(goodsNo, uid);
            } else if (siteId.equals("100213") || siteId.equals("100239") || siteId.equals("100271") || siteId.equals("100262")) {//查询济生堂库存
                String url = baseUrl + "/orders/storage";
                Map<String, Object> requestParams = new HashedMap();
                requestParams.put("GOODSNO", goodsNo);
                requestParams.put("UID", uid);
                logger.info("海典库存信息:{},请求地址:[{}],商品编码:[{}],门店编号:[{}]", siteId, url, goodsNo, uid);
                reMap = erpToolsService.requestHeaderPar(url, requestParams);
            } else if (siteId.equals("100238")) {//查询瑞森库存
                logger.info("瑞森查询库存:{},商品编码:[{}],门店编号:[{}]", siteId, goodsNo, uid);
                reMap = getRuiSenStorage_100238(Integer.parseInt(siteId), goodsNo, uid);
            } else if (siteId.equals("100272")) {//查询成都聚仁堂库存
                logger.info("成都聚仁堂:{},商品编码:[{}],门店编号:[{}]", siteId, goodsNo, uid);
                String url = baseUrl + "/Storage";
                Map<String, Object> requestParams = new HashedMap();
                requestParams.put("GOODSNO", goodsNo);
                requestParams.put("UID", uid);
                reMap = erpToolsService.requestHeaderPar(url, requestParams);
            } else if (siteId.equals("100253")) {//查询安吉中联
                reMap = getZhongLianStorage_100253(Integer.parseInt(siteId), goodsNo, uid);
            } else if (siteId.equals("100203")) {//查询佛心库存
                reMap = getFoXinStorage_100203(Integer.parseInt(siteId), goodsNo, uid);
            } else if (siteId.equals("100268")) {//查询德仁堂库存
                String url = baseUrl + "orders/storage";
                Map<String, Object> requestParams = new HashedMap();
                requestParams.put("GOODSNO", goodsNo);
                requestParams.put("UID", uid);
                logger.info("德仁堂库存信息:{},请求地址:[{}],商品编码:[{}],门店编号:[{}]", siteId, url, goodsNo, uid);
                reMap = JacksonUtils.json2map(OkHttpUtil.postJson(url, JacksonUtils.mapToJson(requestParams)));
            } else if (siteId.equals("100190")) {//查询宏仁堂假库存
                if (!StringUtil.isEmpty(goodsNo)) {
                    if(uid.indexOf(",")>-1){
                        //多个门店，自提订单类型
                        List<Map> web_storage = new ArrayList<>();
                        if (!StringUtil.isEmpty(goodsNo)) {
                            for (String id : goodsNo.split(",")) {
                                int isGoodsYibao=merchantERPMapper.getGoodsYibao(siteId,id);
                                if(isGoodsYibao==0){
                                    //非医保要药
                                    for (String st : uid.split(",")) {
                                        int isStoresYibao=merchantERPMapper.getStoresYibao(siteId,st);
                                        if(isStoresYibao==0){
                                            //非医保要药店
                                            Map<String, Object> storageMap = new HashMap<>();
                                            storageMap.put("UID", st);
                                            storageMap.put("GOODSNO",id );
                                            storageMap.put("kcqty", 50);
                                            web_storage.add(storageMap);
                                        }else {
                                            //医保要药店（不能买医保要）
                                        }
                                    }
                                }else {
                                    //医保要药
                                    for (String st : uid.split(",")) {
                                        Map<String, Object> storageMap = new HashMap<>();
                                        storageMap.put("UID", st);
                                        storageMap.put("GOODSNO", id);
                                        storageMap.put("kcqty", 50);
                                        web_storage.add(storageMap);
                                    }
                                }
                            }
                            reMap.put("code", 1);
                            reMap.put("info", web_storage);
                        }
                    }else {
                        //单个门店送货上门类型
                        String storesStr = storesService.selectidsBywarehouseFromMeta(Integer.parseInt(siteId));
                        List<Map> web_storage = new ArrayList<>();
                        if((storesStr+",").indexOf(uid+",")>-1){
                            for (String id : goodsNo.split(",")) {
                                Map<String, Object> storageMap = new HashMap<>();
                                storageMap.put("UID", uid);
                                storageMap.put("GOODSNO", id);
                                storageMap.put("kcqty", 50);
                                web_storage.add(storageMap);
                            }
                            reMap.put("code", 1);
                            reMap.put("info", web_storage);
                        }
                    }
                }
                logger.info("查询宏仁堂假库存:{},商品编码:[{}],门店编号:[{}]", siteId, goodsNo, uid);
            }
            return reMap;
        } catch (Exception e) {
            logger.info("getStorageBysiteId,siteId:{},问题：{}", siteId, e.getMessage());
            if (erpToolsService.judgeErpAppliBySiteId(Integer.parseInt(siteId))) {
                merchantSettingService.insertFaultStatics(Integer.parseInt(siteId), "预下单查询库存：[getStorageBysiteId]",
                    2, 500, e.getMessage(), e.toString(), "uid：[" + uid + "],goodsNO:[" + goodsNo + "]", 1);
            } else {
                merchantSettingService.insertFaultStatics(Integer.parseInt(siteId), "预下单查询库存：[getStorageBysiteId]",
                    2, 400, e.getMessage(), e.toString(), "uid：[" + uid + "],goodsNO:[" + goodsNo + "]", 1);
            }
        }
        return reMap;
    }

    /**
     * 查询中联的库存信息
     *
     * @param goodsNO
     * @param uid
     * @return
     */
    public Map<String, Object> ZHONGLIAN_storage(String goodsNO, String uid, String baseUrl) throws Exception {
      /*  if (!StringUtil.isEmpty(uid)) {
            uid = uid.substring(0, uid.length() - 1);
        }*/
        String url = baseUrl + "/getstock?goodsno=" + goodsNO + "&uid=" + uid + "";
        logger.info("中联商户查询库存信息接口url:{},商品编码:{},对应门店编码:{}", url, goodsNO, uid);
        Map<String, Object> responseParams = new HashMap<>();
        String reresult = OkHttpUtil.get(url);
        Map<String, Object> erp_params = JacksonUtils.json2map(XmlUtils.xml2map(reresult).get("return").toString());
        List<Map> storageList = (List) erp_params.get("info");
        List<Map> web_storage = new ArrayList<>();
        for (Map st : storageList) {
            Map<String, Object> storageMap = new HashMap<>();
            storageMap.put("UID", st.get("uid"));
            storageMap.put("GOODSNO", st.get("goodsno"));
            storageMap.put("kcqty", st.get("goodsqty"));
            web_storage.add(storageMap);
        }
        responseParams.put("code", 1);
        responseParams.put("info", web_storage);
        return responseParams;
    }

    //查询安吉中联的库存
    public Map<String, Object> getZhongLianStorage_100253(Integer siteId, String goodsNO, String uid) {
        Map<String, Object> responseparams = new HashMap<>();
        List<Map<String, Object>> storageList = new ArrayList<>();
        StringBuffer coderequest = new StringBuffer("");
        String codeStrings = "";
        StringBuffer uidRequest = new StringBuffer("");
        String uidStrings = "";
        if (!StringUtil.isEmpty(goodsNO)) {
            for (String s : goodsNO.split(",")) {
                if (StringUtil.isEmpty(s) || s.equals(" ")) {
                    continue;
                }
                coderequest.append("'" + s + "'" + ",");
            }
            codeStrings = coderequest.substring(0, coderequest.length() - 1);
        }
        if (!StringUtil.isEmpty(uid)) {
            for (String s : uid.split(",")) {
                if (StringUtil.isEmpty(s) || s.equals(" ")) {
                    continue;
                }
                uidRequest.append("'" + s + "'" + ",");
            }
            uidStrings = uidRequest.substring(0, uidRequest.length() - 1);
        }
        if (StringUtil.isEmpty(codeStrings) || StringUtil.isEmpty(uidStrings)) {
            responseparams.put("code", 1);
            responseparams.put("info", storageList);
            return responseparams;
        }
        logger.info("安吉中联{},查询门店编码为{},商品编码为：{}.", siteId, uid, coderequest);
        String sql = String.format("select unit_no as UID ,goodsno as GOODSNO,amount as kcqty from view_unitgoods where goodsno in(%s) and unit_no in(%s)", codeStrings, uidStrings);
        logger.info("安吉中联的库存查询语句" + sql);
        storageList = dataSoureConfig_zhongLian.getZhongLianJDBCTemplate().queryForList(sql);
        responseparams.put("code", 1);
        responseparams.put("info", storageList);
        return responseparams;
    }

    //还没有完成方法，默认一次可以查询多家门店商品信息
    public List<Store> getStoresHasStorageList(Integer siteId, List<Store> stores, List<GoodsInfo> goodsInfoInfos) {
        List<Store> storeList = new ArrayList<>();
        Map<String, Integer> storageNums = new HashMap<>();
        StringBuffer goodsCodes = new StringBuffer("");
        for (GoodsInfo good : goodsInfoInfos) {
            goodsCodes.append(good.getGoodsCode() + ",");
            storageNums.put(good.getGoodsCode(), good.getControlNum());
        }
        StringBuffer uids = new StringBuffer("");
        for (Store store : stores) {
            uids.append(store.getStoresNumber() + ",");
        }
        Map<String, Object> storageList = getStorageBysiteId(String.valueOf(siteId), goodsCodes.toString(), uids.toString());
        logger.info("商户{}查询订单库存信息,门店{},商编:{}.", siteId, uids, goodsCodes);
        if (!StringUtil.isEmpty(storageList.get("info")) && !"[]".equals(storageList.get("info"))) {
            List<Map<String, Object>> storageInfoList = (List<Map<String, Object>>) storageList.get("info");
            Set<String> storeNums = new HashSet<>();//查询到的门店编码
            Set<String> storeNumsError = new HashSet<>();//不符合条件的门店编码
            storageInfoList.stream().forEach(storage -> {
                storeNums.add(storage.get("UID").toString());
                if (storageNums.containsKey(storage.get("GOODSNO"))) {
                    if (Double.parseDouble(storage.get("kcqty").toString()) <
                        Double.parseDouble(storageNums.get(storage.get("GOODSNO")).toString())) {
                        storeNumsError.add(storage.get("UID").toString());
                    }
                } else {
                    storeNumsError.add(storage.get("UID").toString());
                }
            });
            for (Store store : stores) {
                if (storeNums.contains(store.getStoresNumber()) && (!storeNumsError.contains(store.getStoresNumber()))) {
                    storeList.add(store);
                }
            }
            return storeList;
        } else {
            return storeList;
        }

    }

    //查询100203佛心的库存
    public Map<String, Object> getFoXinStorage_100203(Integer siteId, String goodsNO, String uid) throws Exception {
        Map<String, Object> responseparams = new HashMap<>();
        List<Map<String, Object>> storageList = new ArrayList<>();
        StringBuffer coderequest = new StringBuffer("");
        String codeStrings = "";
        StringBuffer uidRequest = new StringBuffer("");
        String uidStrings = "";
        if (!StringUtil.isEmpty(goodsNO)) {
            for (String s : goodsNO.split(",")) {
                if (StringUtil.isEmpty(s) || s.equals(" ")) {
                    continue;
                }
                coderequest.append("'" + s + "'" + ",");
            }
            codeStrings = coderequest.substring(0, coderequest.length() - 1);
        }
        if (!StringUtil.isEmpty(uid)) {
            for (String s : uid.split(",")) {
                if (StringUtil.isEmpty(s) || s.equals(" ")) {
                    continue;
                }
                uidRequest.append("'" + s + "'" + ",");
            }
            uidStrings = uidRequest.substring(0, uidRequest.length() - 1);
        }
        if (StringUtil.isEmpty(codeStrings) || StringUtil.isEmpty(uidStrings)) {
            responseparams.put("code", 1);
            responseparams.put("info", storageList);
            return responseparams;
        }
        logger.info("佛心{},查询门店编码为{},商品编码为：{}.", siteId, uid, coderequest);
        String sql = String.format("select UID as UID ,GOODSNO as GOODSNO,kcqty as kcqty from storage201805 where GOODSNO in(%s) and UID in(%s)", codeStrings, uidStrings);
        logger.info("佛心的库存查询语句" + sql);
        storageList = dataSourceConfig_foXin.getFoXinJDBCTemplate().queryForList(sql);
        responseparams.put("code", 1);
        responseparams.put("info", storageList);
        return responseparams;
    }

    //查询100238瑞森的库存
    public Map<String, Object> getRuiSenStorage_100238(Integer siteId, String goodsNO, String uid) throws Exception {
        Map<String, Object> responseparams = new HashMap<>();
        List<Map<String, Object>> storageList = new ArrayList<>();
        StringBuffer coderequest = new StringBuffer("");
        String codeStrings = "";
        StringBuffer uidRequest = new StringBuffer("");
        String uidStrings = "";
        if (!StringUtil.isEmpty(goodsNO)) {
            for (String s : goodsNO.split(",")) {
                if (StringUtil.isEmpty(s) || s.equals(" ")) {
                    continue;
                }
                coderequest.append("'" + s + "'" + ",");
            }
            codeStrings = coderequest.substring(0, coderequest.length() - 1);
        }
        if (!StringUtil.isEmpty(uid)) {
            for (String s : uid.split(",")) {
                if (StringUtil.isEmpty(s) || s.equals(" ")) {
                    continue;
                }
                uidRequest.append("'" + s + "'" + ",");
            }
            uidStrings = uidRequest.substring(0, uidRequest.length() - 1);
        }
        if (StringUtil.isEmpty(codeStrings) || StringUtil.isEmpty(uidStrings)) {
            responseparams.put("code", 1);
            responseparams.put("info", storageList);
            return responseparams;
        }
        logger.info("瑞森{},查询门店编码为{},商品编码为：{}.", siteId, uid, coderequest);
        String sql = String.format("select mdbh as UID ,cinvcode as GOODSNO,iquantity as kcqty from mdkcb where cinvcode in(%s) and mdbh in(%s)", codeStrings, uidStrings);
        logger.info("瑞森的库存查询语句" + sql);
        storageList = dataSourceConfig_ruiSen.getRuiSenJDBCTemplate().queryForList(sql);
        responseparams.put("code", 1);
        responseparams.put("info", storageList);
        return responseparams;
    }
}
