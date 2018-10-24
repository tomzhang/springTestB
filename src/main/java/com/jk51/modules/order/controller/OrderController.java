package com.jk51.modules.order.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.concession.result.GiftResult;
import com.jk51.model.grouppurchase.GroupPurchase;
import com.jk51.model.order.*;
import com.jk51.model.order.response.DistributeResponse;
import com.jk51.model.order.response.OrderResponse;
import com.jk51.model.order.response.UpdateOrderPayStyleReq;
import com.jk51.model.treat.BGoodsPrebook;
import com.jk51.model.treat.BPrebookNew;
import com.jk51.modules.appInterface.mapper.BGoodsPrebookMapper;
import com.jk51.modules.order.service.DistributeOrderService;
import com.jk51.modules.order.service.OrderPayService;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.sms.service.Sms7MoorService;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.management.monitor.StringMonitor;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:订单创建、查询controller
 * 作者: baixiongfei
 * 创建日期: 2017/2/15
 * 修改记录:
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    public static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private DistributeOrderService distributeOrderService;

    @Autowired
    private TradesMapper tradesMapper;

    @Autowired
    private BGoodsPrebookMapper bGoodsPrebookMapper;

    @Autowired
    private OrderPayService orderPayService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 创建订单，送货上门订单及门店自提订单
     *
     * @param homeDeliveryAndStoresInvite
     * @return
     */
    @RequestMapping("/create")
    @ResponseBody
    public OrderResponse createOrder (@RequestBody HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite) {
        if (StringUtils.isNotEmpty(homeDeliveryAndStoresInvite.getGroupPurchaseJson())) {
            GroupPurchase groupPurchase = JSON.parseObject(homeDeliveryAndStoresInvite.getGroupPurchaseJson(), GroupPurchase.class);
            homeDeliveryAndStoresInvite.setGroupPurchase(groupPurchase);
        }
        return orderService.createOrders(homeDeliveryAndStoresInvite);
    }


//    /**
//     * 创建医生服务的预订单
//     *
//     * @param
//     * @return
//     */
//    @RequestMapping("/doctorServceBeforeCreate")
//    @ResponseBody
//    public ReturnDto createServceOrder(@RequestBody ServceOrder servceOrder){
//
//        //判断参数是否
//        if(null == servceOrder.getSchedulePersonId()) return ReturnDto.buildFailedReturnDto("预约人的信息缺失");
//        if(null ==servceOrder.getDiagnoseStatus()) return ReturnDto.buildFailedReturnDto("初复诊信息缺失");
//        if(StringUtils.isBlank(servceOrder.getDiseaseInfo())) return ReturnDto.buildFailedReturnDto("初复诊信息缺失");
//        if(null == servceOrder.getUseDetailId() || null == servceOrder.getSiteId()){
//            return ReturnDto.buildFailedReturnDto("模版信息或者siteId缺失确认");
//        }
//        //查询预定的该服务是否被预定满
//        ServceOrder servceOrderRes = servceOrderMapper.selectByPrimaryKey(servceOrder.getUseDetailId(), servceOrder.getSiteId());
//        if(null != servceOrderRes && servceOrderRes.getUseCount() < 1) {
//            return ReturnDto.buildFailedReturnDto(" ");
//        }
//        servceOrder.setServeStatus(0);
//        servceOrder.setCreateTime(new Date());
//        servceOrder.setUpdateTime(servceOrder.getCreateTime());
//        //向预约表中插入一条数据

