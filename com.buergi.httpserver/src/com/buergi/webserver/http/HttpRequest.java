package com.buergi.webserver.http;

import java.util.Map;

public class HttpRequest {
	private String httpMethod;
	private String httpVersion;
	private String path;
	public Map<String, String> parameterMap;
	
	public HttpRequest(String httpMethod, String httpVersion, String path, Map<String, String> parameterMap){
		this.httpMethod = httpMethod;
		this.httpVersion = httpVersion;
		this.path = path;
		this.parameterMap = parameterMap;
	};
	
	public String getHTTPMethod() {
		return httpMethod;
	}
	
	public String getHTTPVersion() {
		return httpVersion;
	}
		
	public String getPath() {
		return path;
	}
	
	public Map<String, String> getParameterMap() {
		return parameterMap;
	}	
	

}
