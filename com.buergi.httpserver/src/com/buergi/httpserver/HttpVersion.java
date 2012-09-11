package com.buergi.httpserver;

public enum HttpVersion {
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

	public static HttpVersion get(String s) throws HttpException {
		for (HttpVersion httpVersion : HttpVersion.values()) {
			if (httpVersion.getVersion().equals(s))
				return httpVersion;
		}

		throw new HttpException(HttpStatusCode.NOT_IMPLEMENTED);
	}

}
