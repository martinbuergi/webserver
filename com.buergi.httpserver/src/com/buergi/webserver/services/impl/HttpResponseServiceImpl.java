package com.buergi.webserver.services.impl;

import java.io.File;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import org.apache.tika.Tika;

import com.buergi.webserver.http.HttpContext;
import com.buergi.webserver.http.HttpResponse;
import com.buergi.webserver.http.HttpStatusCode;
import com.buergi.webserver.http.HttpWebServer.HttpServerContext;
import com.buergi.webserver.http.impl.HttpResponseErrorImpl;
import com.buergi.webserver.http.impl.HttpResponseFileImpl;
import com.buergi.webserver.services.HttpResponseService;
import com.google.inject.Inject;

/**
 * @author martinbuergi
 *
 * Handles a http response
 * 
 */
public class HttpResponseServiceImpl implements HttpResponseService {
	@Inject @HttpServerContext private HttpContext httpContext; 
	
	public HttpResponse createErrorResponse(String version, HttpStatusCode statusCode, Map<String, String> parameterMap) {
		return HttpResponseErrorImpl.create(version, statusCode, parameterMap);
	}

	public HttpResponse createFileResponse(String version, String method, String path, Map<String, String> parameterMap) {
		if (path == null || path.contains("..")) 
			return HttpResponseErrorImpl.create(version, HttpStatusCode.BAD_REQUEST, parameterMap);
	
		FileObject fo = createPath(path);
		HttpStatusCode hsc = fo.getHttpStatusCode();
		
		if (hsc.equals(HttpStatusCode.MOVED_PERMANENTLY)){
			parameterMap.put("Location", fo.getPath());
			return HttpResponseErrorImpl.create(version, hsc, parameterMap);			
		}

		if ("45".contains(hsc.toString().substring(0, 1)))
			return HttpResponseErrorImpl.create(version, fo.getHttpStatusCode(), parameterMap);

		
		// file is ready
		Path pathObj = Paths.get(fo.getPath());
		
		parameterMap.put("Content-Type", new Tika().detect(pathObj.getFileName().toString()));
		AsynchronousFileChannel fileChannel;
		long contentLength = 0;
		
		try {
			fileChannel = AsynchronousFileChannel.open(pathObj, StandardOpenOption.READ);
			contentLength = fileChannel.size();
		} catch (IOException e) {
			return HttpResponseErrorImpl.create(version, HttpStatusCode.INTERNAL_SERVER_ERROR, parameterMap);
		}
		
		if (method.equals("HEAD"))
			fileChannel = null;
		
		return HttpResponseFileImpl.create(version, fo.getHttpStatusCode(), fileChannel, contentLength, parameterMap);

	}
	
	private FileObject createPath(String requestedPath) {
		String absolutePath = httpContext.getDocRoot().concat(requestedPath);
		
		// does file exist??
		File file = new File(absolutePath);
		if (file.exists()) {
			if (file.isFile())
				return new FileObject(HttpStatusCode.OK, absolutePath);
			
			// if directory doesn't end with '/', add it to url
			if (!requestedPath.endsWith("/")) 
				return new FileObject(HttpStatusCode.MOVED_PERMANENTLY, requestedPath.concat("/"));
			
			// if no file is given, try index.html
			return createPath(requestedPath.concat("index.html"));
		}
		
		return new FileObject(HttpStatusCode.NOT_FOUND);
	}

	
	
	private class FileObject {
		
		private HttpStatusCode httpStatusCode;
		private String path;

		public FileObject(HttpStatusCode httpStatusCode){
			this(httpStatusCode, null);
		}
		
		public FileObject(HttpStatusCode httpStatusCode, String path){
			this.httpStatusCode = httpStatusCode;
			this.path = path;
		}

		public HttpStatusCode getHttpStatusCode() {
			return httpStatusCode;
		}

		public String getPath() {
			return path;
		}
	}
	
}
