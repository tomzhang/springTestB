package com.jk51.modules.es.utils;


import com.jk51.modules.es.service.AppEsService;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SuggestQuery extends QueryUtils {

    @Autowired
    private TransportClient transportClient;

    @Override
    public TransportClient openClient() {
        return transportClient;
    }

    @Override
    public BoolQueryBuilder buildBool(Object obj) {
        String key=(String) obj;
        BoolQueryBuilder qb = QueryBuilders.boolQuery();

        /*if(isChinese(key))
            qb.should(QueryBuilders.matchQuery("keyword",key));
        else if (AppEsService.strIsEnglish(key)) {
            qb.should(QueryBuilders.queryStringQuery("keyword_py:"+PinYinUtil.getPinYin(key,"")));
        }else if (AppEsService.isAllNum(key)){//搜价格
            qb.should(QueryBuilders.matchQuery("shop_price",key));
        }*/

        if (AppEsService.strIsEnglish(key)) {
            qb.should(QueryBuilders.queryStringQuery("keyword_py:"+PinYinUtil.getPinYin(key,"")));
        }else if (AppEsService.isAllNum(key)) {
            qb.should(QueryBuilders.matchQuery("shop_price",key));
        }else {
            qb.should(QueryBuilders.matchQuery("keyword",key));
        }

        return qb;
    }

    @Override
    public MatchQueryBuilder buildMatchQuery(Object obj) {
        String key=(String) obj;
        MatchQueryBuilder mb = null;
        /*if(isChinese(key))
            mb = QueryBuilders.matchQuery("keyword", key);
        else if (AppEsService.strIsEnglish(key)) {
            mb = QueryBuilders.matchPhraseQuery("keyword_py", PinYinUtil.getPinYin(key, ""));
        }else {//搜价格
            mb = QueryBuilders.matchQuery("shop_price", key);
        }*/

        if (AppEsService.strIsEnglish(key)) {
            mb = QueryBuilders.matchPhraseQuery("keyword_py", PinYinUtil.getPinYin(key.toLowerCase(), ""));
        }else if (AppEsService.isAllNum(key)) {
            //将价格乘100
//            int i = Integer.valueOf(key) * 100;
            Double aDouble = Double.valueOf(key);
            double i = aDouble * 100;
            //判断是否大于999999
            if (i <= 999999) {
                mb = QueryBuilders.matchQuery("shop_price", i);
            }else {//搜商编
                mb = QueryBuilders.matchQuery("goods_code",i);
            }
        }else {
            mb = QueryBuilders.matchQuery("keyword", key);
        }

        return mb;
    }

    private boolean isChinese(String str) {
        boolean result = true;
        for(int i =0 ; i<str.length() ; i++){
            char item = str.charAt(i);
            if (String.valueOf(item).getBytes().length == String.valueOf(item).length()) {
                result = false;
            }
        }
        return result;
    }

    public static String[] getGoodsListFields(){
        return new String[]{"keyword"};
    }
}
