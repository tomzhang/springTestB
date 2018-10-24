package com.jk51.modules.es.utils;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ESClientConnectionFactory {

	private static final Logger logger = LoggerFactory.getLogger(ESClientConnectionFactory.class);
	
	private ESClientConnectionFactory() {}

	private static final ESClientConnectionFactory client = new ESClientConnectionFactory();

	public static ESClientConnectionFactory getInstance() {
		return client;
	}


	public TransportClient getESClientConnection(String host, int port, String clusterName) {
		logger.info("Connection ES Host:{};Port:{};clusterName:{}",host,port,clusterName);
		try {
			Settings settings = Settings.settingsBuilder()
					.put("cluster.name", clusterName)
					.put("tclient.transport.sniff", true).build();

			TransportClient client = TransportClient.builder()
					.settings(settings)
					.build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
			
			return client;
		} catch (UnknownHostException e) {
			logger.error(" Connection ES failed! Host:{}; Port:{}",host,port);
			//e.printStackTrace();
			return null;
		} catch (Exception e) {
			logger.error(" Exception:{}",e);
			//e.printStackTrace();
			return null;
		}
	}
}
