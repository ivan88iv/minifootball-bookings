package com.futsall.country;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ListOfCountries implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6130988962228401077L;
	List<String> countries;

	public List<String> getCountries() {
		return countries;
	}

	public void setCountries(List<String> countries) {
		this.countries = countries;
	}
}
