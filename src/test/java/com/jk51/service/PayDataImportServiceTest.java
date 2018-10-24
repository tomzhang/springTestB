package com.jk51.service;

import com.jk51.Bootstrap;
import com.jk51.model.account.models.AccountImportData;
import com.jk51.model.account.models.PayDataImport;
import com.jk51.model.account.models.SettlementDetail;
import com.jk51.model.order.Trades;
import com.jk51.modules.account.mapper.PayDataImportMapper;
import com.jk51.modules.account.mapper.SettlementDetailMapper;
import com.jk51.modules.account.service.PayDataImportService;
import com.jk51.modules.account.service.SettlementDetailService;
import com.jk51.modules.trades.service.TradesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.goodsService.
 * author   :zw
 * date     :2017/2/15
 * Update   :
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class PayDataImportServiceTest {
    @Autowired
    public PayDataImportService payDataImportService;
    @Autowired
    public PayDataImportMapper payDataImportMapper;
    @Autowired
    private TradesService tradesService;
    @Autowired
    private SettlementDetailService settlementDetailService;

    private static final Integer TRADES_SOURCE_OUR = 1;//来源于平台订单
    private static final Integer TRADES_SOURCE_OTHER = 2;//来源于第三方订单
    private String[] PaymentStatus = {"在线支付", "交易", "买家已支付"};//微信支付宝收款状态
    private String[] aliFashionableStatus = {"交易分账"};//支付宝分款状态
    private String[] aliCommissionStatus = {"收费", "服务费"};//支付宝佣金状态
    private String[] refundStatus = {"成功", "退款成功"};
    private String[] WaitStatus = {"待买家支付"};//微信支付宝收款状态
    private String[] fitter = {"在线支付", "交易", "买家已支付", "交易分账", "收费", "服务费", "成功", "退款成功"};//用于过滤掉这些状态之外的数据

    @Test
    public void getList() {
        List<PayDataImport> importList = payDataImportMapper.getImportListByTradesId("1000011444625899299");
        importList.parallelStream().forEach(p -> System.out.print("----------" + p.getTrades_id()));
        Map<String, Object> resultMap = new HashMap<String, Object>();
        importList.parallelStream()
                .filter(p -> p.getAccount_checking_status() == 0)
                .filter(p -> p.getPay_style().equals("ali"))
                .filter(p -> Arrays.asList(PaymentStatus).contains(p.getTrades_status()))
                .forEach(p -> {
                            resultMap.put("pay_style", p.getPay_style());
                            resultMap.put("data_type", p.getData_type());
                        });
    }

    @Test
    public void testSettlement() {

    }

    public void upAccountStatus(Integer id, Integer isAccountStatus) {
        System.out.print("=========" + id + "=========" + isAccountStatus);
        // int i = payDataImportService.updateImportStatus(id, isAccountStatus);
        //是否成功写入日志。
    }

    public void testGetPayDataImportListByCheckStatus() {
        List<PayDataImport> payDataObj = payDataImportService.getPayDataImportListByCheckStatus();
        payDataObj.parallelStream()
                .forEach(p -> {
                    System.out.print("----" + p.getTrades_id());
                });
    }

    @Autowired
    private SettlementDetailMapper settlementDetailMapper;

    @Test
    public void test1() {
        PayDataImport payDataImport = new PayDataImport();
        payDataImport.setId(251688);
        payDataImport.setPay_style("222222");
        payDataImportMapper.updateImportList(payDataImport);
    }
}