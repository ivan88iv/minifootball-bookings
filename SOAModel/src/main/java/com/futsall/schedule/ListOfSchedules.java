package com.futsall.schedule;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ListOfSchedules implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1584870858390067746L;
	
	private List<Schedule> schedules;

	public List<Schedule> getSchedules() {
		return schedules;
	}

	public void setSchedules(List<Schedule> schedules) {
		this.schedules = schedules;
	}
}
