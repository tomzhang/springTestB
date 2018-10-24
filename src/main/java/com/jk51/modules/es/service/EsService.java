package com.jk51.modules.es.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.BSearchLog;
import com.jk51.model.BTopSearch;
import com.jk51.model.GoodsStoreRelation;
import com.jk51.model.coupon.requestParams.CouponFilterParams;
import com.jk51.model.erpprice.BGoodsErp;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.modules.coupon.service.CouponFilterService;
import com.jk51.modules.erpprice.service.ErpPriceService;
import com.jk51.modules.es.entity.*;
import com.jk51.modules.es.mapper.BSearchLogMapper;
import com.jk51.modules.es.mapper.BTopSearchMapper;
import com.jk51.modules.es.utils.*;
import com.jk51.modules.esn.mapper.GoodsEsMapper;
import com.jk51.modules.goods.mapper.GoodsStoreRelationMapper;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import org.elasticsearch.action.search.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;


@Service
public class EsService {

    private static final Logger logger = LoggerFactory.getLogger(EsService.class);

    @Autowired
    private BSearchLogMapper bSearchLogMapper;
    @Autowired
    private BTopSearchMapper bTopSearchMapper;

    @Value("${es.cluster.host}")
    private String host;

    @Value("${es.cluster.port}")
    private int port;

    @Value("${es.cluster.name}")
    private String clusterName;

    @Value("${es.goods.index}")
    private String gIndex;

    @Value("${es.suggest.index}")
    private String sIndex;

    @Autowired
    private KeywordsQuery query;

    @Autowired
    private AdminQuery adminQuery;

    @Autowired
    private GoodsIdQuery goodsIdQuery;

    @Autowired
    private SuggestQuery suggestQuery;

    @Autowired
    private DelUtils delUtils;

    @Autowired
    private GoodsEsMapper goodsEsMapper;

    @Autowired
    private CouponFilterService couponFilterService;

    @Autowired
    private PromotionsFilterService promotionsFilterService;

    @Autowired
    private ErpPriceService erpPriceService;

    @Autowired
    private GoodsStoreRelationMapper goodsStoreRelationMapper;

    /**
     * 根据商品ID和商铺ID和条形码查询ES索引中商品的详细信息
     *
     * @return
     * @throws Exception
     */
    public GoodsInfoResp getGoodsById(GoodsInfoReq gInfoReq) throws Exception {
        if(gInfoReq.isFail()){
            return GoodsInfoResp.buildErrorResp();
        }
        SearchResponse response = goodsIdQuery.boolQuery(QueryDto.buildDtoByGoodsId(gInfoReq, gIndex));
        logger.debug("****getGoodsById response json:{}",response.toString());
        if(response.getHits().getTotalHits() > 0){
            GoodsInfo gInfo = ObjectMapper.ConvertToObject(GoodsInfo.class, response.getHits().getAt(0).getSource());
            return GoodsInfoResp.buildSuccessResp(gInfo);
        }else{
            return GoodsInfoResp.buildGoodsIdNotExistResp(gInfoReq.getGoodsid());
        }
    }

    /**
     * 查询商品列表(前台用户)
     * @param gInfosReq
     * @return
     * @throws Exception
     */
    public GoodsInfosResp getGoodsListByUser(GoodsInfosAdminReq gInfosReq) throws Exception {
        if (gInfosReq.isDBFail()) {
            return GoodsInfosResp.buildErrorResp();
        }
        SearchResponse response = query.boolQuery(QueryDto.buildFristDto(gInfosReq,gIndex));
        logger.debug("****getGoodsListByUser response json1:{}",response.toString());
        if (response.getHits().getTotalHits() > 0) {
            String cate_code = response.getHits().getAt(0).getFields().get("cateCode").getValue().toString();
            SearchResponse responseList = query.boolQuery(QueryDto.buildDtoByUser(gInfosReq, cate_code,gIndex));
            logger.debug("****getGoodsListByUser response json2:{}",responseList.toString());
            logger.info("****getGoodsListByUser response TotalHits:{}",responseList.getHits().getTotalHits());
            List<GoodsInfos> gInfosList = ObjectMapper.ConvertObjectArray(GoodsInfos.class,
                responseList);
            return GoodsInfosResp.buildSuccess(gInfosList, responseList.getHits().getTotalHits());
        } else {
            return GoodsInfosResp.buildNotExistResp();
        }
    }

