package com.buergi.httpserver;

public class HttpException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HttpStatusCode httpStatusCode;
	
	public HttpException(HttpStatusCode httpStatusCode){
		this.httpStatusCode = httpStatusCode;
	}
	
	@Override
	public String getMessage() {
		return String.format("%s", httpStatusCode.toString());
	}
}
