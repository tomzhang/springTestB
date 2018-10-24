package com.jk51.modules.offline.service;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.okhttp.OkHttpUtil;
import com.jk51.commons.okhttp.PingIPUtils;
import com.jk51.commons.sms.SysType;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.appInterface.mapper.YbAreaMapper;
import com.jk51.modules.merchant.service.YbAreaService;
import com.jk51.modules.offline.mapper.MerchantERPMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.sms.service.DjSmsService;
import com.jk51.modules.sms.service.ZtSmsService;
import com.jk51.modules.trades.service.TradesService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.swing.text.StyledEditorKit;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-07-14
 * 修改记录:
 */
@Service
public class ErpToolsService {

    private static final Logger logger = LoggerFactory.getLogger(ErpToolsService.class);

    @Autowired
    private YbAreaMapper ybAreaMapper;
    @Value("${erp.is_open}")
    private String is_Open;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    TradesService tradesService;
    @Autowired
    private YbAreaService areaService;
    @Autowired
    private OfflineIntegrateService offlineIntegrateService;
    @Autowired
    private OfflineOrderService orderService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MerchantERPMapper merchantERPMapper;
    @Autowired
    private DjSmsService djSmsService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private BaoDaoOfflineService baoDaoOfflineService;


    /**
     * 更新会员信息地址,address：上海市,上海市,虹口区|某某地址
     *
     * @return
     */
    public Map<String, Object> getareaIds(String address) {
        Map<String, Object> areaMap = new HashMap<>();
        String[] a = address.split("\\|");
        if (a.length == 2) {
            String[] area = a[0].split(",");
            if (area.length >= 3) {
                List<Map<String, Object>> provinceIds = ybAreaMapper.queryAreaIdByName(2, area[0], null);
                if (provinceIds.size() > 0) {
                    areaMap.put("province", provinceIds.get(0).get("areaid").toString());
                    List<Map<String, Object>> cityIds = ybAreaMapper.queryAreaIdByName(3, area[1], Integer.parseInt(provinceIds.get(0).get("areaid").toString()));
                    if (cityIds.size() > 0) {
                        areaMap.put("city", cityIds.get(0).get("areaid").toString());
                        List<Map<String, Object>> regionIds = ybAreaMapper.queryAreaIdByName(4, area[2], Integer.parseInt(cityIds.get(0).get("areaid").toString()));
                        if (regionIds.size() > 0) {
                            areaMap.put("area", regionIds.get(0).get("areaid").toString());
                        }
                    }
                }
            }
            areaMap.put("address", a[1]);
        } else if (a.length == 1) {
            String[] area = a[0].split(",");
            if (area.length >= 3) {
                List<Map<String, Object>> provinceIds = ybAreaMapper.queryAreaIdByName(2, area[0], null);
                if (provinceIds.size() > 0) {
                    areaMap.put("province", provinceIds.get(0).get("areaid").toString());
                    List<Map<String, Object>> cityIds = ybAreaMapper.queryAreaIdByName(3, area[1], Integer.parseInt(provinceIds.get(0).get("areaid").toString()));
                    if (cityIds.size() > 0) {
                        areaMap.put("city", cityIds.get(0).get("areaid").toString());
                        List<Map<String, Object>> regionIds = ybAreaMapper.queryAreaIdByName(4, area[2], Integer.parseInt(cityIds.get(0).get("areaid").toString()));
                        if (regionIds.size() > 0) {
                            areaMap.put("area", regionIds.get(0).get("areaid").toString());
                        }
                    }
                }
                areaMap.put("address", "");
            } else {//只填写了地址，没有填省市区
                areaMap.put("province", null);
                areaMap.put("city", null);
                areaMap.put("area", null);
                areaMap.put("address", a[0]);
            }
        } else {
            areaMap.put("province", null);
            areaMap.put("city", null);
            areaMap.put("area", null);
            areaMap.put("address", null);
        }

        return areaMap;
    }

