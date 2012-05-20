package com.futsall.home;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.ws.rs.core.MediaType;

import com.futsall.city.ListOfCities;
import com.futsall.country.ListOfCountries;
import com.futsall.playGround.ListOfPlayGrounds;
import com.futsall.playGround.PlayGround;
import com.futsall.service.provider.ServiceProvider;
import com.futsall.user.UserAccount;

/**
 * The bean manages the condition for the home.xhtml page
 * 
 * @author Ivan
 *
 */
public class HomeBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5582126154702023096L;
	
	private static final String ALL_COUNTRIES_SERVICE_PATH = "/playgrounds/countries";
	
	private static final String CITIES_SERVICE_PATH = "/playgrounds/cities";
	
	private static final String PLAYGROUNDS_SERVICE_PATH = "/playgrounds";
	
	private static final String LOGIN_NAVIGATION = "login";
	
	private static final String SCHEDULE_NAVIGATION = "schedule";
	
	private String selectedCountry;
	
	private String selectedCity;
	
	private String selectedPlaygroundId;
	
	private PlayGround selectedPlayground;

	private List<String> countriesToSelectFrom;
	
	private List<String> citiesToSelectFrom;
	
	private List<SelectItem> playgroundsToSelectFrom;
	
	private Map<String,PlayGround> playgroundsMap;
	
	private boolean isInitialCountryLoadingPerformed = false;
	
	private UserAccount userAccount;
	
	/**
	 * Loads all the countries by calling a service
	 * 
	 * @param event the event
	 */
	public void loadCountries(ComponentSystemEvent event) {
		if(isInitialCountryLoadingPerformed) {
			return;
		}
		
		isInitialCountryLoadingPerformed = true;
		
		countriesToSelectFrom = new ArrayList<>();
		
		ListOfCountries countries = ServiceProvider.getResource().
				path(ALL_COUNTRIES_SERVICE_PATH).accept(MediaType.APPLICATION_XML).
					get(ListOfCountries.class);
		
		List<String> cList = countries.getCountries();
		for(String c : cList) {
			countriesToSelectFrom.add(c);
		}
	}
	
	/**
	 * The method loads the cities for a given country
	 * when the country is selected
	 * 
	 * @param event the event
	 */
	public void loadCities(AjaxBehaviorEvent event) {
		citiesToSelectFrom = new ArrayList<>();
		
		ListOfCities cities = ServiceProvider.getResource().
				path(CITIES_SERVICE_PATH).queryParam("country", selectedCountry).
				accept(MediaType.APPLICATION_XML).get(ListOfCities.class);
		
		List<String> cList = cities.getCities();
		for(String c : cList) {
			citiesToSelectFrom.add(c);
		}
	}
	
	/**
	 * The method loads the playgrounds for a given city
	 * when the city is selected
	 * 
	 * @param event the event
	 */
	public void loadPlaygrounds(AjaxBehaviorEvent event) {
		playgroundsToSelectFrom = new ArrayList<>();
		playgroundsMap = new HashMap<>();
		
		ListOfPlayGrounds playGrounds = ServiceProvider.getResource().
				path(PLAYGROUNDS_SERVICE_PATH).queryParam("city", selectedCity).
					queryParam("country", selectedCountry).accept(MediaType.APPLICATION_XML).get(
					ListOfPlayGrounds.class);
		List<PlayGround> list = playGrounds.getPlayGrounds();
		for(PlayGround pg : list) {
			playgroundsToSelectFrom.add(
					new SelectItem(String.valueOf(pg.getId()),pg.getName()));
			playgroundsMap.put(String.valueOf(pg.getId()), pg);
		}
	}
	
	/**
	 * The method loads the selected playground
	 * 
	 * @param event the event
	 */
	public void loadPlaygroundInfo(AjaxBehaviorEvent event) {
		//find the selected playground by its id in the map
		selectedPlayground = playgroundsMap.get(selectedPlaygroundId);
	}
	
	/**
	 * The method is executed when there is a playground selected
	 *  
	 * @return a string navigating to the login page if the user has not logged in
	 *  or a string navigating to the schedule page otherwise
	 */
	public String selectPlayground() {
		return SCHEDULE_NAVIGATION;
//		if(userAccount.getUsername()==null ) {
//			// the user should be logged in in order to continue
//			return LOGIN_NAVIGATION;
//		} else {
//			//view the schedule of the selected playground
//			return SCHEDULE_NAVIGATION;
//		}
	}

	public String getSelectedCountry() {
		return selectedCountry;
	}

	public void setSelectedCountry(String selectedCountry) {
		this.selectedCountry = selectedCountry;
	}

	public String getSelectedCity() {
		return selectedCity;
	}

	public void setSelectedCity(String selectedCity) {
		this.selectedCity = selectedCity;
	}

	public String getSelectedPlaygroundId() {
		return selectedPlaygroundId;
	}

	public void setSelectedPlaygroundId(String selectedPlaygroundId) {
		this.selectedPlaygroundId = selectedPlaygroundId;
	}

	public List<String> getCountriesToSelectFrom() {
		return countriesToSelectFrom;
	}

	public List<String> getCitiesToSelectFrom() {
		return citiesToSelectFrom;
	}

	public List<SelectItem> getPlaygroundsToSelectFrom() {
		return playgroundsToSelectFrom;
	}
	
	public PlayGround getSelectedPlayground() {
		return selectedPlayground;
	}
	
	public void setUserAccount(UserAccount inUser) {
		userAccount = inUser;
	}
	
	
	/**
	 * Returns true if a playground is selected
	 *  
	 * @return true if a playground is selected and false otherwise
	 */
	public boolean isPlaygroundSelected() {
		return selectedPlayground != null;
	}
}
