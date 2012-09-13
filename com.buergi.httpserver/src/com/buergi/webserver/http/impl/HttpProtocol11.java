package com.buergi.webserver.http.impl;

import java.util.Map;

import com.buergi.webserver.services.HttpResponseService;


public class HttpProtocol11 extends HttpProtocol10{

	// To be defined
	public HttpProtocol11(String version, String method, String path, Map<String, String> parameterMap, HttpResponseService httpResponseService) {
		super(version, method, path, parameterMap, httpResponseService);
	}

}
