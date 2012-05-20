package com.futsall.service.restful.playgrounds;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.futsall.city.ListOfCities;
import com.futsall.country.ListOfCountries;
import com.futsall.db.DBManager;
import com.futsall.playGround.ListOfPlayGrounds;

@Path("/playgrounds")
public class PlayGroundsService {
	private DBManager dbManager = new DBManager();
	
	/**
	 * Extracts a list of the playgrounds for the given city
	 * 
	 * @param cityName the name of the city
	 * @return the playgrounds in the specified city
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public ListOfPlayGrounds getPlayGrounds(
			@QueryParam("city") String cityName,
			@QueryParam("country") String countryName) {
		
		// get the playgrounds from the database
		return dbManager.getPlayGrounds(cityName, countryName);
	}	
	
	/**
	 * The method extracts all the countries in the database
	 * @return the entire set of countries from the database
	 * 
	 */
	@GET
	@Path("/countries")
	@Produces(MediaType.APPLICATION_XML)
	public ListOfCountries getCountries() {
		// gets all the countries in the database
		return dbManager.getAllCountries();
	}
	
	@GET
	@Path("/cities")
	@Produces(MediaType.APPLICATION_XML)
	public ListOfCities getCities(
			@QueryParam("country") String inCountryName) {
		// get all the cities in the database for a given country
		return dbManager.getCities(inCountryName);
	}
}
