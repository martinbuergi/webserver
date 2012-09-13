package com.buergi.webserver.http.impl;

import java.nio.ByteBuffer;
import java.util.Map;

import com.buergi.webserver.http.HttpResponse;
import com.buergi.webserver.http.HttpStatusCode;

public class HttpResponseErrorImpl extends HttpResponse {

	private String errorMessage;
	private int length;

	private HttpResponseErrorImpl(String version, HttpStatusCode httpStatusCode, Map<String, String> parameterMap) {
		if ("45".contains(httpStatusCode.toString().substring(0,1)))
			errorMessage = String.format("<html><header>Server error:</header>%s<body></body></html>", httpStatusCode);
		else
			errorMessage = "";
		
		length = errorMessage.length();
		
		setHeader(version, httpStatusCode.toString(), length, parameterMap);
	}

	public int readMessage(ByteBuffer byteBuffer, int pos) {
		if (pos >= length)
			return -1;

		String string = errorMessage.substring(pos);
		byteBuffer.put(string.getBytes());
		
		return byteBuffer.capacity() >= length ? length : byteBuffer.capacity();
	}
	
	public static HttpResponse create(String version, HttpStatusCode httpStatusCode, Map<String, String> parameterMap) {
		return new HttpResponseErrorImpl(version, httpStatusCode, parameterMap);
	}
}
