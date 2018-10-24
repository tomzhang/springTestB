package com.jk51.modules.trades.service;

import com.jk51.exception.BusinessLogicException;
import com.jk51.model.Stores;
import com.jk51.model.order.Trades;
import com.jk51.model.order.TradesExt;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.meituan.service.MtService;
import com.jk51.modules.offline.service.ErpToolsService;
import com.jk51.modules.tpl.mapper.BLogisticsOrderMapper;
import com.jk51.modules.tpl.service.ImdadaService;
import com.jk51.modules.trades.mapper.DeliverypriceMapper;
import com.jk51.modules.trades.mapper.TradesExtMapper;
import com.jk51.modules.treat.mapper.DeliveryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TradesDeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(TradesDeliveryService.class);

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Autowired
    private MtService mtService;

    @Autowired
    private TradesExtMapper tradesExtMapper;
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private BLogisticsOrderMapper bLogisticsOrderMapper;

    @Autowired
    private ImdadaService imdadaService;

    @Autowired
    private TradesService tradesService;
    @Autowired
    private DeliverypriceMapper deliverypriceMapper;
    @Autowired
    ErpToolsService erpToolsService;

    public void DeliveryDispatchTmp(Trades trades, Stores stores) {

        //eleService.createImOrder(trades.getTradesId() + "", stores.getId());

        mtService.createOrder(trades, stores);
    }

    public Map DeliveryDispatch(Trades trades, Stores stores) {
        Map result = new HashMap();
        try {
            TradesExt tradesExt = tradesExtMapper.getByTradesId(trades.getTradesId());
            int elePrice = getBasicPrice("ele", stores.getCity());
            double d = tradesExt.getDistance()/1000;
            int mtPrice = calcMtPrice(stores.getCity(), d);
            Integer meituanStatus=storesMapper.queryEleStatusMeituan(trades.getSiteId().toString(), stores.getId()+"");
            if(meituanStatus==null||meituanStatus==0){
                tradesExtMapper.updateDeliveryInfo(mtPrice, elePrice, DeliveryDispatchConstanct.FLAG_ELE, trades.getTradesId());
                result = tradesService.notifyExpress(Integer.parseInt(stores.getId()+""), trades.getTradesId());
            }else {
                if (elePrice <= mtPrice) {
                    tradesExtMapper.updateDeliveryInfo(mtPrice, elePrice, DeliveryDispatchConstanct.ELE_MT_IM, trades.getTradesId());
                    result = tradesService.notifyExpress(Integer.parseInt(stores.getId()+""), trades.getTradesId());
                } else {
                    tradesExtMapper.updateDeliveryInfo(mtPrice, elePrice, DeliveryDispatchConstanct.MT_ELE_IM, trades.getTradesId());
                    result = mtService.createOrder(trades, stores);
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("DeliveryDispatch error{}", e);
            result.put("error", e.toString());
            return result;
        }

    }

    /**
     *
     * @param city 城市
     * @param distance 距离(单位km)
     * @return
     */
    public int calcMtPrice(String city, double distance){
//        int i1 = getBasicPrice("mt", city);
//        int i3 = priceMarkupDistance(distance);
        int i1 = getPrice(0,0,"mt", city);
        int i2 = priceMarkupTime();
        int i3 = priceMarkupDistance2(distance);
        int i4 = 0;//priceMarkupWeight(0.5);
        logger.error("mtPrice[base:{},time:{},distance:{},weight:{}]", i1, i2, i3, i4);
        return i1 + i2 + i3 + i4;
    }

    public Map deliveryHandler(long tradesId, String flag, String msg){
        Map result = new HashMap();
        String orderKey = tradesExtMapper.queryDisOrder(tradesId);
        if(orderKey == null) return result;
        String storeId = bLogisticsOrderMapper.queryTrade(tradesId+"");

        if(flag.equals(DeliveryDispatchConstanct.FLAG_ELE)){
            if(DeliveryDispatchConstanct.ELE_MT_IM.equals(orderKey)){
                logger.info("-------配送商调度------蜂鸟配送失败(mt)，订单号"+tradesId+",order"+orderKey);
                result = mtService.createOrderFmt(tradesId, Integer.parseInt(storeId));
            }else if(DeliveryDispatchConstanct.MT_ELE_IM.equals(orderKey)){
                logger.info("-------配送商调度------蜂鸟配送失败(im)，订单号"+tradesId+",order"+orderKey);
                Map imParam = new HashMap();
                imParam.put("tradesId", tradesId);
                imParam.put("storeId", storeId);
                imParam.put("msg", msg);
                result = imdadaService.createOrder(imParam);
            }
        }else if(flag.equals(DeliveryDispatchConstanct.FLAG_MT)){
            if (DeliveryDispatchConstanct.MT_ELE_IM.equals(orderKey)) {
                logger.info("-------配送商调度------美团配送失败(ele)，订单号" + tradesId + ",order" + orderKey);
                try {
                    result = tradesService.notifyExpress(Integer.parseInt(storeId), tradesId);
                } catch (BusinessLogicException e1) {
                    logger.error("error", e1);
                }
            } else if (DeliveryDispatchConstanct.ELE_MT_IM.equals(orderKey)) {
                logger.info("-------配送商调度------美团配送失败(im)，订单号" + tradesId + ",order" + orderKey);
                Map imParam = new HashMap();
                imParam.put("tradesId", tradesId);
                imParam.put("storeId", storeId);
                imParam.put("msg", msg);
                result = imdadaService.createOrder(imParam);
            }
        }

        return result;
    }

    public int priceMarkupTime() {

        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
            Date now = null;
            Date beginTime = null;
            Date endTime = null;
            now = df.parse(df.format(new Date()));

            //now = df.parse("21:00");

            beginTime = df.parse("00:00");
            endTime = df.parse("05:59");
            Boolean flag1 = belongCalendar(now, beginTime, endTime);
            if (flag1) return 200;

            beginTime = df.parse("11:00");
            endTime = df.parse("12:59");
            Boolean flag2 = belongCalendar(now, beginTime, endTime);
            if (flag2) return 200;

            beginTime = df.parse("21:00");
            endTime = df.parse("24:00");
            Boolean flag3 = belongCalendar(now, beginTime, endTime);
            if (flag3) return 200;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }


    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    public int priceMarkupDistance(double d) {
        int i = 0;
        if (d >= 0 && d <= 3) {
            i = 0;
        } else if (d > 3 && d <= 4) {
            i = 100;
        } else if (d > 4 && d <= 5) {
            i = 100 + 100;
        } else if (d > 5) {
            i = 100 + 100 + 200;
        }
        return i;
    }

    /**
     * type1   0=美团，1=蜂鸟  2达达
     * type2   0 基础价 1距离 2重量
     * type3   type2=0基础费时type3表示(1一线 2二线···城市)； type2=1距离时type3表示括号内的距离区间（1:0-1;2:1-3;3:3-5;4:5-7;5:7-10）
     * @param distance 按距离收费 查询数据库设置的价格
     * @return
     */
    public int priceMarkupDistance2(double distance) {
        int a,b,c,d,e;
        Map map = new HashMap();
        map.put("type1",0);//0表示美团
        map.put("type2",1);//距离
        if (distance >= 0 && distance <= 1) {
            map.put("type3",1);
            a = deliverypriceMapper.getPrice(map);
            return a;
        } else if (distance > 1 && distance <= 3) {
            map.put("type3",1);
            a = deliverypriceMapper.getPrice(map);
            map.put("type3",2);
            b = deliverypriceMapper.getPrice(map);

            return (int) (a+b*(Math.ceil(distance-1)));
        } else if (distance > 3 && distance <= 5) {
            map.put("type3",1);
            a = deliverypriceMapper.getPrice(map);
            map.put("type3",2);
            b = deliverypriceMapper.getPrice(map);
            map.put("type3",3);
            c = deliverypriceMapper.getPrice(map);
            return (int) (a+b*(3-1)+c*(Math.ceil(distance-3)));
        } else if (distance > 5 && distance <= 7) {
            map.put("type3",1);
            a = deliverypriceMapper.getPrice(map);
            map.put("type3",2);
            b = deliverypriceMapper.getPrice(map);
            map.put("type3",3);
            c = deliverypriceMapper.getPrice(map);
            map.put("type3",4);
            d = deliverypriceMapper.getPrice(map);
            return (int) (a+b*2+c*2+d*(Math.ceil(distance-5)));
        }else if (distance > 7 && distance <= 10) {
            map.put("type3",1);
            a = deliverypriceMapper.getPrice(map);
            map.put("type3",2);
            b = deliverypriceMapper.getPrice(map);
            map.put("type3",3);
            c = deliverypriceMapper.getPrice(map);
            map.put("type3",4);
            d = deliverypriceMapper.getPrice(map);
            map.put("type3",5);
            e = deliverypriceMapper.getPrice(map);
            return (int) (a+b*2+c*2+d*2+e*(Math.ceil(distance-7)));
        }
        return 0;
    }

    public int priceMarkupWeight(double w) {
        int i = 0;
        if (w >= 0 && w < 5) {
            i = 0;
        } else if (w >= 5 && w < 10) {
            i = 50;
        } else if (w >= 10 && w < 20) {
            i = 50 + 100;
        } else if (w >= 20) {
            i = 50 + 100 + 200;
        }
        return i;
    }

    public int getBasicPrice(String type, String city) {

        Object[] mtArray = {"1", 700, "2", 650, "3", 620, "4", 600, "5", 550, "6", 550};
        Object[] eleArray = {"A", 780, "B", 720, "C", 680, "D", 640, "E", 600, "F", 550, "代理城市", 600};

        List<Map> list = deliveryMapper.queryDeliveryBasicFee(type);
        final int[] result = {0};
        list.stream().forEach(map -> {
            if (city.indexOf(map.get("city") + "") != -1) {
                Object level = map.get("level");
                if (type.equals("ele")) {
                    result[0] = getArrayValue(eleArray, level);
                } else if (type.equals("mt")) {
                    result[0] = getArrayValue(mtArray, level);
                }
            }
        });
        if(result[0]==0){
            if (type.equals("ele")) {
                result[0] = 600;
            } else if (type.equals("mt")) {
                result[0] = 550;
            }
            erpToolsService.sendMailTo51JK("第三方配送技术价格","第三方配送技术价格为0的城市："+city+ ",第三方配送名称：" + type);
        }
        return result[0];
    }

    public int getArrayValue(Object[] array, Object level) {
        for (int i = 0; i < array.length; i++) {
            if (level.equals(array[i])) {
                return Integer.parseInt(array[i + 1] + "");
            }
        }
        return 0;
    }

    /**
     * 获取价格
     * @param type1 0美团
     * @param type  mt
     * @param type2 0基础费
     * @param city
     * @return
     */
    public int getPrice(Integer type1,Integer type2, String type,String city) {
        List<Map> list = deliveryMapper.queryDeliveryBasicFee(type);
        Map parm = new HashMap();
        parm.put("type1",type1);
        parm.put("type2",type2);
        for (Map map : list) {
            if (city.indexOf(map.get("city") + "") != -1) {
                Object level = map.get("level");
                parm.put("type3",level);
                return deliverypriceMapper.getPrice(parm);
            }
        }
        return 0;
    }

}
