package com.jk51.modules.pandian.elasticsearch.repositories;


import com.jk51.commons.string.StringUtil;
import com.jk51.model.BPandianPlan;
import com.jk51.model.Inventories;
import com.jk51.modules.appInterface.util.AuthToken;
import com.jk51.modules.pandian.Response.ClerkCount;
import com.jk51.modules.pandian.elasticsearch.mapper.InventoriesExtMapper;
import com.jk51.modules.pandian.elasticsearch.modle.StoreCount;
import com.jk51.modules.pandian.mapper.BPandianPlanMapper;
import com.jk51.modules.pandian.param.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Future;

import static com.jk51.modules.pandian.elasticsearch.service.InventoryBuilder.buildIndex;
import static com.jk51.modules.pandian.elasticsearch.util.Constant.*;
import static com.jk51.modules.pandian.elasticsearch.util.QueryUtil.getInventoriesBoolQueryBuilder;
import static com.jk51.modules.pandian.elasticsearch.util.QueryUtil.getInventoriesOrBoolQueryBuilder;
import static com.jk51.modules.pandian.util.Constant.IS_CONFIRMED;
import static com.jk51.modules.pandian.util.Constant.NOT_CONFIRMED;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/6/1
 * 修改记录:
 */
@Service
public class InventoryRepository  {

    @Autowired
    private ElasticsearchTemplate template ;
    @Autowired
    private BPandianPlanMapper bPandianPlanMapper;
    @Autowired
    private InventoriesExtMapper inventoriesExtMapper;


    public String index(Inventories entity) {

        entity.setCreate_time(new Date());
        String result =  template.index(buildIndex(entity));
        template.refresh(Inventories.class);
        return result;
    }

    public void bulkIndex(List<Inventories> list){

        List<IndexQuery> queries = new ArrayList<>();
        for(Inventories i:list){
            queries.add(buildIndex(i));
        }

        template.bulkIndex(queries);
        template.refresh(Inventories.class);
    }


    public List<Inventories> findInventories(InventoryParam param) {


        BoolQueryBuilder builder = getInventoriesBoolQueryBuilder(param);
        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(builder)
            .withIndices("inventories")
            .build();

       return template.queryForList(query,Inventories.class);
    }


    public List<Inventories> findInventoriesOr(InventoryParam param) {


        BoolQueryBuilder builder = getInventoriesOrBoolQueryBuilder(param);
        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(builder)
            .withIndices("inventories")
            .build();

        return template.queryForList(query,Inventories.class);
    }

    public Inventories findInventoryById(int inventoryId) {
        GetQuery getQuery = new GetQuery();
        getQuery.setId(String.valueOf(inventoryId));
        return template.queryForObject(getQuery,Inventories.class);
    }

    public String updateIndex(Inventories inventories) {

        inventories.setUpdate_time(new Date());
        IndexQuery query = new IndexQuery();
        query.setObject(inventories);
        String result =  template.index(query);
        template.refresh(Inventories.class);
        return result;
    }

    public Page<Inventories> hasNotCheckPageInfo(HasNotCheckInventories param) {


        BoolQueryBuilder queryBuilder =  boolQuery();

        BoolQueryBuilder query1 =  boolQuery();
        query1.filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("isDel",0))
            .mustNot(existsQuery("actual_store"));

            if(!StringUtil.isEmpty(param.getGoods_code())){
                query1.filter(matchPhraseQuery("goods_code",param.getGoods_code()));
            }

