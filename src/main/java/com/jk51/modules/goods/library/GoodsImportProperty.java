package com.jk51.modules.goods.library;

import com.jk51.commons.string.StringUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/4/24.
 */
public class GoodsImportProperty {
    private static final Logger logger = LoggerFactory.getLogger(GoodsImportProperty.class);
    /**
     * 药品模板
     */
    public static final Map<String, String> ypTpl = new TreeMap() {
        {
            put("A", "goods_code");
            put("B", "approval_number");
            put("C", "com_name");
            put("D", "drug_name");
            put("E", "specif_cation");
            put("F", "goods_company");
            put("G", "barnd_id");
            put("H", "drug_category");
            put("I", "goods_property");
            put("J", "goods_forts");
            put("K", "goods_validity");
            put("L", "goods_use");
            put("M", "goods_forpeople");
            put("N", "is_medicare");
            put("O", "medicare_code");
            put("P", "bar_code");
            put("Q", "main_ingredient");
            put("R", "goods_indications");
            put("S", "goods_use_method");
            put("T", "goods_action");
            put("U", "adverse_reactioins");
            put("V", "goods_note");
            put("W", "goods_contd");
            put("X", "goods_deposit");
            put("Y", "goods_description");
            put("Z", "user_cate_id");
            put("AA", "goods_title");
            put("AB", "goods_tagsid");
            put("AC", "shop_price");
            put("AD", "market_price");
            put("AE", "cost_price");
            put("AF", "in_stock");
            put("AG", "goods_weight");
            put("AH", "control_num");
            put("AI", "wx_purchase_way");
        }
    };

    /**
     * 保健品模板
     */
    public static final Map<String, String> bjpTpl = new TreeMap() {
        {
            put("A", "goods_code");
            put("B", "approval_number");
            put("C", "drug_name");
            put("D", "specif_cation");
            put("E", "goods_company");
            put("F", "barnd_id");
            put("G", "goods_forts");
            put("H", "goods_validity");
            put("I", "bar_code");
            put("J", "main_ingredient");
            put("K", "goods_indications");
            put("L", "goods_use_method");
            put("M", "goods_usage");
            put("N", "forpeople_desc");
            put("O", "adverse_reactioins");
            put("P", "goods_note");
            put("Q", "goods_contd");
            put("R", "goods_deposit");
            put("S", "goods_description");
            put("T", "user_cate_id");
            put("U", "goods_title");
            put("V", "goods_tagsid");
            put("W", "shop_price");
            put("X", "market_price");
            put("Y", "cost_price");
            put("Z", "in_stock");
            put("AA", "goods_weight");
            put("AB", "control_num");
            put("AC", "wx_purchase_way");
        }
    };

    /**
     * 器械模板
     */
    public static final Map<String, String> qxTpl = new TreeMap() {
        {
            put("A", "goods_code");
            put("B", "approval_number");
            put("C", "drug_name");
            put("D", "specif_cation");
            put("E", "goods_company");
            put("F", "barnd_name");
            put("G", "drug_category");
            put("H", "goods_validity");
            put("I", "bar_code");
            put("J", "main_ingredient");
            put("K", "goods_indications");
            put("L", "goods_use_method");
            put("M", "goods_usage");
            put("N", "forpeople_desc");
            put("O", "goods_action");
            put("P", "adverse_reactioins");
            put("Q", "goods_note");
            put("R", "goods_contd");
            put("S", "goods_deposit");
            put("T", "goods_description");
            put("U", "user_cate_id");
            put("V", "goods_title");
            put("W", "goods_tagsid");
            put("X", "shop_price");
            put("Y", "market_price");
            put("Z", "cost_price");
            put("AA", "in_stock");
            put("AB", "goods_weight");
            put("AC", "control_num");
            put("AD", "wx_purchase_way");
        }
    };

