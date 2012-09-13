package com.buergi.webserver.services;

import com.buergi.webserver.http.HttpRequest;

public interface RequestParserService {
	
	public HttpRequest createRequest(String requestHeader);
}
