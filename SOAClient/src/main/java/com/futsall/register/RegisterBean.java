package com.futsall.register;

import java.io.Serializable;

import javax.ws.rs.core.MediaType;

import com.futsall.service.provider.ServiceProvider;
import com.futsall.user.UserAccount;
import com.sun.jersey.api.representation.Form;

public class RegisterBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -423687696071315992L;

	private static final String SUCCESSFULL_REGISTRATION="success";
	
	private static final String REGISTER_PATH = "/register";
	
	private String username;
	
	private String password;
	
	private String confPassword;
	
	private String firstName;
	
	private String lastName;
	
	private String telephone;
	
	private String email;
	
	private String address;

	public String register() {
		
		Form form = new Form();
		form.add("username", username);
		form.add("password", UserAccount.hashPassword(password));
		form.add("firstName", firstName);
		form.add("lastName", lastName);
		form.add("telephone", telephone);
		form.add("email", email);
		form.add("address", address);
		
		String response = ServiceProvider.INSTANCE.getResource().
				path(REGISTER_PATH).accept(MediaType.APPLICATION_XML).
				type(MediaType.APPLICATION_FORM_URLENCODED).
				post(String.class, form);
		
		if(response.equals(SUCCESSFULL_REGISTRATION)){
			return SUCCESSFULL_REGISTRATION;
		}
		
		return null;
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
