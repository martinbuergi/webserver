package com.buergi.webserver.services.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.buergi.webserver.WebServer.HttpServerContext;
import com.buergi.webserver.http.HttpContext;
import com.buergi.webserver.http.HttpResponse;
import com.buergi.webserver.services.HttpRequestParserService;
import com.buergi.webserver.services.WorkerService;
import com.google.inject.Inject;

/**
 * @author martinbuergi
 *
 * Default implementation
 * 
 */
public class WorkerServiceImpl implements WorkerService {
	@Inject @HttpServerContext private HttpContext httpContext; 
	@Inject private HttpRequestParserService requestParserService;

	public void handle(AsynchronousSocketChannel ch) {
		try{
			// if requestHeader is empty, close channel
			String requestHeader = readChannel(ch);
			if (requestHeader.isEmpty()) {
				ch.close();
				return;
			}

			HttpResponse httpResponse = requestParserService.createResponse(requestHeader);

			// Write header
			ch.write(ByteBuffer.wrap(httpResponse.getHeader().getBytes())).get();
			
			// Write message
			writeMessage(httpResponse, ch);

			ch.close();
		} catch (IOException e) {
			System.err.println("I/O error: " + e.getMessage());
		} catch (InterruptedException e) {
			System.err.println("Interrupted: " + e.getMessage());
		} catch (ExecutionException e) {
			System.err.println("Excecution error: " + e.getMessage());
		}
	}

	private String readChannel(AsynchronousSocketChannel ch) throws InterruptedException, ExecutionException{
		ByteBuffer readBuffer = ByteBuffer.allocate(httpContext.getBufferSize());
		
		StringBuffer sb = new StringBuffer();
		
		int x;
		while ((x = ch.read(readBuffer).get()) != -1) {
			readBuffer.flip();
			sb.append(new String(readBuffer.array()));

			if (x < httpContext.getBufferSize())
				break;
		}
		
		return sb.toString();
	}

	
	private void writeMessage(HttpResponse httpResponse, AsynchronousSocketChannel ch) throws InterruptedException, ExecutionException, IOException {
		int bufferSize = httpContext.getBufferSize();
		
		ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
		int pos = 0;

		while (httpResponse.readMessage(readBuffer, pos) >= 0) {
			readBuffer.flip();
			Future<Integer> future = ch.write(readBuffer);
			while (!future.isDone()) {};
			readBuffer.clear();

			pos = pos + bufferSize;
		}
	}
}
