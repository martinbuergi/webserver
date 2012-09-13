package com.buergi.webserver.http;

import java.util.Map;

public class HttpRequest {
	private HttpMethod httpMethod;
	private HttpVersion httpVersion;
	private String path;
	public Map<String, String> parameterMap;
	
	public HttpRequest(HttpMethod httpMethod, HttpVersion httpVersion, String path, Map<String, String> parameterMap){
		this.httpMethod = httpMethod;
		this.httpVersion = httpVersion;
		this.path = path;
		this.parameterMap = parameterMap;
	};
	
	public HttpMethod getHTTPMethod() {
		return httpMethod;
	}
	
	public HttpVersion getHTTPVersion() {
		return httpVersion;
	}
		
	public String getPath() {
		return path;
	}
	
	public Map<String, String> getParameterMap() {
		return parameterMap;
	}	
	

}