    /**
     * 查询商品列表(后台用户)
     *
     * @param gInfosReq
     * @return
     * @throws Exception
     */
    public GoodsInfosResp getGoodsListByAdmin(GoodsInfosAdminReq gInfosReq) throws Exception {
        if (gInfosReq.isDBFail()) {
            return GoodsInfosResp.buildErrorResp();
        }
        QueryDto queryDto = QueryDto.buildDtoByAdmin(gInfosReq, gIndex);
        SearchResponse responseList = adminQuery.boolQuery(queryDto);
        logger.info("****getGoodsListByAdmin response Tota Hits:{}",responseList.getHits().getTotalHits());
        if (responseList.getHits().getTotalHits() > 0) {
            List<GoodsInfos> gInfosList = ObjectMapper.ConvertObjectArray(GoodsInfos.class,
                responseList);

            Integer siteId = Integer.valueOf(gInfosReq.getDbname().split("_")[2]);
            //过滤查询
            gInfosList = getGoodsInfosStorehoutaiList(gInfosReq,siteId,gInfosList);

            if(StringUtil.isNotEmpty(gInfosReq.getGoods_id())) {    //根据传过来的商品id排序
                String[] split = gInfosReq.getGoods_id().split(",");
                List<GoodsInfos> gInfosList1 = new ArrayList<>();
//                while (gInfosList1.size() <= gInfosList.size()) {

                if(StringUtil.isEmpty(gInfosReq.getOrder())){
                    for (int i = 0; i<split.length; i++) {
                        int id = Integer.parseInt(split[i]);
                        for (GoodsInfos g : gInfosList) {
                            if(g.getGoods_id() == id) {
                                gInfosList1.add(g);
                            }
                        }
//                    }
                    }
                    gInfosList = gInfosList1;
                }

        }

            //获取erp价格
            if(Objects.nonNull(gInfosReq.getStoreId())) {
                /*List<Integer> goodsIds = new ArrayList<>();
                gInfosList.stream().forEach(
                    g->{
                        Integer id = Integer.valueOf(g.getGoods_id());
                        goodsIds.add(id);
                    }
                );*/
                List<Integer> goodsIds = gInfosList.stream().map(g -> Integer.valueOf(g.getGoods_id())).collect(Collectors.toList());
                Map<Integer, BGoodsErp> integerBGoodsErpMap = erpPriceService.selectERPPrice(Integer.valueOf(gInfosReq.getDbname().split("_")[2]), goodsIds, gInfosReq.getStoreId(), gInfosReq.getErpAreaCode());

                if(Objects.nonNull(integerBGoodsErpMap)) {
                    gInfosList.stream().forEach(
                        goodsInfos -> {
                            Integer integer = Integer.valueOf(goodsInfos.getGoods_id());
                            BGoodsErp bGoodsErp = integerBGoodsErpMap.get(integer);
                            if(Objects.nonNull(bGoodsErp)) {
                                Integer price = bGoodsErp.getPrice();
                                if(price != null && !price.equals(0)) {
                                    goodsInfos.setShop_price(price);
                                }
                            }
                        }

                    );
                }
            }

            //如果用户已登录则显示标签
            String userId = gInfosReq.getUserId();
            String goodsIds = gInfosList.stream().map(g -> {return String.valueOf(g.getGoods_id());}).collect(Collectors.joining(","));
            Integer user_Id = StringUtil.isNotEmpty(userId)?Integer.valueOf(userId):null;

            try {
                promotionsFilterService.proccessTagsForSearch(siteId, user_Id, gInfosList, goodsIds);
            }catch (Exception e){
                logger.error("查询商品搜索列表优惠券标签失败:{}",e);
            }
            return GoodsInfosResp.buildSuccess(gInfosList,responseList.getHits().getTotalHits());
        } else {
            return GoodsInfosResp.buildNotExistResp();
        }

    }
     //查询过滤方法
    private List<GoodsInfos> getGoodsInfosStorehoutaiList(GoodsInfosAdminReq gInfosReq,int siteId,List<GoodsInfos> gInfosList){
        if(gInfosReq !=null) {
            if (null != gInfosReq.getStoreId() ) {
                String storeId = gInfosReq.getStoreId().toString();

                List<Integer> goodsIdsList = gInfosList.stream().map(GoodsInfos::getGoods_id).collect(Collectors.toList());

                List<GoodsStoreRelation> goodsStoreRelations = goodsStoreRelationMapper.goodsStoreRelationList(siteId, goodsIdsList);

                List<Integer> goodsIdsRelationList = goodsStoreRelations.stream().map(GoodsStoreRelation::getGoodsId).collect(Collectors.toList());

                //取Storeid交集
                boolean bl = goodsIdsList.retainAll(goodsIdsRelationList);
                if (bl) {
                    List<GoodsInfos> gInfosListStore = gInfosList;
                    List<Integer> goodsIdsRemove = new ArrayList<>();
                    goodsIdsList.stream().forEach(goodsId -> {
                        List<GoodsStoreRelation> goodsStoreRelations2 = goodsStoreRelations.stream().filter(goodsStoreRelation -> Objects.equals(goodsStoreRelation.getGoodsId(), goodsId)).collect(Collectors.toList());
                        GoodsStoreRelation goodsStoreRelation = goodsStoreRelations2.get(0);
                        String gfStoreIds = goodsStoreRelation.getStoreIds();
                        List<String> storeslist = Arrays.asList(gfStoreIds.split(","));
                        if (!storeslist.contains(storeId)) {
                            goodsIdsRemove.add(goodsId);
                        }
                    });
                    gInfosList = gInfosListStore.stream().filter(mdgoodsInfo -> !goodsIdsRemove.contains(mdgoodsInfo.getGoods_id())).collect(Collectors.toList());
                }
            }
        }
        return  gInfosList;
    }



