package com.jk51.modules.es.utils;

import com.jk51.commons.string.StringUtil;
import com.jk51.modules.es.entity.GoodsInfosAdminReq;
import com.jk51.modules.es.entity.QueryDto;
import com.jk51.modules.es.service.AppEsService;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;


public abstract class QueryUtils {

    private static final Logger logger = LoggerFactory.getLogger(QueryUtils.class);


    public QueryUtils() {
    }

    public abstract TransportClient openClient();

    public abstract BoolQueryBuilder buildBool(Object obj);

    public abstract MatchQueryBuilder buildMatchQuery(Object obj);

    public SearchResponse boolQuery(QueryDto dto) {
        TransportClient client = openClient();

        if (client == null) {
            throw new RuntimeException("Open ElasticSearch Client Error");
        }
//        FieldValueFactorFunctionBuilder drug_name = new FieldValueFactorFunctionBuilder("drug_name").factor((float) 1.2).modifier(FieldValueFactorFunction.Modifier.LOG1P);
//        FieldValueFactorFunctionBuilder [] ddd = new FieldValueFactorFunctionBuilder[1];
        //类型
        GoodsInfosAdminReq goods = null;
        if (dto.getObj() instanceof GoodsInfosAdminReq) {
            goods = (GoodsInfosAdminReq) dto.getObj();
        } else {
            goods = new GoodsInfosAdminReq();
            goods.setGoods_name("");
        }
        SearchRequestBuilder srb = client.prepareSearch(dto.getIndex())//index name
            .setTypes(dto.getTypes())
            .setRouting(dto.getTypes())
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//                .setQuery(buildBool(dto.getObj())).setFrom(dto.getFrom()).setSize(dto.getSize())
//                .setQuery(QueryBuilders.functionScoreQuery(buildBool(dto.getObj())).add(drug_name).add(drug_name).boostMode("sum")) //将结果加上_score
//                .setQuery(QueryBuilders.functionScoreQuery(buildBool(dto.getObj())).add(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("drug_name", goods.getGoods_name())).must(QueryBuilders.termQuery("goods_status",goods.getGoods_status())),ScoreFunctionBuilders.weightFactorFunction(1000))
            .setFrom(dto.getFrom())
            .setSize(dto.getSize());

        if (null != dto.getFields() && dto.getFields().length > 0) {
            for (String field : dto.getFields()) {
                srb.addField(field);
            }
        }

        if (dto.isStatusFilter()) {
//            if ("-1".equals(dto.getStatus())) {
//                srb.setPostFilter(QueryBuilders.termsQuery("goods_status", "1","2"));
//            }else {
                srb.setPostFilter(QueryBuilders.termQuery("goods_status", dto.getStatus()));
//            }
        }

        //商品状态过滤器
        /*if(StringUtil.isNotEmpty(dto.getStatus()) && "-1".equals(dto.getStatus())) {
            srb.setPostFilter(QueryBuilders.termsQuery("goods_status", "1","2"));
        }*/

        //传值1、2、4， -1相当于1和2，其它数字时不对该字段做查询条件(查询全部)
        //上架
        if(StringUtil.isNotEmpty(dto.getStatus()) && "1".equals(dto.getStatus())){
            srb.setPostFilter(QueryBuilders.termsQuery("goods_status", "1"));
        }else if(StringUtil.isNotEmpty(dto.getStatus()) && "2".equals(dto.getStatus())){//下架
            srb.setPostFilter(QueryBuilders.termsQuery("goods_status", "2"));
        }else if(StringUtil.isNotEmpty(dto.getStatus()) && "4".equals(dto.getStatus())){//软删除(回收站)
            srb.setPostFilter(QueryBuilders.termsQuery("goods_status", "4"));
        }else if(StringUtil.isNotEmpty(dto.getStatus()) && "-1".equals(dto.getStatus())){
            srb.setPostFilter(QueryBuilders.termsQuery("goods_status", "1","2"));
        }

        if (dto.isPriceFilter()) {
            srb.setPostFilter(QueryBuilders.rangeQuery("shop_price").from(dto.getMinPrice()).to(dto.getMaxPrice()));

        }

        if (dto.isBrandFilter()) {
            srb.setPostFilter(QueryBuilders.termQuery("brand_id", dto.getBrandid()));
        }

        /*if(dto.isSort()){
            if(SortOrder.ASC.name().equals(dto.getSortType().toUpperCase())){
                srb.addSort(dto.getSortField(), SortOrder.ASC);
            }
            srb.addSort(dto.getSortField(), SortOrder.DESC);
        }*/
        //只根据销量倒序
        if (StringUtil.isNotEmpty(dto.getGoods_condition()) && "1".equals(dto.getGoods_condition())) {   //销量排序
            if (SortOrder.DESC.name().equals(dto.getSortType().toUpperCase())) {
//                    srb.addSort(dto.getSortField(), SortOrder.ASC);
//
                if (StringUtil.isNotEmpty(goods.getGoods_name())) {
                    srb.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("drug_name", goods.getGoods_name()))
                        .should(QueryBuilders.matchQuery("com_name", goods.getGoods_name()))
                        .should(QueryBuilders.matchQuery("goods_title", goods.getGoods_name()))).setExplain(false).addSort(dto.getSortField(), SortOrder.DESC);
                } else {
                    srb.setQuery(buildBool(dto.getObj())).setExplain(false).addSort(dto.getSortField(), SortOrder.DESC);
                }
            }

        } else if (StringUtil.isNotEmpty(goods.getGoods_name())) {    //综合排序
            //判断是否是数字
            if (AppEsService.isAllNum(goods.getGoods_name())) {//数字只搜shop_price
                //大于五位数搜编码
                int length = goods.getGoods_name().length();
                if (length > 5) {
                    srb.setExplain(true).setQuery(QueryBuilders.matchQuery("goods_code",goods.getGoods_name()));
                }else {
                    Double aDouble = Double.valueOf(goods.getGoods_name());
                    double i = aDouble * 10 * 10;
                    srb.setExplain(true).setQuery(QueryBuilders.matchQuery("shop_price",i));
                }
//                int i = Integer.valueOf(goods.getGoods_name()) * 100;
//                Double aDouble = Double.valueOf(goods.getGoods_name());
//                String format = new DecimalFormat("0.00").format(aDouble);
//                Double i = Double.valueOf(format)*100;
//                double i = aDouble * 100;
                //判断是否大于999999
                /*if (i <= 999999) {
                    srb.setExplain(true).setQuery(QueryBuilders.matchQuery("shop_price",i));
                }else {//搜商编
                    srb.setExplain(true).setQuery(QueryBuilders.matchQuery("goods_code",goods.getGoods_name()));
                }*/
//            }else if (AppEsService.strIsEnglish(goods.getGoods_name())) { //拼音搜索
            }else if (AppEsService.strIsEnglish(goods.getGoods_name())) { //拼音搜索
//                srb.setExplain(true).setQuery(QueryBuilders.queryStringQuery("drug_name_py:"+PinYinUtil.getPinYin(goods.getGoods_name(),"")));
//                srb.setExplain(true).setQuery(QueryBuilders.queryStringQuery("drug_name_py:"+PinYinUtil.getPinYin(goods.getGoods_name(),"")));
//                srb.setExplain(true).setQuery(QueryBuilders.matchQuery("drug_name_py",goods.getGoods_name()));
//                srb.setExplain(false).setQuery(QueryBuilders.matchQuery("drug_name_py",PinYinUtil.getPinYin(goods.getGoods_name(),"")));
                srb.setExplain(true).setQuery(QueryBuilders.matchPhraseQuery("goods_pinyin",PinYinUtil.getPinYin(goods.getGoods_name(),"")));
//                srb.setExplain(false).setQuery(QueryBuilders.matchPhraseQuery("goods_pinyin", PinYinUtil.getPinYin(goods.getGoods_name(), "")));
//                srb.setExplain(false).setQuery(QueryBuilders.matchQuery("drug_name", PinYinUtil.getPinYin(goods.getGoods_name(), "")));


//                srb.setExplain(false).setQuery(QueryBuilders.matchPhraseQuery("goods_shouzimu",PinYinUtil.getPinYin(goods.getGoods_name(),"")));
//                srb.setExplain(true).setQuery(QueryBuilders.matchPhraseQuery("goods_shouzimu",goods.getGoods_name()));
            }else {
                srb.setExplain(true).setQuery(QueryBuilders.functionScoreQuery(buildBool(dto.getObj()))
//                                .add(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("drug_name", goods.getGoods_name())),ScoreFunctionBuilders.weightFactorFunction(1000))
                        //根据字段设置权重
                    /*.add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("goods_title", goods.getGoods_name())),ScoreFunctionBuilders.weightFactorFunction(210)) //商品标题
                    .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("drug_name", goods.getGoods_name())),ScoreFunctionBuilders.weightFactorFunction(1050))   //药品名
                    .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("com_name", goods.getGoods_name())),ScoreFunctionBuilders.weightFactorFunction(105))    //通用名
                    .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("tags_name", goods.getGoods_name())),ScoreFunctionBuilders.weightFactorFunction(70))   //标签名
                    .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("forpeople_desc", goods.getGoods_name())),ScoreFunctionBuilders.weightFactorFunction(70))  //适应症人群
                    .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("cate_name", goods.getGoods_name())),ScoreFunctionBuilders.weightFactorFunction(70))  //适应症人群
                    .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("goods_company", goods.getGoods_name())),ScoreFunctionBuilders.weightFactorFunction(35))   //生产企业
                    .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("barnd_name", goods.getGoods_name())),ScoreFunctionBuilders.weightFactorFunction(35))    //品牌名*/
//                      //
                        .add(QueryBuilders.termQuery("goods_title", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(210)) //商品标题
                        .add(QueryBuilders.termQuery("drug_name", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(105))   //药品名
                        .add(QueryBuilders.termQuery("com_name", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(105))    //通用名
                        .add(QueryBuilders.termQuery("tags_name", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(70))   //标签名
                        .add(QueryBuilders.termQuery("forpeople_desc", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(70))  //适应症人群
                        .add(QueryBuilders.termQuery("cate_name", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(70))  //适应症人群
                        .add(QueryBuilders.termQuery("goods_company", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(35))   //生产企业
                        .add(QueryBuilders.termQuery("barnd_name", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(35)).scoreMode("sum").boostMode("sum")   //品牌名

//                  .setQuery(QueryBuilders.functionScoreQuery(buildBool(dto.getObj()),ScoreFunctionBuilders.fieldValueFactorFunction("drug_name").modifier(FieldValueFactorFunction.Modifier.LN1P).factor(1f)).add(ScoreFunctionBuilders.fieldValueFactorFunction("com_name").modifier(FieldValueFactorFunction.Modifier.RECIPROCAL).factor(1)).boostMode("sum"))
                );

            }

        } else {  //不传Goods_condition条件 不传Goods_name 查询所有 按原来的更新时间倒序
            srb.setQuery(buildBool(dto.getObj())).setExplain(false);
            if (dto.isSort()) {
                if (SortOrder.ASC.name().equals(dto.getSortType().toUpperCase())) {
                    srb.addSort(dto.getSortField(), SortOrder.ASC);
                } else {
                    srb.addSort(dto.getSortField(), SortOrder.DESC);
                }
            }
        }

        /*if (AppEsService.strIsEnglish(goods.getGoods_name())) {//拼音首字母搜索
            long totalHits = srb.execute().actionGet().getHits().getTotalHits();
            if (totalHits <= 0) {
                srb.setExplain(true).setQuery(QueryBuilders.matchQuery("goods_shouzimu",PinYinUtil.getPinYin(goods.getGoods_name(),"")));
            }
        }*/

        //logger.info("****request ES json:{}",srb.toString());
        try {
            ListenableActionFuture<SearchResponse> execute = srb.execute();
            SearchResponse searchResponse = execute.actionGet();
            return searchResponse;
        } catch (Exception e) {
            throw new RuntimeException("Search Error :", e);
        }
    }

    //只搜药品名
    public SearchResponse boolQueryByDrugName(QueryDto dto) {
        TransportClient client = openClient();

        if (client == null) {
            throw new RuntimeException("Open ElasticSearch Client Error");
        }
        GoodsInfosAdminReq goods = null;
        if (dto.getObj() instanceof GoodsInfosAdminReq) {
            goods = (GoodsInfosAdminReq) dto.getObj();
        } else {
            goods = new GoodsInfosAdminReq();
            goods.setGoods_name("");
        }
        String goods_name = goods.getGoods_name();
        SearchRequestBuilder srb = client.prepareSearch(dto.getIndex())//index name
            .setTypes(dto.getTypes())
            .setRouting(dto.getTypes())
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//            .setQuery(QueryBuilders.must(QueryBuilders.matchQuery("drug_name", goods.getGoods_name())))=
            .setSize(dto.getSize())
            .setExplain(true)
            .setFrom(dto.getFrom())
            .setSize(dto.getSize());
        if (StringUtil.isNotEmpty(goods_name)) {
            srb.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("drug_name", goods_name)).must(QueryBuilders.termQuery("goods_status", "1")));
        } else {
//            srb.setQuery(QueryBuilders.matchAllQuery());
            srb.setQuery(buildBool(dto.getObj()));
        }

        if (null != dto.getFields() && dto.getFields().length > 0) {
            for (String field : dto.getFields()) {
                srb.addField(field);
            }
        }

        if (dto.isStatusFilter()) {
            srb.setPostFilter(QueryBuilders.termQuery("goods_status", dto.getStatus()));
        }

        if (dto.isPriceFilter()) {
            srb.setPostFilter(QueryBuilders.rangeQuery("shop_price").from(dto.getMinPrice()).to(dto.getMaxPrice()));

        }

        if (dto.isBrandFilter()) {
            srb.setPostFilter(QueryBuilders.termQuery("brand_id", dto.getBrandid()));
        }

        try {
            return srb.execute().actionGet();
        } catch (Exception e) {
            throw new RuntimeException("Search Error :", e);
        }
    }

    //查询推荐商品
    public SearchResponse boolQueryForRecommendGoods(QueryDto dto) {
        TransportClient client = openClient();
        if (client == null) {
            throw new RuntimeException("Open ElasticSearch Client Error");
        }
        GoodsInfosAdminReq goods = null;
        if (dto.getObj() instanceof GoodsInfosAdminReq) {
            goods = (GoodsInfosAdminReq) dto.getObj();
        } else {
            goods = new GoodsInfosAdminReq();
            goods.setGoods_name("");
        }

        SearchRequestBuilder srb = client.prepareSearch(dto.getIndex())//index name
            .setTypes(dto.getTypes())
            .setRouting(dto.getTypes())
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//            .setQuery(QueryBuilders.must(QueryBuilders.matchQuery("drug_name", goods.getGoods_name())))=
            .setSize(dto.getSize())
            .setExplain(true)
            .setFrom(dto.getFrom())
            .setSize(4);

        if (null != dto.getFields() && dto.getFields().length > 0) {
            for (String field : dto.getFields()) {
                srb.addField(field);
            }
        }

        if (dto.isStatusFilter()) {
            srb.setPostFilter(QueryBuilders.termQuery("goods_status", dto.getStatus()));
        }

        if (dto.isPriceFilter()) {
            srb.setPostFilter(QueryBuilders.rangeQuery("shop_price").from(dto.getMinPrice()).to(dto.getMaxPrice()));

        }

        if (dto.isBrandFilter()) {
            srb.setPostFilter(QueryBuilders.termQuery("brand_id", dto.getBrandid()));
        }
        //相关度查询
        srb.setExplain(true).setQuery(QueryBuilders.functionScoreQuery(QueryBuilders.boolQuery().must(QueryBuilders.prefixQuery("cateCode", goods.getUser_cateid())).must(QueryBuilders.termQuery("goods_status", "1")))
            //.setQuery(QueryBuilders.must(QueryBuilders.matchQuery("drug_name", goods.getGoods_name())))
            .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("forpeople_desc", goods.getForpeople_desc() == null ? "" : goods.getForpeople_desc())), ScoreFunctionBuilders.weightFactorFunction(4))    //适用人群
            .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("main_ingredient", goods.getMain_ingredient() == null ? "" : goods.getMain_ingredient())), ScoreFunctionBuilders.weightFactorFunction(2))    //成分
            .scoreMode("sum")
        );

        try {
            return srb.execute().actionGet();
        } catch (Exception e) {
            throw new RuntimeException("Search Error :", e);
        }
    }

    //只查销量前四的
    @SuppressWarnings("all")
    public SearchResponse boolQueryForSales(QueryDto dto) {
        TransportClient client = openClient();

        if (client == null) {
            throw new RuntimeException("Open ElasticSearch Client Error");
        }
        GoodsInfosAdminReq goods = null;
        if (dto.getObj() instanceof GoodsInfosAdminReq) {
            goods = (GoodsInfosAdminReq) dto.getObj();
        } else {
            goods = new GoodsInfosAdminReq();
            goods.setGoods_name("");
        }
//        String goods_name = goods.getGoods_name();
        SearchRequestBuilder srb = client.prepareSearch(dto.getIndex())//index name
            .setTypes(dto.getTypes())
            .setRouting(dto.getTypes())
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//            .setQuery(QueryBuilders.must(QueryBuilders.matchQuery("drug_name", goods.getGoods_name())))=
            .setSize(dto.getSize())
            .setExplain(true)
            .setFrom(dto.getFrom())
            .setSize(dto.getSize());//QueryBuilders.matchAllQuery()
        srb.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("goods_status", "1"))).addSort(dto.getSortField(), SortOrder.DESC);

        if (null != dto.getFields() && dto.getFields().length > 0) {
            for (String field : dto.getFields()) {
                srb.addField(field);
            }
        }

        if (dto.isStatusFilter()) {
            srb.setPostFilter(QueryBuilders.termQuery("goods_status", dto.getStatus()));
        }

        if (dto.isPriceFilter()) {
            srb.setPostFilter(QueryBuilders.rangeQuery("shop_price").from(dto.getMinPrice()).to(dto.getMaxPrice()));

        }

        if (dto.isBrandFilter()) {
            srb.setPostFilter(QueryBuilders.termQuery("brand_id", dto.getBrandid()));
        }

        try {
            return srb.execute().actionGet();
        } catch (Exception e) {
            throw new RuntimeException("Search Error :", e);
        }
    }

    public SearchResponse searchByGoodsTitle(QueryDto dto, String goodsTitle) {
        TransportClient client = openClient();

        if (client == null) {
            throw new RuntimeException("Open ElasticSearch Client Error");
        }

        SearchRequestBuilder srb = client.prepareSearch(dto.getIndex())//index name
            .setTypes(dto.getTypes())
            .setRouting(dto.getTypes())
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .setQuery(buildBool(dto.getObj())).setFrom(dto.getFrom()).setSize(dto.getSize())
            .setExplain(false);
        if (null != dto.getFields() && dto.getFields().length > 0) {
            for (String field : dto.getFields()) {
                srb.addField(field);
            }
        }

        srb.setPostFilter(QueryBuilders.termQuery("goods_title", goodsTitle));

        if (dto.isSort()) {
            if (SortOrder.ASC.name().equals(dto.getSortType().toUpperCase())) {
                srb.addSort(dto.getSortField(), SortOrder.ASC);
            }
            srb.addSort(dto.getSortField(), SortOrder.DESC);
        }
        try {
            return srb.execute().actionGet();
        } catch (Exception e) {
            throw new RuntimeException("Search Error :", e);
        }
    }


    /**
     * 搜索建议查询
     * @param dto
     * @return
     */
    public SearchResponse suggestQuery(QueryDto dto) {
        TransportClient client = openClient();

        if (client == null) {
            throw new RuntimeException("Open ElasticSearch Client Error");
        }
        //类型
        GoodsInfosAdminReq goods = null;
        if (dto.getObj() instanceof GoodsInfosAdminReq) {
            goods = (GoodsInfosAdminReq) dto.getObj();
        } else {
            goods = new GoodsInfosAdminReq();
            goods.setGoods_name("");
        }
        SearchRequestBuilder srb = client.prepareSearch(dto.getIndex())//index name
            .setTypes(dto.getTypes())
            .setRouting(dto.getTypes())
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .setFrom(dto.getFrom())
            .setSize(dto.getSize());

        if (null != dto.getFields() && dto.getFields().length > 0) {
            for (String field : dto.getFields()) {
                srb.addField(field);
            }
        }

        if (dto.isStatusFilter()) {
            srb.setPostFilter(QueryBuilders.termQuery("goods_status", dto.getStatus()));
        }

        if (dto.isPriceFilter()) {
            srb.setPostFilter(QueryBuilders.rangeQuery("shop_price").from(dto.getMinPrice()).to(dto.getMaxPrice()));

        }

        if (dto.isBrandFilter()) {
            srb.setPostFilter(QueryBuilders.termQuery("brand_id", dto.getBrandid()));
        }

        //只根据销量倒序
        if (StringUtil.isNotEmpty(dto.getGoods_condition()) && "1".equals(dto.getGoods_condition())) {   //销量排序
            if (SortOrder.DESC.name().equals(dto.getSortType().toUpperCase())) {
//                    srb.addSort(dto.getSortField(), SortOrder.ASC);
//
                if (StringUtil.isNotEmpty(goods.getGoods_name())) {
                    srb.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("drug_name", goods.getGoods_name()))
                        .should(QueryBuilders.matchQuery("com_name", goods.getGoods_name()))
                        .should(QueryBuilders.matchQuery("goods_title", goods.getGoods_name()))).setExplain(false).addSort(dto.getSortField(), SortOrder.DESC);
                } else {
                    srb.setQuery(buildBool(dto.getObj())).setExplain(false).addSort(dto.getSortField(), SortOrder.DESC);
                }
            }

        } else if (StringUtil.isNotEmpty(goods.getGoods_name())) {    //综合排序
            //判断是否是数字
            if (AppEsService.isAllNum(goods.getGoods_name())) {//数字只搜shop_price
                srb.setExplain(true).setQuery(QueryBuilders.matchQuery("shop_price",goods.getGoods_name()));
            }else if (AppEsService.strIsEnglish(goods.getGoods_name())) { //拼音搜索
                srb.setExplain(true).setQuery(QueryBuilders.matchPhraseQuery("goods_pinyin",PinYinUtil.getPinYin(goods.getGoods_name(),"")));
            }else {
                srb.setExplain(true).setQuery(QueryBuilders.functionScoreQuery(buildBool(dto.getObj()))
//                                .add(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("drug_name", goods.getGoods_name())),ScoreFunctionBuilders.weightFactorFunction(1000))
                        //根据字段设置权重
                        .add(QueryBuilders.termQuery("goods_title", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(210)) //商品标题
                        .add(QueryBuilders.termQuery("drug_name", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(105))   //药品名
                        .add(QueryBuilders.termQuery("com_name", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(105))    //通用名
                        .add(QueryBuilders.termQuery("tags_name", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(70))   //标签名
                        .add(QueryBuilders.termQuery("forpeople_desc", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(70))  //适应症人群
                        .add(QueryBuilders.termQuery("cate_name", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(70))  //适应症人群
                        .add(QueryBuilders.termQuery("goods_company", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(35))   //生产企业
//                    .add(QueryBuilders.termQuery("shop_price", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(100))   //生产企业
                        .add(QueryBuilders.termQuery("barnd_name", goods.getGoods_name()), ScoreFunctionBuilders.weightFactorFunction(35)).scoreMode("sum").boostMode("sum")   //品牌名

                );

            }

        } else {  //不传Goods_condition条件 不传Goods_name 查询所有 按原来的更新时间倒序
            srb.setQuery(buildMatchQuery(dto.getObj())).setExplain(false);
            if (dto.isSort()) {
                if (SortOrder.ASC.name().equals(dto.getSortType().toUpperCase())) {
                    srb.addSort(dto.getSortField(), SortOrder.ASC);
                } else {
                    srb.addSort(dto.getSortField(), SortOrder.DESC);
                }
            }
        }

        //logger.info("****request ES json:{}",srb.toString());
        try {
            ListenableActionFuture<SearchResponse> execute = srb.execute();
            SearchResponse searchResponse = execute.actionGet();
            return searchResponse;
        } catch (Exception e) {
            throw new RuntimeException("Search Error :", e);
        }
    }


    /**
     * 执行APP查询
     * @param dto
     * @return
     */
    public SearchResponse executeQuery(QueryDto dto) {
        TransportClient client = openClient();

        if (client == null) {
            throw new RuntimeException("Open ElasticSearch Client Error");
        }
        //类型
        GoodsInfosAdminReq goods = null;
        if (dto.getObj() instanceof GoodsInfosAdminReq) {
            goods = (GoodsInfosAdminReq) dto.getObj();
        } else {
            goods = new GoodsInfosAdminReq();
            goods.setGoods_name("");
        }
        SearchRequestBuilder srb = client.prepareSearch(dto.getIndex())//index name
            .setTypes(dto.getTypes())
            .setRouting(dto.getTypes())
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
            .setFrom(dto.getFrom())
            .setSize(dto.getSize());

        if (null != dto.getFields() && dto.getFields().length > 0) {
            for (String field : dto.getFields()) {
                srb.addField(field);
            }
        }

        if (dto.isStatusFilter()) {
            srb.setPostFilter(QueryBuilders.termQuery("goods_status", dto.getStatus()));
        }

        //商品状态过滤器
        //传值1、2、4， -1相当于1和2，其它数字时不对该字段做查询条件(查询全部)
        //上架
        if(StringUtil.isNotEmpty(dto.getStatus()) && "1".equals(dto.getStatus())){
            srb.setPostFilter(QueryBuilders.termsQuery("app_goods_status", "1"));
        }else if(StringUtil.isNotEmpty(dto.getStatus()) && "2".equals(dto.getStatus())){//下架
            srb.setPostFilter(QueryBuilders.termsQuery("app_goods_status", "2"));
        }else if(StringUtil.isNotEmpty(dto.getStatus()) && "4".equals(dto.getStatus())){//软删除(回收站)
            srb.setPostFilter(QueryBuilders.termsQuery("app_goods_status", "4"));
        }else if(StringUtil.isNotEmpty(dto.getStatus()) && "-1".equals(dto.getStatus())){
            srb.setPostFilter(QueryBuilders.termsQuery("app_goods_status", "1","2"));
        }

        if (dto.isPriceFilter()) {
            srb.setPostFilter(QueryBuilders.rangeQuery("shop_price").from(dto.getMinPrice()).to(dto.getMaxPrice()));
        }

        if (dto.isBrandFilter()) {
            srb.setPostFilter(QueryBuilders.termQuery("brand_id", dto.getBrandid()));
        }

        //只根据销量倒序
        if (StringUtil.isNotEmpty(dto.getGoods_condition()) && "1".equals(dto.getGoods_condition())) {   //销量排序
            if (SortOrder.DESC.name().equals(dto.getSortType().toUpperCase())) {
                if (StringUtil.isNotEmpty(goods.getGoods_name())) {
                    srb.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("drug_name", goods.getGoods_name()))
                        .should(QueryBuilders.matchQuery("com_name", goods.getGoods_name()))
                        .should(QueryBuilders.matchQuery("goods_title", goods.getGoods_name()))).setExplain(false).addSort(dto.getSortField(), SortOrder.DESC);
                } else {
                    srb.setQuery(buildBool(dto.getObj())).setExplain(false).addSort(dto.getSortField(), SortOrder.DESC);
                }
            }

        } else if (StringUtil.isNotEmpty(goods.getGoods_name())) {
            //将字符串中的空格“ ”  逗号","    句号"。"  顿号过滤
            //正则表达式
            StringBuffer sb = new StringBuffer();
            String goods_name = goods.getGoods_name();
            for (int i = 0; i < goods_name.length(); i++) {
                char c = goods_name.charAt(i);
                if (c != ' ' && c != ',' && c != '。' && c != '、' && c != '，') {
                    sb.append(c);
                }
            }
            String goodsName = sb.toString().toLowerCase();
            if (StringUtil.isNotEmpty(goodsName)) {
                //判断是否是数字
                if (AppEsService.isAllNum(goodsName)) {//shop_price,goods_code
                    //大于五位数搜编码
                    int length = goodsName.length();
                    if (length > 4) {//编码
//                        srb.setExplain(true).setQuery(QueryBuilders.matchQuery("goods_code",goodsName));
                        srb.setExplain(true).setQuery(QueryBuilders.prefixQuery("goods_code",goodsName));
                    }else {
//                        Double aDouble = Double.valueOf(goodsName);
                        BigDecimal bigDecimal = new BigDecimal(goodsName);
//                        double i = aDouble * 10 * 10;
                        BigDecimal a = bigDecimal.multiply(new BigDecimal(100));
                        double i = a.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
                        srb.setExplain(true).setQuery(QueryBuilders.matchQuery("shop_price",i));
                    }
                }else if (AppEsService.strIsEnglish(goodsName)) { //拼音搜索
                    srb.setExplain(true).setQuery(QueryBuilders.matchPhraseQuery("goods_pinyin",PinYinUtil.getPinYin(goodsName,"")));
                }else if (AppEsService.isNumOrLetter(goodsName)) {//编码
                    srb.setExplain(true).setQuery(QueryBuilders.prefixQuery("goods_code",goodsName));//matchQuery("goods_code",goodsName)
                }else {
                    srb.setExplain(true).setQuery(QueryBuilders.functionScoreQuery(buildBool(dto.getObj())) //查询结果
                        //根据字段设置权重
                        //通过过滤器为字段设置权重
                        .add(QueryBuilders.termQuery("drug_name", goodsName), ScoreFunctionBuilders.weightFactorFunction(50))   //药品名
                        .add(QueryBuilders.termQuery("com_name", goodsName), ScoreFunctionBuilders.weightFactorFunction(30))    //通用名
                        .add(QueryBuilders.termQuery("goods_tagsid", goodsName), ScoreFunctionBuilders.weightFactorFunction(20)).scoreMode("sum").boostMode("sum")   //商品标签
                    );

                }
            }else {
                srb.setQuery(buildBool(dto.getObj())).setExplain(false);
                if (dto.isSort()) {
                    if (SortOrder.ASC.name().equals(dto.getSortType().toUpperCase())) {
                        srb.addSort(dto.getSortField(), SortOrder.ASC);
                    } else {
                        srb.addSort(dto.getSortField(), SortOrder.DESC);
                    }
                }
            }

        } else {  //不传Goods_condition条件 不传Goods_name 查询所有 按原来的更新时间倒序
            srb.setQuery(buildBool(dto.getObj())).setExplain(false);
            if (dto.isSort()) {
                if (SortOrder.ASC.name().equals(dto.getSortType().toUpperCase())) {
                    srb.addSort(dto.getSortField(), SortOrder.ASC);
                } else {
                    srb.addSort(dto.getSortField(), SortOrder.DESC);
                }
            }
        }

        try {
            ListenableActionFuture<SearchResponse> execute = srb.execute();
            SearchResponse searchResponse = execute.actionGet();
            return searchResponse;
        } catch (Exception e) {
            throw new RuntimeException("Search Error :", e);
        }
    }

}
