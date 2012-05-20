package com.futsall.service.provider;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class ServiceProvider {

	private static final String BASIC_SERVICE_URI = 
			"http://localhost:8080/SOAServer/minifootball";
	
	private static ClientConfig clientConfig;
	
	private static Client client;
	
	private static WebResource resource;
	
	static {
		clientConfig = new DefaultClientConfig();
		client = Client.create(clientConfig);
		resource = client.resource(getBaseUri());
	}
	
	public static ClientConfig getClientConfig() {
		return clientConfig;
	}

	public static Client getClient() {
		return client;
	}

	public static WebResource getResource() {
		return resource;
	}

	private static URI getBaseUri() {
		return UriBuilder.fromUri(BASIC_SERVICE_URI).build();
	}
}
