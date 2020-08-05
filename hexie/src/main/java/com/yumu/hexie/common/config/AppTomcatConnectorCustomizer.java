package com.yumu.hexie.common.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;

public class AppTomcatConnectorCustomizer implements TomcatConnectorCustomizer {

	@Override
	public void customize(Connector connector) {

		Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();  
        //设置最大连接数  
//        protocol.setMaxConnections(500);  	//不设置默认10000
        //设置最大线程数  
        protocol.setMaxThreads(400);  
        protocol.setConnectionTimeout(30000);
        protocol.setAcceptCount(500);	//默认100，指定当所有可以使用的处理请求的线程数都被使用时，可以放到处理队列中的请求数，超过这个数的请求将不予处理
        protocol.setCompression("on");	//压缩传输的数据
	}

}
