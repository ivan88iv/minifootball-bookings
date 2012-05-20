package com.futsall.city;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ListOfCities implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1245363479927211624L;
	
	private List<String> cities;

	public List<String> getCities() {
		return cities;
	}

	public void setCities(List<String> cities) {
		this.cities = cities;
	}
}
