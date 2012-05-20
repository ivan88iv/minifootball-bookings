package com.futsall.register;

import java.io.Serializable;

public class RegisterBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -423687696071315992L;

	private static final String SUCCESSFULL_REGISTRATION="success";
	
	private String username;
	
	private String password;
	
	private String confPassword;
	
	private String firstName;
	
	private String lastName;
	
	private String telephone;
	
	private String email;
	
	private String address;

	public String register() {
		// on successfull registration
		return SUCCESSFULL_REGISTRATION;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfPassword() {
		return confPassword;
	}

	public void setConfPassword(String confPassword) {
		this.confPassword = confPassword;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
