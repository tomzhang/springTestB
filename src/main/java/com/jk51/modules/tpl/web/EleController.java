package com.jk51.modules.tpl.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.coupon.requestParams.UpdateOrderPriceParams;
import com.jk51.modules.es.entity.ReturnDto;
import com.jk51.modules.tpl.service.EleService;
import com.jk51.modules.tpl.service.TestEleService;
import com.jk51.modules.trades.service.TradesService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:蜂鸟配送
 * 作者: liufurong
 * 创建日期: 2017-02-21
 * 修改记录:
 */
@Controller
@RequestMapping("/ele")
public class EleController {
    private static final Log logger = LogFactory.getLog(EleController.class);
    @Autowired
    private EleService eleService;
    @Autowired
    private TestEleService testEleService;
    @Autowired
    private TradesService tradesService;

    /**
     * 获取Token(测试)
     * @return
     */
    @RequestMapping("/getToken")
    @ResponseBody
    public Map<String,Object> getToken(){
        return new HashMap() {{
            put("status", "OK");
        }};
    }

    /**
     * 取消订单
     * @param request
     * @return
     */
    @RequestMapping("/cancelOrder")
    @ResponseBody
    public Map<String,Object> cancelOrder(HttpServletRequest request){
        Map<String,String> param = ParameterUtil.getParameterMapString(request);
        return eleService.canceld(param);
    }

    /**
     * 创建订单\测试
     * @param request
     * @return
     */
    @RequestMapping("/createOrder")
    @ResponseBody
    public Map<String,Object> createOrder(HttpServletRequest request){
        return testEleService.test();
    }

    /**
     * 查询订单
     * @param partner_order_code
     * @return
     */
    @RequestMapping("/queryOrder")
    @ResponseBody
    public Map<String,Object> queryOrder(String partner_order_code){
        return eleService.queryOrder(partner_order_code);
    }
    /**
     * 修改订单价格
     */
    @RequestMapping("/upTradesPrice")
    @ResponseBody
    public Object upTradesPrice( UpdateOrderPriceParams params){
        try {
            return tradesService.upTradesPrice(params);
        }catch (Exception e){
            logger.error("修改订单价格失败{}",e);
            return ReturnDto.buildFailedReturnDto("修改订单价格失败");
        }
    }

    /**
     * 下单回调地址
     * @return
     */
    @RequestMapping("/notifyOrderStatus")
    @ResponseBody
    public void notifyOrderStatus(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        logger.info("蜂鸟配送回调--"+param);
        //data={"open_order_code":"-1","partner_order_code":"1000301493870459469","order_status":5,"push_time":1493876209570,"carrier_driver_name":null,"carrier_driver_phone":null,"cancel_reason":null,"platform_code":null,"description":"订单配送距离太远了超过阈值","tracking_id":null,"error_code":"OVER_RANGE_MAX_DISTANCE_ERROR","station_name":null,"station_tel":null}
        try {
            logger.info("蜂鸟配送回调参数：" + JacksonUtils.mapToJson(param));
            eleService.insertLog("蜂鸟配送回调参数：" + JacksonUtils.mapToJson(param));

            String data = param.get("data") + "";
            JSONObject item = (JSONObject) JSON.parse(data);
            eleService.updatbLogisticsOrder(item,0);
        } catch (Exception e) {
            logger.error("蜂鸟回调报错", e);
        }
    }

    /**
     * 查询物流记录
     * @param orderNumber
     * @return
     */
    @RequestMapping("/selectbLogisticsOrder")
    @ResponseBody
    public Map<String,Object> selectbLogisticsOrder(String orderNumber){
        return eleService.selectbLogisticsOrder(orderNumber);
    }
}
