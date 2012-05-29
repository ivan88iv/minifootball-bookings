package com.futsall.service.restful.login;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.futsall.db.DBManager;

@Path("/login")
public class LoginService {
	
	private DBManager dbManager = new DBManager();
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String performLogin(@FormParam("username")String username,
			@FormParam("password")String password)
	{
		
		if (dbManager.isUserValid(username, password))
		{	
			return "success";
		}
		
		return "failure";
	}
}
