package com.futsall.service.test.login;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.futsall.user.User;
import com.futsall.user.UserAccount;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

public class TestLoginService {
	
	private WebResource resource;
	
	@BeforeClass
	public void prepareData() {

		ClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);
		resource = client.resource(getBaseUri());
	}

	private User executeLoginRequest(String username, String password) {
		
		Form form = new Form();
		form.add("username", username);
		form.add("password", UserAccount.hashPassword(password));
		return resource.path("/login").accept(MediaType.APPLICATION_XML).
				type(MediaType.APPLICATION_FORM_URLENCODED).
				post(User.class, form);
	}
	
	@Test
	public void testLoginWithExistingUser() {
		User user = executeLoginRequest("soko", "123");	
		
		Assert.assertNotNull(user.getUsername(), "Username: soko; Password: 123; Login Failed. username was null");
	}
	
	@Test
	public void testLoginWithWrongUser() {
		User user = executeLoginRequest("aa", "123");	
		
		Assert.assertNull(user.getUsername(), "Username: aa; Password: 123; Login succeeded. username was not null");
	}
	
	@Test
	public void testLoginWithExistingUserWrongPassword() {
		User user = executeLoginRequest("soko", "werr");	
		
		Assert.assertNull(user.getUsername(), "Username: soko; Password: werr; Login succeeded. username was not null");
	}
	
	@Test
	public void testLoginWithWrongUserWrongPassword() {
		User user = executeLoginRequest("a", "a");	
		
		Assert.assertNull(user.getUsername(), "Username: a; Password: a; Login succeeded. username was not null");
	}

	private static URI getBaseUri() {
		return UriBuilder.fromUri(
				"http://localhost:8080/SOAServer/minifootball").build();
	}
}
