package com.buergi.webserver.services.impl;

import java.io.File;
import java.util.Map;

import com.buergi.webserver.http.HttpResponse;
import com.buergi.webserver.http.HttpStatusCode;
import com.buergi.webserver.http.impl.HttpResponseErrorImpl;
import com.buergi.webserver.http.impl.HttpResponseFileImpl;
import com.buergi.webserver.services.HttpResponseService;
import com.buergi.webserver.services.impl.WebServerServiceImpl.ServerDocRoot;
import com.google.inject.Inject;

public class HttpResponseServiceImpl implements HttpResponseService {
	@Inject @ServerDocRoot String docRoot;
	
	public HttpResponse createErrorResponse(String version, HttpStatusCode statusCode, Map<String, String> parameterMap) {
		return HttpResponseErrorImpl.create(version, statusCode, parameterMap);
	}

	public HttpResponse createFileResponse(String version, String method, String path, Map<String, String> parameterMap) {
		if (path == null || path.contains("..")) 
			return HttpResponseErrorImpl.create(version, HttpStatusCode.BAD_REQUEST, parameterMap);
	
		FileObject fo = createPath(path);
		HttpStatusCode hsc = fo.getHttpStatusCode();
		
		if (hsc.equals(HttpStatusCode.OK))
			return HttpResponseFileImpl.create(version, method, hsc, fo.getPath(), parameterMap);
		
		if (hsc.equals(HttpStatusCode.MOVED_PERMANENTLY)){
			parameterMap.put("Location", fo.getPath());
			return HttpResponseErrorImpl.create(version, hsc, parameterMap);			
		}

		return HttpResponseErrorImpl.create(version, fo.getHttpStatusCode(), parameterMap);
	}
	
	private FileObject createPath(String requestedPath) {
		String absolutePath = docRoot.concat(requestedPath);
		
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
