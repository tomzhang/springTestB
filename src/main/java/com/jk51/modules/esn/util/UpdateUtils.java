package com.jk51.modules.esn.util;

import com.jk51.modules.esn.mapper.GoodsEsMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UpdateUtils {

    private static final Logger log = LoggerFactory.getLogger(UpdateUtils.class);

    @Autowired
    private TransportClient client;

    @Autowired
    private GoodsEsMapper goodsEsMapper;

    @Value("${es.goods.index}")
    private String goodsIndex;

    public void update(String index,String type,Object obj,String idName) throws Exception{
        ObjectMapper om =new ObjectMapper();
        try {
            String value =om.writeValueAsString(obj).replaceAll("NULL","");
            String goodsid= BeanUtils.getProperty(obj,idName);
            client.prepareDelete(index,type,goodsid).setRouting(type).get();
            IndexResponse response=client.prepareIndex(index,type,goodsid).setRouting(type).setSource(value).get();
            log.info("商品更新完成：{} {}",response.toString(),value);

            if(goodsIndex.equals(index)){
                goodsEsMapper.insertLog(type, value, response.toString());
            }
        }catch (Exception e) {
            log.error(e.toString());
        }

    }

}
