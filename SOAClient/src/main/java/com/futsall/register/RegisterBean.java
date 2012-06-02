package com.futsall.register;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
	
	private static final String MSGS_NAME="msgs";
	
	private static final String PASSWORDS_NOT_EQUAL_KEY="register.not.equal.pass";
	
	private static final String USERNAME_EXISTS_MSG_KEY="register.user.already.registered"; 

	private static final String USERNAME_MSG_HOLDER_ID="username";
	
	private String username;
	
	private String password;
	
	private String confPassword;
	
	private String firstName;
	
	private String lastName;
	
	private String telephone;
	
	private String email;
	
	private String address;

	private static ResourceBundle messages;
	
	public String register() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		if(!checkPasswordEquality()) {
			context.addMessage(null, new FacesMessage(
					getMessages(context).getString(PASSWORDS_NOT_EQUAL_KEY)));
			return null;
		}
			
		
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
		} else {
			context.addMessage(USERNAME_MSG_HOLDER_ID, new FacesMessage(
					getMessages(context).getString(USERNAME_EXISTS_MSG_KEY)));
			return null;
		}
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
	
	private boolean checkPasswordEquality() {
		if(password==null || confPassword == null) {
			return false;
		}else if(!password.equals(confPassword)) {
			return false;
		} else {
			return true;
		}
	}
	
	private ResourceBundle getMessages(FacesContext inContext) {
		if(messages==null){
			messages = inContext.getApplication().getResourceBundle(inContext, MSGS_NAME);
		}
		
		return messages;
	}
}
