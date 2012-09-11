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
	private String contentType;
	private AsynchronousFileChannel fileChannel;
	private long contentLength = 0;
	private Map<String, String> parameterMap;
	
	private String path;
	

	private HttpResponse(){};
	
	public String getHeader(){
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%s %s\r\n", httpVersion.getVersion(), httpStatusCode.toString()));
		sb.append(String.format("Date: %s\nContent-Type: %s\nContent-Length: %s\n", new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(new Date()), contentType, contentLength));

		for (String key : parameterMap.keySet())
			sb.append(String.format("%s:%s", key, parameterMap.get(key)));
		
		sb.append("\r\n");
		
		return sb.toString();
	}

	public AsynchronousFileChannel getFileChannel(){
		return fileChannel;
	}
	
	public static class Builder{
		private HttpRequest request;
		private Map<String, String> parameterMap = new HashMap<String, String>();
		private HttpStatusCode httpStatusCode = null;
		private String contentType = null;
		private Long contentLength = 0l;
		private AsynchronousFileChannel fileChannel;

		private Builder(HttpRequest request){
			this.request = request;
		};
		
		public static Builder create(HttpRequest request){
			return new Builder(request);
		}
		
		public HttpResponse build() {
			HttpResponse response = new HttpResponse();
			response.parameterMap = parameterMap;
			response.httpVersion = request.getHTTPVersion();

			response.path = request.getAbsolutePath();
			addFileChannel(request.getAbsolutePath());
			
			response.httpStatusCode = httpStatusCode == null ? HttpStatusCode.OK : httpStatusCode;
			response.contentType = contentType;
			response.contentLength = contentLength;
			response.fileChannel = fileChannel;

			return response;
		}

		private void addFileChannel(String filePath) {
			Path path = evaluatePath(filePath);	
			
			if (path == null && httpStatusCode == null){
				httpStatusCode = HttpStatusCode.NOT_FOUND;
				path = Paths.get("html/404.html");
			}
			
			if (path == null)
				return;

			this.contentType = new Tika().detect(path.getFileName().toString());

			try {
				this.fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
				this.contentLength = this.fileChannel.size();
			} catch (IOException e) {
				httpStatusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
			}
		}

		private Path evaluatePath(String requestedPath) {
			if (requestedPath == null || requestedPath.contains("..")){
				this.httpStatusCode = HttpStatusCode.BAD_REQUEST;
				return null;
			}
			
			// Default file path
			Path path = Paths.get(requestedPath);
			File file = path.toFile();
			if (file.exists()){
				if (file.isFile())
					return path;
				
				if (requestedPath.endsWith("/"))
					return evaluatePath(requestedPath.concat("index.html"));

				this.httpStatusCode = HttpStatusCode.MOVED_PERMANENTLY;
				this.parameterMap.put("Location", request.getPath().concat("/"));

				return null;
			}
			
			// Ending .htm / .html: file exists for other ending?
			if (requestedPath.endsWith(".htm") || requestedPath.endsWith(".html")){
				requestedPath = requestedPath.endsWith("m") ? requestedPath.concat("l") : requestedPath.substring(0, requestedPath.length()-1);
				if (new File(requestedPath).exists())
					return evaluatePath(requestedPath);
				else
					return null;	
			}
			
			return null;
		}
	}

	public String getFilename() {
		return path;
	}
}
