package com.jk51.modules.es.utils;

import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientPool {

    @Value("${es.cluster.host}")
    private String host;

    @Value("${es.cluster.port}")
    private int port;

    @Value("${es.cluster.name}")
    private String clusterName;

    @Bean
    public TransportClient getTransportClient(){
        return ESClientConnectionFactory.getInstance().getESClientConnection(host,port,clusterName);
    }

}
