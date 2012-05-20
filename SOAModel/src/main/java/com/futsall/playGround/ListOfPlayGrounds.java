package com.futsall.playGround;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ListOfPlayGrounds implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6223562825182810981L;
	
	private List<PlayGround> playGrounds;

	public List<PlayGround> getPlayGrounds() {
		return playGrounds;
	}

	public void setPlayGrounds(List<PlayGround> playGrounds) {
		this.playGrounds = playGrounds;
	}
}	
