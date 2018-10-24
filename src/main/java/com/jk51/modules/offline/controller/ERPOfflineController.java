package com.jk51.modules.offline.controller;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.erpprice.service.ErpPriceImportService;
import com.jk51.modules.erpprice.service.SyncService;
import com.jk51.modules.goods.service.GoodsService;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.offline.mapper.MerchantERPMapper;
import com.jk51.modules.offline.service.*;
import com.jk51.modules.offline.utils.ErpMerchantUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 企业erp对接调度接口集合
 * 请求一律用get
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-10 15:22
 * 修改记录:
 */
@Controller
@ResponseBody
@RequestMapping("/offline/company")
public class ERPOfflineController {

    private static final Logger logger = LoggerFactory.getLogger(ERPOfflineController.class);

    @Autowired
    private TianROfflineService tianROfflineService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GuangJiOfflineService guangJiOfflineService;
    @Autowired
    private ZhongLianService zhongLianService;
    @Autowired
    private ErpToolsService erpToolsService;
    @Autowired
    StoreAdminMapper storeAdminMapper;
    @Autowired
    CouponErpService couponErpService;
    @Autowired
    private BaoDaoOfflineService baoDaoOfflineService;
    @Autowired
    private OfflineIntegrateService offlineIntegrateService;
    @Autowired
    private ErpPriceImportService erpPriceImportService;
    @Autowired
    SyncService syncService;
    @Autowired
    private OfflineOrderService offlineOrderService;
    @Autowired
    private OfflineMemberService offlineMemberService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ErpMerchantSettingService erpMerchantSettingService;
    @Autowired
    private MerchantERPMapper merchantERPMapper;

    /**
     * guangji
     * 获取会员信息接口
     *
     * @param siteId
     * @param mobile
     * @return
     */
/*    @RequestMapping("/getuser")
    public Map<String, Object> getUser(Integer siteId, String mobile, String inviteCode) {
        try {
            if (siteId == 100190) {
                return tianROfflineService.getUser(siteId, mobile);
            } else if (siteId == 100204) {
                return guangJiOfflineService.getUser2(siteId, mobile, inviteCode);
            } else if (siteId == 100173) {
                return zhongLianService.getUser(siteId, mobile, inviteCode);
            }
        } catch (Exception e) {
            logger.info("商家不存在" + e);
        }
        return getDefaultResult();
    }*/

    /**
     * 更新会员接口
     *
     * @return
     */
  /*  @RequestMapping("/updateuser")
    public Map<String, Object> updateUser(@RequestBody Map<String, Object> param) {
        try {
            if (!StringUtil.isEmpty(param.get("siteId"))) {
                Integer siteId = Integer.parseInt(param.get("siteId").toString());
                String mobile = param.get("mobile_no").toString();
                String name = param.get("name").toString();
                String sex = "";
                if (!StringUtil.isEmpty(param.get("sex"))) {
                    sex = param.get("sex").toString();
                }
                String birthday = "";
                if (!StringUtil.isEmpty(param.get("birthday"))) {
                    birthday = param.get("birthday").toString();
                }
                String address = "";
                if (!StringUtil.isEmpty(param.get("address"))) {
                    address = param.get("address").toString();
                }
                if (siteId == 100190) {
                    if (StringUtil.isEmpty(sex)) {
                        sex = "null";
                    }
                    if (StringUtil.isEmpty(birthday)) {
                        birthday = "null";
                    }
                    if (StringUtil.isEmpty(address) || address.equals("|")) {
                        address = "null";
                    }
                    return tianROfflineService.updateinfo(siteId, mobile, name, sex, birthday, address);
                } else if (siteId == 100204) {
                    return guangJiOfflineService.updateUser2(siteId, mobile, name, sex, birthday, address);
                } else if (siteId == 100173) {
                    return zhongLianService.updateUser(siteId, mobile, name, sex, birthday, address);
                }
            } else {
                return getDefaultResult();
            }
        } catch (Exception e) {
            logger.info("商家不存在" + e);
        }
        return getDefaultResult();
    }*/

