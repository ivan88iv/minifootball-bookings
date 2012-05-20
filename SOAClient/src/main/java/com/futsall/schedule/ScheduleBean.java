package com.futsall.schedule;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.ws.rs.core.MediaType;

import com.futsall.playGround.PlayGround;
import com.futsall.service.provider.ServiceProvider;

/**
 * The class is a backing bean for the schedule.xhtml page that
 * manages the condition for the page.
 * 
 * @author Ivan
 *
 */
public class ScheduleBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5220007067806033095L;
	
	private static final String SUCCESSFUL_RESERVATION ="success";
	
	private static final String DATE_QUERY_PARAM = "date";
	
	private static final String PLAYGROUND_QUERY_PARAM = "playGroundId";
	
	private static final String SCHEDULE_PATH = "/schedule";

	private List<Schedule> schedules = new ArrayList<>();
	
	private Date selectedDate;
	
	private PlayGround selectedPlayground;
	
	private Date startTime;
	
	private Date endTime;
	
	/**
	 * The method loads information about the reserved schedules
	 *
	 *NB!!! There is an error in the richfaces framework for rich:calendar's
	 *ajax listeners execution. The listeners are not executed in the right moment.
	 *So as a temporary fix, until this problem is resolved this ajax listener
	 *is called when setting the date in the rich:calendar.
	 * 
	 * @param event the event on which the reservation takes place
	 */
	public void loadSchedules(AjaxBehaviorEvent event) {
		schedules.clear();
		
		if(selectedDate==null || selectedPlayground==null) {
			return;
		}
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = df.format(selectedDate);
		
		ListOfSchedules scheduleList = ServiceProvider.getResource().
				path(SCHEDULE_PATH).queryParam(DATE_QUERY_PARAM, formattedDate).
					queryParam(PLAYGROUND_QUERY_PARAM, String.valueOf(selectedPlayground.getId()))
							.accept(MediaType.APPLICATION_XML).get(ListOfSchedules.class);
		
		List<Schedule> sList = scheduleList.getSchedules();
		for(Schedule s : sList) {
			schedules.add(s);
		}
	}
	
	public String reservePlayground() {
		//TODO here the logic for connecting to the web service for 
		//playground reservation should be implemented. 
		//Useful information for the service is contained in the fields
		//startTime and endTime
		
		return SUCCESSFUL_RESERVATION;
	}
	
	public Date getSelectedDate() {
		return selectedDate;
	}
	
	public void setSelectedDate(Date inSelectedDate) {
		selectedDate = inSelectedDate;
		
		loadSchedules(null);
	}
	
	public PlayGround getSelectedPlayground() {
		return selectedPlayground;
	}
	
	public void setSelectedPlayground(PlayGround inPlayground) {
		selectedPlayground = inPlayground;
	}
	
	public List<Schedule> getSchedules() {
		return schedules;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
