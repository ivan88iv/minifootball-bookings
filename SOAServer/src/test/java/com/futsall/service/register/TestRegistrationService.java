package com.futsall.service.register;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.futsall.service.restful.register.RegisterService;

public class TestRegistrationService {

private static final String SALT = "atyXrEVasdpoinQUCJll";
	
	private RegisterService service;
	
	@BeforeClass
	public void prepareData()
	{
		service = new RegisterService();
	}
	
	private String executeRegistrationRequest(String username, String password, String firstName,
			String lastName, String telephone, String email, String address) {
		
		return service.performRegistration(username, password, firstName, lastName, telephone, email, address);
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
	
	public static String hashPassword(String password)
	{
		MessageDigest digest = null;
		String result = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    digest.reset();
	    try {
			digest.update(SALT.getBytes("UTF-8"));
			byte[] hash = digest.digest(password.getBytes("UTF-8"));
	    
			result = new String(hash, "UTF-8");
	    }
	    catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    
	    return result;
	}
}
