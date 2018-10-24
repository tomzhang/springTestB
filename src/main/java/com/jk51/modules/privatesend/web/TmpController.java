package com.jk51.modules.privatesend.web;

import com.jk51.modules.privatesend.core.AliPrivateSend;
import com.jk51.modules.privatesend.core.PrivateSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("test")
@RestController
public class TmpController {

    @Autowired
    private PrivateSend privateSend;
    @Autowired
    private AliPrivateSend aliPrivateSend;

    private static final Logger logger = LoggerFactory.getLogger(TmpController.class);

    @GetMapping("a")
    public Map a(String siteId){

//        privateSend.orderToPayNotice(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
//            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
//            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test");
//
//        privateSend.orderStoreToTake(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
//            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
//            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","test","test");
//
//        privateSend.orderSendNotice(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
//            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
//            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","3","4","5");
//
//        privateSend.orderSign(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
//            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
//            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","test");
//
//        privateSend.orderRefundSuccess(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
//            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
//            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test");
//
//        privateSend.orderRefundFail(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
//            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
//            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","test");


        privateSend.ecgResultMessage(100190,"o_WI40tU2TAjaLHf146TOXWGg5-Y",
            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768","您好，您刚刚检测了心电，结果如下",
            "点击查看报告详情。","79次/分钟","2018-08-10 15:14","未见异常","未见异常，建议您经常检测，以及早捕捉偶发的心律失常。");

        //开通成功通知
        privateSend.couponIsSuccess(100190,"o_WI40tU2TAjaLHf146TOXWGg5-Y","m.baidu.com","恭喜您，交易成功","点击详情，查看订单信息"
            ,"17398325285","5元优惠券","2018-07-15");

        //服务到期提醒
        privateSend.couponIsExpire(100190,"o_WI40tU2TAjaLHf146TOXWGg5-Y","m.baidu.com","您有一笔订单未付款！","请点击详情，完成付款"
            ,"17398325285","5元优惠券","2018-07-15");

        return null;

    }

