package com.jk51.modules.es.utils;

import com.jk51.modules.es.entity.GoodsInfoReq;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsIdQuery extends QueryUtils {

	@Autowired
    private TransportClient transportClient;

	@Override
	public TransportClient openClient() {
		return transportClient;
	}

	@Override
	public BoolQueryBuilder buildBool(Object obj) {
		GoodsInfoReq req = (GoodsInfoReq)obj;
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		
		if(StringUtils.isNotBlank(req.getGoodsid())){
			qb.must(QueryBuilders.termQuery("goods_id", req.getGoodsid()));
		}else if(StringUtils.isNotBlank(req.getBar_code())){
			qb.must(QueryBuilders.termQuery("bar_code", req.getBar_code()));
			qb.must(QueryBuilders.termQuery("goods_status", req.getGoods_status()));
		}
		return qb;
	}

    @Override
    public MatchQueryBuilder buildMatchQuery(Object obj) {
        return null;
    }
}
