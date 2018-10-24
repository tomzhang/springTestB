package com.jk51.modules.pandian.elasticsearch.config;

import com.jk51.model.Inventories;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/6/1
 * 修改记录:
 */
@Configuration
public class ElasticsearchTemplateConfig implements ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TransportClient client;

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate(){
        return new ElasticsearchTemplate(client);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        boolean indexExists = elasticsearchTemplate().indexExists(Inventories.class);
        if(!indexExists){

            elasticsearchTemplate().createIndex(Inventories.class);
            elasticsearchTemplate().putMapping(Inventories.class);

            logger.debug("create inventory index");
        }
    }


}
