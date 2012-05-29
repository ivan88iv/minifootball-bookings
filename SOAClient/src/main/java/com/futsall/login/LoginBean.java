package com.futsall.login;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;

import com.futsall.service.provider.ServiceProvider;
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
	
	private String username;
	
	private String password;
	
	private UserAccount userAccount;
	
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
		String response = ServiceProvider.getResource().
				path(LOGIN_PATH).accept(MediaType.APPLICATION_XML).
				type(MediaType.APPLICATION_FORM_URLENCODED).
				post(String.class, form);
		
		if ( response.equals(SUCCESSFUL_LOGIN)){
			userAccount.setUsername(username);
			return SUCCESSFUL_LOGIN;
		}
		
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
			userAccount.setUsername(null);

			result = LOGOUT_SUCCESS;
		}

		return result;
	}
}
