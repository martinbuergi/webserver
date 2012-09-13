package com.buergi.webserver.services.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.buergi.webserver.http.HttpRequest;
import com.buergi.webserver.http.HttpResponse;
import com.buergi.webserver.services.RequestParserService;
import com.buergi.webserver.services.WorkerService;
import com.buergi.webserver.services.impl.WebServerServiceImpl.ServerBufferSize;
import com.buergi.webserver.services.impl.WebServerServiceImpl.ServerDocRoot;
import com.google.inject.Inject;

public class WorkerServiceImpl implements WorkerService {
	private String docRoot;
	private Integer bufferSize;
	@Inject RequestParserService requestParserService; 

	@Inject
	WorkerServiceImpl(@ServerDocRoot String docRoot, @ServerBufferSize Integer bufferSize) {
		this.docRoot = docRoot;
		this.bufferSize = bufferSize;
	};

	
	public void handle(AsynchronousSocketChannel ch) {
		try{
			// if requestHeader is empty, close channel
			String requestHeader = readChannel(ch);
			if (requestHeader.isEmpty()) {
				ch.close();
				return;
			}

			HttpRequest request = requestParserService.createRequest(requestHeader);
			HttpResponse response = new HttpResponse(request, docRoot);

			// Write header
			ch.write(ByteBuffer.wrap(response.getHeader().getBytes())).get();
			
			// Write message
			writeMessage(response, ch);

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
		ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
		
		StringBuffer sb = new StringBuffer();
		
		int x;
		while ((x = ch.read(readBuffer).get()) != -1) {
			readBuffer.flip();
			sb.append(new String(readBuffer.array()));

			if (x < bufferSize)
				break;
		}
		
		return sb.toString();
	}

	
	private void writeMessage(HttpResponse httpResponse, AsynchronousSocketChannel ch) throws InterruptedException, ExecutionException, IOException {
		// error message?
		if (httpResponse.getErrorMessage() != null) {
			ch.write(ByteBuffer.wrap(httpResponse.getErrorMessage().getBytes()));
			return;
		}

		AsynchronousFileChannel fCh = httpResponse.getFileChannel();
		if (fCh == null)
			return;
		
		ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
		int pos = 0;

		while (fCh.read(readBuffer, pos).get() >= 0) {
			readBuffer.flip();
			Future<Integer> future = ch.write(readBuffer);
			while (!future.isDone()) {};
			readBuffer.clear();

			pos = pos + bufferSize;
		}
		
		fCh.close();
	}
}
