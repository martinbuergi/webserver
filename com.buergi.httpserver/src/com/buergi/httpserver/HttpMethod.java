package com.buergi.httpserver;

public enum HttpMethod {
	GET, POST, HEAD;
	
	
	public static HttpMethod get(String s) {
		s = s.toUpperCase();
		
		if (s.equals("GET"))
			return HttpMethod.GET;
		else if (s.equals("POST"))
			return HttpMethod.POST;
		else if (s.equals("HEAD"))
			return HttpMethod.HEAD;
		
		return null;
	}
}
