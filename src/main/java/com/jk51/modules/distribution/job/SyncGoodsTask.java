package com.jk51.modules.distribution.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 同步商品信息 每分钟一次
 */
@Component
public class SyncGoodsTask {
    @Autowired
    JdbcTemplate jdbcTemplate;
    public void run() {
        String updateSql = buildSql();

        jdbcTemplate.execute(updateSql);
    }

    public String buildSql() {
        return "UPDATE b_goods_distribute AS d\n" +
            "INNER JOIN b_goods AS g ON (g.site_id = d.site_id AND g.goods_id = d.goods_id)\n" +
            "SET d.approval_number = g.approval_number,\n" +
            " d.`drug_name` = g.drug_name,\n" +
            " d.`com_name` = g.com_name,\n" +
            " d.`specif_cation` = g.specif_cation,\n" +
            " d.`goods_company` = g.goods_company,\n" +
            " d.`barnd_id` = g.barnd_id,\n" +
            " d.`drug_category` = g.drug_category,\n" +
            " d.`goods_property` = g.goods_property,\n" +
            " d.`goods_use` = g.goods_use,\n" +
            " d.`goods_forts` = g.goods_forts,\n" +
            " d.`goods_validity` = g.goods_validity,\n" +
            " d.`goods_forpeople` = g.goods_forpeople,\n" +
            " d.`user_cateid` = g.user_cateid,\n" +
            " d.`goods_title` = g.goods_title,\n" +
            " d.`goods_tagsid` = g.goods_tagsid,\n" +
            " d.`market_price` = g.market_price,\n" +
            " d.`shop_price` = g.shop_price,\n" +
            " d.`cost_price` = g.cost_price,\n" +
            " d.`discount_price` = g.discount_price,\n" +
            " d.`in_stock` = g.in_stock,\n" +
            " d.`goods_weight` = g.goods_weight,\n" +
            " d.`control_num` = g.control_num,\n" +
            " d.`goods_status` = g.goods_status,\n" +
            " d.`freight_payer` = g.freight_payer,\n" +
            " d.`list_time` = g.list_time,\n" +
            " d.`delist_time` = g.delist_time,\n" +
            " d.`postage_id` = g.postage_id,\n" +
            " d.`detail_tpl` = g.detail_tpl,\n" +
            " d.`is_medicare` = g.is_medicare,\n" +
            " d.`medicare_code` = g.medicare_code,\n" +
            " d.`medicare_top_price` = g.medicare_top_price,\n" +
            " d.`bar_code` = g.bar_code,\n" +
            " d.`mnemonic_code` = g.mnemonic_code,\n" +
            " d.`purchase_way` = g.purchase_way,\n" +
            " d.`wx_purchase_way` = g.wx_purchase_way,\n" +
            " d.`goods_code` = g.goods_code,\n" +
            " d.`yb_goods_id` = g.yb_goods_id,\n" +
            " d.`erp_price` = g.erp_price\n" +
            "WHERE\n" +
            "\td.site_id in (SELECT owner FROM d_distribution_store WHERE is_open = 1)";
    }
}
