package com.buergi.webserver.services.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.buergi.webserver.http.HttpRequest;
import com.buergi.webserver.http.HttpResponse;
import com.buergi.webserver.http.HttpStatusCode;
import com.buergi.webserver.services.HttpRequestParserService;
import com.buergi.webserver.services.HttpResponseService;
import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class HttpRequestParserServiceImpl implements HttpRequestParserService {
	private static String DEFAULT_ENCODING = "UTF-8";

	@Inject Injector injector;
	@Inject private HttpResponseService httpResponseService;

	public HttpResponse createResponse(String requestHeader){
		String lines[] = requestHeader.toString().split("\\r?\\n");
		String[] initialLines = lines[0].split(" ", 3);
		
		String path = initialLines[1];

		Map<String, String> parameterMap = new HashMap<String, String>();

		// get request parameters
		addParameterToMap(Arrays.copyOfRange(lines, 1, lines.length), ":", parameterMap);

		// get parameters in URL
		int index = path.indexOf("?");
		if (index > -1){
			String[] params = path.substring(index+1).split("&");
			addParameterToMap(params, "=", parameterMap);
			path = path.substring(0,index);
		}
		
		try {
			path = URLDecoder.decode(path, DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			System.err.println("Unsupported Encoding error: " + e.getMessage());
		}

		
		HttpRequest httpRequest;
		try{
			httpRequest = injector.getInstance(Key.get(HttpRequest.class, Names.named(initialLines[2])));
		} catch (ConfigurationException e){
			return httpResponseService.createErrorResponse(initialLines[2], HttpStatusCode.BAD_REQUEST, parameterMap);			
		}
		
		return httpRequest.createResponse(initialLines[0], path, parameterMap); 
	}

	private void addParameterToMap(String[] parameters, String delimiter, Map<String, String> parameterMap) {
		for (String parameter : parameters){
			parameter = parameter.trim();
			if (parameter.length() == 0)
				continue;
			
			int c = parameter.indexOf(delimiter);
			
			String key = c == -1 ? parameter : parameter.substring(0, c);
			String value = c == -1 ? "" : parameter.substring(c+1).trim();
			
			try {
				key = URLDecoder.decode(key, DEFAULT_ENCODING);
				value = URLDecoder.decode(value, DEFAULT_ENCODING);
			} catch (UnsupportedEncodingException e) {}
			
			parameterMap.put(key, value);
		}
	}
}