    //只搜索药品名
    public GoodsInfosResp getGoodsListByGrugName(GoodsInfosAdminReq gInfosReq) throws Exception {
        if (gInfosReq.isDBFail()) {
            return GoodsInfosResp.buildErrorResp();
        }
        QueryDto queryDto = QueryDto.buildDtoByAdmin(gInfosReq, gIndex);
        SearchResponse responseList = adminQuery.boolQueryByDrugName(queryDto);
        logger.info("****getGoodsListByAdmin response TotalHits:{}",responseList.getHits().getTotalHits());
        if (responseList.getHits().getTotalHits() > 0) {
            List<GoodsInfos> gInfosList = ObjectMapper.ConvertObjectArray(GoodsInfos.class,responseList);
            if(Objects.nonNull(gInfosReq.getStoreId())) {
                List<Integer> goodsIds = gInfosList.stream().map(g -> Integer.valueOf(g.getGoods_id())).collect(Collectors.toList());

                Map<Integer, BGoodsErp> integerBGoodsErpMap = erpPriceService.selectERPPrice(Integer.valueOf(gInfosReq.getDbname().split("_")[2]), goodsIds, gInfosReq.getStoreId(), gInfosReq.getErpAreaCode());
                if(Objects.nonNull(integerBGoodsErpMap)) {
                    gInfosList.stream().forEach(
                        goodsInfos -> {
                            Integer integer = Integer.valueOf(goodsInfos.getGoods_id());
                            BGoodsErp bGoodsErp = integerBGoodsErpMap.get(integer);
                            if(Objects.nonNull(bGoodsErp)) {
                                Integer price = bGoodsErp.getPrice();
                                if(price != null && !price.equals(0)) {
                                    goodsInfos.setShop_price(price);
                                }
                            }

                        }

                    );
                }
            }
            return GoodsInfosResp.buildSuccess(gInfosList, responseList.getHits().getTotalHits());
        } else {
            return GoodsInfosResp.buildNotExistResp();
        }

    }

