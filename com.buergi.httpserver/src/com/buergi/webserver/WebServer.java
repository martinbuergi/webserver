package com.buergi.webserver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.buergi.webserver.http.HttpContext;
import com.buergi.webserver.services.HttpRequestParserService;
import com.buergi.webserver.services.HttpResponseService;
import com.buergi.webserver.services.WebServerService;
import com.buergi.webserver.services.WorkerService;
import com.buergi.webserver.services.impl.HttpRequestParserServiceImpl;
import com.buergi.webserver.services.impl.HttpResponseServiceImpl;
import com.buergi.webserver.services.impl.WebServerServiceImpl;
import com.buergi.webserver.services.impl.WorkerServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;

public class WebServer extends AbstractModule {

	private HttpContext httpContext;
	
	public WebServer(HttpContext httpContext) {
		this.httpContext = httpContext;
	}

	/**
	 * Annotates HttpServerContext
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@BindingAnnotation
	public @interface HttpServerContext {}
		

	@Override
	protected void configure() {
		bind(WebServerService.class).to(WebServerServiceImpl.class);
		bind(WorkerService.class).to(WorkerServiceImpl.class);
		bind(HttpRequestParserService.class).to(HttpRequestParserServiceImpl.class);
		bind(HttpResponseService.class).to(HttpResponseServiceImpl.class);		
		
		bind(HttpContext.class).annotatedWith(HttpServerContext.class).toInstance(httpContext);
	}
}
