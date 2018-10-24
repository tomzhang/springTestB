package com.jk51.trades;

import com.jk51.Bootstrap;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.model.order.Orders;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.trades.mapper.MemberMapper;
import com.jk51.modules.trades.service.TradesService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-02-24
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class TradesServiceTest {
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private TradesService tradesService;

    @Autowired
    private OrdersMapper ordersMapper;

    @Test
    public void testGetTradesByTradesId(){
        System.out.println(tradesService.getTradesByTradesId(1000291444722593446L));
    }

    @Test
    public void testSelectRefundInfo(){
       //System.out.println(tradesService.selectRefundInfo("1000291444722593446L"));
    }
    @Test
    public void testValidationBarCode(){
        System.out.println(tradesService.validationBarCode(100036,"2504587332"));
    }
   /* @Test
    public void testSystemCanel(){  // 系统取消
        tradesService.systemCanelNew();
    }*/
    @Test
    public void testSystemConfirm(){  // 系统确认收货 门店自提
        //tradesService.systemConfirm();
    }
    @Test
    public void testSystemDelivery(){  // 送货上门 系统确认
        //tradesService.systemDelivery();
    }
    @Test
    public void testGetStatus(){
        //System.out.println(tradesService.getStatus(1000601461745145377L,CommonConstant.WAIT_SELLER_SHIPPED));
        System.out.println(memberMapper.getMember(100002,68));
    }
    @Test
    public void test(){
        try {
           System.out.println("sha1==========>"+EncryptUtils.encryptToSHA("6484132454142"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