    /**
     * 查询线下积分总数
     *
     * @param siteId
     * @param mobile
     * @return
     */
    @RequestMapping("/queryscore")
    public Map<String, Object> queryScore(Integer siteId, String mobile) {
        try {
            return offlineIntegrateService.getOffTotalScore(siteId, mobile);
        } catch (Exception e) {
            logger.info("商家不存在" + e.getMessage());
        }
        return getDefaultResult();
    }

    /**
     * 查询线下积分获取的积分列表
     *
     * @param siteId
     * @param mobile
     * @return
     */
    @PostMapping("receiveScoreListBysiteId")
    public Map<String, Object> getScoreList(Integer siteId, String mobile, Integer pageNum, Integer pageSize) {
        try {
            if (siteId == 100204) {
                return guangJiOfflineService.getScoreList(mobile, pageNum, pageSize);
            } else if (siteId == 100190) {
                return tianROfflineService.getScoreList(mobile, pageNum, pageSize);
            } else if (siteId == 100213 || siteId == 100239 || siteId == 100271) {
                return offlineIntegrateService.queryScoreList_jishengtang(siteId, mobile, pageNum, pageSize);
            } else if (siteId == 100272) {
                return offlineIntegrateService.queryScoreList_jurentang(siteId, mobile, pageNum, pageSize);
            }
        } catch (Exception e) {
            logger.info("商家不存在" + e);
        }
        return getDefaultResult();
    }

    public Map<String, Object> getDefaultResult() {
        Map<String, Object> defaultResult = new HashMap<String, Object>();
        defaultResult.put("code", -1);
        defaultResult.put("meg", "该商家不存在");
        return defaultResult;
    }

    @RequestMapping("updateGoodsPrice")
    public Map<String, Object> updateGoodsPrice(@RequestBody Map<String, Object> params) {
        return goodsService.updateGoodsDisPrice(params);
    }

    @RequestMapping("getGoodsCodes")
    public List<String> getGoodsCodes(Integer siteId) {
        return goodsService.getGoodCodeBySiteId(siteId);
    }

    @PostMapping("synchMemberByTimer")
    public void synchMemberByTimer(Integer siteId) {
        if (siteId == 100204) {
            guangJiOfflineService.memberByTimer();
        }
    }

    /**
     * 实时记录积分变化记录
     *
     * @return
     */
    @RequestMapping("integralChange")
    public Map<String, Object> integralChange(@RequestBody Map<String, Object> request) {
        logger.info("记录积分变化：{}", request.toString());
        try {
            Integer siteId = Integer.parseInt(request.get("siteId").toString());
            List integrallist = JSONArray.parseArray(JacksonUtils.obj2json(request.get("integrallist")), Map.class);
            return offlineIntegrateService.getIntegralfromERP(siteId, integrallist);
        } catch (Exception e) {
            logger.info("积分消费信息有误" + e);
        }
        return getDefaultResult();
    }

    /**
     * 实时记录积分变化记录
     *
     * @return
     */
    @RequestMapping("integralChange2")
    public Map<String, Object> integralChange2(@RequestBody Map<String, Object> request) {
        logger.info("记录积分变化：{}", request.toString());
        try {
            Integer siteId = Integer.parseInt(request.get("siteId").toString());
            List integrallist = JSONArray.parseArray(JacksonUtils.obj2json(request.get("integrallist")), Map.class);
            if (siteId == 100190) {
                return offlineIntegrateService.getIntegralfromERP(siteId, integrallist);
            }
        } catch (Exception e) {
            logger.info("积分消费信息有误" + e);
        }
        return getDefaultResult();
    }

