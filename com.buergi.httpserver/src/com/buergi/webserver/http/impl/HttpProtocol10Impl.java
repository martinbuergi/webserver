package com.buergi.webserver.http.impl;

import java.util.Map;

import com.buergi.webserver.http.HttpRequest;
import com.buergi.webserver.http.HttpResponse;
import com.buergi.webserver.http.HttpStatusCode;
import com.buergi.webserver.services.HttpResponseService;

public class HttpProtocol10Impl implements HttpRequest{

	private String version;
	private String method;
	private Map<String, String> parameterMap;
	private String path;
	private HttpResponseService httpResponseService;
	
	public HttpProtocol10Impl(String version, String method, String path, Map<String, String> parameterMap, HttpResponseService httpResponseService) {
		this.version = version;
		this.method = method.toUpperCase();
		this.path = path;
		this.parameterMap = parameterMap;
		this.httpResponseService = httpResponseService;
	}

	public HttpResponse createResponse(String docRoot) {
		// check httpMethod
		if (method == null)
			return httpResponseService.createErrorResponse(version, HttpStatusCode.BAD_REQUEST, parameterMap);

		if ("GET;HEAD".contains(method))
			return httpResponseService.createFileResponse(version, method, path, parameterMap); 

		return httpResponseService.createErrorResponse(version, HttpStatusCode.NOT_IMPLEMENTED, parameterMap);
	}
	
	public String getMethod() {
		return method;
	}
}
