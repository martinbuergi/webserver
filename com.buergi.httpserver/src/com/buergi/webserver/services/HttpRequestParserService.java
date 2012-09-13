package com.buergi.webserver.services;

import com.buergi.webserver.http.HttpResponse;

public interface HttpRequestParserService {
	
	public HttpResponse createResponse(String requestHeader);
}
