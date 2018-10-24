package com.jk51.model.coupon.requestParams;

import java.util.List;

/**
 * <h1>关于rule字段的介绍如下：</h1>
 * <h2>所有阶梯的方式如下</h2>
 * <h3>Rule_type 1/2 满件 满元 折扣</h3>
 * <ul>
 * <li>
 * ruleMap.put("meet_num", "1"); ---- ruleMap.put("meet_money", "1");//满多少
 * </li>
 * <li>
 * ruleMap.put("discount", "99");//折多少
 * </li>
 * <li>
 * ruleMap.put("ladder", "1");// 阶梯
 * </li>
 * <li>
 * ruleMap.put("meet_num", "2");
 * </li>
 * <li>
 * ruleMap.put("discount", "88");
 * </li>
 * <li>
 * ruleMap.put("ladder", "2");
 * </li>
 * </ul>
 * <h3>Rule_type 1/2 满件 满元 直减</h3>
 * <ul>
 * <li>
 * ruleMap.put("meet_money", "1"); ---- ruleMap.put("meet_num", "1")//满多少
 * </li>
 * <li>
 * ruleMap.put("reduce_price", "5");//减多少
 * </li>
 * <li>
 * ruleMap.put("ladder", "1");// 阶梯
 * </li>
 * <li>
 * ruleMap.put("meet_money", "2");
 * </li>
 * <li>
 * ruleMap.put("reduce_price", "10");
 * </li>
 * <li>
 * ruleMap.put("ladder", "2");
 * </li>
 * </ul>
 * <h2>非阶梯</h2>
 * <h3>Rule_type 0 现金券 每满多少减</h3>
 * <ul>
 * <li>
 * ruleMap.put("each_full_money", "1");
 * </li>
 * <li>
 * ruleMap.put("reduce_price", "1000");
 * </li>
 * <li>
 * ruleMap.put("max_reduce", "1000"); //如果为0表示不封顶 否则最多减多少
 * </li>
 * </ul>
 * <h3>Rule_type 3 限价券
 * <li>
 * ruleMap.put("each_goods_price", "500");
 * </li>
 * <li>
 * ruleMap.put("buy_num_max", "5");
 * </li>
 * <li>
 * ruleMap.put("each_goods_max_buy_num", "15");
 * </li>
 * </h3>
 * <h3>Rule_type 4 现金券和折扣券 每满多少减/折多少</h3>
 * <ul>
 * <li>
 * ruleMap.put("direct_money", "500"); ---- ruleMap.put("direct_discount", "88");//商品直接减去多少
 * </li>
 * <li>
 * ruleMap.put("max_reduce", "5000"); //如果为0表示不封顶 否则最多减多少
 * </li>
 * </ul>
 * <h3>Rule_type 5 折扣券 第二件半价</h3>
 * <ul>
 * <li>
 * ruleMap.put("how_piece", "2"); //表示第几件
 * </li>
 * <li>
 * ruleMap.put("discount", "50"); //多少折 半价表示50
 * </li>
 * <li>
 * ruleMap.put("max_buy_num", "5"); //如果为0表示不封顶 否则最多减多少
 * </li>
 * </ul>
 * <h3>当CouponRule的couponType为500，rule字段存储的是满商品件数送赠品数量或满商品价额送赠品数量</h3>
 * 其中当rule_type字段为1代表满件送，2代表满元送<br/>
 * [{
 *     "meetNum":1
 *     "sendNum":1
 *     "ladder":1
 * },
 * {
 *     "meetNum":2
 *     "sendNum":3
 *     "ladder":2
 * }...]        <br/>
 * 或者是
 * [{
 *     "meetMoney":100
 *     "sendNum":1
 *     "ladder":1
 * },
 * {
 *     "meetMoney":200
 *     "sendNum":3
 *     "ladder":2
 * }...]        <br/>
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司<br/>
 * 作者: zw<br/>
 * 创建日期: 2017/3/6<br/>
 * 修改记录: <br/>
 */
