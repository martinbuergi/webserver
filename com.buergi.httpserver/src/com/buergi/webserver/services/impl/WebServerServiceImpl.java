package com.buergi.webserver.services.impl;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.buergi.webserver.services.WebServerService;
import com.buergi.webserver.services.WorkerService;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;

public class WebServerServiceImpl implements WebServerService {

	/**
	 * Annotates the docRoot.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@BindingAnnotation
	public @interface ServerDocRoot {}
	
	/**
	 * Annotates the buffSize.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@BindingAnnotation
	public @interface ServerBufferSize {}
	
	/**
	 * Annotates the port.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@BindingAnnotation
	public @interface ServerPort {}
	
	
	private int port;
	private String docRoot;
	private int bufferSize;
	@Inject private WorkerService workerService;

	@Inject
	WebServerServiceImpl(@ServerDocRoot String docRoot, @ServerPort int port, @ServerBufferSize int bufferSize){
		this.port = port;
		this.docRoot = docRoot;
		this.bufferSize = bufferSize;		
	}

	public void start() {
		try {
			AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(50));
			final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(port));

			System.out.println("Server listening on port " + port);
			System.out.println("document root directory is " + docRoot);
			System.out.println("buffersize is " + bufferSize);
			
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
