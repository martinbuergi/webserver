package com.buergi.webserver.http;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public abstract class HttpResponse {

	private String header;
	
	public String getHeader() {
		return header;
	}
	
	public int readMessage(ByteBuffer byteBuffer, int pos) {
		return -1;
	}

	protected void setHeader(String version, String statusCode, long contentLength, Map<String, String> parameterMap) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%s %s\n", version, statusCode));
		sb.append(String.format("Date: %s\nContent-Length: %s\n", new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(new Date()), contentLength));
		
		if (parameterMap != null){
			for (String key : parameterMap.keySet())
				sb.append(String.format("%s:%s\n", key, parameterMap.get(key)));			
		}
		
		header = sb.append("\r\n").toString();
		
	}
}
