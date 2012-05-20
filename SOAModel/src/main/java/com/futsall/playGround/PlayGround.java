package com.futsall.playGround;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.futsall.company.Company;

@XmlRootElement
public class PlayGround implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1570069061992182134L;

	private int id;
	
	private Company company;
	
	private String name;
	
	private String address;
	
	private String telephone;
	
	private String email;
	
	private Double width;
	
	private Double length;
	
	private String flooring;
	
	private String city;
	
	private String country;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public String getFlooring() {
		return flooring;
	}

	public void setFlooring(String flooring) {
		this.flooring = flooring;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
