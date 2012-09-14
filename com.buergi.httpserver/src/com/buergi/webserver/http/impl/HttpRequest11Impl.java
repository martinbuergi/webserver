package com.buergi.webserver.http.impl;

import com.buergi.webserver.http.HttpRequest;
import com.google.inject.multibindings.Multibinder;

/**
 * @author martinbuergi
 *
 * Prepared for implementation HTTP/1.1
 *
 */
public class HttpRequest11Impl extends HttpRequest10Impl{

	@Override
	protected String getVersion(){
		return "HTTP/1.1";
	}

	@Override
	protected void configure() {
		Multibinder<HttpRequest> httpRequestBinder = Multibinder.newSetBinder(binder(), HttpRequest.class);
		httpRequestBinder.addBinding().to(HttpRequest11Impl.class);
	}

}
