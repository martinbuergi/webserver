package com.buergi.webserver.http.impl;

import com.buergi.webserver.http.HttpRequest;
import com.google.inject.multibindings.Multibinder;


public class HttpRequest11Impl extends HttpRequest10Impl{

	public String getVersion(){
		return "HTTP/1.1";
	}

	@Override
	protected void configure() {
		Multibinder<HttpRequest> httpRequestBinder = Multibinder.newSetBinder(binder(), HttpRequest.class);
		httpRequestBinder.addBinding().to(HttpRequest11Impl.class);
	}

}
