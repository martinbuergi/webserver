package com.buergi.webserver.services.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.buergi.webserver.WebServer.HttpServerContext;
import com.buergi.webserver.http.HttpContext;
import com.buergi.webserver.services.WebServerService;
import com.buergi.webserver.services.WorkerService;
import com.google.inject.Inject;

public class WebServerServiceImpl implements WebServerService {
	
	@Inject @HttpServerContext private HttpContext httpContext; 
	@Inject private WorkerService workerService;

	public void start() {
		try {
			AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(50));
			final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(httpContext.getPort()));

			System.out.println("Server listening on port " + httpContext.getPort());
			System.out.println("document root directory is " + httpContext.getDocRoot());
			
			server.accept("Client connection",
				new CompletionHandler<AsynchronousSocketChannel, Object>() {
					public void completed(AsynchronousSocketChannel ch, Object att) {
							System.out.println("Accepted a connection");
							server.accept(null, this);
							workerService.handle(ch);							
					}

					public void failed(Throwable exc, Object att) {
						System.err.println("Failed to accept a connection");
					}
				}
			);

			group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

		} catch (IOException e) {
			System.err.println("I/O error: " + e.getMessage());
		} catch (InterruptedException e) {
			System.err.println("Interrupted: " + e.getMessage());
		}
	}

}
