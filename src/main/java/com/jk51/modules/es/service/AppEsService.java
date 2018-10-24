package com.jk51.modules.es.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.GoodsStoreRelation;
import com.jk51.model.coupon.tags.TagsGoodsPromotions;
import com.jk51.model.coupon.tags.TagsParam;
import com.jk51.model.erpprice.BGoodsErp;
import com.jk51.modules.coupon.tags.PromotionsTimeTagsFilter;
import com.jk51.modules.erpprice.service.ErpPriceService;
import com.jk51.modules.es.entity.*;
import com.jk51.modules.es.entity.ReturnDto;
import com.jk51.modules.es.utils.AppEsQuery;
import com.jk51.modules.es.utils.ObjectMapper;
import com.jk51.modules.es.utils.SuggestQuery;
import com.jk51.modules.esn.mapper.GoodsEsMapper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.mapper.GoodsStoreRelationMapper;
import com.jk51.modules.task.mapper.BTaskplanMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName AppEsService
 * @Description App中ES搜索
 * @Date 2018-06-07 15:07
 */
@Service
public class AppEsService {

    public static final Logger logger = LoggerFactory.getLogger(AppEsService.class);


    @Value("${es.goods.index}")
    private String gIndex; //ES商品索引

    @Value("${es.suggest.index}")
    private String sIndex;

    @Autowired
    private AppEsQuery appEsQuery; //根据字段值来生成默认的查询语句

    @Autowired
    private ErpPriceService erpPriceService;

    @Autowired
    private GoodsEsMapper goodsEsMapper;


    @Autowired
    private ServletContext servletContext;

    @Autowired
    private SuggestQuery suggestQuery;


    @Autowired
    private GoodsStoreRelationMapper goodsStoreRelationMapper;

