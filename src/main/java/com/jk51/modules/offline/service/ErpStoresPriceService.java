package com.jk51.modules.offline.service;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.erpDataConfig.DataSourceConfig_RuiSen;
import com.jk51.modules.erpprice.mapper.BErpSettingMapper;
import com.jk51.modules.erpprice.service.ErpPriceImportService;
import com.jk51.modules.erpprice.service.SyncService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.offline.mapper.MerchantERPMapper;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-04-10
 * 修改记录:
 */
@Service
public class ErpStoresPriceService {

    private static final Logger logger = LoggerFactory.getLogger(ErpStoresPriceService.class);

    @Autowired
    private BErpSettingMapper erpSettingMapper;
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private MerchantERPMapper merchantERPMapper;
    @Autowired
    private ErpToolsService erpToolsService;
    @Autowired
    private ErpPriceImportService erpPriceImportService;
    @Resource
    private DataSourceConfig_RuiSen dataSourceConfig_ruiSen;
    @Autowired
    SyncService syncService;

    /**
     * 当多价格规则设置时调用接口同步
     *
     * @param siteId
     * @return
     */
    @Async
    public ReturnDto getStoresPriceFromErp(Integer siteId, Byte type) {
        Map<String, Object> merchantErpInfo = merchantERPMapper.selectMerchantERPInfo(Integer.parseInt(String.valueOf(siteId)));
        if (StringUtil.isEmpty(merchantErpInfo) || Integer.parseInt(merchantErpInfo.get("price").toString()) == 0) {
            return ReturnDto.buildFailedReturnDto("不存在该商户erp信息或多价格同步接口未打通");
        }
        String baseUrl = merchantErpInfo.containsKey("erpUrl") ? (StringUtil.isEmpty(merchantErpInfo.get("erpUrl")) ? null : merchantErpInfo.get("erpUrl").toString()) : null;//erp请求地址
        String storeNumbers = "";//获取要求的门店编码，多个用逗号隔开
        if (type == 40) {
            storeNumbers = storesMapper.selectAllStoreNumbers(siteId);
        } else {
            storeNumbers = erpSettingMapper.getErpStoresNumbers(siteId);
        }
        if (!StringUtil.isEmpty(storeNumbers)) {
            if (siteId == 100213) {
                List<String> goodCodeList = goodsMapper.getGoodCodeBySiteId(siteId, 1);
                int codeNums = 1000;
                for (int i = 0; i < goodCodeList.size(); i += codeNums) {
                    List l;
                    if (goodCodeList.size() >= (i + codeNums)) {
                        l = goodCodeList.subList(i, i + codeNums);
                    } else {
                        l = goodCodeList.subList(i, goodCodeList.size());
                    }
                    Map<String, Object> requestParams = new HashedMap();
                    requestParams.put("store_number", storeNumbers);
                    requestParams.put("goodsno", StringUtil.join(l, ","));
                    syncStorePrice_jishengtang(siteId, requestParams, baseUrl);
                }
            } else if (siteId == 100238) {
                String unitNO = "";
                if (StringUtil.contains(storeNumbers, ",")) {
                    StringBuffer storeNO = new StringBuffer();
                    for (String s : StringUtil.toList(storeNumbers, ",")) {
                        storeNO.append(StringUtil.singleQuote(s)).append(",");
                    }
                    unitNO = storeNO.substring(0, storeNO.length() - 1);
                } else {
                    unitNO = StringUtil.singleQuote(storeNumbers);
                }
                List<String> goodCodeList = goodsMapper.getGoodCodeBySiteId(siteId, 1);
                int codeNums = 1000;
                for (int i = 0; i < goodCodeList.size(); i += codeNums) {
                    List l;
                    if (goodCodeList.size() >= (i + codeNums)) {
                        l = goodCodeList.subList(i, i + codeNums);
                    } else {
                        l = goodCodeList.subList(i, goodCodeList.size());
                    }
                    syncStorePrice_ruisen(siteId, unitNO, StringUtil.join(l, ","));
                }
            } else if (siteId == 100268) {//德仁堂多价格对接
                List<String> goodCodeList = goodsMapper.getGoodCodeBySiteId(siteId, 1);
                int codeNums = 400;
                for (int i = 0; i < goodCodeList.size(); i += codeNums) {
                    List l;
                    if (goodCodeList.size() >= (i + codeNums)) {
                        l = goodCodeList.subList(i, i + codeNums);
                    } else {
                        l = goodCodeList.subList(i, goodCodeList.size());
                    }
                    Map<String, Object> requestParams = new HashedMap();
                    requestParams.put("store_number", storeNumbers);
                    requestParams.put("goodsno", StringUtil.join(l, ","));
                    syncStorePrice_derenbtang(siteId, requestParams, baseUrl);
                }
            }
        }
        syncService.syncStorePrice(siteId);
        return ReturnDto.buildSuccessReturnDto();
    }