    /**
     * 实时推送积分变动情况
     *
     * @param siteId
     * @param requestParams
     * @return
     */
    public Map<String, Object> integralChange(Integer siteId, Map<String, Object> requestParams) {
        try {
            if (is_Open.equals("true")) {//正式环境，走推送
                logger.info("erpToolService===integralChange,实时推送积分变动情况，" +
                    "sieId{},requestParms{}", siteId, requestParams.toString());
                return offlineIntegrateService.recordIntegerChange(siteId, requestParams);
            } else {
                logger.info("erpToolService===integralChange,测试环境，不推送积分" +
                    "sieId{},requestParms{}", siteId, requestParams.toString());
            }
        } catch (Exception e) {
            logger.info("积分消费信息有误" + e);
        }
        return null;
    }

    /**
     * 根据订单编号查询商品编码
     *
     * @param siteId
     * @param tradesId
     * @return
     */
    public String getGoodsCodebyTradesId(Integer siteId, Long tradesId) {
        List<Map> orderList = ordersMapper.selectOrderListFromERP(siteId, tradesId);
        if (orderList.size() > 0) {
            StringBuilder goodcodes = new StringBuilder("");
            for (Map good : orderList) {
                goodcodes.append(good.get("gcode").toString() + ",");
            }
            logger.info("订单推送商品编码:goodcodes" + goodcodes);
            return goodcodes.toString();
        } else {
            return null;
        }
    }

    /**
     * 订单推送
     *
     * @param siteId
     * @param tradesId
     */
    public void erpOrdersService(Integer siteId, Long tradesId) {
        orderService.erpOrdersService(siteId, tradesId);
    }

