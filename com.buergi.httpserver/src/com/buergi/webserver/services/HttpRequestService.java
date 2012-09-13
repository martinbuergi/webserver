package com.buergi.webserver.services;

import java.util.Map;

import com.buergi.webserver.http.HttpRequest;

public interface HttpRequestService {
	
	public HttpRequest createRequestInstance(String version, String method, String path, Map<String, String> parameterMap);
}