    @GetMapping("b")
    public Map b(String siteId){
//        return privateSend.togetherOrderCreateSuccessNotice(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
//            "http://51jk.com", "您好，您有一笔订单还未支付，超过支付时效将被自动取消，赶快支付吧~", "点击进入订单详情页完成支付", "测试1", "测试22", "测试444","测试555","测试333");

        privateSend.togetherOrderCreateSuccess(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","test","test","test");

        privateSend.togetherOrderJoinSuccess(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","test","test","test");

        privateSend.togetherOrderCreateSuccessNotice(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","test");

        privateSend.togetherOrderCreateFailNotice(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test");

        privateSend.togetherOrderCreateFailNoticeRefund(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","test");

        privateSend.togetherOrderPeopleLack(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","test");

        privateSend.togetherOrderCancel(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","test","test");

        privateSend.togetherOrderUnPay(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","test","test","666666");

        privateSend.togetherOrderSendNotice(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","test","test");
        return null;

    }

    @GetMapping("c")
    public void c(String siteId){
        privateSend.togetherOrderCreateFailNotice(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","999");

        privateSend.togetherOrderCreateFailNoticeRefund(100190, "o4ayHwbhV3e95WT0XP_NCzWVDq0k",
            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","999","00");

    }

    @GetMapping("d")
    public Map d(String siteId){
       privateSend.togetherOrderCreateFailNoticeRefund(100247, "oB22zjsIQ1D2GRplqqf01m2skJvY",
                "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "测试，您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
                "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","999","00");

        return privateSend.togetherOrderCreateFailNoticeRefund(100247, "oB22zjq_FBnOV_yzEqeDCdvGW6_U",
                "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "测试，您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
                "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","999","00");



    }

    @GetMapping("e")
    public Map e(){
//        测试
//        return aliPrivateSend.orderIsSuccess(100190,"2088702700652265","m.baidu.com","开卡提醒","你好开卡成功","卡明","卡号");

//        参加拼单成功
//        return aliPrivateSend.togetherOrderJoinSuccess(100190,"2088702700652265","m.baidu.com",
//            "恭喜您参加拼单成功","查看详情呦",
//            "桂圆","123元","胖大星","2人","2017.8.15");

//        未支付超时
//        return aliPrivateSend.togetherOrderUnPay(100190, "2088702700652265",
//            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
//            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","test","test","666666");

//      退款成功
//        aliPrivateSend.orderRefundSuccess(100190, "2088702700652265",
//            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
//            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test");

//        //创建拼单成功提醒 1
//        aliPrivateSend.togetherOrderCreateSuccess(100190, "2088702700652265","m.baidu.com","喜您与xx拼单成功，点击去看看他还买了什么~",
//            "赶快点击进入拼单页面","桂圆红枣枸杞茶","胖大星、海绵宝宝、章鱼哥","2017.8.15","2017.8.15","2017.8.15");
//
//        //拼单人数不足提醒 1
//        aliPrivateSend.togetherOrderPeopleLack(100190, "2088702700652265","m.baidu.com","您的当前拼单还剩余2人才能成功呦",
//            "点击详情进入拼单页面","简爱男装","30分钟","2");
//
//        //商品发货通知 1
//        aliPrivateSend.togetherOrderSendNotice(100190, "2088702700652265","m.baidu.com","亲，您的商品已发货",
//            "请记得收货哦！","菜鸟物流","65456465","黑色短袖一件","1");
//
//        //门店自提通知 1
//        aliPrivateSend.orderStoreToTake(100190, "2088702700652265","m.baidu.com","您预定的超级美味已经新鲜到店，钱小妹提醒您来店提货哟~！",
//            "请您于当天到达门店提货，期待您的光临！","101200241","65456465","88","上海市虹口区");
//
//        //订单发货通知 1
//        aliPrivateSend.orderSendNotice(100190, "2088702700652265","m.baidu.com","您好，您订购的货物已经发货",
//            "可点击详情查看！","TCL电视","65456465","顺丰","980456952123","上海市虹口区");
//
//        //订单签收 1
//        aliPrivateSend.orderSign(100190, "2088702700652265","m.baidu.com","尊敬的用户，你的订单已被签收",
//            "感谢您对我们的支持。","W43578348937","分钟","2018-07-02 10:53:22");
//
//        //退款失败 1
//        aliPrivateSend.orderRefundFail(100190, "2088702700652265","m.baidu.com","x订单退款失败通知",
//            "请直接联系服务商","W43578348937","88","支付处理失败");

//        //订单取消通知 1
//        aliPrivateSend.togetherOrderCancel(100190,"2088702700652265","m.baidu.com","您好，您的订单已成功取消。","如有问题请致电400-222-3333或直接留言，小博将第一时间为您服务！",
//            "123456789@163.com","2016604092343","韩版T恤","500元");
//
//        //拼单成功通知 1
//        aliPrivateSend.togetherOrderCreateSuccessNotice(100190,"2088702700652265","m.baidu.com","恭喜您,拼单成功","点击详情进入拼单页面"
//        ,"桂圆红枣枸杞茶","150****1558","2018.8.15");

//        //拼单失败通知 1
//        aliPrivateSend.togetherOrderCreateFailNotice(100190,"2088702700652265","m.baidu.com","很遗憾，拼单失败","感谢您的参与，欢迎下次拼单"
//        ,"85685896988","精品男装","2018-07-15 12:34:12");
//
        //交易成功 1
//        aliPrivateSend.orderIsSuccess(100190,"2088902462705243","m.baidu.com","恭喜您，交易成功","点击详情，查看订单信息"
//            ,"100元","85685896988","2018-07-15 12:34:12");//交易成功 1

//
//        //抽中红包通知 1
//        aliPrivateSend.redBacketSuccess(100190,"2088802565450454","m.baidu.com","恭喜您，中奖啦","红包一会到您余额"
//            ,"51健康","100元","恭喜发财，大吉大利");

//      拼单失败通知（退款）
//         aliPrivateSend.togetherOrderCreateFailNoticeRefund(100190, "2088702700652265",
//            "http://100190.weixin-test.51jk.com/new/orderShsm?tradesId=1001901512375602768", "您购买的商品已经发货了，请及时签收哦。订单号：1001901512375602768",
//            "周一至周日上午10:00到下午19:00会员享受4公里之内1小时免费送货上门！", "云南白药","test","test");

         return null;
    }

}
