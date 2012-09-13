package com.buergi.webserver.http;

import java.util.Map;


public interface HttpRequest {

	public String getVersion();
	public HttpResponse createResponse(String method, String path, Map<String, String> parameterMap);
}
