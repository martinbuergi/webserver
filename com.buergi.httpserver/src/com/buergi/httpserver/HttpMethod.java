package com.buergi.httpserver;

public enum HttpMethod {
	GET, POST, HEAD;
	
	
	public static HttpMethod get(String s) throws HttpException {
		if (s.equalsIgnoreCase("GET"))
			return HttpMethod.GET;
		else if (s.equalsIgnoreCase("POST"))
			return HttpMethod.POST;
		else if (s.equalsIgnoreCase("HEAD"))
			return HttpMethod.HEAD;
		
		throw new HttpException(HttpStatusCode.NOT_IMPLEMENTED);
	}
}
