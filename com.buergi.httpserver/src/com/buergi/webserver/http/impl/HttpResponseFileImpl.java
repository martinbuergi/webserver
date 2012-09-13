package com.buergi.webserver.http.impl;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.buergi.webserver.http.HttpResponse;
import com.buergi.webserver.http.HttpStatusCode;

public class HttpResponseFileImpl extends HttpResponse {

	private AsynchronousFileChannel fileChannel;

	private HttpResponseFileImpl(String version, HttpStatusCode hsc, long contentLength, Map<String, String> parameterMap, AsynchronousFileChannel fileChannel) {
		this.fileChannel = fileChannel;
		setHeader(version, hsc.toString(), contentLength, parameterMap);

	}
	public static HttpResponse create(String version, HttpStatusCode hsc, AsynchronousFileChannel fileChannel, long contentLength, Map<String, String> parameterMap) {
		return new HttpResponseFileImpl(version, hsc, contentLength, parameterMap, fileChannel);
	}

	public int readMessage(ByteBuffer byteBuffer, int pos) {
		if (fileChannel == null)
			return super.readMessage(byteBuffer, pos);
		
		try {
			return fileChannel.read(byteBuffer, pos).get();
		} catch (InterruptedException e) {
			System.err.println("Interrupted Error: " + e.getMessage());
		} catch (ExecutionException e) {
			System.err.println("Execution Error: " + e.getMessage());
		}
		
		return -1;
	}

}