    //接口导入商品多价格
    @RequestMapping("storeSyncPrice")
    public Map<String, Object> storeSyncPrice(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer siteId = Integer.parseInt(params.get("siteId").toString());
            List goodsList = JSONArray.parseArray(JacksonUtils.obj2json(params.get("goodlist")), Map.class);
            String counts = stringRedisTemplate.opsForValue().get(siteId + "_erpPriceListCounts");
            if (StringUtil.isEmpty(counts)) {
                stringRedisTemplate.opsForValue().set(siteId + "_erpPriceListCounts", String.valueOf(goodsList.size()));
            } else {
                if (Integer.parseInt(counts) > 20000) {
                    result.put("code", -1);
                    result.put("msg", "传递值中的数量已经超过2万条，请稍后重试。");
                    return result;
                } else {
                    stringRedisTemplate.opsForValue().getAndSet(siteId + "_erpPriceListCounts", String.valueOf(Integer.parseInt(counts) + goodsList.size()));
                }
            }
            String nums = stringRedisTemplate.opsForValue().get(siteId + "_erpPriceNums");
            if (StringUtil.isEmpty(nums)) {
                stringRedisTemplate.opsForValue().set(siteId + "_erpPriceNums", String.valueOf(1), 1, TimeUnit.DAYS);
            } else {
                if (Integer.parseInt(nums) > 100) {
                    result.put("code", -1);
                    result.put("msg", "今天传递的次数已达到上限100次");
                    return result;
                } else {
                    stringRedisTemplate.opsForValue().getAndSet(siteId + "_erpPriceNums", String.valueOf(Integer.parseInt(nums) + 1));
                }
            }
            stringRedisTemplate.opsForList().leftPush(siteId + "_erpStorePrice", JacksonUtils.mapToJson(params));
            Long size = stringRedisTemplate.opsForList().size(siteId + "_erpStorePrice");
            logger.info("商户Id:{},method:[storeSyncPrice],缓存条数:{}", siteId, size);
            result.put("code", 0);
            result.put("msg", "处理数据成功");
        } catch (Exception e) {
            logger.info("更新商品的多价格体系" + e.getMessage());
        }
        return result;
    }

    /**
     * 点击同步多价格，从b_good_erp同步到yb_stores_good_price
     *
     * @param siteId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "synchErpPriceBySiteId", method = RequestMethod.POST)
    public String synchErpPriceBySiteId(Integer siteId) {
        syncService.syncStorePrice(siteId);
        return "1";
    }

    /**
     * 手动推送订单信息到线下erp中
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "erpPushOrder", method = RequestMethod.POST)
    public ReturnDto erpPushOrder(@RequestBody Map<String, Object> params) {
        try {
            Integer siteId = Integer.parseInt(params.get("siteId").toString());
            List tradeIds = JSONArray.parseArray(JacksonUtils.obj2json(params.get("tradeIds")), String.class);
            for (Object tradeId : tradeIds) {
                offlineOrderService.erpOrdersService(siteId, Long.valueOf(tradeId.toString()));
            }
            return ReturnDto.buildSuccessReturnDto();
        } catch (Exception e) {
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
    }

    /**
     * 手动推送会员到线下erp中
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "erpPushMember", method = RequestMethod.POST)
    public ReturnDto erpPushMember(@RequestBody Map<String, Object> params) {
        try {
            Integer siteId = Integer.parseInt(params.get("siteId").toString());
            List mobiles = JSONArray.parseArray(JacksonUtils.obj2json(params.get("mobiles")), String.class);
            for (Object mobile : mobiles) {
                offlineMemberService.getuser(siteId, mobile.toString(), "");
            }
            return ReturnDto.buildSuccessReturnDto();
        } catch (Exception e) {
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }
    }

    /**
     * 手动点击取消商户库存的缓存数据
     *
     * @param siteId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "cancelStorageRedis", method = RequestMethod.POST)
    public ReturnDto cancelStorageRedis(Integer siteId) {
        return erpToolsService.clearStorageCacheRedis(siteId);
    }

    @ResponseBody
    @PostMapping(value = "getErpMerchantSetting")
    public List<Map<String, Object>> getErpMerchantSetting(Integer siteId, Integer status) {
        try {
            return erpMerchantSettingService.getErpMerchantInfo(siteId, status);
        } catch (Exception e) {
            logger.error("method:[/offline/company/getErpMerchantSetting],siteId:{},reason:{}",
                siteId, e.getMessage());
            return null;
        }
    }

    @ResponseBody
    @PostMapping(value = "selectErpMerchantName")
    public List<Map<String, Object>> selectErpMerchantName(Integer siteId, String merchantName, Integer status) {
        try {
            return erpMerchantSettingService.selectErpMerchantName(siteId, merchantName, status);
        } catch (Exception e) {
            logger.error("method:[/offline/company/selectErpMerchantName],siteId:{},merchantName:{},status:{},reason:{}",
                siteId, merchantName, status, e.getMessage());
            return null;
        }
    }

    @ResponseBody
    @PostMapping(value = "updateErpAppliStatus")
    public Integer updateErpAppliStatus(@RequestBody ErpMerchantUtils merchantUtils) {
        return erpMerchantSettingService.updateErpAppliStatus(merchantUtils);
    }

    @ResponseBody
    @PostMapping(value = "selectErpFault")
    public PageInfo selectErpFault(Integer siteId, Integer type, String merchantName, String startTime, String endTime,
                                   Integer pageNum, Integer pageSize) {
        return erpMerchantSettingService.selectErpFault(siteId, type, merchantName, startTime, endTime, pageNum, pageSize);
    }

    /**
     * 重新推送订单信息
     *
     * @param faultIds
     * @return
     */
    @ResponseBody
    @PostMapping(value = "pushFaultInfoByTypeAndSiteId")
    public ReturnDto pushFaultInfoByTypeAndSiteId(String faultIds) {
        Integer total = 0;
        Integer pushNum = 0;
        Integer pushFaild = 0;
        try {
            List<Map<String, Object>> faultList = erpMerchantSettingService.selectFaultInfoById(faultIds);
            total = faultList.size();
            if (!CollectionUtils.isEmpty(faultList)) {
                for (Map<String, Object> fault : faultList) {
                    if (Integer.parseInt(fault.get("type").toString()) == 1) {
                        Integer result = offlineOrderService.erpOrdersService_faultCenter(
                            Integer.parseInt(fault.get("id").toString()), Integer.parseInt(fault.get("siteId").toString()),
                            Integer.parseInt(fault.get("type").toString()), Long.parseLong(fault.get("pushInfo").toString()));
                        if (result == 1) {
                            pushNum++;
                        } else {
                            pushFaild++;
                        }
                    } else {
                        pushFaild++;
                    }
                }
            } else {
                pushFaild = total;
            }
            if (total == 1 && pushNum == 1) {
                return ReturnDto.buildSuccessReturnDtoByMsg("推送成功。");
            }
            return ReturnDto.buildSuccessReturnDtoByMsg("需要推送的数量总计:" + total + ",其中推送成功数量:" + pushNum + ",失败数量:" + pushFaild + "。");
        } catch (Exception e) {
            logger.info("故障中心推送订单信息失败:{}", e.getMessage());
            return ReturnDto.buildFailedReturnDto("此次推送订单失败。");
        }
    }

    @ResponseBody
    @PostMapping(value = "getMerchantErpAppli")
    public Map<String, Object> getMerchantErpAppli(Integer siteId) {
        return merchantERPMapper.selectMerchantAppli(siteId);
    }

    @ResponseBody
    @PostMapping(value = "insertERpMerchantInfo")
    public Integer insertERpMerchantInfo(@RequestBody ErpMerchantUtils merchantUtils) {
        return erpMerchantSettingService.insertERpMerchantInfo(merchantUtils);
    }
}
