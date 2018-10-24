package com.jk51.commons.json;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/6/7.
 */
public class JacksonUtilsTest {
    @Test
    public void json2map() throws Exception {
        String json = "{\"code\":\"000\",\"message\":\"Success\",\"value\":\"{\\\"siteId\\\":100073,\\\"ruleId\\\":44,\\\"ruleName\\\":\\\"1\\\",\\\"markedWords\\\":\\\"1\\\",\\\"couponType\\\":100,\\\"amount\\\":-1,\\\"timeRule\\\":\\\"{\\\\\\\"validity_type\\\\\\\":2,\\\\\\\"draw_day\\\\\\\":0,\\\\\\\"how_day\\\\\\\":1,\\\\\\\"assign_type\\\\\\\":null,\\\\\\\"assign_rule\\\\\\\":null,\\\\\\\"startTime\\\\\\\":null,\\\\\\\"endTime\\\\\\\":null}\\\",\\\"limitRule\\\":\\\"{\\\\\\\"is_first_order\\\\\\\":0,\\\\\\\"register_auto_send\\\\\\\":null,\\\\\\\"order_type\\\\\\\":\\\\\\\"100,200,300\\\\\\\",\\\\\\\"apply_channel\\\\\\\":\\\\\\\"101,103,105\\\\\\\",\\\\\\\"apply_store\\\\\\\":-1,\\\\\\\"use_stores\\\\\\\":null,\\\\\\\"is_share\\\\\\\":1,\\\\\\\"can_get_num\\\\\\\":1}\\\",\\\"limitState\\\":\\\"1\\\",\\\"limitRemark\\\":\\\"1\\\",\\\"aimAt\\\":1,\\\"startTime\\\":null,\\\"endTime\\\":null,\\\"createTime\\\":1496777918000,\\\"updateTime\\\":1496777918000,\\\"status\\\":0,\\\"orderRule\\\":\\\"null\\\",\\\"areaRule\\\":\\\"null\\\",\\\"goodsRule\\\":\\\"{\\\\\\\"is_ml\\\\\\\":null,\\\\\\\"is_round\\\\\\\":null,\\\\\\\"goods_num_max\\\\\\\":null,\\\\\\\"type\\\\\\\":0,\\\\\\\"promotion_goods\\\\\\\":\\\\\\\"all\\\\\\\",\\\\\\\"is_post\\\\\\\":0,\\\\\\\"rule_type\\\\\\\":5,\\\\\\\"rule\\\\\\\":[{\\\\\\\"meet_money\\\\\\\":100,\\\\\\\"reduce_price\\\\\\\":100,\\\\\\\"ladder\\\\\\\":1},{\\\\\\\"meet_money\\\\\\\":100,\\\\\\\"reduce_price\\\\\\\":200,\\\\\\\"ladder\\\\\\\":2},{\\\\\\\"meet_money\\\\\\\":300,\\\\\\\"reduce_price\\\\\\\":300,\\\\\\\"ladder\\\\\\\":3}]}\\\",\\\"version\\\":0,\\\"useAmount\\\":0,\\\"total\\\":-1,\\\"sendNum\\\":0,\\\"shareNum\\\":0,\\\"orderPrice\\\":0,\\\"goodsNum\\\":0,\\\"receiveNum\\\":0,\\\"oldCouponId\\\":null,\\\"memberNum\\\":null,\\\"couponView\\\":null}\",\"status\":null}";
        Map<String, Object> map = JacksonUtils.json2map(json);
        System.out.println(map);
    }

    @Test
    public void json2mapDeeply() throws Exception {
        String json = "{\"code\":\"000\",\"message\":\"Success\",\"value\":\"{\\\"siteId\\\":100073,\\\"ruleId\\\":44,\\\"ruleName\\\":\\\"1\\\",\\\"markedWords\\\":\\\"1\\\",\\\"couponType\\\":100,\\\"amount\\\":-1,\\\"timeRule\\\":\\\"{\\\\\\\"validity_type\\\\\\\":2,\\\\\\\"draw_day\\\\\\\":0,\\\\\\\"how_day\\\\\\\":1,\\\\\\\"assign_type\\\\\\\":null,\\\\\\\"assign_rule\\\\\\\":null,\\\\\\\"startTime\\\\\\\":null,\\\\\\\"endTime\\\\\\\":null}\\\",\\\"limitRule\\\":\\\"{\\\\\\\"is_first_order\\\\\\\":0,\\\\\\\"register_auto_send\\\\\\\":null,\\\\\\\"order_type\\\\\\\":\\\\\\\"100,200,300\\\\\\\",\\\\\\\"apply_channel\\\\\\\":\\\\\\\"101,103,105\\\\\\\",\\\\\\\"apply_store\\\\\\\":-1,\\\\\\\"use_stores\\\\\\\":null,\\\\\\\"is_share\\\\\\\":1,\\\\\\\"can_get_num\\\\\\\":1}\\\",\\\"limitState\\\":\\\"1\\\",\\\"limitRemark\\\":\\\"1\\\",\\\"aimAt\\\":1,\\\"startTime\\\":null,\\\"endTime\\\":null,\\\"createTime\\\":1496777918000,\\\"updateTime\\\":1496777918000,\\\"status\\\":0,\\\"orderRule\\\":\\\"null\\\",\\\"areaRule\\\":\\\"null\\\",\\\"goodsRule\\\":\\\"{\\\\\\\"is_ml\\\\\\\":null,\\\\\\\"is_round\\\\\\\":null,\\\\\\\"goods_num_max\\\\\\\":null,\\\\\\\"type\\\\\\\":0,\\\\\\\"promotion_goods\\\\\\\":\\\\\\\"all\\\\\\\",\\\\\\\"is_post\\\\\\\":0,\\\\\\\"rule_type\\\\\\\":5,\\\\\\\"rule\\\\\\\":[{\\\\\\\"meet_money\\\\\\\":100,\\\\\\\"reduce_price\\\\\\\":100,\\\\\\\"ladder\\\\\\\":1},{\\\\\\\"meet_money\\\\\\\":100,\\\\\\\"reduce_price\\\\\\\":200,\\\\\\\"ladder\\\\\\\":2},{\\\\\\\"meet_money\\\\\\\":300,\\\\\\\"reduce_price\\\\\\\":300,\\\\\\\"ladder\\\\\\\":3}]}\\\",\\\"version\\\":0,\\\"useAmount\\\":0,\\\"total\\\":-1,\\\"sendNum\\\":0,\\\"shareNum\\\":0,\\\"orderPrice\\\":0,\\\"goodsNum\\\":0,\\\"receiveNum\\\":0,\\\"oldCouponId\\\":null,\\\"memberNum\\\":null,\\\"couponView\\\":null}\",\"status\":null}";
        Map<String, Object> map = JacksonUtils.json2mapDeeply(json);
        System.out.println(map);
    }

}