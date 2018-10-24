package com.jk51.modules.es.utils;


import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeywordsQuery extends QueryUtils{

    @Autowired
    private TransportClient transportClient;

    @Override
    public TransportClient openClient() {
        return transportClient;
    }

    @Override
    public BoolQueryBuilder buildBool(Object obj) {
        String[] strs=(String[])obj;
        String keywords=strs[0];
        BoolQueryBuilder qb = QueryBuilders.boolQuery()
                .should(QueryBuilders.termQuery("drug_name", keywords).boost(6))
                .should(QueryBuilders.matchQuery("drug_name", keywords).boost(4))
                .should(QueryBuilders.matchQuery("goods_pinyin", PinYinUtil.getPinYin(keywords, "")).boost(4))
                .should(QueryBuilders.matchQuery("goods_shouzimu", PinYinUtil.getPinYin(keywords, "")).boost(4))
                .should(QueryBuilders.matchQuery("goods_indications", keywords).boost(2))
                .should(QueryBuilders.matchQuery("brand_name", keywords).boost(2))
                .should(QueryBuilders.matchQuery("goods_company", keywords).boost(2));

        if(strs.length>1 && StringUtils.isNotBlank(strs[1])){
            qb.should(QueryBuilders.matchQuery("cateCode", strs[1]).boost(1));
        }

        return qb;
    }

    @Override
    public MatchQueryBuilder buildMatchQuery(Object obj) {
        return null;
    }

    public static String[] getGoodsListFields(){
        return new String[]{"goods_id","def_url","drug_name","com_name","brand_name","specif_cation","goods_code","goods_title","goods_company","cate_code","bar_code","approval_number","shop_price","market_price","in_stock","goods_status","purchase_way","wx_purchase_way","user_cateid","update_time"};
    }

}
