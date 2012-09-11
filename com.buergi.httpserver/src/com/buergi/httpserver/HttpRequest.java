package com.buergi.httpserver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HttpRequest {
	private HttpMethod httpMethod;
	private HttpVersion httpVersion;
	private String path;
	private String absolutePath;
	public Map<String, String> parameterMap;
	
	private HttpRequest(){
	};
	
	public HttpMethod getHTTPMethod(){
		return httpMethod;
	}
	
	public HttpVersion getHTTPVersion(){
		return httpVersion;
	}
	
	public String getPath(){
		return path;
	}
	
	public String getAbsolutePath() {
		return absolutePath;
	}

	public static class Builder{
		private static int bufferSize = 1024;

		private Builder(){};
		
		public static HttpRequest build(AsynchronousSocketChannel ch, String root){
			HttpRequest request = new HttpRequest();
			
			ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
			
			StringBuffer sb = new StringBuffer();
			
			try {
				int x;
				while ((x = ch.read(readBuffer).get(10, TimeUnit.SECONDS)) != -1) {
					sb.append(new String(readBuffer.array()));
					readBuffer.rewind();
					if (x < bufferSize)
						break;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
							
			String lines[] = sb.toString().split("\\r?\\n");
//				lines = URLDecoder.decode(sb.toString(), "UTF-8").split("\\r?\\n");
			String[] initialLine = lines[0].split(" ", 3);
			try {
				request.httpMethod = HttpMethod.get(initialLine[0]);
				request.httpVersion = HttpVersion.get(initialLine[2]);
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Map<String, String> parameterMap = new HashMap<String, String>();

			request.path = initialLine[1];
			int index = request.path.indexOf("?");
			if (index > -1){
				String[] params = request.path.substring(index+1).split("&");
				for (String parameter : params)
					addParameterToMap(parameter, "=", parameterMap);
				
					request.path = request.path.substring(0,index);
			}
			
			try {
				request.path = URLDecoder.decode(request.path, "UTF-8");
				request.absolutePath = root.concat(request.path);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			for (int x = 1; x < lines.length; x++){
				String line = lines[x].trim();
				if (line.length() == 0)
					continue;
				
				addParameterToMap(line, ":", parameterMap);
			}
			request.parameterMap = parameterMap;

			return request;
		}

		private static void addParameterToMap(String parameter, String delimiter, Map<String, String> parameterMap) {
			int c = parameter.indexOf(delimiter);
			String key = parameter.substring(0, c);
			String value = parameter.substring(c+1).trim();
			
			try {
				key = URLDecoder.decode(key, "UTF-8");
				value = URLDecoder.decode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) {}
			
			parameterMap.put(key, value);					
		}
	}
}