    //查询商品推荐
    public GoodsInfosResp getRecommendGoodsList(GoodsInfosAdminReq gInfosReq) throws Exception {
        if (gInfosReq.isDBFail()) {
            return GoodsInfosResp.buildErrorResp();
        }
        QueryDto queryDto = QueryDto.buildDtoByAdmin(gInfosReq, gIndex);
        SearchResponse responseList = adminQuery.boolQueryForRecommendGoods(queryDto);
        logger.info("****getGoodsListByAdmin response TotalHits:{}",responseList.getHits().getTotalHits());
        if (responseList.getHits().getTotalHits() > 0) {
            List<GoodsInfos> gInfosList = ObjectMapper.ConvertObjectArray(GoodsInfos.class,responseList);
            //获取erp价格
            if(Objects.nonNull(gInfosReq.getStoreId())) {
                /*List<Integer> goodsIds = new ArrayList<>();
                gInfosList.stream().forEach(
                    g->{
                        Integer id = Integer.valueOf(g.getGoods_id());
                        goodsIds.add(id);
                    }
                );*/
                List<Integer> goodsIds = gInfosList.stream().map(g -> Integer.valueOf(g.getGoods_id())).collect(Collectors.toList());
                Map<Integer, BGoodsErp> integerBGoodsErpMap = erpPriceService.selectERPPrice(Integer.valueOf(gInfosReq.getDbname().split("_")[2]), goodsIds, gInfosReq.getStoreId(), gInfosReq.getErpAreaCode());

                if(Objects.nonNull(integerBGoodsErpMap)) {
                    gInfosList.stream().forEach(
                            goodsInfos -> {
                                Integer integer = Integer.valueOf(goodsInfos.getGoods_id());
                                BGoodsErp bGoodsErp = integerBGoodsErpMap.get(integer);
                                if(Objects.nonNull(bGoodsErp)) {
                                    Integer price = bGoodsErp.getPrice();
                                    if(price != null && !price.equals(0)) {
                                        goodsInfos.setShop_price(price);
                                    }
                                }
                            }

                    );
                }
            }
            return GoodsInfosResp.buildSuccess(gInfosList, responseList.getHits().getTotalHits());
        } else {
            return GoodsInfosResp.buildNotExistResp();
        }
    }

    //只查销量前四的.
    @SuppressWarnings("all")
    public GoodsInfosResp getGoodsListForSales(GoodsInfosAdminReq gInfosReq) throws Exception {
        if (gInfosReq.isDBFail()) {
            return GoodsInfosResp.buildErrorResp();
        }
        QueryDto queryDto = QueryDto.buildDtoByAdmin(gInfosReq, gIndex);
        SearchResponse responseList = adminQuery.boolQueryForSales(queryDto);
        logger.info("****getGoodsListByAdmin response TotalHits:{}",responseList.getHits().getTotalHits());
        if (responseList.getHits().getTotalHits() > 0) {
            List<GoodsInfos> gInfosList = ObjectMapper.ConvertObjectArray(GoodsInfos.class,responseList);
            return GoodsInfosResp.buildSuccess(gInfosList, responseList.getHits().getTotalHits());
        } else {
            return GoodsInfosResp.buildNotExistResp();
        }

    }

    /**
     * 商品搜索，先在goods_title中搜索，再在文档中搜索
     *
     * @param gInfosReq
     * @return
     * @throws Exception
     */
    public GoodsInfosResp searchAppointGoods(GoodsInfosAdminReq gInfosReq) throws Exception {
        if (gInfosReq.isDBFail()) {
            return GoodsInfosResp.buildErrorResp();
        }
        QueryDto queryDto = QueryDto.buildDtoByAdmin(gInfosReq,gIndex);

        SearchResponse r = adminQuery.searchByGoodsTitle(queryDto, gInfosReq.getGoods_name());//前台参数是goods_name
        long total1 = r.getHits().getTotalHits();
        List<GoodsInfos> rList = ObjectMapper.ConvertObjectArray(GoodsInfos.class,r);
        SearchResponse responseList = adminQuery.boolQuery(queryDto);
        long total2 = responseList.getHits().getTotalHits();
        if ((total1 + total2) == 0 ) {
            return GoodsInfosResp.buildNotExistResp();
        }
        List<GoodsInfos> gInfosList = ObjectMapper.ConvertObjectArray(GoodsInfos.class, responseList);

        List<GoodsInfos> norepeatl = delRepeat(rList, gInfosList);
        return GoodsInfosResp.buildSuccess(norepeatl, total2);
    }

