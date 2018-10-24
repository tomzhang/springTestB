package com.jk51.goods;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.jk51.Bootstrap;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.Goods;
import com.jk51.model.order.GoodsInfo;
import com.jk51.model.order.OrderGoods;
import com.jk51.modules.comment.service.CommentService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.request.GoodsData;
import com.jk51.modules.goods.service.GoodsService;
import com.jk51.modules.order.service.DistributeOrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
//@EnableAutoConfiguration
//@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
@Rollback
public class GoodsInfoServiceTest {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    DistributeOrderService distributeOrderService;

    @Test
    public void testList(){
        List<OrderGoods> orderGoodss = new ArrayList<>();
        OrderGoods o = new OrderGoods();
        o.setGoodsId(1);
        OrderGoods o1 = new OrderGoods();
        o.setGoodsId(2);
        OrderGoods o2 = new OrderGoods();
        o.setGoodsId(3);
        orderGoodss.add(o);
        orderGoodss.add(o1);
        orderGoodss.add(o2);

        //List<GoodsInfo> goodsInfos = distributeOrderService.getGoodsInfos(100030, orderGoodss);
       // System.out.println(goodsInfos);

    }

    @Test
    public void find() throws Exception {
        Map<String, Object> param = new HashMap<>();

        param.put("site_id", "100002");
        param.put("goods_id", "456");
        param.put("goods_title", "地衣芽");

        List<Goods> goods = goodsService.find(param);

        assertTrue(goods.size() > 0);
    }

    @Test
    public void create() throws Exception {
        String jsonstr = "{\"detail_tpl\":\"10\",\"drug_name\":\"123\",\"com_name\":\"123\"," +
                "\"in_stock\":\"11111\",\"discount_price\":0,\"shop_price\":12300,\"market_price\":12300," +
                "\"goods_tagsid\":\"11111111111111\",\"goods_title\":\"111111\",\"user_cateid\":\"298\"," +
                "\"goods_forpeople\":\"110,120\",\"goods_validity\":\"1\",\"goods_forts\":\"220\"," +
                "\"goods_use\":\"110\",\"goods_property\":\"120\",\"drug_category\":\"140\"," +
                "\"barnd_id\":\"4025\",\"goods_company\":\"33333333333333\",\"specif_cation\":\"33333333\"," +
                "\"approval_number\":\"adc\\u554a\",\"main_ingredient\":\"1\",\"goods_indications\":\"1111111\"," +
                "\"goods_action\":\"111111111111111\",\"adverse_reactioins\":\"11111111111111111111111111111111111111111\"," +
                "\"goods_use_method\":\"111111111111\",\"goods_contd\":\"11111111111111\",\"goods_desc\":\"11111111111111\"," +
                "\"goods_mobile_desc\":\"1111111111111\",\"goods_note\":\"1111111111111111111111111111111\"," +
                "\"goods_usage\":\"\",\"goods_deposit\":\"1111111111111111\",\"forpeople_desc\":\"\"," +
                "\"goods_description\":\"11\",\"purchase_way\":\"110\",\"bar_code\":\"1111111111111\"," +
                "\"goods_code\":\"111111111111\",\"is_medicare\":\"1\",\"medicare_code\":\"1\"," +
                "\"control_num\":0,\"goods_weight\":\"111111\",\"qualification\":\"111111111111111111111\"," +
                "\"wx_purchase_way\":\"110\",\"cost_price\":12300,\"goods_batch_no\":\"111111111111\"}";

        ObjectMapper objectMapper = JacksonUtils.getInstance();
        // 忽略未知字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 驼峰
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        GoodsData data = JacksonUtils.json2pojo(jsonstr, GoodsData.class);
        data.setSiteId(100043);
        int goods_id = goodsService.create(data);

        System.out.print(goods_id);

        assertNotNull(goods_id);
    }

    @Test
    public void update() throws Exception {

        GoodsData goodsData = new GoodsData();
        goodsData.setSiteId(100043);
        goodsData.setGoodsId(12758);
//        goodsData.setGoodsTitle("地衣芽1");
//        goodsData.setGoodsAction("asdfsdfdsfs");
        goodsData.setApprovalNumber("123123");

        boolean isUpdate = goodsService.updateGoods(goodsData);
        assertTrue(isUpdate);
    }

   /* @Test
    public void delete() throws Exception {
        boolean isDel = goodsService.delete(12685, 100043);
        // 检查数据库值是否修改
        assertTrue(isDel);
    }*/
/*
    @Test
    public void listing() throws Exception {
        boolean isDel = goodsService.listing(12685, 100043);
        assertTrue(isDel);
    }

    @Test
    public void delisting() throws Exception {
        boolean isDel = goodsService.delisting(12685, 100043);
        assertTrue(isDel);
    }*/

    @Test
    public void batchDelete() throws Exception {
        int[] goods_ids = {12758, 12759, 12760};
        goodsService.delete(goods_ids, 100043);

    }

  /*  @Test
    public void batchListing() throws Exception {
        int[] goods_ids = {12758, 12759, 12760};
        goodsService.listing(goods_ids, 100043);
    }

    @Test
    public void batchDeListing() throws Exception {
        int[] goods_ids = {12758, 12759, 12760};
        goodsService.delisting(goods_ids, 100043);
    }*/

    @Test
    public void testComments(){
        Map<String,Object> param = new HashMap<>();
        param.put("siteId",100030);
        param.put("buyerNick","ppppp");
        param.put("isShow",1);
        param.put("tradesRank",5);
        param.put("tradesId","8888727272");
        param.put("goodsId1","123,很好,456");
        param.put("goodsId2","234,很b好,567");
        String re = commentService.addComment(param);
        System.out.println(re);
    }



    @Test
    public void testPrice(){
        for(double i=0.1;i< 1;){

            //double price = Double.parseDouble(i);
            //goodsInfo.put(s, String.valueOf((int) (price * 100)));
            System.out.println(i+"------------------"+String.valueOf((int) (i * 100)));
            i=i+0.1;
        }

    }
}
