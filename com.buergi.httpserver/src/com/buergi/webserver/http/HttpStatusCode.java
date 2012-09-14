package com.buergi.webserver.http;

/**
 * @author martinbuergi
 *
 * http://www.w3.org/Protocols/HTTP/1.0/spec.html#Status-Codes
 *
 */
public enum HttpStatusCode {
	OK("200 OK"),
	CREATED("201 Created"),
	ACCEPTED("202 Accepted"),
	NO_CONTENT("204 No Content"),
	MOVED_PERMANENTLY("301 Moved Permanently"),
	MOVED_TEMPORARILY("302 Moved Temporarily"),
	NOT_MODIFIED("304 Not Modified"),
	BAD_REQUEST("400 Bad Request"),
	UNAUTHORIZED("401 Unauthorized"),
	FORBIDDEN("403 Forbidden"),
	NOT_FOUND("404 Not Found"),
	INTERNAL_SERVER_ERROR("500 Internal Server Error"),
	NOT_IMPLEMENTED("501 Not Implemented"),
	BAD_GATEWAY("502 Bad Gateway"),
	SERVICE_UNAVAILABLE("503 Service Unavailable");

	private String message;

	HttpStatusCode(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
}
