package com.futsall.login;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;

import com.futsall.service.provider.ServiceProvider;
import com.futsall.user.User;
import com.futsall.user.UserAccount;
import com.sun.jersey.api.representation.Form;

public class LoginBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8909417924203126409L;

	private static final String SUCCESSFUL_LOGIN = "success";
	
	private static final String LOGOUT_SUCCESS = "logout";
	
	private static final String USERNAME_PARAM = "username";
	
	private static final String PASSWORD_PARAM = "password";
	
	private static final String LOGIN_PATH = "/login";
	
	private static final String MSGS_NAME = "msgs";
	
	private static final String INCORRECT_LOGIN_MSG_KEY="login.incorrect";
	
	private String username;
	
	private String password;
	
	private UserAccount userAccount;
	
	private ResourceBundle messages;
	
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

	public void setUserAccount(UserAccount inUA) {
		userAccount = inUA;
	}
	
	public String login() {

		Form form = new Form();
		form.add(USERNAME_PARAM, username);
		form.add(PASSWORD_PARAM, UserAccount.hashPassword(password));
		User response = ServiceProvider.INSTANCE.getResource().
				path(LOGIN_PATH).accept(MediaType.APPLICATION_XML).
				type(MediaType.APPLICATION_FORM_URLENCODED).
				post(User.class, form);
		
		if ( response.getUsername() != null){
			userAccount.setId(response.getId());
			userAccount.setUsername(username);
			userAccount.setAddress(response.getAddress());
			userAccount.setEmail(response.getEmail());
			userAccount.setLastName(response.getLastName());
			userAccount.setFirstName(response.getFirstName());
			userAccount.setTelephone(response.getTelephone());
			
			return SUCCESSFUL_LOGIN;
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(
				getMessages(context).getString(INCORRECT_LOGIN_MSG_KEY)));
		
		return null;
	}
	
	/**
	 * The action method logs out the current user from the application
	 * 
	 * @return special message on successful logout and null otherwise
	 */
	public String logout()
	{
		String result = null;

		HttpSession session =
				(HttpSession) FacesContext.getCurrentInstance()
						.getExternalContext().getSession(false);
		if (session != null)
		{
			session.invalidate();
			userAccount.invalidateUser();

			result = LOGOUT_SUCCESS;
		}

		return result;
	}
	
	private ResourceBundle getMessages(FacesContext inContext) {
		if(messages==null){
			messages = inContext.getApplication().getResourceBundle(inContext, MSGS_NAME);
		}
		
		return messages;
	}
}
