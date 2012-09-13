package com.buergi.webserver.http.impl;

import java.util.Map;

import com.buergi.webserver.http.HttpRequest;
import com.buergi.webserver.http.HttpResponse;
import com.buergi.webserver.http.HttpStatusCode;
import com.buergi.webserver.services.HttpResponseService;
import com.buergi.webserver.services.impl.WebServerServiceImpl.ServerDocRoot;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.multibindings.Multibinder;

public class HttpRequest10Impl extends AbstractModule implements HttpRequest {

	@Inject private HttpResponseService httpResponseService;
	@Inject @ServerDocRoot String docRoot;

	public String getVersion(){
		return "HTTP/1.0";
	}
	
	public HttpResponse createResponse(String method, String path, Map<String, String> parameterMap) {
		// check httpMethod
		if (method == null)
			return httpResponseService.createErrorResponse(getVersion(), HttpStatusCode.BAD_REQUEST, parameterMap);

		if ("GET;HEAD".contains(method))
			return httpResponseService.createFileResponse(getVersion(), method, path, parameterMap); 

		return httpResponseService.createErrorResponse(getVersion(), HttpStatusCode.NOT_IMPLEMENTED, parameterMap);
	}

	@Override
	protected void configure() {
		Multibinder<HttpRequest> httpRequestBinder = Multibinder.newSetBinder(binder(), HttpRequest.class);
		httpRequestBinder.addBinding().to(HttpRequest10Impl.class);
	}
}
