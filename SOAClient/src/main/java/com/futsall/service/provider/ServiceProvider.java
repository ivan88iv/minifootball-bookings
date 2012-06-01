package com.futsall.service.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * The class is a utility class for making a service connection.
 * It represents a Singleton pattern.
 * 
 * @author Ivan
 *
 */
public final class ServiceProvider {
	private static final String CONFIG_FILE = 
			"/com/futsall/configuration.properties";
	
	public final static ServiceProvider INSTANCE = new ServiceProvider();
	
	private static final String CONNECTION_URL_KEY="server.connection.url";
	
	private ClientConfig clientConfig;
	
	private Client client;
	
	private WebResource resource;
	
	private Properties properties;
	
	private ServiceProvider() {
		clientConfig = new DefaultClientConfig();
		client = Client.create(clientConfig);
		
		properties = new Properties();
		InputStream is = getClass().getResourceAsStream(CONFIG_FILE);
		try {
			properties.load(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		resource = client.resource(getBaseUri());
	}
	
	public ClientConfig getClientConfig() {
		return clientConfig;
	}

	public Client getClient() {
		return client;
	}

	public WebResource getResource() {
		return resource;
	}

	private URI getBaseUri() {
		if(properties!=null) {
			return UriBuilder.fromUri(properties.getProperty(CONNECTION_URL_KEY)).build();
		}
		
		return null;
	}
}
