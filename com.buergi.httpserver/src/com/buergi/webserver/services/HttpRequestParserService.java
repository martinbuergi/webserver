package com.buergi.webserver.services;

import com.buergi.webserver.http.HttpResponse;

/**
 * @author martinbuergi
 *
 * Parses the requestHeader and create the response
 *
 */
public interface HttpRequestParserService {
	
	public HttpResponse createResponse(String requestHeader);
}
