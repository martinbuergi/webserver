package com.buergi.webserver;

import com.buergi.webserver.services.HttpRequestService;
import com.buergi.webserver.services.HttpResponseService;
import com.buergi.webserver.services.RequestParserService;
import com.buergi.webserver.services.WebServerService;
import com.buergi.webserver.services.WorkerService;
import com.buergi.webserver.services.impl.HttpRequestServiceImpl;
import com.buergi.webserver.services.impl.HttpResponseServiceImpl;
import com.buergi.webserver.services.impl.RequestParserServiceImpl;
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

		if (args.length == 0) {
            System.err.println("Please specify document root directory");
            System.exit(1);
          }
			
		String docRoot = args[0];
		
		if (args.length >= 2)
			port = Integer.valueOf(args[1]);
		if (args.length >= 3)
			bufferSize = Integer.valueOf(args[2]);

		
		Injector injector = Guice.createInjector(new WebServer(docRoot, port, bufferSize));
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
		bind(RequestParserService.class).to(RequestParserServiceImpl.class);
		bind(HttpRequestService.class).to(HttpRequestServiceImpl.class);
		bind(HttpResponseService.class).to(HttpResponseServiceImpl.class);
		
		bind(String.class).annotatedWith(ServerDocRoot.class).toInstance(docRoot);
	    bind(Integer.class).annotatedWith(ServerPort.class).toInstance(port);
	    bind(Integer.class).annotatedWith(ServerBufferSize.class).toInstance(bufferSize);
	}
}
