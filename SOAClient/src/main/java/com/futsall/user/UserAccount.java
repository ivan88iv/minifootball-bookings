package com.futsall.user;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

public class UserAccount implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6648894461890377076L;
	
	private static final String LOGIN_PAGE_ID = "login.xhtml";
	
	private static final String SALT = "atyXrEVasdpoinQUCJll";
	
	private int id;
	
	private String username;
	
	private String firstName;
	
	private String lastName;
	
	private String telephone;
	
	private String address;
	
	private String email;

	/**
	 * Checks if the user has been logged in
	 * 
	 * @param event the event on which the check is performed
	 */
	public void checkUserLoggedIn(ComponentSystemEvent event)
	{
		if (!isUserInSystem())
		{
			// the user is not logged in - do not grant access
			FacesContext context = FacesContext.getCurrentInstance();
			ConfigurableNavigationHandler handler =
					(ConfigurableNavigationHandler) context.getApplication()
							.getNavigationHandler();
			handler.performNavigation(LOGIN_PAGE_ID);
		}
	}
	
	/**
	 * Gets greeting to be displayed
	 * 
	 * @return the greeting to be displayed
	 */
	public String getGreeting() {
		if(!isUserInSystem()) {
			return "";
		} else {
			return "Welcome "+username;
		}
	}
	
	public boolean isUserInSystem() {
		return (null!=username);
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public void invalidateUser() {
		id = 0;
		username = null;
		firstName = null;
		lastName = null;
		telephone = null;
		address = null;
		email = null;
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
