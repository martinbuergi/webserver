package com.buergi.webserver.http;


public interface HttpRequest {

	public HttpResponse createResponse(String docRoot);
	public String getMethod();
}