    public List<GoodsInfos> delRepeat(List<GoodsInfos> l1, List<GoodsInfos> l2) {
        List<GoodsInfos> result = new ArrayList<>();
        result.addAll(l1);
        boolean flag = true;
        for (GoodsInfos goods : l2) {
            for (GoodsInfos g : l1) {
                if (goods.getGoods_id() == g.getGoods_id()) {
                    flag =false;
                    continue;
                }
            }
            if(flag)result.add(goods);
        }
        return result;
    }

    /**
     * 根据id删除具体索引文档
     * @param index
     * @param type
     * @param id
     * @return
     */
    public ReturnDto delGoodsById(String index,String type,String id){
        try {
            delUtils.delGoodsById(index, type, id);
            return ReturnDto.buildSuccessReturnDto();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnDto.buildSystemErrorReturnDto();
    }

    public ReturnDto delGoodsByType(String index,String type){
        try {
            delUtils.delGoodsBytype(index, type);
            return ReturnDto.buildSuccessReturnDto();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnDto.buildSystemErrorReturnDto();
    }

    public ReturnDto querySuggest(String key,String brand)throws Exception{
        SearchResponse responseList = suggestQuery.boolQuery(
            new QueryDto(10,sIndex,1, SuggestQuery.getGoodsListFields(),brand,key));
        logger.info("querySuggest--responseList--:"+responseList+"-----key:"+key);
        ArrayList<GoodsSuggest> result=new ArrayList<>();
        Set<String> keyw = new HashSet<>();
        Stream.of(responseList.getHits().getHits()).forEach(f->{
            String keyword = f.getFields().get("keyword").getValue().toString();
            if(keyw.add(keyword)) {
                result.add(new GoodsSuggest(keyword));
            }
        });
        logger.info("querySuggest--result--:"+result);
        return ReturnDto.buildSuccessReturnDto(result);
    }

    public ReturnDto searchLogList(String siteId,String mobile){
        List<BSearchLog> list=bSearchLogMapper.queryList(siteId,mobile);
        List<BSearchLog> bSearchLogList = list.parallelStream().filter(p -> p.getIsClean() ==0).collect(Collectors.toList());
        return ReturnDto.buildSuccessReturnDto(bSearchLogList);
    }

    public ReturnDto  insert(BSearchLog bSearchLog){
        try {
            bSearchLogMapper.insertSelective(bSearchLog);
            return ReturnDto.buildSuccessReturnDto();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ReturnDto.buildFailedReturnDto("搜索记录添加失败");
    }

    public ReturnDto batchUpdate(String siteId,String mobile){
        try {
            bSearchLogMapper.batchUpdate(siteId,mobile);
            return ReturnDto.buildSuccessReturnDto();
        }catch (Exception e){
            logger.info("搜索记录清除失败"+e);
        }
        return ReturnDto.buildFailedReturnDto("搜索记录清除失败");
    }

    public ReturnDto queryBtopSearch(String siteId){
        try{
            List<BTopSearch> list=bTopSearchMapper.queryBtopSearch(siteId);
            if(list.size() >0){
                BTopSearch bTopSearch=list.get(0);
                String ker_words=bTopSearch.getKeyWords();
                Map kerWords = null;
                kerWords= JacksonUtils.json2map(ker_words);
                return ReturnDto.buildSuccessReturnDto(kerWords);
            }
        }catch (Exception e){
            logger.info("获取热门搜索失败"+e);
        }
        return ReturnDto.buildFailedReturnDto("获取热门搜索失败");
    }

    public Map<String,Object> queryLastRecord(Integer siteId, String phoneNum) {
        return bSearchLogMapper.queryLastRecord(siteId,phoneNum);
    }

}