    @Autowired
    private BTaskplanMapper bTaskplanMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    //根据多种条件查询
    @SuppressWarnings("all")
    public AppGoodsInfosResp getGoodsListByCondition(GoodsInfosAdminReq gInfosReq, String brand) throws Exception {
        //将siteId赋值给brand
        int siteId = Integer.parseInt(brand);
        if (gInfosReq.isDBFail()) {
            return AppGoodsInfosResp.buildErrorResp();
        }
        gInfosReq.setSiteId(siteId);
        //判断goods_name是否是拼音
        String goods_name = gInfosReq.getGoods_name();
        if (strIsEnglish(goods_name)) {
            String s = goods_name.toLowerCase();
            gInfosReq.setGoods_name(s);
        }
        QueryDto queryDto = QueryDto.appBuildDto(gInfosReq, gIndex);
        SearchResponse responseList = appEsQuery.executeQuery(queryDto);

        logger.info("****getGoodsListByCondition response TotalHits:{}", responseList.getHits().getTotalHits());
        if (responseList.getHits().getTotalHits() > 0) {
            List<AppGoodsInfo> gInfosList = ObjectMapper.ConvertObjectArray(AppGoodsInfo.class, responseList);

            //过滤查询
            gInfosList = getAppGoodsInfos(gInfosReq, siteId, gInfosList);
            //根据user_cateid查询关联分类
            /*gInfosList.stream().forEach(g -> {
                if (g.getUser_cateid().length() > 8) {
                    Map<String, Object> map = goodsMapper.queryRelevanceClassify(siteId, g.getUser_cateid());
                    g.setRelevanceClassify(String.valueOf(map.get("relevanceClassify")));
                }
            });*/

            //查询对应门店中对应商品价格
                StringBuffer sb = new StringBuffer();
                gInfosList.stream().forEach(goodsInfo -> {
                    sb.append(goodsInfo.getGoods_id());
                    sb.append(",");
                    //去除def_url空值
                /*DefUrl defUrl = JSON.parseObject(goodsInfo.getDef_url(), DefUrl.class);
                if (Objects.nonNull(defUrl) && StringUtil.isEmpty(defUrl.getHostId())) {
                    defUrl.setHostId("");
//                    defUrl.put("hostId","");
                    goodsInfo.setDef_url(JSON.toJSONString(defUrl));
                }*/
                    //根据user_cateid查询关联分类
                    if (goodsInfo.getUser_cateid().length() > 8) {
                        Map<String, Object> map = goodsMapper.queryRelevanceClassify(siteId, goodsInfo.getUser_cateid());
                        if (Objects.nonNull(map)) {
                            goodsInfo.setRelevanceClassify(String.valueOf(map.get("relevanceClassify")));
                            goodsInfo.setRelevanceReson(String.valueOf(map.get("relevanceReson")));
                        }
                    }
                });
                // 添加tag ----starts
                PromotionsTimeTagsFilter promotionsTagsFilter = null;
            /*String goodsIds = gInfosList.stream()
                .map(gs -> {
                    return String.valueOf(gs.getGoods_id());
                }).collect(Collectors.joining(","));*/

                promotionsTagsFilter = new PromotionsTimeTagsFilter(servletContext, new TagsParam(Integer.parseInt(brand), null, sb.toString(), 3));
                promotionsTagsFilter.collection();
                try {
                    promotionsTagsFilter.sorted().resolve();
                } catch (Exception e) {
                    logger.error("处理标签异常:{}", e);
                }
                List<TagsGoodsPromotions> tags = promotionsTagsFilter.getTags();
            /*tags.forEach(tg->{
                if(items.get("goods_id").toString().equals(tg.getGoodsId()))
                    items.put("tag", tg.getTags());
            });*/
                List<Map<String, Object>> goodsPrice = goodsEsMapper.queryAppGoodsPrice(brand, gInfosReq.getStoreId(), sb.toString().split(","));
                gInfosList.stream().forEach(goodsIn -> {
                    int goods_id = goodsIn.getGoods_id();
                    goodsPrice.stream().forEach(goodsP -> {
                        int goods_id1 = Integer.parseInt(String.valueOf(goodsP.get("goods_id")));
                        if (goods_id == goods_id1) {
                            Object goods_price = goodsP.get("goods_price");
                            Object discount_price = goodsP.get("discount_price");
                            if (Objects.nonNull(goods_price)) {
                                goodsIn.setGoods_price(Integer.valueOf(goods_price.toString()));
                            }
                            if (Objects.nonNull(discount_price)) {
                                goodsIn.setDiscount_price(Integer.valueOf(discount_price.toString()));
                            }
                        }
                    });
                    tags.stream().forEach(tag -> {
                        String goodsId = tag.getGoodsId();
                        if (String.valueOf(goods_id).equals(goodsId)) {
                            goodsIn.setTag(tag.getTags());
                        }
                    });
                });

                if (StringUtil.isNotEmpty(gInfosReq.getGoods_id())) {    //根据传过来的商品id排序
                    String[] split = gInfosReq.getGoods_id().split(",");
                    List<AppGoodsInfo> gInfosList1 = new ArrayList<>();
                    if (StringUtil.isEmpty(gInfosReq.getOrder())) {
                        for (int i = 0; i < split.length; i++) {
                            int id = Integer.parseInt(split[i]);
                            for (AppGoodsInfo g : gInfosList) {
                                if (g.getGoods_id() == id) {
                                    gInfosList1.add(g);
                                }
                            }
                        }
                        gInfosList = gInfosList1;
                    }

                }
                //获取erp价格
                if (Objects.nonNull(gInfosReq.getStoreId())) {
                    List<Integer> goodsIds = gInfosList.stream().map(g -> Integer.valueOf(g.getGoods_id())).collect(Collectors.toList());
                    Map<Integer, BGoodsErp> integerBGoodsErpMap = erpPriceService.selectERPPrice(Integer.valueOf(gInfosReq.getDbname().split("_")[2]), goodsIds, gInfosReq.getStoreId(), gInfosReq.getErpAreaCode());
                    if (Objects.nonNull(integerBGoodsErpMap)) {
                        gInfosList.stream().forEach(
                            goodsInfos -> {
                                Integer integer = Integer.valueOf(goodsInfos.getGoods_id());
                                BGoodsErp bGoodsErp = integerBGoodsErpMap.get(integer);
                                if (Objects.nonNull(bGoodsErp)) {
                                    Integer price = bGoodsErp.getPrice();
                                    if (price != null && !price.equals(0)) {
                                        goodsInfos.setShop_price(price);
                                    }
                                }
                            }

                        );
                    }
                }
                //购买记录
                if(org.apache.commons.lang3.StringUtils.isNotBlank(gInfosReq.getMobile())){
                    List<Integer> goodsIdList=goodsEsMapper.queryGoodsIdsByUserPay(gInfosReq);
                    if(CollectionUtils.isNotEmpty(goodsIdList)){
                        List<Integer> goodsIdsList = gInfosList.stream().map(AppGoodsInfo::getGoods_id).collect(Collectors.toList());
                        goodsIdList.retainAll(goodsIdsList);
                        if(CollectionUtils.isNotEmpty(goodsIdList)){
                            gInfosList.stream().forEach(appGoodsInfo -> {
                                if(goodsIdList.contains(appGoodsInfo.getGoods_id())){
                                    appGoodsInfo.setShopping_record(1);
                                }
                            });
                        }
                    }
                }
                //是否有任务
                if(Objects.nonNull(gInfosReq.getStoreId()) && Objects.nonNull(gInfosReq.getStoreAdminId())){
                    List<String> taskplans = bTaskplanMapper.queryTaskPlanForGoodsId(gInfosReq);
                    List<String> splitList = new ArrayList<>();
                    boolean isAll = false;
                    if(CollectionUtils.isNotEmpty(taskplans)){
                        Long all=taskplans.stream().filter(s -> Objects.equals("all",s)).count();
                        if(all > 0L){
                            isAll =true;
                        }else{
                            splitList =Arrays.asList(StringUtil.join(taskplans,",").split(","));
                        }
                    }
                    if(isAll){
                        gInfosList.stream().forEach(appGoodsInfo -> {
                            appGoodsInfo.setHave_task_plan(1);
                        });
                    }else{
                        List<Integer> taskGoodsIdsList = splitList.stream().map(Integer::parseInt).collect(Collectors.toList());
                        for (AppGoodsInfo appGoodsInfo : gInfosList) {
                            if(taskGoodsIdsList.contains(appGoodsInfo.getGoods_id())){
                                appGoodsInfo.setHave_task_plan(1);
                            }
                        }
                    }
                }

                return AppGoodsInfosResp.buildSuccess(gInfosList, responseList.getHits().getTotalHits());
            } else {
                return AppGoodsInfosResp.buildNotExistResp();
            }
    }

