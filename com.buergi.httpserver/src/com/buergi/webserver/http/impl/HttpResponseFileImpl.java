package com.buergi.webserver.http.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.tika.Tika;

import com.buergi.webserver.http.HttpResponse;
import com.buergi.webserver.http.HttpStatusCode;

public class HttpResponseFileImpl extends HttpResponse {

	private AsynchronousFileChannel fileChannel;

	private HttpResponseFileImpl(String version, HttpStatusCode hsc, long contentLength, Map<String, String> parameterMap, AsynchronousFileChannel fileChannel) {
		this.fileChannel = fileChannel;
		setHeader(version, hsc.toString(), contentLength, parameterMap);

	}
	public static HttpResponse create(String version, String method, HttpStatusCode hsc, String absolutePath, Map<String, String> parameterMap) {
		Path path = Paths.get(absolutePath);
		
		parameterMap.put("Content-Type", new Tika().detect(path.getFileName().toString()));
		AsynchronousFileChannel fileChannel;
		long contentLength = 0;
		
		try {
			fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
			contentLength = fileChannel.size();
		} catch (IOException e) {
			return HttpResponseErrorImpl.create(version, HttpStatusCode.INTERNAL_SERVER_ERROR, parameterMap);
		}
		
		if (method.equals("HEAD"))
			fileChannel = null;
		
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
