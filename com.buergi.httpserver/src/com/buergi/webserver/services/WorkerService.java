package com.buergi.webserver.services;

import java.nio.channels.AsynchronousSocketChannel;

public interface WorkerService {
	
	public void handle(AsynchronousSocketChannel ch);
}
