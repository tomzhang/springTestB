package com.jk51.modules.offline.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2018-08-06
 * 修改记录:
 */
public class FaultERPParams {

    private static final Logger logger = LoggerFactory.getLogger(FaultERPParams.class);

    //接口类型名称
    private final static Map<String, Object> erpPropertyItemName = new HashMap() {{
        put("trades", "订单接口");
        put("storage", "库存接口（订单）");
        put("price", "多价格接口");
        put("pandian", "盘点接口");
        put("member", "会员接口");
    }};
    //接口类型
    private final static Map<Integer, Object> erpPropertyItem = new HashMap() {{
        put(1, "订单接口");
        put(2, "库存接口（订单）");
        put(3, "多价格接口");
        put(4, "盘点接口");
        put(5, "会员接口");
        put(6, "优惠券接口");
    }};
    //故障现象
    private final static Map faultPheno = new HashMap() {{
        put(1, "线下erp未接收到订单信息");
        put(2, "用户下单时不显示可分配门店");
        put(3, "商户后台未读取到多价格数据");
    }};
    //订单故障原因
    private final static Map<Integer, String> faultReason_trades = new HashMap() {{
        put(500, "接口请求数量过多，造成堵塞");
        put(400, "接口连接中断，推送请求失败");
        put(401, "程序再次请求失败（N>0）");
        put(300, "待排查异常");
        put(201, "订单接口关闭");
        put(200, "订单推送成功");
    }};
    //库存故障原因
    private final static Map<Integer, String> faultReason_storage = new HashMap() {{
        put(200, "所有库存为0或未查询到库存");
        put(400, "接口或数据库连接不上");
        put(500, "请求次数太多导致接口堵塞");
    }};
    //多价格故障原因
    private final static Map<Integer, String> faultReason_price = new HashMap() {{
        put(400, "接口连接中断或数据库连接不上");
        put(500, "数据处理时出现异常");
    }};

    //获取设置接口的名称
    public static Map<String, Object> getERPPropertyItemNameAll() {
        try {
            return erpPropertyItemName;
        } catch (Exception e) {
            return null;
        }
    }

    //获取设置接口的名称
    public static String getERPPropertyItemName(String name) {
        try {
            return erpPropertyItemName.get(name).toString();//结果转换为字符串送出
        } catch (Exception e) {
            return "";
        }
    }

    //获取故障接口类型
    public static String getERPProperty(Integer type) {
        try {
            return erpPropertyItem.get(type).toString();//结果转换为字符串送出
        } catch (Exception e) {
            return "";
        }
    }

    //获取故障现象
    public static String getERPFaultPheno(Integer type) {
        try {
            return faultPheno.get(type).toString();//结果转换为字符串送出
        } catch (Exception e) {
            return "";
        }
    }

    //获取故障原因
    public static String getERPFaultReason(Integer type, Integer faultStatus) {
        try {
            if (type == 1) {
                return faultReason_trades.get(faultStatus);
            } else if (type == 2) {
                return faultReason_storage.get(faultStatus);
            } else if (type == 3) {
                return faultReason_price.get(faultStatus);
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

}