    /**
     * 消毒模板
     */
    public static final Map<String, String> xdTpl = new TreeMap() {
        {
            put("A", "goods_code");
            put("B", "approval_number");
            put("C", "drug_name");
            put("D", "specif_cation");
            put("E", "goods_company");
            put("F", "barnd_name");
            put("G", "goods_forts");
            put("H", "goods_validity");
            put("I", "bar_code");
            put("J", "main_ingredient");
            put("K", "goods_indications");
            put("L", "goods_use_method");
            put("M", "goods_usage");
            put("N", "forpeople_desc");
            put("O", "adverse_reactioins");
            put("P", "goods_note");
            put("Q", "goods_contd");
            put("R", "goods_deposit");
            put("S", "goods_description");
            put("T", "user_cate_id");
            put("U", "goods_title");
            put("V", "goods_tagsid");
            put("W", "shop_price");
            put("X", "market_price");
            put("Y", "cost_price");
            put("Z", "in_stock");
            put("AA", "goods_weight");
            put("AB", "control_num");
            put("AC", "message");
            put("AD", "wx_purchase_way");
        }
    };

    /**
     * 化妆品模板
     */
    public static final Map<String, String> hzTpl = new TreeMap() {
        {
            put("A", "goods_code");
            put("B", "approval_number");
            put("C", "drug_name");
            put("D", "specif_cation");
            put("E", "goods_company");
            put("F", "barnd_name");
            put("G", "goods_forts");
            put("H", "goods_validity");
            put("I", "bar_code");
            put("J", "main_ingredient");
            put("K", "goods_indications");
            put("L", "goods_use_method");
            put("M", "goods_usage");
            put("N", "forpeople_desc");
            put("O", "adverse_reactioins");
            put("P", "goods_note");
            put("Q", "goods_contd");
            put("R", "goods_deposit");
            put("S", "goods_description");
            put("T", "user_cate_id");
            put("U", "goods_title");
            put("V", "goods_tagsid");
            put("W", "shop_price");
            put("X", "market_price");
            put("Y", "cost_price");
            put("Z", "in_stock");
            put("AA", "goods_weight");
            put("AB", "control_num");
            put("AC", "message");
            put("AD", "wx_purchase_way");
        }
    };

    /**
     * 中药材模板
     */
    public static final Map<String, String> zyTpl = new TreeMap() {
        {
            put("A", "goods_code");
            put("B", "drug_name");
            put("C", "com_name");
            put("D", "specif_cation");
            put("E", "goods_company");
            put("F", "drug_category");
            put("G", "goods_property");
            put("H", "goods_validity");
            put("I", "main_ingredient");
            put("J", "goods_indications");
            put("K", "goods_use_method");
            put("L", "goods_usage");
            put("M", "forpeople_desc");
            put("N", "goods_action");
            put("O", "adverse_reactioins");
            put("P", "goods_note");
            put("Q", "goods_contd");
            put("R", "goods_deposit");
            put("S", "user_cate_id");
            put("T", "goods_title");
            put("U", "goods_tagsid");
            put("V", "shop_price");
            put("W", "market_price");
            put("X", "cost_price");
            put("Y", "in_stock");
            put("Z", "goods_weight");
            put("AA", "control_num");
            put("AB", "message");
            put("AC", "wx_purchase_way");
        }
    };
    /**
     * erp库存模版
     */
    public static final Map<String, String> kcTpl = new TreeMap() {
        {
            put("A", "stores_number");
            put("B", "goods_code");
            put("C", "goods_batch_number");
            put("D", "in_stock");
        }
    };
    /**
     * erp价格模版
     */
    public static final Map<String, String> epTpl = new TreeMap() {
        {
            put("A", "goods_code");
            put("B", "erp_price");
        }
    };
    /**
     * erp库存模版
     */
    public static final Map<String, String> eppTpl = new TreeMap() {
        {
            put("A", "goods_code");
            put("B", "store_number");
            put("C", "price");
        }
    };
    /**
     * 其他模板
     */
    public static final Map<String, String> qtTpl = new TreeMap() {
        {
            put("A", "goods_code");
            put("B", "approval_number");
            put("C", "drug_name");
            put("D", "specif_cation");
            put("E", "goods_company");
            put("F", "barnd_name");
            put("G", "goods_validity");
            put("H", "bar_code");
            put("I", "main_ingredient");
            put("J", "goods_indications");
            put("K", "goods_use_method");
            put("L", "goods_usage");
            put("M", "forpeople_desc");
            put("N", "goods_action");
            put("O", "adverse_reactioins");
            put("P", "goods_note");
            put("Q", "goods_contd");
            put("R", "goods_deposit");
            put("S", "goods_description");
            put("T", "user_cate_id");
            put("U", "goods_title");
            put("V", "goods_tagsid");
            put("W", "shop_price");
            put("X", "market_price");
            put("Y", "cost_price");
            put("Z", "in_stock");
            put("AA", "goods_weight");
            put("AB", "control_num");
            put("AC", "message");
            put("AD", "wx_purchase_way");
        }
    };

