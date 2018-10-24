package com.jk51.modules.esn.util;


import com.alibaba.fastjson.JSON;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.modules.esn.entity.GoodsInfo;
import com.jk51.modules.esn.entity.ImageInfo;
import com.jk51.modules.esn.mapper.GoodsEsMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class BulkUpdateUtils {

    private static final Logger log = LoggerFactory.getLogger(BulkUpdateUtils.class);

    @Autowired
    private TransportClient client;

    @Autowired
    private GoodsEsMapper goodsEsMapper;

    private boolean isExistsType(String indexName,String indexType){
        TypesExistsResponse response =
                client.admin().indices()
                        .typesExists(new TypesExistsRequest(new String[]{indexName}, indexType)
                        ).actionGet();
        return response.isExists();
    }

    private BulkProcessor buildBulk(Integer actions){
        BulkProcessor bulkProcessor = BulkProcessor.builder(
                client,
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId,
                                           BulkRequest request) {
                        log.debug("批次数据量"+request.numberOfActions());
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          BulkResponse response) {}

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          Throwable failure) {
                        log.error("happen fail = " + failure.getMessage() + " cause = " +failure.getCause());
                    }
                })
                .setBulkActions(actions)
                .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1)
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();

        return bulkProcessor;
    }


    public void delByBrandId(String index,String brandId){
        log.info("ESCreateService.delByBrandId :{}|{}",index,brandId);
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch()
                .setIndices(index).setTypes(brandId).setRouting(brandId)
                .setSize(5000).setScroll(new TimeValue(30000));
        SearchResponse response = searchRequestBuilder.get();

        BulkProcessor processor=buildBulk(new Long(response.getHits().getTotalHits()).intValue());
        String scrollId = response.getScrollId();
        if(response.getHits().getTotalHits()<1) {
            clearScroll(client, Arrays.asList(scrollId));
            return;
        }
        addDelBulk(processor,index,response.getHits().getHits(),brandId);

        while (true){
            SearchHit[] searchHits = searchByScrollId(scrollId);
            if(searchHits.length==0)
                break;
            addDelBulk(processor,index,searchHits,brandId);
        }

        processor.close();

        clearScroll(client, Arrays.asList(scrollId));
    }

    private void addDelBulk(BulkProcessor processor,String index,SearchHit[] hits,String brandid){
        Stream.of(hits).forEach(h->{
            processor.add(client.prepareDelete(index,brandid,h.getId()).setRouting(brandid).request());
        });
    }

    private SearchHit[] searchByScrollId(String scrollId){
        TimeValue timeValue = new TimeValue(30000);
        SearchScrollRequestBuilder searchScrollRequestBuilder;
        searchScrollRequestBuilder = client.prepareSearchScroll(scrollId);
        searchScrollRequestBuilder.setScroll(timeValue);
        SearchResponse response = searchScrollRequestBuilder.get();
        return response.getHits().getHits();
    }

    private void clearScroll(TransportClient client, List<String> scrollIdList){
        ClearScrollRequestBuilder clearScrollRequestBuilder = client.prepareClearScroll();
        clearScrollRequestBuilder.setScrollIds(scrollIdList);
        ClearScrollResponse response = clearScrollRequestBuilder.get();
    }

    public void batchInsert(String index, String shopid, List list, String idName){
        log.info("ESCreateService.batchInsert index:{},shopid:{},listsize:{},idname:{}",index,shopid,list.size(),idName);
        BulkProcessor processor=buildBulk(list.size());
        Long time = System.currentTimeMillis();
        list.stream().forEach(o -> {
            String goodsid="";
            try {
                ObjectMapper mapper = new ObjectMapper();
                goodsid=BeanUtils.getProperty(o,idName);
                //log.debug("addprocess {}",goodsid);
                //log.info("toJSONString:{}",JSON.toJSONString(o).replaceAll("NULL",""));
                //log.info("-----------------------------------------------------------------------------------------");
                //log.info("writeValueAsString:{}",mapper.writeValueAsString(o).replaceAll("NULL",""));
                String str=mapper.writeValueAsString(o).replaceAll("NULL","");
                BulkProcessor resp = processor.add(client.prepareIndex(index, shopid, goodsid).setRouting(shopid)
                        .setSource(str).request());
 //                   .setSource(JSON.toJSONString(o).replaceAll("NULL","")).request());


            }catch (Exception e){
                log.error("batchUpdate error:{} -----------goodsid:{}",e,goodsid);
                try {
                    goodsEsMapper.insertLog(shopid, time+":"+JacksonUtils.obj2json(o), "更新异常："+e);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        goodsEsMapper.insertLog(shopid,time+":"+"批量更新"+list.size()+"条数据", "更新完成");
        processor.close();

    }

    public void batchUpdateByMaps(String index, String shopid, List<Map<String,String>> map, String idName){

        BulkProcessor processor=buildBulk(map.size());

        ObjectMapper mapper = new ObjectMapper();
        map.parallelStream().forEach(o -> {
            try {
                processor.add(client.prepareUpdate(index, shopid, o.get(idName)).setRouting(shopid)
                        .setDoc(o).request());
            }catch (Exception e){
                log.error("batchUpdate error:",e);
            }
        });
        processor.close();
    }

    public void defineSuggestTypeMapping(String index,String type) {
        log.info("ESCreateService.defineSuggestTypeMapping :{}|{}",index,type);
        if(isExistsType(index,type)){
            log.info(type+"类型已经存在");
            return;
        }
        try {

            XContentBuilder mapBuilder = XContentFactory.jsonBuilder();
            mapBuilder.startObject()
                    .startObject(type)
                    .startObject("properties")
                    .startObject("keyword").field("type", "string").field("analyzer","ik").field("search_analyzer","ik_syno").endObject()
                    .startObject("num").field("type", "long").endObject()
                    .startObject("keyword_py").field("type","string").field("similarity","base").field("search_analyzer","lc_search").field("analyzer","lc_index")
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();

            PutMappingRequest putMappingRequest = Requests
                    .putMappingRequest(index).type(type)
                    .source(mapBuilder);
            client.admin().indices().putMapping(putMappingRequest).actionGet();
        } catch (IOException e) {
            log.error("defineSuggestTypeMapping error:",e);
        }
    }

    public void defineIndexTypeMapping(String index,String type) {
        log.info("ESCreateService.defineIndexTypeMapping :{}|{}",index,type);
        if(isExistsType(index,type)){
            log.info(type+"类型已经存在");
            return;
        }
        try {
            XContentBuilder mapBuilder = XContentFactory.jsonBuilder();
            mapBuilder.startObject()
                    .startObject(type)
                    .startObject("properties")
                    .startObject("approval_number").field("type", "string").endObject()
                    .startObject("category_path").field("type", "string").field("analyzer", "ik").field("search_analyzer","ik_syno").endObject()
                    .startObject("bar_code").field("type", "string").endObject()
                    .startObject("brand_id").field("type", "long").endObject()
                    .startObject("brand_name").field("type", "string").field("analyzer", "ik").field("search_analyzer","ik_syno").endObject()
                    .startObject("brand_desc").field("type", "string").field("analyzer", "ik").field("search_analyzer","ik_syno").endObject()
                    .startObject("browse_number").field("type", "long").endObject()
                    .startObject("cate_code").field("type", "long").endObject()
                    .startObject("cate_ishow").field("type", "long").endObject()
                    .startObject("cate_name").field("type", "string").endObject()
                    .startObject("com_name").field("type", "string").field("analyzer", "ik").field("search_analyzer","ik_syno").endObject()
                    .startObject("control_num").field("type", "long").endObject()
                    .startObject("create_time").field("type", "date").field("format", "dateOptionalTime||yyyy-MM-dd HH:mm:ss").endObject()
                    .startObject("def_url").field("type", "string").endObject()
                    .startObject("delist_time").field("type", "date").field("format", "dateOptionalTime||yyyy-MM-dd HH:mm:ss").endObject()
                    .startObject("detail_tpl").field("type", "long").endObject()
                    .startObject("cost_price").field("type", "long").endObject()
                    .startObject("discount_price").field("type", "long").endObject()
                    .startObject("drug_category").field("type", "long").endObject()
                    .startObject("drug_name").field("type", "string").field("analyzer", "ik").field("search_analyzer","ik_syno").endObject()
//                    .startObject("drug_name").field("type", "string").field("analyzer", "pinyin").field("search_analyzer","ik_syno").endObject()
//                        .startObject("drug_name").field("type","string").field("similarity","base").field("search_analyzer","lc_search").field("analyzer","lc_index").endObject()
                    .startObject("drug_name_py").field("type","string").field("similarity","base").field("search_analyzer","lc_search").field("analyzer","lc_index").endObject()
//                    .startObject("drug_name_py").field("type", "string").field("analyzer","ik_smart").endObject()
//                    .startObject("drug_name_py").field("type","string").field("analyzer","lc_index").endObject()
                    .startObject("forpeople_desc").field("type", "string").endObject()
                    .startObject("freight_payer").field("type", "long").endObject()
                    .startObject("goods_action").field("type", "string").endObject()
                    .startObject("goods_batch_no").field("type", "string").endObject()
                    .startObject("goods_company").field("type", "string").endObject()
                    .startObject("goods_contd").field("type", "string").endObject()
                    .startObject("goods_deposit").field("type", "string").endObject()
                    .startObject("goods_code").field("type", "string").endObject()
                    .startObject("goods_description").field("type", "string").endObject()
                    .startObject("goods_desc").field("type", "string").endObject()
                    .startObject("goods_forpeople").field("type", "string").field("analyzer", "ik").field("search_analyzer","ik_syno").endObject()
                    .startObject("goods_indications").field("type", "string").field("analyzer", "ik").field("search_analyzer","ik_syno").endObject()
                    .startObject("goods_pinyin").field("type","multi_field")
                    .startObject("fields")
                    .startObject("goods_pinyin").field("type","string").field("store","no").field("term_vector","with_positions_offsets").field("analyzer","pinyin_analyzer").field("boost",10).endObject()
                    .startObject("goods_pinyin_primitive").field("type","string").field("store","yes").field("analyzer","keyword").endObject()
                    .endObject()
                    .endObject()
                    .startObject("goods_shouzimu").field("type", "string").field("similarity","base").field("search_analyzer","lc_search").field("analyzer","pinyin_analyzer").endObject()
                    .startObject("goods_note").field("type", "string").endObject()
                    .startObject("goods_forts").field("type", "long").endObject()
                    .startObject("goods_id").field("type", "long").endObject()
                    .startObject("goods_property").field("type", "long").endObject()
                    .startObject("goods_status").field("type", "long").endObject()
                    .startObject("goods_tagsid").field("type", "string").field("analyzer", "ik").field("search_analyzer","ik_syno").endObject()
                    .startObject("goods_title").field("type", "string").field("analyzer", "ik").field("search_analyzer","ik_syno").endObject()
                    .startObject("goods_url").field("type", "string").endObject()
                    .startObject("goods_usage").field("type", "string").endObject()
                    .startObject("goods_use_method").field("type", "string").endObject()
                    .startObject("goods_validity").field("type", "long").endObject()
                    .startObject("goods_weight").field("type", "long").endObject()
                    .startObject("goods_img").field("type", "long").endObject()
                    .startObject("goodsextd_id").field("type", "long").endObject()
                    .startObject("in_stock").field("type", "long").endObject()
                    .startObject("is_medicare").field("type", "long").endObject()
                    .startObject("list_time").field("type", "date").field("format", "dateOptionalTime||yyyy-MM-dd HH:mm:ss").endObject()
                    .startObject("main_ingredient").field("type", "string").endObject()
                    .startObject("market_price").field("type", "long").endObject()
                    .startObject("mnemonic_code").field("type", "string").endObject()
                    .startObject("medicare_code").field("type", "string").endObject()
                    .startObject("medicare_top_price").field("type", "long").endObject()
                    .startObject("product_date").field("type", "string").endObject()
                    .startObject("parent_id").field("type", "long").endObject()
                    .startObject("postage_id").field("type", "long").endObject()
                    .startObject("purchase_way").field("type", "long").endObject()
                    .startObject("qualification").field("type", "string").endObject()
                    .startObject("shop_price").field("type", "long").field("analyzer","ik").field("search_analyzer","ik_syno").endObject()
                    .startObject("shopping_number").field("type", "long").endObject()
                    .startObject("specif_cation").field("type", "string").endObject()
                    .startObject("trans_mumber").field("type", "long").endObject()
                    .startObject("update_time").field("type", "date").field("format", "dateOptionalTime||yyyy-MM-dd HH:mm:ss").endObject()
                    .startObject("user_cateid").field("type", "long").endObject()
                    .startObject("wx_purchase_way").field("type", "string").endObject()
                    .startObject("cateCode").field("type", "string").endObject()
                    .startObject("isDistribute").field("type", "string").endObject()
                    .startObject("goods_num").field("type", "long").endObject()
                    .startObject("gross_profit").field("type", "long").endObject()
                    .startObject("is_main_push").field("type", "long").endObject()
                    .endObject()
                    .endObject()
                    .endObject();

            PutMappingRequest putMappingRequest = Requests
                    .putMappingRequest(index).type(type)
                    .source(mapBuilder);
            client.admin().indices().putMapping(putMappingRequest).actionGet();
        } catch (IOException e) {
            log.error("defineIndexTypeMapping error:",e);
        }
    }

}
