package com.futsall.service.test.registration;

import java.net.URI;
import java.util.Random;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.futsall.user.UserAccount;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

public class TestRegistrationService {
	
	private WebResource resource;
	
	@BeforeClass
	public void prepareData() {

		ClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);
		resource = client.resource(getBaseUri());
	}
	
	private String executeRegistrationRequest(String username, String password, String firstName,
			String lastName, String telephone, String email, String address) {
		
		Form form = new Form();
		form.add("username", username);
		form.add("password", UserAccount.hashPassword(password));
		form.add("firstName", firstName);
		form.add("lastName", lastName);
		form.add("telephone", telephone);
		form.add("email", email);
		form.add("address", address);
		
		return resource.path("/register").accept(MediaType.APPLICATION_XML).
				type(MediaType.APPLICATION_FORM_URLENCODED).
				post(String.class, form);
	}

	@Test
	public void testRegistrationOfNotExistingUsername() {
		
		Random rand = new Random(System.currentTimeMillis());
		String testUsername = "testUser" + rand.nextInt(100000);
		Assert.assertEquals(executeRegistrationRequest(testUsername, "pass", "john",
				"smith", "(+359)887123123", "john@gmail.com", "john's address"), "success", 
				"Registration of username = " + testUsername + " failed. User already exists");
	}
	
	@Test
	public void testRegistrationOfExistingUsername() {
		
		Assert.assertEquals(executeRegistrationRequest("soko", "pass", "john",
				"smith", "(+359)887123123", "john@gmail.com", "john's address"), "failure", 
				"Registration of username = soko succeeded although user already exists");
	}
	
	private static URI getBaseUri() {
		return UriBuilder.fromUri(
				"http://localhost:8080/SOAServer/minifootball").build();
	}
}
