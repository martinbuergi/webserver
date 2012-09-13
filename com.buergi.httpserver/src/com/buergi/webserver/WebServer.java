package com.buergi.webserver;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import com.buergi.webserver.http.impl.HttpRequest10Impl;
import com.buergi.webserver.http.impl.HttpRequest11Impl;
import com.buergi.webserver.services.HttpRequestParserService;
import com.buergi.webserver.services.HttpResponseService;
import com.buergi.webserver.services.WebServerService;
import com.buergi.webserver.services.WorkerService;
import com.buergi.webserver.services.impl.HttpRequestParserServiceImpl;
import com.buergi.webserver.services.impl.HttpResponseServiceImpl;
import com.buergi.webserver.services.impl.WebServerServiceImpl;
import com.buergi.webserver.services.impl.WebServerServiceImpl.ServerBufferSize;
import com.buergi.webserver.services.impl.WebServerServiceImpl.ServerDocRoot;
import com.buergi.webserver.services.impl.WebServerServiceImpl.ServerPort;
import com.buergi.webserver.services.impl.WorkerServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class WebServer extends AbstractModule {

	private String docRoot;
	private int port;
	private int bufferSize;
	
	public static void main(String[] args) {
		int port = 8080;
		int bufferSize = 2*1024;
		String docRoot = null;
		
		try {
			 PropertiesConfiguration config = new PropertiesConfiguration(WebServer.class.getResource("properties.config"));
			 if (!config.containsKey("root") || (docRoot = config.getString("root")).length() == 0){
		            System.err.println("Please specify document root directory in properties.config (Default is user.home)");
					 docRoot = System.getProperty("user.home");
			 }
			 
			 if (config.containsKey("port"))
				 port = config.getInt("port");
	
			 if (config.containsKey("buffer"))
				 bufferSize = config.getInt("buffer");
			 
		} catch (ConfigurationException e) {
			System.err.println("Configfile error: " + e.getMessage());
			System.exit(1);
		}
	
		Injector injector = Guice.createInjector(new HttpRequest10Impl(), new HttpRequest11Impl(), new WebServer(docRoot, port, bufferSize));
		WebServerService httpServer = injector.getInstance(WebServerService.class);
		httpServer.start();				
	}
	
	
	public WebServer(String docRoot, int port, int bufferSize) {
		this.docRoot = docRoot;
		this.port = port;
		this.bufferSize = bufferSize;
	}
	
	@Override
	protected void configure() {
		bind(WebServerService.class).to(WebServerServiceImpl.class);
		bind(WorkerService.class).to(WorkerServiceImpl.class);
		bind(HttpRequestParserService.class).to(HttpRequestParserServiceImpl.class);
		bind(HttpResponseService.class).to(HttpResponseServiceImpl.class);
		
		bind(String.class).annotatedWith(ServerDocRoot.class).toInstance(docRoot);
	    bind(Integer.class).annotatedWith(ServerPort.class).toInstance(port);
	    bind(Integer.class).annotatedWith(ServerBufferSize.class).toInstance(bufferSize);
	}
}
