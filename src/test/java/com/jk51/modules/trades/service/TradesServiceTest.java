//package com.jk51.modules.trades.service;
//
//import com.jk51.commons.json.JacksonUtils;
//import com.jk51.model.order.QueryOrdersReq;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mybatis.spring.batch.MyBatisCursorItemReader;
////import org.springframework.batch.item.ExecutionContext;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.HashMap;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ActiveProfiles("test")
//@SpringBootTest
//public class TradesServiceTest {
//    @Autowired
//    TradesService tradesService;
//
//    @Autowired
//    SqlSessionFactory sqlSessionFactory;
//
//    @Test
//    public void testGetStoreTradesReport() {
//        QueryOrdersReq ordersReq = new QueryOrdersReq();
//        ordersReq.setSiteId("100166");
//
////        SqlSession sqlSession = sqlSessionFactory.openSession();
////        sqlSession.getConfiguration().addMapper(TradesMapper.class);
////        Cursor<Object> getStoreTradesReport = sqlSession.selectCursor("getStoreTradesReport", JacksonUtils.getInstance().convertValue(ordersReq, HashMap.class));
//
////        Iterator iterator = getStoreTradesReport.iterator();
////        while (iterator.hasNext()) {
////            System.out.println(1);
////        }
//        MyBatisCursorItemReader myBatisCursorItemReader = new MyBatisCursorItemReader();
//        myBatisCursorItemReader.setSqlSessionFactory(sqlSessionFactory);
//        myBatisCursorItemReader.setQueryId("com.jk51.modules.trades.mapper.TradesMapper.getStoreTradesReport");
//        myBatisCursorItemReader.setParameterValues(JacksonUtils.getInstance().convertValue(ordersReq, HashMap.class));
//        myBatisCursorItemReader.open(new ExecutionContext());
//        try {
//            Object record;
//            while ((record = myBatisCursorItemReader.read()) != null) {
//                System.out.println(record);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            myBatisCursorItemReader.close();
//        }
//    }
//}