    public final static Map goodsPropertyItem = new HashMap() {{
        put("化学药制剂", 110);
        put("中成药", 120);
        put("生物制品", 130);
        put("抗生素", 140);
        put("中药材", 150);
        put("中药饮片", 160);
        put("复方制剂", 170);
        put("根茎类", 180);
        put("茎木类", 190);
        put("皮类", 200);
        put("叶类", 210);
        put("花类", 220);
        put("全草类", 230);
        put("果实种子类", 240);
        put("矿物类", 250);
        put("动物类", 260);
        put("其它", 9999);
    }};

    public final static Map drugCategoryItem = new HashMap() {{
        put("甲类非处方药", 110);
        put("乙类非处方药", 120);
        put("处方药", 130);
        put("双轨药", 140);
        put("非方剂", 150);
        put("方剂", 160);
        put("一类", 170);
        put("二类", 180);
        put("三类", 190);
    }};
    public final static Map drugCateToName = new HashMap() {{
        put("110", "甲类非处方药");
        put("120", "乙类非处方药");
        put("130", "处方药");
        put("140", "双轨药");
        put("150", "非方剂");
        put("160", "方剂");
        put("170", "一类");
        put("180", "二类");
        put("190", "三类");
    }};
    public final static Map goodsUseItem = new HashMap() {{
        put("口服", 110);
        put("外用", 120);
        put("注射", 130);
        put("含服", 140);
        put("其它", 9999);
    }};

    public final static Map goodsFortsItem = new HashMap() {{
        put("片剂", 110);
        put("胶囊", 120);
        put("丸剂", 130);
        put("颗粒", 140);
        put("液体", 150);
        put("膏剂", 160);
        put("贴剂", 170);
        put("糖浆", 180);
        put("散剂", 190);
        put("栓剂", 200);
        put("喷雾", 210);
        put("溶液剂", 220);
        put("乳剂", 230);
        put("混悬剂", 240);
        put("气雾剂", 250);
        put("粉雾剂", 260);
        put("洗剂", 270);
        put("搽剂", 280);
        put("糊剂", 290);
        put("凝胶剂", 300);
        put("滴眼剂", 310);
        put("滴鼻剂", 320);
        put("滴耳剂", 330);
        put("眼膏剂", 340);
        put("含漱剂", 350);
        put("舌下片剂", 360);
        put("粘贴片", 370);
        put("贴膜剂", 380);
        put("滴剂", 390);
        put("滴丸剂", 400);
        put("芳香水剂", 410);
        put("甘油剂", 420);
        put("醑剂", 430);
        put("注射剂", 440);
        put("涂膜剂", 450);
        put("合剂", 460);
        put("酊剂", 470);
        put("膜剂", 480);
        put("其他", 9999);
    }};

    public final static Map goodsForpeopleItem = new HashMap() {{
        put("不限", 110);
        put("成人", 120);
        put("婴幼儿", 130);
        put("儿童", 140);
        put("男性", 150);
        put("妇女", 160);
        put("中老年", 170);
    }};

    public final static Map goodsIsMedicareItem = new HashMap() {{
        put("非医保", 1);
        put("甲类医保", 2);
        put("乙类医保", 3);
    }};

    public final static Map controlNumItem = new HashMap() {{
        put("不限购", 0);
        put("限购", 1);
    }};

    public final static Map wxpurchasewaybyItem = new HashMap() {{
        put("立即购买，购物车", 110);
        put("该商品仅供展示", 120);
        put("预约购买", 130);
    }};

    public static Map<String, String> getFieldMap(int detailIpl) {
        switch (detailIpl) {
            case 10:
                return ypTpl;
            case 40:
                return bjpTpl;
            case 30:
                return qxTpl;
            case 80:
                return xdTpl;
            case 60:
                return hzTpl;
            case 70:
                return zyTpl;
            case 20:
                return qtTpl;
            case 110:
                return kcTpl;
            case 120:
                return epTpl;
            case 130:
                return eppTpl;
            default:
                return qtTpl;
        }
    }