public class GoodsRule {
    private Integer is_ml; // 0不抹零 1按角抹零 2按分抹零
    private Integer is_round; // 0四舍五入 1直接抹去
    private Integer goods_num_max; // 每件商品最多可以购买多少件
    private Integer type; // 0全部 1指定类目 2指定商品 3指定商品不参加
    private String promotion_goods; // 商品或者类目id
    private Integer is_post;  // 是否计算邮费 0默认不计算 1计算

    private Integer rule_type; // 0每满多少减 1满多少元减/折多少 2满多少件减/折多少 3每个商品限价多少元  4立减多少钱/折 5第二件半价（打折券）6距离券
    private Object rule;

    /**
     * 计算是单品计算还是组合计算：1代表单品计算一次符合规则，2代表组合，3代表单品计算所有符合规则叠加优惠，null表示默认
     * !!! 虽然取名和满赠券相关，但由于后期需求，更改字段意义作为全类型通用，不仅限于赠品券
     */
    private Integer gift_calculate_base;

    /**
     * 满赠券中 <br/>
     * 选择赠送的商品 <br/>
     * 1 代表 送任意一种商品(多种赠品中任意选一种) <br/>
     * 2 代表 送同种商品(买啥送啥) <br/>
     * 3 代表 送任何不同种商品(多种赠品中随便选几种) <br/>
     * 4 代表 送固定商品(多种赠品一起送) <br/>
     */
    private Integer gift_send_type;

    private List<GiftStorage> gift_storage;

    public Integer getIs_post() {
        return is_post;
    }

    public void setIs_post(Integer is_post) {
        this.is_post = is_post;
    }

    public Integer getIs_ml() {
        return is_ml;
    }

    public void setIs_ml(Integer is_ml) {
        this.is_ml = is_ml;
    }

    public Integer getIs_round() {
        return is_round;
    }

    public void setIs_round(Integer is_round) {
        this.is_round = is_round;
    }

    public Integer getGoods_num_max() {
        return goods_num_max;
    }

    public void setGoods_num_max(Integer goods_num_max) {
        this.goods_num_max = goods_num_max;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPromotion_goods() {
        return promotion_goods;
    }

    public void setPromotion_goods(String promotion_goods) {
        this.promotion_goods = promotion_goods;
    }

    public Integer getRule_type() {
        return rule_type;
    }

    public void setRule_type(Integer rule_type) {
        this.rule_type = rule_type;
    }

    public Object getRule() {
        return rule;
    }

    public void setRule(Object rule) {
        this.rule = rule;
    }

    public Integer getGift_calculate_base() {
        return gift_calculate_base;
    }

    public void setGift_calculate_base(Integer gift_calculate_base) {
        this.gift_calculate_base = gift_calculate_base;
    }

    public Integer getGift_send_type() {
        return gift_send_type;
    }

    public void setGift_send_type(Integer gift_send_type) {
        this.gift_send_type = gift_send_type;
    }

    public List<GiftStorage> getGift_storage() {
        return gift_storage;
    }

    public void setGift_storage(List<GiftStorage> gift_storage) {
        this.gift_storage = gift_storage;
    }

    public static class GiftStorage {
        private Integer giftId;

        /**
         * 实际意义是库存剩余量
         */
        private Integer sendNum;

        /**
         * 库存历史总量，用于统计
         */
        private Integer total;

        public GiftStorage() {
        }

        public GiftStorage(Integer giftId, Integer sendNum, Integer total) {
            this.giftId = giftId;
            this.sendNum = sendNum;
            this.total = total;
        }

        public Integer getGiftId() {
            return giftId;
        }

        public void setGiftId(Integer giftId) {
            this.giftId = giftId;
        }

        public Integer getSendNum() {
            return sendNum;
        }

        public void setSendNum(Integer sendNum) {
            this.sendNum = sendNum;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        @Override
        public String toString() {
            return "GiftStorage{" +
                    "giftId=" + giftId +
                    ", sendNum=" + sendNum +
                    ", total=" + total +
                    '}';
        }
    }
}
