package com.buergi.webserver.services;

import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author martinbuergi
 *
 * Provides service to handle one connection
 * 
 */
public interface WorkerService {
	
	public void handle(AsynchronousSocketChannel ch);
}