    //过滤查询方法
    private List<AppGoodsInfo> getAppGoodsInfos(GoodsInfosAdminReq gInfosReq, int siteId, List<AppGoodsInfo> gInfosList) {
        //开始
        String storeId = gInfosReq.getStoreId().toString();
        //拿到goodsid
        List<Integer> goodsIdsList = gInfosList.stream().map(AppGoodsInfo::getGoods_id).collect(Collectors.toList());

        //拿到关联表的数据
        List<GoodsStoreRelation> goodsStoreRelations = goodsStoreRelationMapper.goodsStoreRelationList(siteId, goodsIdsList);
        List<Integer> goodsIdsRelationList = goodsStoreRelations.stream().map(GoodsStoreRelation::getGoodsId).collect(Collectors.toList());

            //取Storeid交集
            boolean b = goodsIdsList.retainAll(goodsIdsRelationList);
            if (b) {
                List<AppGoodsInfo> gInfosLists = gInfosList;
                List<Integer> goodsIdsRemove = new ArrayList<>();
                goodsIdsList.stream().forEach(goodsId -> {
                    List<GoodsStoreRelation> goodsStoreRelations1 = goodsStoreRelations.stream().filter(goodsStoreRelation -> Objects.equals(goodsStoreRelation.getGoodsId(), goodsId)).collect(Collectors.toList());
                    GoodsStoreRelation goodsStoreRelation = goodsStoreRelations1.get(0);
                    String ss = goodsStoreRelation.getStoreIds();
                    List<String> storeslist = Arrays.asList(ss.split(","));
                        if (!storeslist.contains(storeId)) {
                            //下面是判断过滤
                            goodsIdsRemove.add(goodsId);
                         }
                });
                gInfosList = gInfosLists.stream().filter(appGoodsInfo -> !goodsIdsRemove.contains(appGoodsInfo.getGoods_id())).collect(Collectors.toList());

            }

        return gInfosList;
    }


    public static boolean isChinese(String str) {
        boolean result = true;
        for (int i = 0; i < str.length(); i++) {
            char item = str.charAt(i);
            if (String.valueOf(item).getBytes().length == String.valueOf(item).length()) {
                result = false;
            }
        }
        return result;
    }

    public static boolean isAllNum(String str) {
        for (int i = 0; i < str.length(); i++) {
            boolean b1 = str.charAt(i) >= '0';
            char charAt = str.charAt(i);
            boolean b2 = charAt <= '9';
            boolean p = charAt == '.';
            if (!(b1 && b2) && !p) {
                return false;
            }
        }
        return true;
    }

    public static boolean strIsEnglish(String word) {
        if (StringUtil.isEmpty(word)) {
            return false;
        }
        for (int i = 0; i < word.length(); i++) {
            if (!(word.charAt(i) >= 'A' && word.charAt(i) <= 'Z')
                && !(word.charAt(i) >= 'a' && word.charAt(i) <= 'z')) {
                return false;
            }
        }
        return true;
    }

    //判断字符串仅为数字和字母组成
    public static Boolean isNumOrLetter(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isDigit(c)) {
                if (!(c >= 'A' && c <= 'Z') && !(c >= 'a' && c <= 'z')) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 搜索建议
     *
     * @param key
     * @param brand
     * @return
     * @throws Exception
     */
    public ReturnDto querySuggest(String key, String brand) throws Exception {
        SearchResponse responseList = suggestQuery.suggestQuery(
            new QueryDto(10, sIndex, 1, SuggestQuery.getGoodsListFields(), brand, key));
        logger.info("querySuggest--responseList--:" + responseList + "-----key:" + key);
        ArrayList<GoodsSuggest> result = new ArrayList<>();
        Set<String> keyw = new HashSet<>();
        Stream.of(responseList.getHits().getHits()).forEach(f -> {
            String keyword = f.getFields().get("keyword").getValue().toString();
            if (keyw.add(keyword)) {
                result.add(new GoodsSuggest(keyword));
            }
        });
        logger.info("querySuggest--result--:" + result);
        return ReturnDto.buildSuccessReturnDto(result);
    }


}
