package com.futsall.playGround;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ListOfPlayGrounds {
	private List<PlayGround> playGrounds;

	public List<PlayGround> getPlayGrounds() {
		return playGrounds;
	}

	public void setPlayGrounds(List<PlayGround> playGrounds) {
		this.playGrounds = playGrounds;
	}
}	
