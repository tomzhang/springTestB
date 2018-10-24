package com.jk51.modules.pandian.elasticsearch.util;

import com.jk51.commons.string.StringUtil;
import com.jk51.modules.pandian.param.InventoryParam;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.search.MultiMatchQuery;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/5/28
 * 修改记录:
 */
public class QueryUtil {

    public static BoolQueryBuilder getInventoriesBoolQueryBuilder(InventoryParam param){

        BoolQueryBuilder result = boolQuery();

        result.must(termQuery("pandian_num", param.getPandian_num()));
        result.must(termQuery("isDel",0));
        result.must(termQuery("store_id", param.getStoreId()));
        result.must(termQuery("site_id", param.getSiteId()));

        if(!StringUtil.isEmpty(param.getDrug_name())){
            result.must(matchPhraseQuery("drug_name", param.getDrug_name()));
        }

        if(!StringUtil.isEmpty(param.getGoodsCodes())){
            result.must(termsQuery("goods_code", param.getGoodsCodes()));
        }

        if(!StringUtil.isEmpty(param.getBatch_number())){
            result.must(matchPhraseQuery("batch_number", param.getBatch_number()));
        }

        return result;
    }


    public static BoolQueryBuilder getInventoriesOrBoolQueryBuilder(InventoryParam param){


        BoolQueryBuilder result = boolQuery();

        BoolQueryBuilder queryBuilder1 = boolQuery();
        queryBuilder1.must(termQuery("pandian_num", param.getPandian_num()));
        queryBuilder1.must(termQuery("isDel",0));
        queryBuilder1.must(termQuery("store_id", param.getStoreId()));
        queryBuilder1.must(termQuery("site_id", param.getSiteId()));
        queryBuilder1.must(matchPhraseQuery("drug_name", param.getDrug_name()));

        BoolQueryBuilder queryBuilder2 = boolQuery();
        queryBuilder2.must(termQuery("pandian_num", param.getPandian_num()));
        queryBuilder2.must(termQuery("isDel",0));
        queryBuilder2.must(termQuery("store_id", param.getStoreId()));
        queryBuilder2.must(termQuery("site_id", param.getSiteId()));
        queryBuilder2.must(termsQuery("goods_code", param.getGoodsCodes()));


        if(StringUtil.isEmpty(param.getDrug_name())){
            return queryBuilder2;
        }

        result.should(queryBuilder1);
        result.should(queryBuilder2);

        return result;
    }


}