//        Boolean flag = servceOrderService.insert(servceOrder);
//        if(flag) return ReturnDto.buildStatusOK();
//        else return ReturnDto.buildFailedReturnDto("创建预订单失败");
//
//    }

    /**
     * 创建医生服务的订单
     *
     * @param
     * @return
     */
    @RequestMapping("/doctorServceOrderCreate")

    @ResponseBody
    public OrderResponse doctorServceOrderCreate (@RequestBody HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite) {
        logger.info(homeDeliveryAndStoresInvite.toString());
        OrderResponse response = new OrderResponse();
        if (homeDeliveryAndStoresInvite.getFlag() == 1) {
            //查询预定的该服务是否被预定满
            //ServceOrder servceOrderRes = servceOrderMapper.selectByPrimaryKey(homeDeliveryAndStoresInvite.getUserDetailId(), homeDeliveryAndStoresInvite.getSiteId());
            if (homeDeliveryAndStoresInvite.getUseCount() < 1) {
                response.setCode(CommonConstant.TRADES_RESP_CODE_MISSINF_PARAMS);
                response.setMessage("该服务已经被预约满");
                return response;
            }
        }
        return orderService.createOrders(homeDeliveryAndStoresInvite);
    }

    /**
     * 获取服务医生的预订单
     *
     * @param
     * @return
     */
    @RequestMapping("/getDoctorServceOrder")
    @ResponseBody
    public ReturnDto getDoctorServceOrder (@RequestParam(required = false) String tradesId) {
        if (StringUtils.isNotBlank(tradesId)) {
            Trades trades = tradesMapper.getTradesByTradesId(Long.valueOf(tradesId));
            return ReturnDto.buildSuccessReturnDto(trades);
        }
        Trades trades = new Trades();
        trades.setServceTpye(100);
        List<Trades> tradesList = tradesMapper.getDocetorServceTrades(trades);
        return ReturnDto.buildSuccessReturnDto(tradesList);
    }

    /**
     * 创建门店直购订单
     *
     * @param storeDirect
     * @return
     */
    @RequestMapping("/storedirect")
    @ResponseBody
    public OrderResponse createStoreDirect (@RequestBody StoreDirect storeDirect) {
        logger.info(storeDirect.toString());
        return orderService.createStoreDirectOrders(storeDirect);
    }

    /**
     * 预下单(送货上门订单，门店自提订单，门店自提订单)，计算订单价格信息
     *
     * @param req
     * @return
     */
    @RequestMapping("/beforeorder")
    @ResponseBody
    public ReturnDto beforeOrder (@RequestBody BeforeCreateOrderReq req) {
        logger.info(req.toString());
        ReturnDto returnDto = new ReturnDto();
        try {
            if (StringUtils.isNotBlank(req.getGroupPurchaseJson())) {
                GroupPurchase groupPurchase = JSON.parseObject(req.getGroupPurchaseJson(), GroupPurchase.class);
                req.setGroupPurchase(groupPurchase);
            }

            if (StringUtils.isNotBlank(req.getGiftGoodsJson())) {
                List<GiftResult> giftResultList = JSON.parseArray(req.getGiftGoodsJson(), GiftResult.class);
                req.setGiftGoods(giftResultList);
            }

            DistributeResponse response = distributeOrderService.beforeOrder(req, "2");
            returnDto.setCode("0000");
            returnDto.setMessage("success");
            returnDto.setValue(JacksonUtils.obj2jsonIgnoreNull(response));
        } catch (Exception e) {
            logger.error("预下单错误：", e);
            returnDto.setCode("9999");
            returnDto.setMessage("failed");
            String message = e.getMessage();
            if (StringUtils.isNotBlank(message) && message.startsWith("不符合规则:")) {
                returnDto.setErrorMessage(message.split(":")[1]);
            }
        }
        return returnDto;
    }

    /**
     * 门店自提
     *
     * @param params
     * @return
     */
    @RequestMapping("/getBestStore")
    @ResponseBody
    public ReturnDto getBestStore (@RequestBody BestStoreQueryParams params) {
        logger.info("获取最优门店请求参数:{}", params.toString());
        ReturnDto response;
        Map<String, Object> map =null;
        try {
            map = orderService.getBestStore(params);
            response = ReturnDto.buildSuccessReturnDto(map);

        } catch (Exception e) {
            logger.error("获取最优门店信息失败!{}", e);
            response = ReturnDto.buildStatusERRO(ExceptionUtils.getRootCauseMessage(e));
        }
        try {
            if(map!=null&&map.containsKey("store")){
                Store store= (Store) map.get("store");
                logger.error("获取最优门店信息params!{}", params);
                stringRedisTemplate.opsForValue().set(params.getSiteId()+params.getMobile()+"_BestStore",store.getId()+"");
            }

        } catch (Exception e) {
            logger.error("获取最优门店信息失败!解析错误：{}", e);
        }
        return response;
    }

    /**
     * 预下单获取分销商品信息和招募信息
     *
     * @param req
     * @return
     */
    @RequestMapping("/getDistributeInfo")
    @ResponseBody
    public ReturnDto showDistributeInfo (@RequestBody BeforeCreateOrderReq req) {
        ReturnDto returnDto = new ReturnDto();
        try {
            String distributeInfo = this.distributeOrderService.getDistributeInfo(req);
            returnDto.setCode("0000");
            returnDto.setMessage("success");
            returnDto.setValue(distributeInfo);
        } catch (Exception e) {
            logger.error("获取分销信息失败", e);
            returnDto.setCode("9999");
            returnDto.setMessage("failed");
        }
        return returnDto;
    }

    /**
     * 更新订单的买家支付方式
     *
     * @param req
     * @return
     */
    @RequestMapping("/updatePayStyle")
    @ResponseBody
    public ReturnDto updatePayStyle (@RequestBody UpdateOrderPayStyleReq req) {
        logger.info("更新订单支付方式请求参数:{}", req.toString());
        ReturnDto response = null;
        try {
            if (orderService.updateOrderPayStyle(req)) {
                response = ReturnDto.buildSuccessReturnDto("");
            } else {
                logger.info("更新订单支付方式失败!");
            }
        } catch (Exception e) {
            logger.error("更新订单支付方式失败!", e);
            response = ReturnDto.buildFailedReturnDto(ExceptionUtils.getRootCauseMessage(e));
        }
        return response;
    }

    /**
     * 查询预约列表(预约购买商品)
     *
     * @param req
     * @return
     */
    @RequestMapping("/queryPreOrderList")
    @ResponseBody
    public Map<String, Object> queryPreOrderList (@RequestBody OrderPreQueryReq req) {
        //分页查询
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<BPrebookNew> goodsPrebookList = bGoodsPrebookMapper.getGoodsPrebookList(req);
        //查询总预约数,已关闭,未关闭
//        Integer closedNum = bGoodsPrebookMapper.getClosedNum();//总关闭数

        PageInfo<BPrebookNew> bPrebookNewPageInfo = new PageInfo<>(goodsPrebookList);
//        long total = bPrebookNewPageInfo.getTotal();
        //查询预约总数prebookNums和已关闭closedNum和未关闭数noClosedNum
        Map<String,Object> allNums = bGoodsPrebookMapper.queryAllKindsNums(req.getSiteId());
//        long noCloseNum = total - closedNum;
        Map<String, Object> preOrderMap = new HashMap<String, Object>();
        preOrderMap.put("goodsPrebooks", goodsPrebookList);
//        preOrderMap.put("pageInfo", page.toPageInfo(goodsPrebooks));
        preOrderMap.put("pageInfo", bPrebookNewPageInfo);
        preOrderMap.put("total",allNums.get("prebookNums"));
        preOrderMap.put("closedNum",allNums.get("closedNum"));
        preOrderMap.put("noCloseNum",allNums.get("noClosedNum"));

        return preOrderMap;
    }

    /**
     * App保存预约商品信息
     * @param opq
     *
     * @return
     */
    @RequestMapping(value = "/savePrebookInfo",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto savePrebookInfo(@RequestBody OrderPreQueryReq opq) {
        int result = bGoodsPrebookMapper.savePreInfo(opq);
        if(result == 1) {
            return ReturnDto.buildSuccessReturnDto("保存预约信息成功!");
        }else {
            return ReturnDto.buildFailedReturnDto("保存预约信息失败!");
        }
    }

    @PostMapping("/updatePrebookStatus")
    @ResponseBody
    public ReturnDto updatePreOrderStatus(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        int result = bGoodsPrebookMapper.updatePreStatus(parameterMap);

        if (result == 1) {
            return ReturnDto.buildSuccessReturnDto("关闭预约单状态成功~!");
        }else {
            return ReturnDto.buildFailedReturnDto("关闭预约单状态失败!");
        }
    }

    /**
     * 送货上门订单电话提醒回调
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("recall")
    public Map<String, Object> recall (HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        logger.info("recall----------------{}", param);
        if (!"4".equals(param.get("Message"))) {
            logger.info("actionid----------------{}", param.get("actionid"));
            if (param.get("actionid") != null) {
                Long tradesId = Long.parseLong(param.get("actionid").toString());
                String oldtradesId = stringRedisTemplate.opsForValue().get(tradesId + "_7moorOrder");
                if (!StringUtils.isEmpty(oldtradesId)) {
                    orderPayService.sendMqMessage(tradesId, 30, null);
                    stringRedisTemplate.opsForValue().set(tradesId + "_7moorOrder", "");
                }

            }
        }
        return null;
    }

    @Autowired
    Sms7MoorService _7moorService;

    @RequestMapping("/tail")
    public Map<String, Object> tail (HttpServletRequest request) {
        Map<String, Object> str = _7moorService.webcall("18302196165", 0);
        return str;
    }
    @RequestMapping("/getRedisShorturl")
    @ResponseBody
    public String getOldshortUrl(@RequestParam(required = false) String shorturl) {
        String url = "";
        try {
            url =stringRedisTemplate.opsForValue().get(shorturl+"_shortUrl");
        } catch (Exception e) {
            logger.error("获取URL微信端异常：{}", e);
        }
        return url;
    }

    @RequestMapping("/querySettingDis")
    @ResponseBody
    public Map<String, Object> querySettingDis (@RequestParam(required = false) String siteId,@RequestParam(required = false)int tradesSource,@RequestParam(required = false)int rderRealPrice) {
       return distributeOrderService.querySettingDis(siteId, tradesSource, rderRealPrice);
    }
}
