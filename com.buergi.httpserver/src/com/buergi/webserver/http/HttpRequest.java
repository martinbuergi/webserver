package com.buergi.webserver.http;

import java.util.Map;


/**
 * @author martinbuergi
 *
 * Interface to generate http response for a specific http version  
 * 
 */
public interface HttpRequest {

	public HttpResponse createResponse(String method, String path, Map<String, String> parameterMap);
}
