package com.buergi.webserver.http.impl;

import java.util.Map;

import com.buergi.webserver.services.HttpResponseService;


public class HttpProtocol11Impl extends HttpProtocol10Impl{

	// To be defined
	public HttpProtocol11Impl(String version, String method, String path, Map<String, String> parameterMap, HttpResponseService httpResponseService) {
		super(version, method, path, parameterMap, httpResponseService);
	}

}
