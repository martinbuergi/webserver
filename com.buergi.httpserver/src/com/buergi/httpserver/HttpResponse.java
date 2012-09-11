package com.buergi.httpserver;

import java.io.File;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.Tika;

public class HttpResponse {
	private HttpVersion httpVersion;
	private HttpStatusCode httpStatusCode;
	private AsynchronousFileChannel fileChannel;
	private Map<String, String> parameterMap;
	private String errorMessage;
	private long contentLength;
	

	public HttpResponse(HttpRequest request, String docRoot) {
		parameterMap = new HashMap<String, String>();
		httpStatusCode = HttpStatusCode.OK;
		errorMessage = null;
		contentLength = 0;
		
		// check httpVersion
		httpVersion = HttpVersion.get(request.getHTTPVersion());
		if (httpVersion == null) {
			httpVersion = HttpVersion.HTTP10;
			httpStatusCode = HttpStatusCode.BAD_REQUEST;
			return;
		}
		
		// check httpMethod
		HttpMethod httpMethod = HttpMethod.get(request.getHTTPMethod());
		if (httpMethod == null) {
			httpStatusCode = HttpStatusCode.NOT_IMPLEMENTED;
			return;
		}

		// get message
		Path path = evaluatePath(docRoot, request.getPath());	
		if (path == null) {
			if ("45".contains(httpStatusCode.toString().substring(0, 1))) {
				errorMessage = String.format("<html><header>Server error:</header>%s<body></body></html>", httpStatusCode);
				contentLength = errorMessage.length();
			}

			return;
		}
		
		parameterMap.put("Content-Type", new Tika().detect(path.getFileName().toString()));

		try {
			fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
			contentLength = fileChannel.size();
		} catch (IOException e) {
			httpStatusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
		}
		
		if (httpMethod.equals(HttpMethod.HEAD))
			fileChannel = null;
		

		
	};
	
	public String getHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%s %s\n", httpVersion.getVersion(), httpStatusCode.toString()));
		sb.append(String.format("Date: %s\nContent-Length: %s\n", new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(new Date()), contentLength));
		
		for (String key : parameterMap.keySet())
			sb.append(String.format("%s:%s\n", key, parameterMap.get(key)));
		
		return sb.append("\r\n").toString();
	}

	public AsynchronousFileChannel getFileChannel() {
		return fileChannel;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	private Path evaluatePath(String docRoot, String requestedPath) {
		if (requestedPath == null || requestedPath.contains("..")) {
			httpStatusCode = HttpStatusCode.BAD_REQUEST;
			return null;
		}
		
		String absolutePath = docRoot.concat(requestedPath);
		
		// does file exist??
		Path path = Paths.get(absolutePath);
		File file = path.toFile();
		if (file.exists()) {
			if (file.isFile())
				return path;
			
			// if directory doesn't end with '/', add it to url
			if (!requestedPath.endsWith("/")) {
				httpStatusCode = HttpStatusCode.MOVED_PERMANENTLY;
				parameterMap.put("Location", requestedPath.concat("/"));
				
				return null;
			}
			
			// if no file is given, try index.html
			return evaluatePath(docRoot, requestedPath.concat("index.html"));
		}
		
		httpStatusCode = HttpStatusCode.NOT_FOUND;
		return null;
	}
	

	private enum HttpVersion {
		HTTP09("HTTP/0.9"),
		HTTP10("HTTP/1.0"),
		HTTP11("HTTP/1.1");

		private String version;

		HttpVersion(String version) {
			this.version = version;
		}

		public String getVersion() {
			return version;
		}

		public static HttpVersion get(String s) {
			for (HttpVersion httpVersion : HttpVersion.values()) {
				if (httpVersion.getVersion().equals(s))
					return httpVersion;
			}

			return null;
		}
	}
	
	private enum HttpMethod {
		GET, POST, HEAD;
		
		public static HttpMethod get(String s) {
			s = s.toUpperCase();
			
			if (s.equals("GET"))
				return HttpMethod.GET;
			else if (s.equals("POST"))
				return HttpMethod.POST;
			else if (s.equals("HEAD"))
				return HttpMethod.HEAD;
			
			return null;
		}
	}	
}
