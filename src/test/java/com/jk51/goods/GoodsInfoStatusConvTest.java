package com.jk51.goods;

import com.jk51.Bootstrap;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.goods.library.GoodsStatusConv;
import com.jk51.modules.goods.mapper.GoodsMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("dev")
public class GoodsInfoStatusConvTest {
    private GoodsStatusConv goodsStatusConv;
    @Autowired
    private GoodsMapper goodsMapper;

    @Before
    public void setUp() throws Exception {
        goodsStatusConv = new GoodsStatusConv();
    }

    @Test
    public void allowChangeStatusToValue() throws Exception {
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
                "\"wx_purchase_way\":\"110\",\"cost_price\":12300,\"goods_batch_no\":\"111111111111\",\"goods_status\":\"1\"}";

        Map<String, Object> map = JacksonUtils.json2map(jsonstr);
        Map<String, String> goods = new HashMap<>();
        for(Map.Entry<String,Object> entry:map.entrySet()){
            goods.put(entry.getKey(), String.valueOf(entry.getValue()));
        }

//        boolean flag = GoodsStatusConv.allowChangeStatusToValue(goods, GoodsStatusConv.STATUS_SOFTDEL);
//        assertFalse(flag);
        String errorMsg = GoodsStatusConv.getLastErrorMessage();
        System.out.print(errorMsg);
    }
}
