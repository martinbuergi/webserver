package com.buergi.webserver.services.impl;

import java.util.Map;

import com.buergi.webserver.http.HttpRequest;
import com.buergi.webserver.http.impl.HttpProtocol10Impl;
import com.buergi.webserver.http.impl.HttpProtocol11Impl;
import com.buergi.webserver.services.HttpRequestService;
import com.buergi.webserver.services.HttpResponseService;
import com.google.inject.Inject;

public class HttpRequestServiceImpl implements HttpRequestService {
	@Inject private HttpResponseService httpResponseService;
	
	public HttpRequest createRequestInstance(String version, String method, String path, Map<String, String> parameterMap){
		if (version.equals("HTTP/1.1"))
			return new HttpProtocol11Impl(version, method, path, parameterMap, httpResponseService);
		
		if (version.equals("HTTP/1.0") || version.equals("HTTP/0.9"))
			return new HttpProtocol10Impl(version, method, path, parameterMap, httpResponseService);
		
		return null;
	}
}
