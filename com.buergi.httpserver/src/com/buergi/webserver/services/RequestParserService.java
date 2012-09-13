package com.buergi.webserver.services;

import com.buergi.webserver.http.HttpResponse;

public interface RequestParserService {
	
	public HttpResponse createResponse(String requestHeader);
}