    public void syncStorePrice_jishengtang(Integer siteId, Map<String, Object> requestParams, String baseUrl) {
        String url = baseUrl + "/offline/storeSyncPrice";
        try {
            Map<String, Object> result = erpToolsService.requestHeaderPar(url, requestParams);
            logger.info("济生堂同步多价格数据:{}", result.toString());
            if (result.containsKey("code") && Integer.parseInt(result.get("code").toString()) == 0) {
                List goodsList = JSONArray.parseArray(JacksonUtils.obj2json(result.get("info")), Map.class);
                Map list = getList(goodsList);
                list.keySet().forEach((Object key) -> {
                    int n = Integer.valueOf(key + "") + 1;
                    List<Map> data = (List<Map>) list.get(key);
                    Map params = new HashedMap();
                    params.put("siteId", siteId);
                    params.put("goodlist", data);
                    erpPriceImportService.storeSyncPrice(params);
                });
            }
        } catch (Exception e) {
            logger.info("济生堂同步多价格失败，原因:{}", e.getMessage());
        }
    }

    public void syncStorePrice_derenbtang(Integer siteId, Map<String, Object> requestParams, String baseUrl) {
        String url = baseUrl + "offline/storeSyncPrice";
        try {
            Map<String, Object> result = JacksonUtils.json2map(OkHttpUtil.postJson(url, JacksonUtils.mapToJson(requestParams)));
            logger.info("德仁堂同步多价格数据:{}", result.toString());
            if (result.containsKey("code") && Integer.parseInt(result.get("code").toString()) == 1 && result.containsKey("info")) {
                List goodsList = JSONArray.parseArray(JacksonUtils.obj2json(result.get("info")), Map.class);
                Map list = getList(goodsList);
                list.keySet().forEach((Object key) -> {
                    int n = Integer.valueOf(key + "") + 1;
                    List<Map> data = (List<Map>) list.get(key);
                    Map params = new HashedMap();
                    params.put("siteId", siteId);
                    params.put("goodlist", data);
                    erpPriceImportService.storeSyncPrice(params);
                });
            }
        } catch (Exception e) {
            logger.info("德仁堂同步多价格失败，原因:{}", e.getMessage());
        }
    }

    public void syncStorePrice_ruisen(Integer siteId, String storeNumbers, String goodsCodes) {
        String gcode = "";
        if (StringUtil.contains(goodsCodes, ",")) {
            StringBuffer stringBuffer = new StringBuffer();
            for (String s : StringUtil.toList(goodsCodes, ",")) {
                stringBuffer.append(StringUtil.singleQuote(s)).append(",");
            }
            gcode = stringBuffer.substring(0, stringBuffer.length() - 1);
        } else {
            gcode = StringUtil.singleQuote(goodsCodes);
        }
        try {
            String selectSQL = "select sj as shop_price ,cinvcode as goods_code,mdbh as store_number from mdsjb where " +
                "cinvcode in(" + gcode + ") and mdbh in (" + storeNumbers + ")";
            List<Map<String, Object>> result = dataSourceConfig_ruiSen.getRuiSenJDBCTemplate().queryForList(selectSQL);
            Map list = getList((List) result);
            list.keySet().forEach((Object key) -> {
                int n = Integer.valueOf(key + "") + 1;
                List<Map> data = (List<Map>) list.get(key);
                Map params = new HashedMap();
                params.put("siteId", siteId);
                params.put("goodlist", data);
                logger.info("瑞森同步价格的数据:{}", params.toString());
                erpPriceImportService.storeSyncPrice(params);
            });
        } catch (Exception e) {
            logger.info("瑞森同步多价格失败，原因:{}", e.getMessage());
        }
    }

    public Map getList(List<Map> erpList) {
        Map map = new HashMap();
        int k = 0;
        int count = 2000;
        if (erpList.size() > count) {
            for (int i = 0; i < erpList.size(); i += count) {
                List l;
                if (erpList.size() >= (i + count)) {
                    l = erpList.subList(i, i + count);
                } else {
                    l = erpList.subList(i, erpList.size());
                }
                map.put(k, l);
                k++;
            }
        } else {
            map.put(k, erpList);
        }
        return map;
    }

}
