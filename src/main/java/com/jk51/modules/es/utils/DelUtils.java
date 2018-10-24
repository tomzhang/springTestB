package com.jk51.modules.es.utils;

import com.jk51.modules.es.entity.GoodsIdsInfo;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class DelUtils {

	private static final Logger log = LoggerFactory.getLogger(DelUtils.class);
			
	@Autowired
	private TransportClient client;

	public boolean delGoodsById(String index, String type, String id) {

		DeleteResponse response = client.prepareDelete(index, type, id).setRouting(type).execute().actionGet();
		if (response.isFound()) {
			return true;
		}
		return false;
	}

	public boolean delGoodsBytype(String index, String type) {
		List<Integer> ids = null;
		try {
			//由于查询不支持查询全部数据.固，先查询出总记录数，然后根据总记录做分页循环查询。
			SearchRequestBuilder srb = client.prepareSearch(index)// index name
					.setTypes(type)
					.setRouting(type)
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.addField("goods_id");
			SearchResponse response = srb.execute().actionGet();
			int total = (int) response.getHits().getTotalHits();
			log.info("*******total: " + total);
			if(total > 0){
				ids = pagingQuery(total, index, type);
				log.info("*******ids.size():"+ids.size());
				//删除操作
				for(int i=0;i<ids.size();i++){
					log.info("****goods_id:{}",ids.get(i));
					delGoodsById(index, type, String.valueOf(ids.get(i)));
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<Integer> pagingQuery(int total,String index, String type) throws Exception{
		List<Integer> ids = new ArrayList<Integer>();
		//默认每页一百条数据
		if(total > 100){
			//总页数
			int totalPageNum = total / 100;
			//是否有余数，如果有则总页数加1
			if(total % 100 > 0){
				totalPageNum += 1;
			}
			for(int i=1;i<=totalPageNum;i++){
				if(i == totalPageNum && (total % 100 > 0)){
					buildQuery(index, type, (i-1)*100, total % 100, ids);
				}else{
					buildQuery(index, type, (i-1)*100, 100, ids);
				}
			}
		}else{
			buildQuery(index, type, 0, total, ids);
		}
		return ids;
	}
	
	public void buildQuery(String index,String type,int form,int size,List<Integer> ids) throws Exception{
		SearchRequestBuilder srb = client.prepareSearch(index)// index name
				.setTypes(type)
				.setRouting(type)
				.setFrom(form).setSize(size)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.addField("goods_id");
		SearchResponse response = srb.execute().actionGet();
		if (response.getHits().getTotalHits() > 0) {
			List<GoodsIdsInfo> gInfosList = ObjectMapper.ConvertObjectArray(GoodsIdsInfo.class,
					response);
			for(int j=0;j<gInfosList.size();j++){
				ids.add((int) gInfosList.get(j).getGoods_id());
			}
		}
	}
}
