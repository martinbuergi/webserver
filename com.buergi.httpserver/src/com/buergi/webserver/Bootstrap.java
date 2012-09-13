package com.buergi.webserver;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.buergi.webserver.http.HttpContext;
import com.buergi.webserver.services.WebServerService;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Bootstrap {
	public static void main(String[] args) throws org.apache.commons.configuration.ConfigurationException {
		
		Configuration configuration = new PropertiesConfiguration(WebServer.class.getResource("properties.config"));
		HttpContext httpContext = new HttpContext(configuration);
		
		Injector injector = Guice.createInjector(new WebServer(httpContext));

		WebServerService httpServer = injector.getInstance(WebServerService.class);
		httpServer.start();				
	}
}
