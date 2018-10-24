package com.jk51.pandian;


import com.jk51.Bootstrap;
import com.jk51.model.treat.O2OMeta;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.offline.service.OfflineCheckService;
import com.jk51.modules.pandian.mapper.BPandianOrderMapper;
import com.jk51.modules.pandian.param.PandianNum;
import com.jk51.modules.pandian.service.InventoriesManager;
import com.jk51.modules.pandian.service.InventoryService;
import com.jk51.modules.pandian.service.satatusManager.InventoriesStatusManager;
import com.jk51.modules.treat.service.DeliveryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-10-18
 * 修改记录:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class PandianTest {

    @Autowired
    InventoriesManager inventoriesManager;
    @Autowired
    private BPandianOrderMapper pandianOrderMapper;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private InventoriesStatusManager inventoriesStatusManager;
    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private BPandianOrderMapper bPandianOrderMapper;
    @Autowired
    private OfflineCheckService offlineCheckService;
    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    public void test2() {

        Double counterValue = redisTemplate.opsForValue().increment("testaa1", 0.0000000001D);
        System.out.println(counterValue);
       /* Set<String> keys = RedisUtil.scan("notifyId*");
        keys.size();*/
/*
        long leng = "12804.000000100001".length();
        double[] aa = new double[100];
        for (int i = 0; i < 100; i++) {
            // BigDecimal score = new BigDecimal(100190).add(new BigDecimal(i).divide(new BigDecimal(10000000)));

            BigDecimal score = new BigDecimal(100190).add(new BigDecimal(i).divide(new BigDecimal(1000000), 12, BigDecimal.ROUND_HALF_UP));
         *//*  Integer storeAdminId = 100190;
           Double score = storeAdminId.doubleValue() +(i/10000000);*//*
            aa[i] = score.doubleValue();
        }

        System.out.println(aa);*/

    }

    @Test
    public void test() {

       /* OrderInfo orderInfo = bPandianOrderMapper.getBPandianOrderByPandianNumAndStoreId("PD100190170027",1934);
        orderInfo.getStoreId();*/
        O2OMeta o2OMeta = new O2OMeta();
        o2OMeta.setSiteId(100190);
        o2OMeta.setOrderTime(new Date());
        String aa = deliveryService.getDeliveryConf(o2OMeta);
        System.out.println(aa);
       /* InventoryConfirmParam param = new InventoryConfirmParam();
        param.setPandian_num("PD201710181549");
        param.setStoreAdminId(123456);
        param.setGoods_code("4444");
        param.setGoods_code("6666");
        param.setDrug_name("灵泛得乐");
        param.setGoods_company("灵泛得乐公司");

        List<InventoryBatchNum> batchNums = new ArrayList<>();

        InventoryBatchNum batchNum = new InventoryBatchNum();
        batchNum.setBatchNum("01170501");
        batchNum.setSpecif_cation("2.5g*20s+5s");
        batchNum.setNum(8);
        batchNum.setQulify("合格");

        InventoryBatchNum newBatchNum = new InventoryBatchNum();
        newBatchNum.setBatchNum("01170503");
        newBatchNum.setSpecif_cation("2.5g*20s+5s");
        newBatchNum.setNum(8);
        newBatchNum.setQulify("合格");

        batchNums.add(batchNum);
        batchNums.add(newBatchNum);
        param.setBatchNums(batchNums);

        Result result = inventoryService.addInventories(param);

        result.getCode();*/

       /* BPandianOrder ss = pandianOrderMapper.getBPandianOrder("PD100166170001");
        BPandianPlan pp = ss.getPandianPlan();*/
        /*List<Inventories> inventories =  inventoriesManager.getInventoriesList("PD201710181549");
        List<BPandianOrderExt> bPandianOrderExts =  inventoriesManager.getPandianOrderExtList(100166);

        List<Inventories> codeList =  inventoriesManager.getInventoriesListBygGoodsCode(2293,"PD201710181549");
        List<Inventories> nameList =  inventoriesManager.getInventoriesListByName("C惠普生天然","PD201710181549");
        List<Inventories> notCheckList = inventoriesManager.getHasNotCheckInventoriesList("PD201710181549");

        Assert.assertEquals(2,notCheckList.size());
        Assert.assertEquals("2293",codeList.get(0).getGoods_code());
        Assert.assertEquals("C惠普生天然维生素E软胶囊",nameList.get(0).getDrug_name());
        Assert.assertEquals(2,inventories.size());
        Assert.assertEquals(1,bPandianOrderExts.size());*/

      /*  Inventories inventories1 = new Inventories();
        inventories1.setPlan_id(2);
        inventories1.setPandian_num("PD201710190943");
        inventories1.setStore_id(1111);
        inventories1.setGood_type("保健类型");
        inventories1.setGoods_code("2433");
        inventories1.setDrug_name("D铁锌钙咀嚼片");
        inventories1.setApproval_number("国食健字G20110360");
        inventories1.setSpecif_cation("1g*100s");
        inventories1.setGoods_company("南宁富莱欣生物科技有限公司");
        inventories1.setInventory_accounting(15);
        inventories1.setSite_id(100166);
        inventories1.setBatch_number("20160401");*/

        /*int i = inventoriesManager.insertInventory(inventories1);
        Assert.assertEquals(1,i);

        inventories1.setActual_store(14);

        int u = inventoriesManager.updateInventory(inventories1);
        Assert.assertEquals(1,u);
*/
      /*  List<Inventories> differences =  inventoriesManager.getHasDifferenceInventories("PD201710190943");
        Assert.assertEquals(3,differences.size());*/

      /*  InventoryConfirmParam param = new InventoryConfirmParam();
        param.setGoods_code("2293");

        InventoryBatchNum batchNum1 = new InventoryBatchNum();
        batchNum1.setInventoryId(1);
        batchNum1.setNum(9);
        batchNum1.setQulify("合格");

        InventoryBatchNum batchNum2= new InventoryBatchNum();
        batchNum2.setNum(1);
        batchNum2.setQulify("合格");
        batchNum2.setBatchNum("01170502");
        batchNum2.setSpecif_cation("2.5g*20s+5s");

        List<InventoryBatchNum> batchNums = new ArrayList<>();
        batchNums.add(batchNum1);
        batchNums.add(batchNum2);

        param.setBatchNums(batchNums);

        try{
            inventoriesManager.confirmInventories(param);
        }catch (Exception e){

            System.out.println(e.getMessage());
        }*/

    }


    @Test
    public void test1() {

       /* InventoryParam param = new InventoryParam();
        param.setPandian_num("PD201710181549");
        param.setBar_code("6922195932215");

        Result result = inventoryService.repeatInventoryCheck("PD201710181549",123456);
        result.getCode();*/

        /*ClerkParam param = new ClerkParam();
        param.setStoreAdminId(100896);
        param.setSiteId(100190);
        param.setStoreId(1175);

        List<BPandianOrder> bPandianOrders = inventoriesManager.getPandianPlan(param);
        bPandianOrders.size();
*/
       /*String aa = "1234565";

       int bb = Integer.parseInt(aa);
       System.out.println(bb);*/

        /*PandianOrderStatusParam param = new PandianOrderStatusParam();
        param.setSiteId(100190);
        param.setPageNum(1);
        param.setPageSize(2);
        //param.setSiteId(1175);
        Result ss =  inventoryService.getPandianOrderExtList(param);
        ss.getData();*/
      /* ClerkParam param = new ClerkParam();
       param.setStoreAdminId(100896);
        param.setStoreId(1175);
        param.setSiteId(100190);
        List<PandainPlanMap> ss = inventoriesManager.getPandianPlan(param);*/
        //Assert.assertEquals(inventoriesManager.getPandianPlanNum("100190","1175","100896"),2);

       /* Result ss = inventoryService.getPandianPlan("eyJ1c2VyX2lkIjo2MDQxNCwicGhhcm1hY2lzdF9pZCI6Mzc1NDEsInN0b3JlX2lkIjoxMTc1LCJzdG9yZV91c2VyX2lkIjozNjgyLCJzdG9yZV9hZG1pbl9pZCI6MTAwODk2LCJwaG9uZSI6IjE1NjI5MTkzNzYzIiwic2l0ZV9pZCI6MTAwMTkwfQ==");
        ss.getStatus();*/
     /*   List<StatusParam> statusParams = new ArrayList<>();
        StatusParam statusParam1 = new StatusParam();
        statusParam1.setPandian_num("PD201710181549");
        statusParam1.setStoreId(1175);
        statusParam1.setSiteId(100190);

        StatusParam statusParam2 = new StatusParam();
        statusParam2.setPandian_num("PD201710181549");
        statusParam2.setStoreId(1176);
        statusParam2.setSiteId(100190);

        StatusParam statusParam3 = new StatusParam();
        statusParam3.setPandian_num("PD201710181549");
        statusParam3.setStoreId(1177);
        statusParam3.setSiteId(100190);

        statusParams.add(statusParam1);
        statusParams.add(statusParam2);
        statusParams.add(statusParam3);

        PandianStatusUpdateParam param = new PandianStatusUpdateParam();
        param.setTo_status(300);
        param.setStatusParams(statusParams);


        List<StatusResponse> statusResponse =  inventoriesStatusManager.updatePandianStatus(param);
        statusResponse.size();*/

       /* PandianOrderDetailParam param = new PandianOrderDetailParam();
        param.setSiteId(100190);
        param.setStoreId(1175);
        param.setPandian_num("PD201710181549");
        param.setPageNum(1);
        param.setPageSize(15);
        PageInfo pageInfo = inventoriesManager.getPandianOrderDetail(param);
        pageInfo.getList();*/



       /* StatusParam statusParam1 = new StatusParam();
        statusParam1.setPandian_num("PD201710181549");
        statusParam1.setStoreId(1175);
        statusParam1.setSiteId(100190);
        statusParam1.setStoreAdminId(123);

        StatusParam statusParam2 = new StatusParam();
        statusParam2.setPandian_num("PD201710181549");
        statusParam2.setStoreId(1176);
        statusParam2.setSiteId(100190);
        statusParam2.setStoreAdminId(123);

        StatusParam statusParam3 = new StatusParam();
        statusParam3.setPandian_num("PD100166170002");
        statusParam3.setStoreId(1175);
        statusParam3.setSiteId(100190);
        statusParam3.setStoreAdminId(123);


        List<StatusParam> statusParams = new ArrayList<>();
        statusParams.add(statusParam1);
        statusParams.add(statusParam2);
        statusParams.add(statusParam3);

        PandianStatusUpdateParam param = new PandianStatusUpdateParam();
        param.setTo_status(300);
        param.setStatusParams(statusParams);
        param.setOperateType(1);


        List<StatusResponse> statusResponse =  inventoriesStatusManager.updatePandianStatus(param);
        statusResponse.size();*/
        /*PandianOrderDetailParam param = new PandianOrderDetailParam();
        param.setPandian_num("PD201710181549");
        param.setStoreId(1175);
        param.setPageSize(15);
        param.setPageNum(1);*/
/*
        PandianOrderStatusParam param = new PandianOrderStatusParam();
        param.setSiteId(100190);
        param.setPageNum(1);
        param.setPageSize(15);

        Result result = inventoryService.getPandianOrderExtList(param);
        result.getData();*/

/*        PandianStatusUpdateParam param = new PandianStatusUpdateParam();
        param.setTo_status(500);
        param.setOperateType(3);

        List<StatusParam> statusParams = new ArrayList<>();
        StatusParam statusParam = new StatusParam();
        statusParam.setPandian_num("PD201710181549");
        statusParam.setStoreAdminId(100896);
        statusParam.setStoreId(1175);
        statusParam.setSiteId(100190);
        statusParams.add(statusParam);

        param.setStatusParams(statusParams);*/
        PandianNum pandianNum = new PandianNum();
        pandianNum.setPandian_num("PD201710181549");
        Result result = inventoryService.isallowSiteUpload(pandianNum);
        result.getData();
    }

    @Test
    public void testCheck() {
//        offlineCheckService.updateOfflinetqty(100166, "PD100166180010", "2057");
        offlineCheckService.updateOfflinetqty(100166, "PD100166180007", "2039", null);

//        offlineCheckService.updateOfflinetqty(100190, "PD100190180456", "001");

    }
}
