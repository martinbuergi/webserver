package com.buergi.webserver.http.services;

import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author martinbuergi
 *
 * Provides service to handle one connection
 * 
 */
public interface HttpWorkerService {
	
	public void handle(AsynchronousSocketChannel ch);
}
