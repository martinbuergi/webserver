package com.buergi.webserver.http;

import org.apache.commons.configuration.Configuration;

/**
 * @author martinbuergi
 *
 * Contains the configuration
 * 
 */
public class HttpContext {

	private String docRoot;
	private int port;
	private int bufferSize;
	private int threads;

	public HttpContext(Configuration configuration) {
		
		docRoot = configuration.containsKey("root") ? configuration.getString("root") : "";
		if (docRoot.length() == 0)
			docRoot = System.getProperty("user.home").concat("/html");
			
		port = configuration.containsKey("port") ? configuration.getInt("port") : 8080;
		bufferSize = configuration.containsKey("buffer") ? configuration.getInt("buffer") : 2048;
		threads = configuration.containsKey("threads") ? configuration.getInt("threads") : 50;
	}
	
	
	public String getDocRoot() {
		return docRoot;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}


	public int getThreads() {
		return threads;
	}
}
