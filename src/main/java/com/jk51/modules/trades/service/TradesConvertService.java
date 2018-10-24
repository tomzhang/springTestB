package com.jk51.modules.trades.service;

import com.jk51.commons.CommonConstant;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.UnknownTypeException;
import com.jk51.model.YbMeta;
import com.jk51.model.order.Merchant;
import com.jk51.model.order.Trades;
import com.jk51.model.order.TradesLog;
import com.jk51.modules.account.service.SettingDealTimeService;
import com.jk51.modules.concession.service.ConcessionResultHandler;
import com.jk51.modules.grouppurchase.response.GroupInfo;
import com.jk51.modules.grouppurchase.service.GroupPurChaseService;
import com.jk51.modules.integral.mapper.IntegralRuleMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.trades.mapper.TradesLogMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:订单状态判断
 * 作者: lfr
 * 创建日期: 2018-01-06
 * 修改记录:
 */
@Service
public class TradesConvertService {

    private static final Logger logger = LoggerFactory.getLogger(TradesConvertService.class);
    @Autowired
    private SettingDealTimeService settingDealTimeService;
    /*@Autowired
    private TradesMapper tradesMapper;*/
    @Autowired
    private ConcessionResultHandler concessionResultHandler;
    @Autowired
    private IntegralRuleMapper integralRuleMapper;
    @Autowired
    private GroupPurChaseService groupPurChaseService;
    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * 订单状态判断转换
     *
     * @param trades
     * @param platform 0-微信商城，1-pc商城，2-商户后台，3-门店后台，4-门店APP，
     * @param type     0-列表，1-详情
     * @param flag     是否查询优惠券信息部分
     * @return
     */
    public Trades convert(Trades trades, int platform, int type,boolean flag) {
        long startMillis = System.currentTimeMillis();
        try {
            if (StringUtil.isEmpty(trades)) {
                logger.info("订单空不能转换");
                return null;
            }
            Map pageShow = new HashedMap();
            List<Map> buttons = new ArrayList<>();
            GroupInfo groupInfo = groupPurChaseService.queryGroupInfoByTradesId(trades.getSiteId(), trades.getTradesId());
            if(!StringUtil.isEmpty(groupInfo.getGroupStatus())){
                pageShow.put("groupPurChase", groupInfo);
            }
            if (trades.getTradesStatus() == 110) {
                //待付款(110)
                pageShow.put("tradesStatus", "待付款");
                pageShow.put("tradesDescribe", "等待买家付款");
                if (platform == 0 || platform == 1) {
                    Map button = new HashedMap();

                    button.put("index", "1");
                    button.put("show", "付款");
                    buttons.add(button);
                    Map button1 = new HashedMap();
                    button1.put("index", "2");
                    button1.put("show", "找人代付");
                    buttons.add(button1);
                    if (type == 1) {
                        Date tradesEndTime=getTradesEndTime(trades.getSiteId(), trades.getCreateTime());
                        if(!StringUtil.isEmpty(groupInfo.getMainStatus())&&(groupInfo.getMainStatus()==11)){
                            Date groupEndDateTime=DateUtils.parse(groupInfo.getGroupEndDateTime(),"yyyy-MM-dd HH:mm:ss");
                            if(tradesEndTime.getTime() > groupEndDateTime.getTime()){
                                tradesEndTime=groupEndDateTime;
                            }
                        }
                        pageShow.put("tradesEndTime", tradesEndTime);
                        if ("0".equals(trades.getGroupStatus()) || "1".equals(trades.getGroupStatus())) {

                        } else {
                            Map button3 = new HashedMap();
                            button3.put("index", "3");
                            button3.put("show", "取消订单");
                            buttons.add(button3);
                        }

                    }

                } else if (platform == 2 || platform == 3) {
                    if (type == 0) {
                        Map button = new HashedMap();
                        button.put("index", "1");
                        button.put("show", "修改价格");
                        buttons.add(button);
                        Map button1 = new HashedMap();
                        button1.put("index", "2");
                        button1.put("show", "取消订单");
                        buttons.add(button1);
                    }
                } else if (platform == 4) {
                    if (type == 0) {
                        Map button = new HashedMap();
                        button.put("index", "1");
                        button.put("show", "收款");
                        buttons.add(button);
                        Map button1 = new HashedMap();
                        button1.put("index", "2");
                        button1.put("show", "取消订单");
                        buttons.add(button1);
                    }
                }
            }
            if (trades.getTradesStatus() == 160 || trades.getTradesStatus() == 170 || trades.getTradesStatus() == 180) {
                //已取消(160，170，180)
                pageShow.put("tradesStatus", "已取消");
                if (platform == 0 || platform == 1) {
                    Map button = new HashedMap();

                    button.put("index", "1");
                    button.put("show", "再次购买");
                    buttons.add(button);
                }
                if (type == 1) {
                    if (trades.getTradesStatus() == 160) {
                        pageShow.put("tradesDescribe", "买家取消订单，交易已取消。");
                    } else if (trades.getTradesStatus() == 170) {
                        pageShow.put("tradesDescribe", "超时未付款，交易已取消。");
                    } else if (trades.getTradesStatus() == 180) {
                        pageShow.put("tradesDescribe", "商家取消订单，交易已取消。");
                    }
                } else {
                    if (platform == 4) {
                        if (type == 0) {
                            if (trades.getTradesStatus() == 160) {
                                pageShow.put("tradesDescribe", "顾客关闭，交易已取消");
                            } else if (trades.getTradesStatus() == 170) {
                                pageShow.put("tradesDescribe", "超时未付款，交易已取消。");
                            } else if (trades.getTradesStatus() == 180) {
                                pageShow.put("tradesDescribe", "商家取消订单，交易已取消。");
                            }
                        }
                    }
                }
            }
            if (StringUtil.isEmpty(trades.getIsRefund()) || trades.getIsRefund() == 0) {
                if (trades.getTradesStatus() == 120) {
                    //待备货(120+110)
                    if (trades.getStockupStatus() == 110) {
                        pageShow.put("tradesStatus", "待备货");
                        //送货上门
                        if (trades.getPostStyle() == 150) {
                            if (platform == 0 || platform == 1) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "查看物流");
                                    buttons.add(button);
                                    if(!StringUtil.isEmpty(groupInfo.getMainStatus())&&(groupInfo.getMainStatus()==12||groupInfo.getMainStatus()==14)){
                                        buttons = new ArrayList<>();
                                        Map button1 = new HashedMap();
                                        button1.put("index", "2");
                                        button1.put("show", "邀请拼团");
                                        buttons.add(button1);
                                    }
                                } else {
                                    pageShow.put("tradesDescribe", "商家备货中");
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "查看物流");
                                    buttons.add(button);
                                    Map button1 = new HashedMap();
                                    button1.put("index", "2");
                                    button1.put("show", "申请退款");
                                    buttons.add(button1);
                                    Map button2 = new HashedMap();
                                    button2.put("index", "3");
                                    button2.put("show", "再次购买");
                                    buttons.add(button2);
                                    if(!StringUtil.isEmpty(groupInfo.getMainStatus())&&(groupInfo.getMainStatus()==12||groupInfo.getMainStatus()==14)){
                                        buttons = new ArrayList<>();
                                        Map button3 = new HashedMap();
                                        button3.put("index", "4");
                                        button3.put("show", "邀请拼团");
                                        buttons.add(button3);
                                    }
                                }

                            } else if (platform == 2) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "指定服务门店");
                                    buttons.add(button);
                                    Map button1 = new HashedMap();
                                    button1.put("index", "2");
                                    button1.put("show", "退款");
                                    buttons.add(button1);
                                    Map button2 = new HashedMap();
                                    button2.put("index", "2");
                                    button2.put("show", "发货");
                                    buttons.add(button2);
                                }
                            } else if (platform == 3) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "完成备货");
                                    buttons.add(button);
                                    Map button2 = new HashedMap();
                                    button2.put("index", "3");
                                    button2.put("show", "退款");
                                    buttons.add(button2);
                                }
                            } else if (platform == 4) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "完成备货");
                                    buttons.add(button);
                                }
                            }
                        } else if (trades.getPostStyle() == 160) {
                            //门店自提
                            if (platform == 0 || platform == 1) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "门店导航");
                                    buttons.add(button);
                                    if(!StringUtil.isEmpty(groupInfo.getMainStatus())&&(groupInfo.getMainStatus()==12||groupInfo.getMainStatus()==14)){
                                        Map button1 = new HashedMap();
                                        button1.put("index", "2");
                                        button1.put("show", "邀请拼团");
                                        buttons.add(button1);
                                    }
                                } else {
                                    pageShow.put("tradesDescribe", "商家备货中");
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "门店导航");
                                    buttons.add(button);
                                    Map button1 = new HashedMap();
                                    button1.put("index", "2");
                                    button1.put("show", "申请退款");
                                    buttons.add(button1);
                                    Map button2 = new HashedMap();
                                    button2.put("index", "3");
                                    button2.put("show", "再次购买");
                                    buttons.add(button2);
                                    if(!StringUtil.isEmpty(groupInfo.getMainStatus())&&(groupInfo.getMainStatus()==12||groupInfo.getMainStatus()==14)){
                                        buttons = new ArrayList<>();
                                        Map button3 = new HashedMap();
                                        button3.put("index", "4");
                                        button3.put("show", "邀请拼团");
                                        buttons.add(button3);
                                    }
                                }

                            } else if (platform == 2) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "指定服务门店");
                                    buttons.add(button);
                                    Map button1 = new HashedMap();
                                    button1.put("index", "2");
                                    button1.put("show", "退款");
                                    buttons.add(button1);
                                }
                            } else if (platform == 3 || platform == 4) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "完成备货");
                                    buttons.add(button);
                                    Map button1 = new HashedMap();
                                    button1.put("index", "2");
                                    button1.put("show", "退款");
                                    buttons.add(button1);
                                    Map button2 = new HashedMap();
                                    button2.put("index", "3");
                                    button2.put("show", "发送提货码");
                                    buttons.add(button2);
                                }
                            }
                        }


                    }
                    //待发货(120+120)
                    if (trades.getStockupStatus() == 120) {
                        pageShow.put("tradesStatus", "待发货");
                        if (platform == 0 || platform == 1) {
                            if (type == 0) {
                                Map button = new HashedMap();
                                button.put("index", "1");
                                button.put("show", "查看物流");
                                buttons.add(button);
                            } else {
                                pageShow.put("tradesDescribe", "备货完成，等待发货");
                                Map button = new HashedMap();
                                button.put("index", "1");
                                button.put("show", "查看物流");
                                buttons.add(button);
                                Map button1 = new HashedMap();
                                button1.put("index", "2");
                                button1.put("show", "申请退款");
                                buttons.add(button1);
                                Map button2 = new HashedMap();
                                button2.put("index", "3");
                                button2.put("show", "再次购买");
                                buttons.add(button2);
                            }

                        } else if (platform == 2) {
                            if (type == 0) {
                                Map button = new HashedMap();
                                button.put("index", "1");
                                button.put("show", "发货");
                                buttons.add(button);
                                Map button1 = new HashedMap();
                                button1.put("index", "2");
                                button1.put("show", "退款");
                                buttons.add(button1);
                            }
                        } else if (platform == 3) {
                            if (type == 0) {
                                Map button = new HashedMap();
                                button.put("index", "1");
                                button.put("show", "发货");
                                buttons.add(button);
                                Map button1 = new HashedMap();
                                button1.put("index", "2");
                                button1.put("show", "取消备货");
                                buttons.add(button1);
                                Map button2 = new HashedMap();
                                button2.put("index", "2");
                                button2.put("show", "退款");
                                buttons.add(button2);
                                Map button3 = new HashedMap();
                                button3.put("index", "2");
                                button3.put("show", "打印小票");
                                buttons.add(button3);
                            }
                        } else if (platform == 4) {
                            if (type == 0) {
                                Map button = new HashedMap();
                                button.put("index", "1");
                                button.put("show", "我去送货");
                                buttons.add(button);
                                Map button1 = new HashedMap();
                                button1.put("index", "2");
                                button1.put("show", "快递送货");
                                buttons.add(button1);
                                Map button2 = new HashedMap();
                                button2.put("index", "2");
                                button2.put("show", "蜂鸟配送");
                                buttons.add(button2);
                                Map button3 = new HashedMap();
                                button3.put("index", "2");
                                button3.put("show", "取消备货");
                                buttons.add(button3);
                            }
                        }
                    }
                } else if (trades.getTradesStatus() == 130) {
                    //已发货（130）
                    pageShow.put("tradesStatus", "已发货");
                    if (platform == 0 || platform == 1) {
                        if (type == 0) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "查看物流");
                            buttons.add(button);
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "确认收货");
                            buttons.add(button1);
                        } else {
                            pageShow.put("tradesDescribe", "商家已发货，请及时查收");
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "查看物流");
                            buttons.add(button);
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "确认收货");
                            buttons.add(button1);
                            Map button2 = new HashedMap();
                            button2.put("index", "3");
                            button2.put("show", "申请退款");
                            buttons.add(button2);
                            /*Map button3 = new HashedMap();
                            button3.put("index", "4");
                            button3.put("show", "再次购买");
                            buttons.add(button3);*/
                        }

                    } else if (platform == 2) {
                        if (type == 0) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "确认收货");
                            buttons.add(button);
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "退款");
                            buttons.add(button1);
                        }
                    } else if (platform == 3) {
                        if (type == 0) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "确认收货");
                            buttons.add(button);
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "退款");
                            buttons.add(button1);
                            Map button2 = new HashedMap();
                            button2.put("index", "3");
                            button2.put("show", "打印小票");
                            buttons.add(button2);
                        }
                    } else if (platform == 4) {
                        if (type == 0) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "确认收货");
                            buttons.add(button);
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "查看物流");
                            buttons.add(button1);
                        }
                    }
                } else if (trades.getTradesStatus() == 200) {
                    //待自提（200）
                    pageShow.put("tradesStatus", "待自提");
                    if (platform == 0 || platform == 1) {
                        if (type == 0) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "门店导航");
                            buttons.add(button);
                           /* Map button1=new HashedMap();
                            button1.put("index","2");
                            button1.put("show","确认提货");
                            buttons.add(button1);*/
                        } else {
                            pageShow.put("tradesDescribe", "备货完成，请到门店提货");
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "门店导航");
                            buttons.add(button);
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "申请退款");
                            buttons.add(button1);
                            Map button3 = new HashedMap();
                            button3.put("index", "4");
                            button3.put("show", "再次购买");
                            buttons.add(button3);
                        }

                    } else if (platform == 2) {
                        if (type == 0) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "确认提货");
                            buttons.add(button);
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "退款");
                            buttons.add(button1);
                        }
                    } else if (platform == 3 || platform == 4) {
                        if (type == 0) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "确认自提");
                            buttons.add(button);
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "取消备货");
                            buttons.add(button1);
                            Map button2 = new HashedMap();
                            button2.put("index", "2");
                            button2.put("show", "退款");
                            buttons.add(button2);
                            Map button3 = new HashedMap();
                            button3.put("index", "2");
                            button3.put("show", "打印小票");
                            buttons.add(button3);
                            Map button4 = new HashedMap();
                            button4.put("index", "2");
                            button4.put("show", "发送提货码");
                            buttons.add(button4);
                        }
                    }
                }
            } else if (trades.getIsRefund() == 100) {
                pageShow.put("refundShow", "退款详情");
                //退款中
                if (trades.getTradesStatus() == 120) {
                    //待备货(120+110)
                    if (trades.getStockupStatus() == 110) {
                        pageShow.put("tradesStatus", "待备货");
                    } else if (trades.getStockupStatus() == 120) {
                        //待发货(120+120)
                        pageShow.put("tradesStatus", "待发货");
                    }
                } else if (trades.getTradesStatus() == 130) {
                    pageShow.put("tradesStatus", "已发货");
                } else if (trades.getTradesStatus() == 200) {
                    pageShow.put("tradesStatus", "待自提");
                }

                if (platform == 0 || platform == 1) {
                    pageShow.put("refundStatus", "退款中");
                    pageShow.put("tradesDescribe", "退款中，等待商家审核");
                    Map button = new HashedMap();
                    button.put("index", "1");
                    button.put("show", "查看物流");
                    buttons.add(button);
                    Map button1 = new HashedMap();
                    button1.put("index", "2");
                    button1.put("show", "退款详情");
                    buttons.add(button1);
                } else if (platform == 2 || platform == 3 || platform == 4) {
                    pageShow.put("refundStatus", "退款中");


                    Map button = new HashedMap();
                    button.put("index", "1");
                    button.put("show", "退款处理");
                    buttons.add(button);
                }
            } else if (trades.getIsRefund() == 120) {
                pageShow.put("refundShow", "退款详情");
                pageShow.put("tradesStatus", "退款成功");
                /*//退款成功
                TradesLog tradesLog=tradesLogMapper.selectbTradeslogs(trades.getTradesId());
                if (tradesLog.getOldTradesStatus()==120) {
                    //待备货(120+110)
                    if(trades.getStockupStatus()==110){
                        pageShow.put("tradesStatus","待发货");
                    }else if(trades.getStockupStatus()==120){
                        //待发货(120+120)
                        pageShow.put("tradesStatus","待发货");
                    }
                }else if (tradesLog.getOldTradesStatus()==130) {
                    pageShow.put("tradesStatus","已发货");
                }else if (tradesLog.getOldTradesStatus()==200) {
                    pageShow.put("tradesStatus","待自提");
                }else if (tradesLog.getOldTradesStatus()==220) {
                    pageShow.put("tradesStatus","用户确认收货");
                }else if (tradesLog.getOldTradesStatus()==230) {
                    pageShow.put("tradesStatus","门店确认收货");
                }else if (tradesLog.getOldTradesStatus()==800) {
                    pageShow.put("tradesStatus","系统确认收货");
                }*/
                if (platform == 0 || platform == 1) {

                    pageShow.put("tradesDescribe", "退款成功，请及时查收");
                    Map button = new HashedMap();
                    button.put("index", "1");
                    button.put("show", "查看物流");
                    buttons.add(button);
                    Map button1 = new HashedMap();
                    button1.put("index", "2");
                    button1.put("show", "退款详情");
                    buttons.add(button1);
                } else if (platform == 2 || platform == 4) {
                    pageShow.put("refundShow", "退款详情");
                } else if (platform == 3) {
                    pageShow.put("refundShow", "退款详情");
                    Map button = new HashedMap();
                    button.put("index", "1");
                    button.put("show", "打印小票");
                    buttons.add(button);
                }
            } else if (trades.getIsRefund() == 110) {
                pageShow.put("refundShow", "退款详情");
                //拒绝退款
                if (trades.getTradesStatus() == 120) {
                    //待备货(120+110)
                    if (trades.getStockupStatus() == 110) {
                        pageShow.put("tradesStatus", "待备货");
                        //送货上门
                        if (trades.getPostStyle() == 150) {
                            if (platform == 0 || platform == 1) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "查看物流");
                                    buttons.add(button);
                                    Map button1 = new HashedMap();
                                    button1.put("index", "2");
                                    button1.put("show", "退款详情");
                                    buttons.add(button1);
                                    if(!StringUtil.isEmpty(groupInfo.getMainStatus())&&(groupInfo.getMainStatus()==12||groupInfo.getMainStatus()==14)){
                                        buttons = new ArrayList<>();
                                        Map button2 = new HashedMap();
                                        button2.put("index", "3");
                                        button2.put("show", "邀请拼团");
                                        buttons.add(button2);
                                    }
                                } else {
                                    pageShow.put("tradesDescribe", "商家备货中");
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "查看物流");
                                    buttons.add(button);
                                    /*Map button1=new HashedMap();
                                    button1.put("index","2");
                                    button1.put("show","申请退款");
                                    buttons.add(button1);*/
                                    Map button2 = new HashedMap();
                                    button2.put("index", "2");
                                    button2.put("show", "再次购买");
                                    buttons.add(button2);
                                    if(!StringUtil.isEmpty(groupInfo.getMainStatus())&&(groupInfo.getMainStatus()==12||groupInfo.getMainStatus()==14)){
                                        buttons = new ArrayList<>();
                                        Map button3 = new HashedMap();
                                        button3.put("index", "4");
                                        button3.put("show", "邀请拼团");
                                        buttons.add(button3);
                                    }
                                }

                            } else if (platform == 2) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "指定服务门店");
                                    buttons.add(button);
                                    Map button1 = new HashedMap();
                                    button1.put("index", "2");
                                    button1.put("show", "发货");
                                    buttons.add(button1);
                                }
                            } else if (platform == 3) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "完成备货");
                                    buttons.add(button);
                                }
                            } else if (platform == 4) {
                                if (type == 0) {

                                }
                            }
                        } else if (trades.getPostStyle() == 160) {
                            //门店自提
                            if (platform == 0 || platform == 1) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "门店导航");
                                    buttons.add(button);
                                    Map button2 = new HashedMap();
                                    button2.put("index", "2");
                                    button2.put("show", "退款详情");
                                    buttons.add(button2);
                                    if(!StringUtil.isEmpty(groupInfo.getMainStatus())&&(groupInfo.getMainStatus()==12||groupInfo.getMainStatus()==14)){
                                        buttons = new ArrayList<>();
                                        Map button1 = new HashedMap();
                                        button1.put("index", "3");
                                        button1.put("show", "邀请拼团");
                                        buttons.add(button1);
                                    }
                                } else {
                                    pageShow.put("tradesDescribe", "商家备货中");
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "门店导航");
                                    buttons.add(button);
                                    Map button1 = new HashedMap();
                                    button1.put("index", "2");
                                    button1.put("show", "退款详情");
                                    buttons.add(button1);
                                    Map button2 = new HashedMap();
                                    button2.put("index", "2");
                                    button2.put("show", "再次购买");
                                    buttons.add(button2);
                                    if(!StringUtil.isEmpty(groupInfo.getMainStatus())&&(groupInfo.getMainStatus()==12||groupInfo.getMainStatus()==14)){
                                        buttons = new ArrayList<>();
                                        Map button3 = new HashedMap();
                                        button3.put("index", "3");
                                        button3.put("show", "邀请拼团");
                                        buttons.add(button3);
                                    }
                                }

                            } else if (platform == 2) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "指定服务门店");
                                    buttons.add(button);
                                }
                            } else if (platform == 3) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "完成备货");
                                    buttons.add(button);
                                    Map button1 = new HashedMap();
                                    button1.put("index", "2");
                                    button1.put("show", "发送提货码");
                                    buttons.add(button1);
                                }
                            } else if (platform == 4) {
                                if (type == 0) {
                                    Map button = new HashedMap();
                                    button.put("index", "1");
                                    button.put("show", "完成备货");
                                    buttons.add(button);
                                }
                            }
                        }


                    }
                    //待发货(120+120)
                    if (trades.getStockupStatus() == 120) {
                        pageShow.put("tradesStatus", "待发货");
                        if (platform == 0 || platform == 1) {
                            if (type == 0) {
                                Map button = new HashedMap();
                                button.put("index", "1");
                                button.put("show", "查看物流");
                                buttons.add(button);
                                Map button1 = new HashedMap();
                                button1.put("index", "2");
                                button1.put("show", "退款详情");
                                buttons.add(button1);
                            } else {
                                pageShow.put("tradesDescribe", "备货完成，等待发货");
                                Map button = new HashedMap();
                                button.put("index", "1");
                                button.put("show", "查看物流");
                                buttons.add(button);
                                Map button1 = new HashedMap();
                                button1.put("index", "2");
                                button1.put("show", "退款详情");
                                buttons.add(button1);
                                Map button2 = new HashedMap();
                                button2.put("index", "3");
                                button2.put("show", "再次购买");
                                buttons.add(button2);
                            }

                        } else if (platform == 2) {
                            if (type == 0) {
                                Map button = new HashedMap();
                                button.put("index", "1");
                                button.put("show", "发货");
                                buttons.add(button);
                            }
                        } else if (platform == 3 || platform == 4) {
                            if (type == 0) {
                                Map button = new HashedMap();
                                button.put("index", "1");
                                button.put("show", "发货");
                                buttons.add(button);
                                Map button1 = new HashedMap();
                                button1.put("index", "2");
                                button1.put("show", "取消备货");
                                buttons.add(button1);
                                Map button3 = new HashedMap();
                                button3.put("index", "2");
                                button3.put("show", "打印小票");
                                buttons.add(button3);
                            }
                        }
                    }
                } else if (trades.getTradesStatus() == 130) {
                    //已发货（130）
                    pageShow.put("tradesStatus", "已发货");
                    if (platform == 0 || platform == 1) {
                        if (type == 0) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "查看物流");
                            buttons.add(button);
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "确认收货");
                            buttons.add(button1);
                        } else {
                            pageShow.put("tradesDescribe", "商家已发货，请及时查收");
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "查看物流");
                            buttons.add(button);
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "确认收货");
                            buttons.add(button1);
                            Map button2 = new HashedMap();
                            button2.put("index", "3");
                            button2.put("show", "退款详情");
                            buttons.add(button2);
                            Map button3 = new HashedMap();
                            button3.put("index", "4");
                            button3.put("show", "再次购买");
                            buttons.add(button3);
                        }

                    } else if (platform == 2) {
                        if (type == 0) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "确认收货");
                            buttons.add(button);
                        }
                    } else if (platform == 3 || platform == 4) {
                        if (type == 0) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "确认收货");
                            buttons.add(button);
                            /*Map button1=new HashedMap();
                            button1.put("index","2");
                            button1.put("show","退款");
                            buttons.add(button1);*/
                            Map button2 = new HashedMap();
                            button2.put("index", "2");
                            button2.put("show", "打印小票");
                            buttons.add(button2);
                        }
                    }
                } else if (trades.getTradesStatus() == 200) {
                    //待自提（200）
                    pageShow.put("tradesStatus", "待自提");
                    if (platform == 0 || platform == 1) {
                        if (type == 0) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "门店导航");
                            buttons.add(button);
                            /*Map button1=new HashedMap();
                            button1.put("index","2");
                            button1.put("show","确认提货");
                            buttons.add(button1);*/
                        } else {
                            pageShow.put("tradesDescribe", "备货完成，请到门店提货");
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "门店导航");
                            buttons.add(button);
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "退款详情");
                            buttons.add(button1);
                            Map button2 = new HashedMap();
                            button2.put("index", "3");
                            button2.put("show", "再次购买");
                            buttons.add(button2);
                        }

                    } else if (platform == 2) {
                        if (type == 0) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "确认提货");
                            buttons.add(button);
                        }
                    } else if (platform == 3 || platform == 4) {
                        if (type == 0) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "确认自提");
                            buttons.add(button);
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "取消备货");
                            buttons.add(button1);
                            Map button2 = new HashedMap();
                            button2.put("index", "3");
                            button2.put("show", "打印小票");
                            buttons.add(button2);
                            Map button3 = new HashedMap();
                            button3.put("index", "4");
                            button3.put("show", "发送提货码");
                            buttons.add(button3);
                        }
                    }
                }
                pageShow.put("refundStatus", "拒绝退款");
                //pageShow.put("tradesDescribe","商家拒绝退款，详情咨询客服");
            }
            //交易成功
            if (trades.getTradesStatus() == 210 || trades.getTradesStatus() == 220 || trades.getTradesStatus() == 230 || trades.getTradesStatus() == 800) {
                pageShow.put("tradesStatus", "交易成功");
                if (platform == 0 || platform == 1) {
                    //待评价
                    if (trades.getTradesRank() == 0) {
                        if (type == 1) {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "再次购买");
                            buttons.add(button);
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "去评价");
                            buttons.add(button1);
                            Map<String, Object> param = new HashMap<String, Object>();
                            //评价送积分
                            param.put("type", CommonConstant.TYPE_ORDER_ASSESS);
                            param.put("siteId", trades.getSiteId());
                            Map<String, Object> ruleMap = integralRuleMapper.queryIntegralRule(param);
                            if (ruleMap != null) {
                                Map<String, Object> integralRuleMap = JacksonUtils.json2map(ruleMap.get("rule") + "");
                                pageShow.put("tradesPrompt", "输入20字好评成功，送" + integralRuleMap.get("orderEvaluate") + "积分哦。");
                            }
                            pageShow.put("tradesDescribe", "交易成功，感谢您的支持");

                            if (!StringUtil.isEmpty(pageShow.get("refundShow"))) {
                                Map button2 = new HashedMap();
                                button2.put("index", "2");
                                button2.put("show", "退款详情");
                                buttons.add(button2);
                            }else {
                                if (trades.getDealFinishStatus() != 1) {
                                    Map button2 = new HashedMap();
                                    button2.put("index", "2");
                                    button2.put("show", "申请退款");
                                    buttons.add(button2);
                                }
                            }
                        } else {
                            pageShow.put("tradesDescribe", "交易成功，感谢您的支持");
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "去评价");
                            buttons.add(button);
                        }
                    } else {
                        //已评价
                        if (type == 1) {
                            pageShow.put("tradesDescribe", "交易成功，感谢您的支持");
                            /*功能暂时不做
                            Map button=new HashedMap();
                            button.put("index","1");
                            button.put("show","我的评价");
                            buttons.add(button);*/
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "再次购买");
                            buttons.add(button1);
                            if (!StringUtil.isEmpty(pageShow.get("refundShow"))) {
                                Map button2 = new HashedMap();
                                button2.put("index", "2");
                                button2.put("show", "退款详情");
                                buttons.add(button2);
                            }else {
                                if (trades.getDealFinishStatus() != 1) {
                                    Map button2 = new HashedMap();
                                    button2.put("index", "2");
                                    button2.put("show", "申请退款");
                                    buttons.add(button2);
                                }
                            }
                        } else {
                            Map button = new HashedMap();
                            button.put("index", "1");
                            button.put("show", "再次购买");
                            buttons.add(button);
                        }

                    }

                } else if (platform == 3) {
                    if (type == 0) {
                        Map button1 = new HashedMap();
                        button1.put("index", "2");
                        button1.put("show", "打印小票");
                        buttons.add(button1);
                        if (StringUtil.isEmpty(trades.getIsRefund()) || trades.getIsRefund() == 0) {
                            if (trades.getDealFinishStatus() != 1) {
                                Map button2 = new HashedMap();
                                button2.put("index", "2");
                                button2.put("show", "退款");
                                buttons.add(button2);
                            }
                        }
                    }
                } else if (platform == 2) {
                    if (StringUtil.isEmpty(trades.getIsRefund()) || trades.getIsRefund() == 0) {
                        if (trades.getDealFinishStatus() != 1) {
                            Map button1 = new HashedMap();
                            button1.put("index", "2");
                            button1.put("show", "退款");
                            buttons.add(button1);
                        }
                    }
                }

            }

            if(!StringUtil.isEmpty(groupInfo.getMainStatus())){
                switch (groupInfo.getMainStatus()) {
                    case 11:
                        pageShow.put("tradesPrompt", "付款后才能拼团成功，超时将自动关闭。");
                        pageShow.put("groupStatus", "拼团中");
                        pageShow.put("groupEndShow","time");
                        break;
                    case 13:
                        pageShow.put("tradesPrompt", "付款后才能拼团成功，超时将自动关闭。");
                        pageShow.put("groupStatus", "拼团中");
                        pageShow.put("groupEndShow","time");
                        break;
                    case 12:
                        //pageShow.put("tradesPrompt", "time");
                        pageShow.put("groupStatus", "拼团中");
                        pageShow.put("tradesDescribe", "开团成功，邀请好友拼团");
                        pageShow.put("groupEndShow", "开团成功");
                        break;
                    case 14:
                        //pageShow.put("tradesPrompt", "time");
                        pageShow.put("groupStatus", "拼团中");
                        pageShow.put("tradesDescribe", "参团成功，邀请好友拼团");
                        pageShow.put("groupEndShow", "参团成功");
                        break;
                    case 21:
                        pageShow.put("groupStatus", "拼团成功");
                        pageShow.put("groupEndShow", "拼团成功");
                        break;
                    case 31:
                    case 32:
                    case 33:
                        pageShow.put("groupStatus", "拼团取消");
                        pageShow.put("groupEndShow", "拼团取消");
                        break;
                    default:
                        throw new UnknownTypeException();
                }
            }

           /* if (trades.getTradesStatus() == 900) {
                pageShow.put("groupStatus", "拼团取消");
            } else {
                if ("0".equals(trades.getGroupStatus()) || "1".equals(trades.getGroupStatus())) {
                    pageShow.put("groupStatus", "拼团中");
                    if (trades.getTradesStatus() == 110) {
                        pageShow.put("tradesPrompt", "付款后才能拼团成功，超时将自动关闭。");
                    }
                } else if ("2".equals(trades.getGroupStatus())) {
                    pageShow.put("groupStatus", "拼团成功");
                } else if ("3".equals(trades.getGroupStatus())) {
                    pageShow.put("groupStatus", "拼团失败");
                } else if ("4".equals(trades.getGroupStatus())) {
                    pageShow.put("groupStatus", "拼团取消");
                }
                if (trades.getTradesStatus() != 110 && trades.getTradesStatus() != 900 && !StringUtil.isEmpty(groupInfo) && groupInfo.isGroup() && type == 1) {
                    if (groupInfo.isHead()) {//true团长 false 团员
                        pageShow.put("tradesDescribe", "开团成功，邀请好友拼团");
                    } else {
                        pageShow.put("tradesDescribe", "参团成功，邀请好友拼团");
                    }
                }

            }*/


            //查询订单的优惠信息

            if (platform == 0 || platform == 1) {
                Optional<Map<String, String>> optional = concessionResultHandler.getConcessionResultByTradesId2(trades.getSiteId(), trades.getTradesId());
                Map<String, String> discountMap=optional.get();
                if("0".equals(discountMap.get("couponDiscount"))&&"0".equals(discountMap.get("couponDiscount"))){

                }else {
                    pageShow.put("discountMap", discountMap);
                }

            }
            if(flag){
                Optional<List<Map<String, String>>> optional = concessionResultHandler.getConcessionResultByTradesId(trades);
                if (!optional.isPresent()) {
                    // 异常发生

                } else {
                    List<Map<String, String>> discountList = optional.get();
                    if (discountList.size() > 0) {
                        trades.setDiscountList(discountList);
                    }
                }
            }

            //查询订单的推荐人优惠信息
            Map<String, Object> distributorDiscountMap = ordersMapper.getDistributorDiscount(trades.getSiteId(), trades.getTradesId());
            if (!StringUtil.isEmpty(distributorDiscountMap)) {
                pageShow.put("refereeDiscount", distributorDiscountMap.get("tradesDiscount"));
            }

            pageShow.put("buttons", buttons);
            trades.setPageShow(pageShow);

        } catch (Exception e) {
            logger.info("订单状态判断转换异常：{}", e);
        }
        long endMillis = System.currentTimeMillis();
        //logger.info("订单状态判断转换耗时: {}", endMillis - startMillis);
        return trades;
    }

    public Date getTradesEndTime(Integer siteId, Timestamp createTime) {
        //交易结束 默认3天
        int metaKeyFinish = 3;
        YbMeta meta = null;
        try {
            meta = settingDealTimeService.getDealTime(siteId, CommonConstant.META_KEY_FINISH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (meta != null) {
            String result = meta.getMetaVal();
            metaKeyFinish = Integer.parseInt(result);
        }
        return DateUtils.plusDayDate(metaKeyFinish, createTime);

    }
}
