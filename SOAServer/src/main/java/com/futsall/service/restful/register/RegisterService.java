package com.futsall.service.restful.register;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.futsall.db.DBManager;

@Path("/register")
public class RegisterService {
	
	private DBManager dbManager = new DBManager();
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String performRegistration(@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("firstName") String firstName,
			@FormParam("lastName") String lastName,
			@FormParam("telephone") String telephone,
			@FormParam("email") String email,
			@FormParam("address") String address)
	{
		
		if (dbManager.addNewUser(username, password, firstName, lastName, telephone, email, address))
		{	
			return "success";
		}
		
		return "failure";
	}
}
