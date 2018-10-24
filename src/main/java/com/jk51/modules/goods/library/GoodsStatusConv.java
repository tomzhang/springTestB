package com.jk51.modules.goods.library;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jk51.model.Goods;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GoodsStatusConv {
    /**
     * 出售中
     */
    public static final int STATUS_LISTING = 1;
    /**
     * 库存中
     */
    public static final int STATUS_DELISTING = 2;
    /**
     * 违规
     */
    public static final int STATUS_EXCEPTION = 3;
    /**
     * 软删除
     */
    public static final int STATUS_SOFTDEL = 4;

    private static final String ALLOW_KEY = "allow";

    private static final String ILLEGAL_KEY = "illegal";

    /**
     * 当前状态可以转换成什么状态
     * 这个map有点大....
     */
    private static final Map<Integer, Map<String, Object>> GOODS_STATUS_CONV_MAP = new HashMap(){
        {
            // 上架状态可以下架、违规
            put(STATUS_LISTING, new HashMap() {
                {
                    put(ALLOW_KEY, new int[]{STATUS_DELISTING, STATUS_EXCEPTION});
                    put(ILLEGAL_KEY, new HashMap<Integer, String>() {
                        {
                            put(STATUS_SOFTDEL, "请先下架之后再删除商品");
                        }
                    });
                }
            });
            // 下架状态可以上架、违规、删除
            put(STATUS_DELISTING, new HashMap() {
                {
                    put(ALLOW_KEY, new int[]{STATUS_LISTING, STATUS_SOFTDEL, STATUS_EXCEPTION});
                    put(ILLEGAL_KEY, new HashMap<Integer, String>() {});
                }
            });
            // 违规状态可以删除
            put(STATUS_EXCEPTION, new HashMap() {
                {
                    put(ALLOW_KEY, new int[]{STATUS_SOFTDEL});
                    put(ILLEGAL_KEY, new HashMap<Integer, String>() {
                        {
                            put(STATUS_LISTING, "商品违规，请处理之后审核通过再进行上架操作");
                            put(STATUS_DELISTING, "商品违规，无效的操作");
                        }
                    });
                }
            });
            // 删除了的可以通过回收站还原到下架状态
            put(STATUS_SOFTDEL, new HashMap() {
                {
                    put(ALLOW_KEY, new int[]{STATUS_DELISTING});
                    put(ILLEGAL_KEY, new HashMap<Integer, String>() {
                        {
                            put(STATUS_LISTING, "商品已删除");
                            put(STATUS_EXCEPTION, "商品已删除");
                        }
                    });
                }
            });
        }
    };

    private static String lastErrorMsg;

    /**
     * 是否可以将b_goods.googd_status 修改为指定值 false请调用getMessage()
     *
     * @param goodsMap
     * @param goodsStatus
     * @return
     */
    public static boolean allowChangeStatusToValue(Map<String, Object> goodsMap, int goodsStatus, String onLineShopChecked, String status) {
        if (goodsMap == null) {
            lastErrorMsg = "未找到该商品";
            return false;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        // 忽略未知字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 禁用注解
        objectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        // 下面代码没用 懒得搞了 忽略掉注解上的命名策略
        /*objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public boolean isAnnotationBundle(Annotation ann) {
                if (ann.annotationType().equals(JsonNaming.class)) {
                    return false;
                } else {
                    return super.isAnnotationBundle(ann);
                }
            }
        });*/

//         驼峰
//        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        // 转换为pojo

        Goods goods = objectMapper.convertValue(goodsMap, Goods.class);
//        GoodsExtd goodsExtd = JacksonUtils.map2pojo(goodsMap, GoodsExtd.class);

        if (! GOODS_STATUS_CONV_MAP.containsKey(goods.getGoodsStatus())) {
            lastErrorMsg = "商品状态值非法";
            return false;
        }
        if (! GOODS_STATUS_CONV_MAP.containsKey(goods.getAppGoodsStatus())) {
            lastErrorMsg = "app商品状态值非法";
            return false;
        }

        // 当前商品的状态可以转换的值
        int[] currentStatusTo = (int[])GOODS_STATUS_CONV_MAP.get(goods.getGoodsStatus()).get(ALLOW_KEY);
        //需求，添加app端的商品状态的校验 ------start
        int[] appCurrentStatusTo = (int[])GOODS_STATUS_CONV_MAP.get(goods.getAppGoodsStatus()).get(ALLOW_KEY);
        boolean appFlag = Arrays.binarySearch(appCurrentStatusTo, goodsStatus) > -1;
        //需求，添加app端的商品状态的校验 ------end

        boolean flag = Arrays.binarySearch(currentStatusTo, goodsStatus) > -1;
        /*if (! flag && ! appFlag) {
            // 记录不能变更的原因
            statusNotToValueMessage(goods.getGoodsStatus(), goodsStatus);
            return false;
        }*/
        // 如果是上架还要检查上架条件是否满足
        if (goodsStatus == STATUS_LISTING) {
            // 库存不足不能上架
            /*if (goods.getInStock() < 0) {
                lastErrorMsg = goods.getGoodsTitle() + ":库存不足不能上架";
                return false;
            }*/

            //商品价格为 0 不能上架
            if(goods.getShopPrice()==null || 0 >= goods.getShopPrice()){
                lastErrorMsg = goods.getGoodsTitle() + ":商品价格为空或小于等于0不能上架";
                return false;
            }

            // other
            //添加产品需求：微信上架要求：商品编码 商品名称 商品标题 规格 生成厂家  现价（shop price） 主图； APP上架要求：商品编码 商品名称 商品标题 规格 生成厂家  现价（shop price） ------start
            //商品编码为为空，不可上架
            if (Objects.isNull(goods.getGoodsCode()) || StringUtils.isBlank(goods.getGoodsCode())){
                lastErrorMsg = goods.getGoodsTitle() + ":商品编码为空不能上架";
                return false;
            }
            //商品名称为空，不可上架
            if (Objects.isNull(goods.getDrugName()) || StringUtils.isBlank(goods.getDrugName())){
                lastErrorMsg = goods.getGoodsTitle() + ":商品名称为空不能上架";
                return false;
            }
            //商品标题为空，不可上架
            if (Objects.isNull(goods.getGoodsTitle()) || StringUtils.isBlank(goods.getGoodsTitle())){
                lastErrorMsg = goods.getGoodsTitle() + ":商品标题为空不能上架";
                return false;
            }
            //商品规格为空，不可上架
            if (Objects.isNull(goods.getSpecifCation()) || StringUtils.isBlank(goods.getSpecifCation())){
                lastErrorMsg = goods.getGoodsTitle() + ":商品规格为空不能上架";
                return false;
            }
            //商品生产厂家为空，不可上架
            if (Objects.isNull(goods.getGoodsCompany()) || StringUtils.isBlank(goods.getGoodsCompany())){
                lastErrorMsg = goods.getGoodsTitle()+ ":商品生产厂家为空不能上架";
                return false;
            }
            //微信商城，无主图，不可上架
            if (StringUtils.isNotBlank(onLineShopChecked) || Objects.nonNull(status) && "1".equals(status)){
                if (Objects.isNull(goods.getIsDefault())){
                    lastErrorMsg = goods.getGoodsTitle() + ":商品主图为空不能上架，线上商城商品上架失败";
                    return false;
                }
            }
            //添加产品需求：微信上架要求：商品编码 商品名称 商品标题 规格 生成厂家  现价（shop price） 主图； APP上架要求：商品编码 商品名称 商品标题 规格 生成厂家  现价（shop price） ------end
        }
        return flag || appFlag;
    }

    private static void statusNotToValueMessage(int goodsStatus, int newGoodsStatus) {
        Map a = (HashMap)GOODS_STATUS_CONV_MAP.get(goodsStatus).get(ILLEGAL_KEY);
        lastErrorMsg = (String)a.get(newGoodsStatus);
    }

    public static String getLastErrorMessage() {
        return lastErrorMsg;
    }

    public static void setLastErrorMessage(String msg) {
        lastErrorMsg = msg;
    }
}
