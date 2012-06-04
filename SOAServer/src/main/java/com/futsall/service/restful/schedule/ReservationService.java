package com.futsall.service.restful.schedule;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import com.futsall.db.DBManager;
import com.futsall.schedule.ReservationResponse;
import com.futsall.schedule.Schedule;

@Path("/reservation")
public class ReservationService {
	private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());
	
	private DBManager dbManager = new DBManager();

	public DBManager getDbManager() {
		return dbManager;
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public ReservationResponse reservePlayground(Schedule schedule) {
		Calendar calendar = Calendar.getInstance();
		
		if(schedule.getEndTime().before(schedule.getStartTime()) || 
				schedule.getEndTime().getTime()== schedule.getStartTime().getTime()) {
			return ReservationResponse.START_TIME_BEFORE_END_TIME_OR_EQUAL;
		} else if(schedule.getStartTime().getTime() <= calendar.getTime().getTime()||
				schedule.getEndTime().getTime() <= calendar.getTime().getTime()) {
			// end or start time before the current time
			return ReservationResponse.TIME_INFO_IN_PAST;
		} 
		
		try{
			if (dbManager.reservePlayground(schedule)) {
				return ReservationResponse.RESERVED_SUCCESSFULLY;
			} else {
				return ReservationResponse.ALREADY_RESERVED;
			}
		} catch(SQLException sqle) {
			LOGGER.log(Level.WARNING,sqle.getMessage());
			return ReservationResponse.DB_ERROR;
		}
	}
}