    public static Map<String, String> conv2GoodsInfo(Row row, int detailTpl) {
        Map<String, String> goodsInfo = new HashMap();
        Map<String, String> fieldMap = getFieldMap(detailTpl);

        // 价格字段
        String[] priceFiled = new String[]{"shop_price", "market_price", "cost_price", "erp_price"};
        fieldMap.forEach((k, v) -> {
            try {
                int cellnum = columnIndexFormString(k);
                Cell cell = Optional.ofNullable(row.getCell(cellnum)).orElse(row.createCell(cellnum));
                String cellValue;

                if (CellType.NUMERIC.equals(cell.getCellTypeEnum())
                    && StringUtil.indexOfAny(fieldMap.get(k), priceFiled) == StringUtil.INDEX_NOT_FOUND) {
                    long temp = GoodsImportXlsHelper.getCellValueParse(cell, Long.class);
                    cellValue = String.valueOf(temp);
                } else {
                    cellValue = GoodsImportXlsHelper.getCellValueParse(cell, String.class);
                }

                String key = fieldMap.get(k);
                goodsInfo.put(key, StringUtil.trimAll(cellValue));
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        });
        convFieldNum(goodsInfo);
        price100(goodsInfo, priceFiled);

        return goodsInfo;
    }

    public static Map<String, String> getErpPrice(Row row, int detailTpl) {
        Map<String, String> goodsInfo = new HashMap();
        Map<String, String> fieldMap = getFieldMap(detailTpl);

        // 价格字段
        String[] priceFiled = new String[]{"shop_price", "market_price", "cost_price", "erp_price"};
        fieldMap.forEach((k, v) -> {
            try {
                int cellnum = columnIndexFormString(k);
                Cell cell = Optional.ofNullable(row.getCell(cellnum)).orElse(row.createCell(cellnum));
                String cellValue;
                if (CellType.NUMERIC.equals(cell.getCellTypeEnum())
                    && StringUtil.indexOfAny(fieldMap.get(k), priceFiled) == StringUtil.INDEX_NOT_FOUND) {
                    DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
                    cellValue = String.valueOf(decimalFormat.format(cell.getNumericCellValue()));
                } else {
                    cellValue = cell.getStringCellValue();
                }
                String key = fieldMap.get(k);
                goodsInfo.put(key, StringUtil.trimAll(cellValue));
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        });

        return goodsInfo;
    }

    public static String safeToString(Object value) {
        return String.valueOf(Optional.ofNullable(value).orElse(""));
    }

    public static void convFieldNum(Map<String, String> goodsInfo) {
        // 将部分字段的值转为对应的数字
        goodsInfo.put("drug_category", safeToString(drugCategoryItem.get(goodsInfo.get("drug_category"))));
        goodsInfo.put("goods_use", safeToString(goodsUseItem.get(goodsInfo.get("goods_use"))));
        goodsInfo.put("goods_forts", safeToString(goodsFortsItem.get(goodsInfo.get("goods_forts"))));
        // 适用人群是多选的
        String goodsForpeople = goodsInfo.get("goods_forpeople");
        if (StringUtil.isNotEmpty(goodsForpeople)) {
            // 将中文逗号替换成英文逗号
            goodsForpeople = StringUtil.replace(goodsForpeople, "，", ",");
            goodsForpeople = Arrays.stream(StringUtil.split(goodsForpeople, ","))
                .map(v -> safeToString(goodsForpeopleItem.get(v)))
                .filter(v -> StringUtil.isNotEmpty(v))
                .collect(Collectors.joining(","));
        }

        goodsInfo.put("goods_forpeople", goodsForpeople);
        goodsInfo.put("is_medicare", safeToString(goodsIsMedicareItem.get(goodsInfo.get("is_medicare"))));
        goodsInfo.put("goods_property", safeToString(goodsPropertyItem.get(goodsInfo.get("goods_property"))));
        if (StringUtil.isNumber(goodsInfo.get("control_num"))) {
            goodsInfo.put("control_num", goodsInfo.get("control_num"));
        } else {
            goodsInfo.put("control_num", safeToString(controlNumItem.get(goodsInfo.get("control_num"))));
        }

        //处理购买方式
        goodsInfo.put("wx_purchase_way", safeToString(wxpurchasewaybyItem.get(goodsInfo.get("wx_purchase_way"))));


        // 品牌???? 这什么鬼东西
        if (goodsInfo.containsKey("barnd_id")) {
            goodsInfo.put("barnd_name", goodsInfo.get("barnd_id"));
            goodsInfo.remove("barnd_id");
        }
    }

    public static void price100(Map<String, String> goodsInfo, String[] field) {
        // 价格字段*100
        for (String s : field) {
            if (StringUtil.isNotEmpty(goodsInfo.get(s))) {
                try {
                    //double price = Double.parseDouble(goodsInfo.get(s));
                    //goodsInfo.put(s, String.valueOf((int) (price * 100)));
                    BigDecimal price = new BigDecimal(goodsInfo.get(s));
                    goodsInfo.put(s, price.multiply(new BigDecimal(100)).intValue() + "");
                } catch (Exception e) {
                    logger.debug(e.getMessage());
                }
            }
        }

    }

    public static int columnIndexFormString(String column) throws Exception {
        char[] pStrings = column.toCharArray();
        if (pStrings.length == 1) {
            return pStrings[0] - 'A';
        } else if (pStrings.length == 2) {
            return (pStrings[0] - 'A' + 1) * 26 + pStrings[1] - 'A';
        } else if (pStrings.length == 3) {
            return ((pStrings[0] - 'A') * 676) + ((pStrings[1] - 'A') * 26) + pStrings[2] - 'A';
        } else {
            throw new Exception("不支持");
        }
    }

    public static Map<String, String> merge(Map<String, String> goodsInfo, Map<String, String> ybGoodsInfoMap) {
        goodsInfo.forEach((k, v) -> {
            if (StringUtil.isEmpty(v)) {
                // 空的字段 读取yb数据
                String newValue = String.valueOf(Optional.ofNullable(ybGoodsInfoMap.get(k)).orElse(""));
                logger.info("{} => {}", k, newValue);
                goodsInfo.put(k, newValue);
            }
        });
        return goodsInfo;
    }

    public static void checkRequire(boolean isAdd, Map<String, String> goodsInfo) {
        if (isAdd) {
            if (StringUtil.isEmpty(goodsInfo.get("approval_number"))) {
                throw new RuntimeException("请填写批准文号和规格");
            }
        } else {
            if (StringUtil.isEmpty(goodsInfo.get("goods_code"))) {
                throw new RuntimeException("请填写商品编码");
            }
        }
    }

    public static void storage_checkRequire(Map<String, String> goodsInfo) {
        if (StringUtil.isEmpty(goodsInfo)) {
            return;
        }
        if (StringUtil.isEmpty(goodsInfo.get("stores_number"))) {
            throw new RuntimeException("请填写门店编号");
        }
        if (StringUtil.isEmpty(goodsInfo.get("goods_code"))) {
            throw new RuntimeException("请填写商品编码");
        }
        if (StringUtil.isEmpty(goodsInfo.get("in_stock"))) {
            throw new RuntimeException("请填写库存数量");
        }

    }

    public static void erp_price_checkRequire(Map<String, String> goodsInfo) {
        if (StringUtil.isEmpty(goodsInfo.get("goods_code"))) {
            throw new RuntimeException("请填写商品编码");
        }
        if (StringUtil.isEmpty(goodsInfo.get("erp_price")) || Double.parseDouble(goodsInfo.get("erp_price").toString()) == 0.00d) {
            throw new RuntimeException("请填写商品价格");
        }
    }

    public static void checkRequireNew(boolean isAdd, String detailTpl, Map<String, String> goodsInfo) {
        if (isAdd) {
            String reason = "";

            if ((StringUtil.isEmpty(goodsInfo.get("com_name")) && "10".equals(detailTpl))
                || (StringUtil.isEmpty(goodsInfo.get("drug_name")) && !"10".equals(detailTpl))) {
                reason += "请填写品名; ";
            }

            if (StringUtil.isEmpty(goodsInfo.get("goods_code"))) {
                reason += "请填写商品编码; ";
            }

            if (StringUtil.isEmpty(goodsInfo.get("shop_price"))) {
                reason += "请填写现价; ";
            }

            if (StringUtil.isNotBlank(reason)) {
                throw new RuntimeException(reason);
            }

        }
//        if (isAdd) {
//            if (StringUtil.isEmpty(goodsInfo.get("approval_number"))) {
//                throw new RuntimeException("请填写批准文号和规格");
//            }
//        } else {
//            if (StringUtil.isEmpty(goodsInfo.get("goods_code"))) {
//                throw new RuntimeException("请填写商品编码");
//            }
//        }
    }

}