        BoolQueryBuilder query2 = boolQuery();
        query2.filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("isDel",0))
            .mustNot(existsQuery("actual_store"));

            if(!StringUtil.isEmpty(param.getDrug_name())){
                query2.must(matchPhraseQuery("drug_name",param.getDrug_name()));
            }


       if(StringUtil.isEmpty(param.getGoods_code())||StringUtil.isEmpty(param.getDrug_name())){
           queryBuilder.filter(query1);
       } else {
           queryBuilder.should(query1);
           queryBuilder.should(query2);
       }

        PageRequest request = new PageRequest(param.getPageNum()-1,param.getPageSize());

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .withPageable(request)
            .build();

       return template.queryForPage(query,Inventories.class);
    }

    public Page<Inventories> hasNotCheckPageInfoforRepeat(HasNotCheckInventories param) {

        BoolQueryBuilder queryBuilder =  boolQuery();

        BoolQueryBuilder query1 =  boolQuery();
        query1.filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("isDel",0))
            .filter(scriptQuery(new Script("doc['actual_store'].value - doc['inventory_accounting'].value != 0")))
            .filter(termQuery("inventory_checker",param.getStoreAdminId()))
            .filter(termQuery("modify",NOT_MODIFY));

        if(!StringUtil.isEmpty(param.getGoods_code())){
            query1.filter(matchPhraseQuery("goods_code",param.getGoods_code()));
        }

        BoolQueryBuilder query2 = boolQuery();
        query2.filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("isDel",0))
            .filter(scriptQuery(new Script("doc['actual_store'].value - doc['inventory_accounting'].value != 0")))
            .filter(termQuery("inventory_checker",param.getStoreAdminId()))
            .filter(termQuery("modify",NOT_MODIFY));

        if(!StringUtil.isEmpty(param.getDrug_name())){
            query2.must(matchPhraseQuery("drug_name",param.getDrug_name()));
        }


        if(StringUtil.isEmpty(param.getGoods_code())||StringUtil.isEmpty(param.getDrug_name())){
            queryBuilder.filter(query1);
        } else {
            queryBuilder.should(query1);
            queryBuilder.should(query2);
        }

        PageRequest request = new PageRequest(param.getPageNum()-1,param.getPageSize());

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .withPageable(request)
            .build();

        return template.queryForPage(query,Inventories.class);
    }


    public long getPandianDetailCount(HasNotCheckInventories param) {

        BoolQueryBuilder queryBuilder =  boolQuery();
        queryBuilder.filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("isDel",0));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return template.count(query);
    }

    public long getPandianDetailCountforRepeat(HasNotCheckInventories param) {

        BoolQueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("isDel",0))
            .filter(termQuery("inventory_checker",param.getStoreAdminId()))
            .filter(termQuery("repeat",REPEAT));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return template.count(query);
    }


    public List<Inventories> getHasDifferenceInventories(String pandian_num, int storeAdminId) {

        BoolQueryBuilder queryBuilder =  boolQuery();
        queryBuilder.must(termQuery("pandian_num",pandian_num))
            .filter(termQuery("inventory_checker",storeAdminId))
            .filter(termQuery("isDel",0))
            .filter(termQuery("modify",0))
            .filter(existsQuery("actual_store"))
            .filter(scriptQuery(new Script("doc['actual_store'].value - doc['inventory_accounting'].value != 0")));


        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        String scrollId = template.scan(query,1000,false);
        List<Inventories> list = new ArrayList<>();
        boolean hashRecords = true;
        while(hashRecords){

            Page<Inventories> page = template.scroll(scrollId,1000L,Inventories.class);
            if(page.hasContent()){
                list.addAll(page.getContent());
            }else {
                hashRecords = false;
            }
        }

        template.clearScroll(scrollId);

        return list;
    }

    public List<Inventories> repeatInventoryForCondition(RepeatInventoryForConditionParam param) {

        BoolQueryBuilder queryBuilder = boolQuery();

        BoolQueryBuilder query1 = boolQuery()
            .filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("inventory_checker",param.getStoreAdminId()))
            .filter(existsQuery("actual_store"))
            .filter(termQuery("isDel",0))
            .must(matchPhraseQuery("drug_name",param.getDrug_name()));


        BoolQueryBuilder query2 = boolQuery()
            .filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("inventory_checker",param.getStoreAdminId()))
            .filter(existsQuery("actual_store"))
            .filter(termQuery("isDel",0))
            .filter(termsQuery("goods_code",param.getGoodsCodes()));

        queryBuilder.should(query1);
        queryBuilder.should(query2);

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return template.queryForList(query,Inventories.class);
    }

    public Page<Inventories> getPandianDetail(PandianOrderDetailParam param) {

        BoolQueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("isDel",0))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()));


        if(!StringUtil.isEmpty(param.getGoods_code())){
            queryBuilder.filter(termQuery("goods_code",param.getGoods_code()));
        }
        if(!StringUtil.isEmpty(param.getDrug_name())){
            queryBuilder.must(matchPhraseQuery("drug_name",param.getDrug_name()));
        }

        if(!StringUtil.isEmpty(param.getGoods_company())){
            queryBuilder.must(matchPhraseQuery("goods_company",param.getGoods_company()));
        }

        if(!StringUtil.isEmpty(param.getInventory_confirm())){
            queryBuilder.filter(termQuery("inventory_confirm",param.getInventory_confirm()));
        }

        if(!StringUtil.isEmpty(param.getIsInventoryed())){
            queryBuilder.filter(termQuery("modify",param.getIsInventoryed()));
        }


        if(!StringUtil.isEmpty(param.getProfitOrLossStatu())){

            if(param.getProfitOrLossStatu().equals(0)){
                queryBuilder.filter(scriptQuery(new Script("doc['inventory_accounting'].value - doc['actual_store'].value == 0 ")));
            }
            if(param.getProfitOrLossStatu().equals(1)){
                queryBuilder.filter(scriptQuery(new Script("doc['inventory_accounting'].value - doc['actual_store'].value < 0 ")));
            }
            if(param.getProfitOrLossStatu().equals(-1)){
                queryBuilder.filter(scriptQuery(new Script("doc['inventory_accounting'].value - doc['actual_store'].value > 0 ")));
                queryBuilder.must(existsQuery("actual_store"));
            }
        }

        if(!StringUtil.isEmpty(param.getCheckerStoreAdminId())){
            queryBuilder.filter(termsQuery("inventory_checker",param.getCheckerStoreAdminId()));
        }

        PageRequest request = new PageRequest((param.getPageNum()-1)<0?0:param.getPageNum()-1,param.getPageSize());

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .withPageable(request)
            .build();

        return template.queryForPage(query,Inventories.class);
    }




    public void storeAdminConfirm(StoreAdminConfirmParam param) {

        QueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("isDel",0))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("inventory_checker",param.getStoreAdminId()));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        String scrollId = template.scan(query,1000,false);
        List<Inventories> list =  new ArrayList<>();
        boolean hashRecords = true;

        while (hashRecords){
            Page<Inventories> page = template.scroll(scrollId, 1000L, Inventories.class);

            if(page.hasContent()){
                list.addAll(page.getContent());
            }else {
                hashRecords = false;
            }

        }

        template.clearScroll(scrollId);


        List<UpdateQuery> queries = new ArrayList<UpdateQuery>();
        for(Inventories i:list){

            IndexRequest indexRequest = new IndexRequest();
            indexRequest.source("inventory_confirm",1);

            UpdateQuery updateQuery = new UpdateQueryBuilder().
                withId(i.getId().toString())
                .withClass(Inventories.class)
                .withIndexRequest(indexRequest)
                .build();

            queries.add(updateQuery);
        }

        if(StringUtil.isEmpty(list)){
            return;
        }

        template.bulkUpdate(queries);

        template.refresh(Inventories.class);

    }

    public int updateInvenroty(Inventories inventories){


        BoolQueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("isDel",inventories.getIsDel()))
            .filter(termQuery("site_id",inventories.getSite_id()))
            .filter(termQuery("order_id",inventories.getOrder_id()))
            .filter(termQuery("store_num",inventories.getStore_num()))
            .filter(termQuery("goods_code",inventories.getGoods_code()))
            .must(matchPhraseQuery("batch_number",inventories.getBatch_number()));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();


        String scrollId = template.scan(query,1000,false);
        List<Inventories> list =  new ArrayList<>();
        boolean hashRecords = true;

        while (hashRecords){
            Page<Inventories> page = template.scroll(scrollId, 1000L, Inventories.class);

            if(page.hasContent()){
                list.addAll(page.getContent());
            }else {
                hashRecords = false;
            }

        }

        template.clearScroll(scrollId);

        if(StringUtil.isEmpty(list)){
            return 0;
        }

        List<UpdateQuery> queries = new ArrayList<UpdateQuery>();
        for(Inventories i:list){
            IndexRequest indexRequest = new IndexRequest();

            if(!StringUtil.isEmpty(i.getInventory_accounting())){
                indexRequest.source("store_id",i.getStore_id(),"drug_name",i.getDrug_name(),"specif_cation",i.getSpecif_cation(),"goods_company",i.getGoods_company(),"inventory_accounting",i.getInventory_accounting());
            }else{
                indexRequest.source("store_id",i.getStore_id(),"drug_name",i.getDrug_name(),"specif_cation",i.getSpecif_cation(),"goods_company",i.getGoods_company());
            }


            UpdateQuery updateQuery = new UpdateQueryBuilder().
                withId(i.getId().toString())
                .withClass(Inventories.class)
                .withIndexRequest(indexRequest)
                .build();

            queries.add(updateQuery);
        }

        template.bulkUpdate(queries);

        template.refresh(Inventories.class);
        return list.size();
    }


    public StoreCount countInventoryByPandianNumAndStoreId(Integer siteId,Integer storeId,String pandianNum){

        BoolQueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("isDel",0))
            .filter(termQuery("site_id",siteId))
            .filter(termQuery("store_id",storeId))
            .filter(termQuery("pandian_num",pandianNum));



        TermsBuilder storeIdAgg = AggregationBuilders
            .terms("storeIds")
            .field("store_id");

        SumBuilder inventoryAgg = AggregationBuilders
            .sum("sumInventory")
            .field("inventory_accounting");

        SumBuilder actualAgg = AggregationBuilders
            .sum("sumActual")
            .field("actual_store");

        ValueCountBuilder inventoryedAgg = AggregationBuilders
            .count("countInventoryed")
            .field("inventory_checker");

        ValueCountBuilder goodsAgg = AggregationBuilders
            .count("totalGoods")
            .field("goods_code");

        CardinalityBuilder checkerAgg = AggregationBuilders
            .cardinality("checkerNum")
            .field("inventory_checker");


        storeIdAgg.subAggregation(inventoryAgg).subAggregation(actualAgg).subAggregation(inventoryedAgg).subAggregation(goodsAgg).subAggregation(checkerAgg);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .addAggregation(storeIdAgg)
            .build();

        Aggregations aggregations = template.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        StoreCount result = new StoreCount();

        Map<String, Aggregation> storeMap =  aggregations.asMap();
        LongTerms storeTerms =(LongTerms)storeMap.get("storeIds");
        List<Terms.Bucket> sbucketlist  = storeTerms.getBuckets();

        if(StringUtil.isEmpty(sbucketlist)){
            return result;
        }

        Terms.Bucket sbucket = sbucketlist.get(0);

        result.setStoreId(Integer.parseInt(sbucket.getKey().toString()));

        Map<String, Aggregation> sumMap = sbucket.getAggregations().asMap();
        InternalSum sumActual = (InternalSum) sumMap.get("sumActual");
        InternalSum sumInventory = (InternalSum)sumMap.get("sumInventory");
        InternalValueCount countInventoryed = (InternalValueCount)sumMap.get("countInventoryed");
        InternalValueCount totalGoods = (InternalValueCount)sumMap.get("totalGoods");
        InternalCardinality checkerNum = (InternalCardinality)sumMap.get("checkerNum");

        result.setSumActual(new BigDecimal(sumActual.getValue()).setScale(4,RoundingMode.HALF_UP).doubleValue());
        result.setSumInventory(new BigDecimal(sumInventory.getValue()).setScale(4,RoundingMode.HALF_UP).doubleValue());
        result.setCheckerNum(checkerNum.getValue());
        result.setCountInventoryed(countInventoryed.getValue());
        result.setTotalGoods(totalGoods.getValue());

        return result;
    }

    public List<OrderClerkCount> orderClerkCount(OrderClerkCountParam param) {


        List<OrderClerkCount> result = new ArrayList<>();

        BoolQueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("isDel",0))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("pandian_num",param.getPandianNum()));



        TermsBuilder clerksAgg = AggregationBuilders
            .terms("clerks")
            .field("inventory_checker");

        SumBuilder actualAgg = AggregationBuilders
            .sum("sumActual")
            .field("actual_store");

        ValueCountBuilder inventoryedAgg = AggregationBuilders
            .count("countInventoryed")
            .field("inventory_checker");

        FilterAggregationBuilder sameAgg = AggregationBuilders
            .filter("same")
            .filter(scriptQuery(new Script("doc['actual_store'].value - doc['inventory_accounting'].value == 0")));

        FilterAggregationBuilder moreAgg = AggregationBuilders
            .filter("more")
            .filter(scriptQuery(new Script("doc['actual_store'].value - doc['inventory_accounting'].value > 0")));

        FilterAggregationBuilder lessAgg = AggregationBuilders
            .filter("less")
            .filter(scriptQuery(new Script("doc['actual_store'].value - doc['inventory_accounting'].value < 0")));

        FilterAggregationBuilder confirmAgg = AggregationBuilders
            .filter("notConfirm")
            .filter(termQuery("inventory_confirm",NOT_CONFIRMED));

        clerksAgg.subAggregation(actualAgg)
            .subAggregation(inventoryedAgg)
            .subAggregation(sameAgg)
            .subAggregation(moreAgg)
            .subAggregation(confirmAgg)
            .subAggregation(lessAgg);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .addAggregation(clerksAgg)
            .build();

        Aggregations aggregations = template.query(searchQuery, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });



        Map<String, Aggregation> storeMap =  aggregations.asMap();
        LongTerms storeTerms =(LongTerms)storeMap.get("clerks");
        List<Terms.Bucket> sbucketlist  = storeTerms.getBuckets();

        if(StringUtil.isEmpty(sbucketlist)){
            return result;
        }

        for(Terms.Bucket bucket:sbucketlist){

            OrderClerkCount orderClerkCount = new OrderClerkCount();
            orderClerkCount.setStoreAdminId(Integer.parseInt(bucket.getKey().toString()));

            Map<String, Aggregation> sumMap = bucket.getAggregations().asMap();
            InternalSum sumActual = (InternalSum) sumMap.get("sumActual");
            InternalValueCount countInventoryed = (InternalValueCount)sumMap.get("countInventoryed");
            long notConfirmNum = ((InternalFilter)sumMap.get("notConfirm")).getDocCount();

            orderClerkCount.setSumActual(new BigDecimal(sumActual.getValue()).setScale(4,RoundingMode.HALF_UP).doubleValue());
            orderClerkCount.setCountInventoryed(countInventoryed.getValue());
            orderClerkCount.setMore(((InternalFilter)sumMap.get("more")).getDocCount());
            orderClerkCount.setSame(((InternalFilter)sumMap.get("same")).getDocCount());
            orderClerkCount.setLess(((InternalFilter)sumMap.get("less")).getDocCount());
            if(notConfirmNum == 0 ){
                orderClerkCount.setIsConfirm(true);
            }

            result.add(orderClerkCount);

        }

        return result;

    }


    public void restInventoryonfirm(Integer storeId, String pandian_num) {

        BoolQueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("pandian_num",pandian_num))
            .filter(termQuery("store_id",storeId))
            .filter(termQuery("isDel",0));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withIndices("inventories")
            .withQuery(queryBuilder)
            .build();

        String scrollId = template.scan(query,1000,false);

        List<Inventories> list =  new ArrayList<>();
        boolean hashRecords = true;

        while (hashRecords){
            Page<Inventories> page = template.scroll(scrollId, 1000L, Inventories.class);

            if(page.hasContent()){
                list.addAll(page.getContent());
            }else {
                hashRecords = false;
            }

        }

        template.clearScroll(scrollId);

        List<UpdateQuery> queries = new ArrayList<UpdateQuery>();
        for(Inventories i:list){

            IndexRequest indexRequest = new IndexRequest();
            indexRequest.source("inventory_confirm",NOT_CONFIRM,"modify",NOT_MODIFY,"repeat",REPEAT);

            UpdateQuery updateQuery = new UpdateQueryBuilder().
                withId(i.getId().toString())
                .withClass(Inventories.class)
                .withIndexRequest(indexRequest)
                .build();

            queries.add(updateQuery);
        }

        if(StringUtil.isEmpty(list)){
            return;
        }

        template.bulkUpdate(queries);

        template.refresh(Inventories.class);
    }


    public void deleteByAdd(Integer siteId, Integer storeId, Integer orderId) {

        BoolQueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("site_id",siteId))
            .filter(termQuery("isDel",0))
            .filter(termQuery("order_id",orderId));

        if(!StringUtil.isEmpty(storeId)){
            queryBuilder.filter(termQuery("store_id",storeId));
        }

        SearchQuery query = new NativeSearchQueryBuilder()
            .withIndices("inventories")
            .withQuery(queryBuilder)
            .build();

        String scrollId = template.scan(query,1000,false);

        List<Inventories> list =  new ArrayList<>();
        boolean hashRecords = true;

        while (hashRecords){
            Page<Inventories> page = template.scroll(scrollId, 1000L, Inventories.class);

            if(page.hasContent()){
                list.addAll(page.getContent());
            }else {
                hashRecords = false;
            }

        }

        template.clearScroll(scrollId);

        if(StringUtil.isEmpty(list)){
            return;
        }

        bulkIsDelUpdate(list);

        template.refresh(Inventories.class);
    }


    public void deleteByStoreIdList(Integer siteId, Integer orderId, List<Integer> storeIds) {

        BoolQueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("site_id",siteId))
            .filter(termQuery("isDel",0))
            .filter(termQuery("order_id",orderId));

        if(!StringUtil.isEmpty(storeIds)){
            queryBuilder.filter(termsQuery("store_id",storeIds));
        }

        SearchQuery query = new NativeSearchQueryBuilder()
            .withIndices("inventories")
            .withQuery(queryBuilder)
            .build();

        String scrollId = template.scan(query,1000,false);

        List<Inventories> list =  new ArrayList<>();
        boolean hashRecords = true;

        while (hashRecords){
            Page<Inventories> page = template.scroll(scrollId, 1000L, Inventories.class);

            if(page.hasContent()){
                list.addAll(page.getContent());
            }else {
                hashRecords = false;
            }

        }

        template.clearScroll(scrollId);


        if(StringUtil.isEmpty(list)){
            return;
        }

        bulkIsDelUpdate(list);

        template.refresh(Inventories.class);
    }

    private void bulkIsDelUpdate(List<Inventories> list){

        List<UpdateQuery> queries = new ArrayList<UpdateQuery>();
        for(Inventories i:list){

            IndexRequest indexRequest = new IndexRequest();
            indexRequest.source("isDel", 1);

            UpdateQuery updateQuery = new UpdateQueryBuilder().
                withId(i.getId().toString())
                .withClass(Inventories.class)
                .withIndexRequest(indexRequest)
                .build();

            queries.add(updateQuery);
        }

        template.bulkUpdate(queries);
    }

    public List<Inventories> getNextNotCheckerInventories(InventoryParam param){

        QueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("isDel",0))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("goods_code",param.getGoods_code()))
            .mustNot(existsQuery("inventory_checker"));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        List<Inventories> list = template.queryForList(query,Inventories.class);

        if(StringUtil.isEmpty(list)){
            return new ArrayList<>();
        }

        BPandianPlan bPandianPlan = bPandianPlanMapper.findByPandianNum(param.getPandian_num(),param.getSiteId());
        for(Inventories i:list){
            i.setPlan_stock_show(bPandianPlan.getPlanStockShow());
        }

        return list;
    }




    private List<Inventories> findSyncIntenvoryData(Inventories i){

        QueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("pandian_num",i.getPandian_num()))
             .filter(termQuery("isDel",0))
            .filter(termQuery("site_id",i.getSite_id()))
            .filter(termQuery("store_id",i.getStore_id()))
            .filter(termQuery("goods_code",i.getGoods_code()))
            .filter(matchPhraseQuery("batch_number",i.getBatch_number()));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return template.queryForList(query,Inventories.class);
    }

    public int getHasNotStoreAdminConfirmNum(String pandianNum,Integer storeId){

        QueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("pandian_num",pandianNum))
            .filter(termQuery("isDel",0))
            .filter(termQuery("store_id",storeId))
            .mustNot(matchQuery("inventory_confirm",1));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return template.queryForList(query,Inventories.class).size();
    }

    public void manualOperationIntenvories(SyncInventoryDataParam param) {

        BoolQueryBuilder queryBuilder =  boolQuery();
        queryBuilder.must(termQuery("pandian_num",param.getPandianNum()))
            .filter(termQuery("isDel",0))
            .filter(termQuery("store_id",param.getStoreId()));


        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        String scrollId = template.scan(query,1000,false);
        List<Inventories> list = new ArrayList<>();
        boolean hashRecords = true;
        while(hashRecords){

            Page<Inventories> page = template.scroll(scrollId,1000L,Inventories.class);
            if(page.hasContent()){
                list.addAll(page.getContent());
            }else {
                hashRecords = false;
            }
        }

        template.clearScroll(scrollId);



        List<UpdateQuery> queries = new ArrayList<UpdateQuery>();
        for(Inventories i:list){

            IndexRequest indexRequest = new IndexRequest();
            indexRequest.source("actual_store",1,"inventory_checker",param.getStoreAdminId(),"modify",1);

            UpdateQuery updateQuery = new UpdateQueryBuilder().
                withId(i.getId().toString())
                .withClass(Inventories.class)
                .withIndexRequest(indexRequest)
                .build();

            queries.add(updateQuery);
        }

        if(StringUtil.isEmpty(list)){
            return;
        }

        template.bulkUpdate(queries);

        template.refresh(Inventories.class);

        manualOperationIntenvories2Sql(param);
    }

    @Async
    public void manualOperationIntenvories2Sql(SyncInventoryDataParam param){
        inventoriesExtMapper.manualOperationIntenvories2Sql(param);
    }

    public void updateScore(String pandianNum,Integer storeId,String goodsCode,Double score){

        BoolQueryBuilder queryBuilder =  boolQuery();
        queryBuilder.must(termQuery("pandian_num",pandianNum))
            .filter(termQuery("isDel",0))
            .filter(termQuery("store_id",storeId))
            .filter(termQuery("goods_code",goodsCode));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        String scrollId = template.scan(query,1000,false);
        List<Inventories> list = new ArrayList<>();
        boolean hashRecords = true;
        while(hashRecords){

            Page<Inventories> page = template.scroll(scrollId,1000L,Inventories.class);
            if(page.hasContent()){
                list.addAll(page.getContent());
            }else {
                hashRecords = false;
            }
        }

        template.clearScroll(scrollId);


        List<UpdateQuery> queries = new ArrayList<UpdateQuery>();
        for(Inventories i:list){

            IndexRequest indexRequest = new IndexRequest();
            indexRequest.source("score",score);

            UpdateQuery updateQuery = new UpdateQueryBuilder().
                withId(i.getId().toString())
                .withClass(Inventories.class)
                .withIndexRequest(indexRequest)
                .build();

            queries.add(updateQuery);
        }

        if(StringUtil.isEmpty(list)){
            return;
        }

        template.bulkUpdate(queries);

        template.refresh(Inventories.class);
    }

    public Long waitInventoryNum(WaitInventoryNumParam param) {

        BoolQueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("isDel",0))
            .mustNot(existsQuery("actual_store"));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return template.count(query);
    }

    public Long waitInventoryNumforRepeat(WaitInventoryNumParam param) {

        BoolQueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("isDel",0))
            .filter(scriptQuery(new Script("doc['actual_store'].value - doc['inventory_accounting'].value != 0")))
            .filter(termQuery("inventory_checker",param.getStoreAdminId()))
            .filter(termQuery("modify",NOT_MODIFY));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return template.count(query);
    }


    public Long doneInventoryNumforRepeat(WaitInventoryNumParam param) {

        BoolQueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("isDel",0))
            .filter(termQuery("inventory_checker",param.getStoreAdminId()))
            .filter(termQuery("modify",MODIFY))
            .filter(termQuery("repeat",REPEAT));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return template.count(query);
    }

    public Long repeatInventoryNum(WaitInventoryNumParam param){

        BoolQueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("isDel",0))
            .filter(termQuery("inventory_checker",param.getStoreAdminId()))
            .filter(termQuery("modify",NOT_MODIFY))
            .filter(scriptQuery(new Script("doc['inventory_accounting'].value - doc['actual_store'].value != 0")));


        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return template.count(query);
    }

    public Set<Integer> findInventoryCheckers(Integer siteId,Integer storeId, String pandianNum) {

       BoolQueryBuilder queryBuilder = boolQuery()
           .filter(termQuery("pandian_num",pandianNum))
           .filter(termQuery("site_id",siteId))
           .filter(termQuery("store_id",storeId))
           .filter(termQuery("isDel",0))
           .must(existsQuery("inventory_checker"));

       SearchQuery query = new NativeSearchQueryBuilder()
           .withQuery(queryBuilder)
           .withIndices("inventories")
           .withFields("inventory_checker")
           .build();


        String scrollId = template.scan(query,10000,false);
        Set<Integer> list = new HashSet<>();
        boolean hasRecords = true;
        while (hasRecords){

            Page<Integer> page = template.scroll(scrollId,10000L,new SearchResultMapper(){

                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {

                    List<Integer> result = new ArrayList<>();

                    for(SearchHit searchHit:response.getHits()){
                        result.add(searchHit.getFields().get("inventory_checker").getValue());
                    }

                    if (result.size() > 0) {
                        return new AggregatedPageImpl<T>((List<T>) result);
                    }

                    return null;
                }
            });

            if(page != null){
                list.addAll(page.getContent());
            }else{
                hasRecords = false;
            }
        }

        template.clearScroll(scrollId);

        return list;
    }

    @Async
    public Future<Long> doneInventoryNum(WaitInventoryNumParam param) {

        BoolQueryBuilder queryBuilder = getClerkCountQuery(param);
        queryBuilder.must(existsQuery("actual_store"));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return new AsyncResult(template.count(query));
    }

    private BoolQueryBuilder getClerkCountQuery(WaitInventoryNumParam param){

        BoolQueryBuilder result = boolQuery()
            .filter(termQuery("pandian_num",param.getPandian_num()))
            .filter(termQuery("site_id",param.getSiteId()))
            .filter(termQuery("store_id",param.getStoreId()))
            .filter(termQuery("isDel",0))
            .filter(termQuery("inventory_checker",param.getStoreAdminId()));

        return result;
    }


    @Async
    public Future<Long> moreInventoryNum(WaitInventoryNumParam param) {

        BoolQueryBuilder queryBuilder = getClerkCountQuery(param);
        queryBuilder.filter(scriptQuery(new Script("doc['actual_store'].value - doc['inventory_accounting'].value > 0")));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return new AsyncResult(template.count(query));
    }
    @Async
    public Future<Long> lessInventoryNum(WaitInventoryNumParam param) {
        BoolQueryBuilder queryBuilder = getClerkCountQuery(param);
        queryBuilder.filter(scriptQuery(new Script("doc['actual_store'].value - doc['inventory_accounting'].value < 0")));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return new AsyncResult(template.count(query));
    }

    @Async
    public Future<Long> sameInventoryNum(WaitInventoryNumParam param) {

        BoolQueryBuilder queryBuilder = getClerkCountQuery(param);
        queryBuilder.filter(scriptQuery(new Script("doc['actual_store'].value - doc['inventory_accounting'].value == 0")));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return new AsyncResult(template.count(query));
    }

    @Async
    public Future<Long> notClerkConfirmNum(WaitInventoryNumParam param) {

        BoolQueryBuilder queryBuilder = getClerkCountQuery(param);
        queryBuilder.filter(termQuery("inventory_confirm",NOT_CONFIRM));

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .build();

        return new AsyncResult(template.count(query));
    }


    public Double getProfitAndLossNum(Integer siteId, Integer storeId, String pandianNum) {

        BoolQueryBuilder queryBuilder = boolQuery()
            .filter(termQuery("isDel",0))
            .filter(termQuery("site_id",siteId))
            .filter(termQuery("store_id",storeId))
            .filter(termQuery("pandian_num",pandianNum))
            .must(existsQuery("actual_store"));

        TermsBuilder storeIdAgg = AggregationBuilders
            .terms("storeIds")
            .field("store_id");

        SumBuilder inventoryAgg = AggregationBuilders
            .sum("sumInventory")
            .field("inventory_accounting");

        SumBuilder actualAgg = AggregationBuilders
            .sum("sumActual")
            .field("actual_store");

        storeIdAgg.subAggregation(inventoryAgg).subAggregation(actualAgg);

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .withIndices("inventories")
            .addAggregation(storeIdAgg)
            .build();

        Aggregations aggregations = template.query(query, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });


        Map<String, Aggregation> storeMap =  aggregations.asMap();
        LongTerms storeTerms =(LongTerms)storeMap.get("storeIds");
        List<Terms.Bucket> sbucketlist  = storeTerms.getBuckets();

        if(StringUtil.isEmpty(sbucketlist)){
            return null;
        }

        Terms.Bucket sbucket = sbucketlist.get(0);

        Map<String, Aggregation> sumMap = sbucket.getAggregations().asMap();
        InternalSum sumActual = (InternalSum) sumMap.get("sumActual");
        InternalSum sumInventory = (InternalSum)sumMap.get("sumInventory");


        BigDecimal sumActualNum = new BigDecimal(sumActual.getValue()).setScale(4,RoundingMode.HALF_UP);
        BigDecimal sumInventoryNum = new BigDecimal(sumInventory.getValue()).setScale(4,RoundingMode.HALF_UP);

        return sumActualNum.subtract(sumInventoryNum).doubleValue();
    }
}
