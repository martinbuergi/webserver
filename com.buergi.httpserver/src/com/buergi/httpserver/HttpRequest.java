package com.buergi.httpserver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author martinbuergi
 *
 * Splits request string into lines.
 * First line contains "method url version"
 * Optional other lines contain parameters
 *  
 * Checks for parameters in url
 * 
 */

public class HttpRequest {
	private static String DEFAULT_ENCODING = "UTF-8";
	private String[] initialLines;
	private String path;
	public Map<String, String> parameterMap;
	
	public HttpRequest(String requestHeader){
		String lines[] = requestHeader.toString().split("\\r?\\n");
		initialLines = lines[0].split(" ", 3);
		
		path = initialLines[1];

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
	};
	
	public String getHTTPMethod() {
		return initialLines[0];
	}
	
	public String getHTTPVersion() {
		return initialLines[2];
	}
		
	public String getPath() {
		return path;
	}
	
	public Map<String, String> getParameterMap() {
		return parameterMap;
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
