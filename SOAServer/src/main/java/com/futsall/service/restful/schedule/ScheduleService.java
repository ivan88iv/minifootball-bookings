package com.futsall.service.restful.schedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.futsall.db.DBManager;
import com.futsall.schedule.ListOfSchedules;

@Path("/schedule")
public class ScheduleService {
	private DBManager dbManager = new DBManager();
	
	private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());
	
	/**
	 * The method extracts the schedule about a given day and playground
	 * 
	 * @param inDate the day for which the schedule is extracted
	 * @param inMonth the month for which the schedule is extracted
	 * @param inYear the year for which the schedule is extracted
	 * @param inPId the id of the play ground for which the schedule is extracted
	 * @return the schedule for the given date
	 * 
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public ListOfSchedules getSchedule(
			@QueryParam("date")String inDate,
			@QueryParam("playGroundId") String inPId) {
		
		try{
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date date = df.parse(inDate);
			int playGroundId = Integer.valueOf(inPId).intValue();
			
			return dbManager.getSchedule(date,playGroundId);
		} catch(ParseException pe) {
			LOGGER.log(Level.WARNING,pe.getMessage());
		} catch (NumberFormatException nfe) {
			LOGGER.log(Level.WARNING,nfe.getMessage());
		}
		
		return null;
	}
	
}
