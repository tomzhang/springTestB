package com.jk51.service;

import com.jk51.Bootstrap;
import com.jk51.interceptor.LoginInterceptor;
import com.jk51.model.account.models.PayDataImport;
import com.jk51.model.account.models.SettlementDetail;
import com.jk51.model.account.models.SettlementDetailAndTrades;
import com.jk51.model.order.Refund;
import com.jk51.model.order.Trades;
import com.jk51.modules.account.controller.SettlementController;
import com.jk51.modules.account.mapper.PayDataImportMapper;
import com.jk51.modules.account.mapper.SettlementDetailAndTradesMapper;
import com.jk51.modules.account.service.PayDataImportService;
import com.jk51.modules.account.service.SettlementDetailAndTradesService;
import com.jk51.modules.account.service.SettlementDetailService;
import com.jk51.modules.trades.mapper.RefundMapper;
import com.jk51.modules.trades.service.RefundService;
import com.jk51.modules.trades.service.TradesService;
import org.bouncycastle.util.Times;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.goodsService.
 * author   :zw
 * date     :2017/2/20
 * Update   :
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class SettlementsServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    @Autowired
    private PayDataImportService payDataImportService;
    @Autowired
    private TradesService tradesService;
    @Autowired
    private SettlementDetailService settlementDetailService;
    @Autowired
    private SettlementDetailAndTradesService settlementDetailAndTradesService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private PayDataImportMapper payDataImportMapper;

    private static final Integer TRADES_SOURCE_OUR = 1;//来源于平台订单
    private static final Integer TRADES_SOURCE_OTHER = 2;//来源于第三方订单
    private String[] PaymentStatus = {"在线支付", "交易", "买家已支付"};//微信支付宝收款状态
    private String[] aliFashionableStatus = {"交易分账"};//支付宝分款状态
    private String[] aliCommissionStatus = {"收费", "服务费"};//支付宝佣金状态
    private String[] refundStatus = {"成功", "退款成功"};
    private String[] WaitStatus = {"待买家支付"};//微信支付宝收款状态
    private String[] fitter = {"在线支付", "交易", "买家已支付", "交易分账", "收费", "服务费"};//用于过滤掉这些状态之外的数据


    @Test
    public void testSettlementAndTradesList(){
        /*List<SettlementDetailAndTrades> sdat = settlementDetailAndTradesService.getSettlementListByTradesId(10001611);
        sdat.parallelStream().forEach(p-> {
                System.out.print(">>>>>>>>>>>>>"+p.getSeller_id()+"-------"+p.getEnd_time());
        });*/

    }
    @Test
    public void testGetRefundList(){
       Refund ref = settlementDetailAndTradesService.getRefundListByTradesId("10011450403673701");
       System.out.print("------"+ref);
    }



    @Test
    public void testGetList(){
        List<PayDataImport> payDataListObj = payDataImportService.getPayDataImportListByCheckStatus();

        payDataListObj.parallelStream().forEach(p-> {
            System.out.print(">>>>>>>>>>>>>"+p.getTrades_id());
        });
        //获取traders表中的记录
        List<Trades> tradesListObj = tradesService.getTradesListByAccountCheckingStatus();
        tradesListObj.parallelStream().forEach(p-> {
//            System.out.print("-------------"+p.getTrades_id());
        });
        //获取refund表未对帐数据
        List<Refund> refundListObj = refundService.getRefundListByAccountCheckingStatus();
    }
    @Test
    public void testBatch(){
        settlementDetailService.batchAccountChecking(null);
    }
    @Test
    public void testBatchs(){
      //  tradesService.updateAccountStatus(1001791481791960198l);
        //refundMapper.updateAccountStatus(11111111); //修改对账订单状态

        //payDataImportMapper.updateImportStatus("1000431444647797759",0);
        /*SettlementDetail settlementList = settlementDetailService.getSettlementListByTradesId(1000171448507440908l);
        System.out.println("-------------"+settlementList.getTrades_id() +"********"+ settlementList==null);*/
        //settlementDetailService.batchAccountChecking();
    }
    @Test
    public void sss(){
     String str = "1.0";
            double a = 0;
            try {
                a = Double.parseDouble(str);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            int b = (int)a;
        System.out.println("----------"+b);

    }
}
