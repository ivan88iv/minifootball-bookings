package com.futsall.service.login;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.futsall.service.restful.login.LoginService;
import com.futsall.user.User;

public class TestLoginService {

	private static final String SALT = "atyXrEVasdpoinQUCJll";
	
	private LoginService service;
	
	@BeforeClass
	public void prepareData()
	{
		service = new LoginService();
	}
	
	private User executeLoginRequest(String username, String password){
		
		return service.performLogin(username, hashPassword(password));
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
