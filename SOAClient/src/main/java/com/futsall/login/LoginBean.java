package com.futsall.login;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.futsall.user.UserAccount;

public class LoginBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8909417924203126409L;

	private static final String SUCCESSFUL_LOGIN = "success";
	
	private static final String LOGOUT_SUCCESS = "logout";
	
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
		// on successfull login
		return SUCCESSFUL_LOGIN;
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
