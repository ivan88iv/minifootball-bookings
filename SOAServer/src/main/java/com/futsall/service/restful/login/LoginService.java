package com.futsall.service.restful.login;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.futsall.db.DBManager;
import com.futsall.user.User;

@Path("/login")
public class LoginService {
	
	private DBManager dbManager = new DBManager();
	
	/**
	 * The service accepts as form parameters the username and
	 * password to be checked and returns the User account
	 * that corresponds to them or null if there is none
	 * 
	 * @param username the username of the person that wants to log in
	 * @param password the password of the person that wants to log in
	 * @return a User instance that corresponds to the username and password
	 * or null if none exists
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public User performLogin(@FormParam("username")String username,
			@FormParam("password")String password)
	{
		return dbManager.getValidUser(username, password); 
	}
}