    public Map<String, Object> requestErp(String url) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = JacksonUtils.json2map( OkHttpUtil.get(url));
        } catch (Exception e) {
            logger.info("调用线下erp信息失败" + e);
        }
        return result;
    }

    public Map<String, Object> requestErp(String url, List list) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = JacksonUtils.json2map(OkHttpUtil.getList(url, list));
        } catch (Exception e) {
            logger.info("调用线下erp信息失败" + e);
        }
        return result;
    }

    public Map<String, Object> requestErp(String url, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = JacksonUtils.json2map(OkHttpUtil.postMap(url, params));
        } catch (Exception e) {
            logger.info("调用线下erp信息失败" + e);
        }
        return result;
    }

    public Map<String, Object> requestHeaderPar(String url, Map<String, Object> requestParams) throws Exception {
        Map<String, Object> headerParams = new HashMap<>();
        headerParams.put("Content-Type", "application/x-www-form-urlencoded");
        return JacksonUtils.json2map(OkHttpUtil.postMap(url, requestParams, headerParams));
    }

    public String getAddress(Object provinceObj, Object cityObj, Object areaObj, Object addressObj) {
        String allAddress = "";
        if (!StringUtil.isEmpty(areaObj)) {
            try {
                String province = areaService.queryAreaByAreaId(Integer.parseInt(provinceObj.toString())).get("name").toString();
                String city = areaService.queryAreaByAreaId(Integer.parseInt(cityObj.toString())).get("name").toString();
                String country = areaService.queryAreaByAreaId(Integer.parseInt(areaObj.toString())).get("name").toString();
                allAddress = province + "," + city + "," + country + "|" + (StringUtil.isEmpty(addressObj) ? "" : addressObj);
            } catch (Exception e) {
                logger.info("解析地址出错" + e);
                allAddress = "|" + (StringUtil.isEmpty(addressObj) ? "" : addressObj);
            }
        } else {
            allAddress = "|" + (StringUtil.isEmpty(addressObj) ? "" : addressObj);
        }
        return allAddress;
    }

    /**
     * erp 接口调用出错时短信通知和邮件通知
     *
     * @param siteId       出错的商家id
     * @param appli        错误接口
     * @param msg          报错信息
     * @param faultDetails
     * @param type         接口错误类型 1：订单；2：库存；3：多价格；4：盘点
     */
    public void sendError(Integer siteId, String appli, String msg, String faultDetails, Integer type) {
        logger.info("调用邮件系统发送错误异常信息。商户:{},接口:{},错误信息；{}", siteId, appli, msg);
        if (is_Open.equals("true")) {
            Map<String, Object> merchantErp = merchantERPMapper.selectMerchantByStatus(siteId, 1).get(0);
            String word = "报错接口：" + appli + ",基础报错信息：" + msg;
            if (!StringUtil.isEmpty(merchantErp)) {
                if (type == 2) {
                    if (merchantErp.get("storage").toString().equals("1")) {
                        if (StringUtil.isEmpty(stringRedisTemplate.opsForValue().get(siteId + "_orderStorage_Error"))) {
                            sendSmS(siteId, word, faultDetails, merchantErp.get("mobiles").toString(), merchantErp.get("emails").toString());
                            stringRedisTemplate.opsForValue().set(siteId + "_orderStorage_Error", "1", 1, TimeUnit.HOURS);
                        }
                    }
                } else if (type == 4) {
                    if (merchantErp.get("pandian").toString().equals("1")) {
                        if (StringUtil.isEmpty(stringRedisTemplate.opsForValue().get(siteId + "_pandian_Error"))) {
                            sendSmS(siteId, word, faultDetails, merchantErp.get("mobiles").toString(), merchantErp.get("emails").toString());
                            stringRedisTemplate.opsForValue().set(siteId + "_pandian_Error", "1", 1, TimeUnit.HOURS);
                        }
                    }
                } else if (type == 1) {
                    if (merchantErp.get("trades").toString().equals("1")) {
                        if (StringUtil.isEmpty(stringRedisTemplate.opsForValue().get(siteId + "_pushOrder_Error"))) {
                            sendSmS(siteId, word, faultDetails, merchantErp.get("mobiles").toString(), merchantErp.get("emails").toString());
                            stringRedisTemplate.opsForValue().set(siteId + "_pushOrder_Error", "1", 1, TimeUnit.HOURS);
                        }
                    }
                }
            }
        }
    }

    public void sendSmS(Integer siteId, String msg, String faultDetails, String mobiles, String emails) {
        if (StringUtil.isEmpty(mobiles)) {
            List<String> mobileList = Arrays.asList(mobiles.split(","));
            for (String mobile : mobileList) {
                if (!StringUtil.isEmpty(mobile)) {
                    djSmsService.SendMessage(siteId, msg, mobile, null, SysType.PREWARNING_VALUE);
                }
            }
        }
        sendMailTo51JK("erp报错信息提醒", "商户站点：" + siteId + "," + msg + ",详细报错信息：" + faultDetails);
        if (StringUtil.isEmpty(emails)) {
            List<String> emailsList = Arrays.asList(emails.split(","));
            sendMailToMerchant(emailsList, "erp报错信息提醒", msg + "。具体报错代码：" + faultDetails);
        }
    }

    public void sendMailTo51JK(String header, String word) {
        SimpleEmail email = new SimpleEmail();
        //email.setTLS(true); //是否TLS校验，，某些邮箱需要TLS安全校验，同理有SSL校验
        email.setDebug(true);
        //email.setSSL(true);
        email.setHostName("smtphm.qiye.163.com");
        email.setAuthenticator(new DefaultAuthenticator("alarm@51jk.com", "Abc258258"));//发送方
        try {
            email.setFrom("alarm@51jk.com"); //发送方,这里可以写多个
            email.addTo("warning@51jk.com"); // 接收方
            email.setCharset("GB2312");
            email.setSubject(header); // 标题
            email.setMsg(word);// 内容
            email.send();
        } catch (EmailException e) {
            logger.info("erp接口调用失败发送邮件失败。" + e.getMessage());
        }
    }

    public void sendMailToMerchant(List<String> emails, String header, String word) {
        SimpleEmail email = new SimpleEmail();
        email.setDebug(true);
        email.setHostName("smtphm.qiye.163.com");
        email.setAuthenticator(new DefaultAuthenticator("alarm@51jk.com", "Abc258258"));//发送方
        try {
            email.setFrom("alarm@51jk.com"); //发送方,这里可以写多个
            for (String emailStr : emails) {
                if (!StringUtil.isEmpty(emailStr)) {
                    email.addTo(emailStr); // 接收方
                }
            }
            email.setCharset("GB2312");
            email.setSubject(header); // 标题
            email.setMsg(word);// 内容
            email.send();
        } catch (EmailException e) {
            logger.info("erp接口调用失败发送邮件失败。" + e.getMessage());
        }
    }

    /**
     * 当数据量过多时是否会造成效率过低
     *
     * @param siteId
     * @return
     */
    public ReturnDto clearStorageCacheRedis(Integer siteId) {
        try {
            Set<String> kcqtyList = stringRedisTemplate.keys(siteId + "_" + "*" + "_Kcqty");
            stringRedisTemplate.delete(kcqtyList);
            return ReturnDto.buildSuccessReturnDto();
        } catch (Exception e) {
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
    }

    /**
     * 通过库存去判断判断商户的erp接口是否打通
     *
     * @param siteId
     * @return
     */
    public boolean judgeErpAppliBySiteId(Integer siteId) {
        Map<String, Object> erpMap = merchantERPMapper.selectMerchantERPInfo(siteId);
        String baseUrl = erpMap.get("erpUrl").toString();//erp请求地址
        Map<String, Object> reMap = new HashMap<>();
        try {
            if (siteId == 100166) {
                String url = baseUrl + "/orders/storages?GOODSNO=" + 0 + "&UNIT_NO=" + 0;
                reMap = JacksonUtils.json2map(OkHttpUtil.get(url));
            } else if (siteId == 100190) {
                String url = baseUrl + "/getinventory/" + 0 + "/" + 0;
                reMap = JacksonUtils.json2map(OkHttpUtil.get(url));
            } else if (siteId == 100180) {
                String url = baseUrl + "/wscitystockservlet?GOODSNO=" + 0 + "&UID=" + 0;
                reMap = JacksonUtils.json2map(OkHttpUtil.get(url));
            } else if (siteId == 100173) {
                reMap = storageService.ZHONGLIAN_storage("0", "0", baseUrl);
            } else if (siteId == 100030) {
                reMap = baoDaoOfflineService.getStorageList("0", "0");
            } else if (siteId == 100213 || siteId == 100239 || siteId == 100271 || siteId == 100262) {//查询济生堂库存
                String url = baseUrl + "/orders/storage";
                Map<String, Object> requestParams = new HashedMap();
                requestParams.put("GOODSNO", 0);
                requestParams.put("UID", 0);
                reMap = requestHeaderPar(url, requestParams);
            } else if (siteId == 100238) {//查询瑞森库存
                reMap = storageService.getRuiSenStorage_100238(siteId, "0", "0");
            } else if (siteId == 100272) {//查询成都聚仁堂库存
                String url = baseUrl + "/Storage";
                Map<String, Object> requestParams = new HashedMap();
                requestParams.put("GOODSNO", '0');
                requestParams.put("UID", '0');
                reMap = requestHeaderPar(url, requestParams);
            } else if (siteId == 100253) {//查询安吉中联
                reMap = storageService.getZhongLianStorage_100253(siteId, "0", "0");
            } else if (siteId == 100203) {//查询佛心库存
                reMap = storageService.getFoXinStorage_100203(siteId, "0", "0");
            } else if (siteId == 100268) {//查询德仁堂库存
                String url = baseUrl + "orders/storage";
                Map<String, Object> requestParams = new HashedMap();
                requestParams.put("GOODSNO", "0");
                requestParams.put("UID", "0");
                reMap = JacksonUtils.json2map(OkHttpUtil.postJson(url, JacksonUtils.mapToJson(requestParams)));
            }
            if (reMap.containsKey("code")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.info("getStorageBysiteId,siteId:{},问题：{}", siteId, e.getMessage());
            return false;
        }
    }
}
