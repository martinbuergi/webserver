package com.buergi.webserver.services;

import java.util.Map;

import com.buergi.webserver.http.HttpResponse;
import com.buergi.webserver.http.HttpStatusCode;


/**
 * @author martinbuergi
 *
 * Provides different response types
 * 
 */
public interface HttpResponseService {

	public HttpResponse createFileResponse(String version, String method, String path, Map<String, String> parameterMap);
	public HttpResponse createErrorResponse(String version, HttpStatusCode httpStatusCode, Map<String, String> parameterMap);

}
