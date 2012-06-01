package com.futsall.schedule;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.ws.rs.core.MediaType;

import com.futsall.playGround.PlayGround;
import com.futsall.service.provider.ServiceProvider;
import com.futsall.user.User;
import com.futsall.user.UserAccount;

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
	
	private static final String RESERVE_PATH= "/reservation";
	
	private static final String MSGS_NAME="msgs";
	
	private static final String DB_ERROR_MSG_KEY="schedule.db.error";
	
	private static final String START_AFTER_END_TIME_MSG_KEY="schedule.start.after.end.time";
	
	private static final String PAST_RESRVATION_INFO_MSG_KEY="schedule.past.reservation.time.info";
	
	private static final String ALREADY_RESERVED_MSG_KEY="schedule.already.reserved";
	
	private static final String UNKNOWN_PROBLEM_MSG_KEY="schedule.unknown.problem";
	
	private static final Logger LOGGER = Logger.getLogger(ScheduleBean.class.getName());

	private static ResourceBundle messages;

	private List<Schedule> schedules = new ArrayList<>();
	
	private Date selectedDate;
	
	private PlayGround selectedPlayground;
	
	private Date startTime;
	
	private Date endTime;
	
	private UserAccount userAccount;
	
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

		ListOfSchedules scheduleList = ServiceProvider.INSTANCE.getResource().
				path(SCHEDULE_PATH).queryParam(DATE_QUERY_PARAM, formattedDate).
					queryParam(PLAYGROUND_QUERY_PARAM, String.valueOf(selectedPlayground.getId()))
							.accept(MediaType.APPLICATION_XML).get(ListOfSchedules.class);
		
		List<Schedule> sList = scheduleList.getSchedules();
		for(Schedule s : sList) {
			schedules.add(s);
		}
	}
	
	public String reservePlayground() {
		Schedule scheduleToRegister = createScheduleToRegister();
		ReservationResponse response = ServiceProvider.INSTANCE.getResource().path(RESERVE_PATH)
			.accept(MediaType.APPLICATION_XML).put(ReservationResponse.class, scheduleToRegister);
		
		return performReservationNavigation(response);
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
		this.startTime =appendTimeInfo(startTime);
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime =appendTimeInfo(endTime);
	}
	
	public void setUserAccount(UserAccount inUserAccount) {
		this.userAccount = inUserAccount;
	}
	
	private Schedule createScheduleToRegister() {
		Schedule scheduleToRegister = new Schedule();
		scheduleToRegister.setEndTime(endTime);
		scheduleToRegister.setStartTime(startTime);
		scheduleToRegister.setPlayGroundId(selectedPlayground.getId());
		
		User newUser = new User();
		newUser.setAddress(userAccount.getAddress());
		newUser.setEmail(userAccount.getEmail());
		newUser.setFirstName(userAccount.getFirstName());
		newUser.setId(userAccount.getId());
		newUser.setLastName(userAccount.getLastName());
		newUser.setTelephone(userAccount.getTelephone());
		newUser.setUsername(userAccount.getUsername());
		scheduleToRegister.setUser(newUser);
		
		return scheduleToRegister;
	}
	
	private String performReservationNavigation(ReservationResponse response) {
		FacesContext context = FacesContext.getCurrentInstance();
		String navigationString = null;
		
		switch (response) {
		case DB_ERROR:
			context.addMessage(null, 
					new FacesMessage(getMessages(context).getString(DB_ERROR_MSG_KEY)));
			break;
		case START_TIME_BEFORE_END_TIME_OR_EQUAL:
			context.addMessage(null, 
					new FacesMessage(getMessages(context).getString(START_AFTER_END_TIME_MSG_KEY)));
			break;
		case TIME_INFO_IN_PAST:
			context.addMessage(null, 
					new FacesMessage(getMessages(context).getString(PAST_RESRVATION_INFO_MSG_KEY)));
			break;
		case ALREADY_RESERVED:
			context.addMessage(null, 
					new FacesMessage(getMessages(context).getString(ALREADY_RESERVED_MSG_KEY)));
			break;
		case RESERVED_SUCCESSFULLY:
			navigationString = SUCCESSFUL_RESERVATION;
			break;
		default:
			// unknown status
			context.addMessage(null, 
					new FacesMessage(getMessages(context).getString(UNKNOWN_PROBLEM_MSG_KEY)));
			break;
		}
		
		return navigationString;
	}
	
	private ResourceBundle getMessages(FacesContext inContext) {
		if(messages==null){
			messages = inContext.getApplication().getResourceBundle(inContext, MSGS_NAME);
		}
		
		return messages;
	}
	
	/**
	 * The method tries to append time information presented as a Date instance
	 * parameter to the previously selected date
	 * 
	 * @param inTime the date instance representing the exact time of the date
	 * represented by selected date
	 * @return the appended time and date
	 */
	private Date appendTimeInfo(Date inTime) {
		Date result = null;
		
		// set the date according to the selected one
		if(selectedDate!= null) {
			DateFormat timeFormattter = new SimpleDateFormat("HH:mm");
			String timeStr = timeFormattter.format(inTime);
			DateFormat selectedDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = selectedDateFormatter.format(selectedDate);
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			try {
				result = formatter.parse(dateStr + " " + timeStr);
			} catch (ParseException e) {
				LOGGER.log(Level.SEVERE,e.getMessage());
			}
		}
		
		return result;
	}
}
