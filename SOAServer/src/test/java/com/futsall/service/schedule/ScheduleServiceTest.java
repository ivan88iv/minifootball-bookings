package com.futsall.service.schedule;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.futsall.schedule.ListOfSchedules;
import com.futsall.schedule.Schedule;
import com.futsall.service.restful.schedule.ScheduleService;
import com.futsall.user.User;

public class ScheduleServiceTest {
	
	private static final String SELECT_EXISTING_RESERVATION=
			"SELECT * FROM minifootball.reserved r WHERE r.playGroundId = ? AND ( "+
			"r.startTime LIKE CONCAT(?,'%') OR r.endTime LIKE CONCAT(?,'%') )";
	
	private static final Logger LOGGER = Logger
			.getLogger(ReservationServiceTest.class.getName());

	private static String ELEMENT_DELIMITER = ";";
	
	private static ScheduleService service;

	private static PropertyResourceBundle testDataFile;

	private static Connection connection;

	private static Statement stmnt;

	private static ResultSet resultSet;

	private static DateFormat dateFormatter;

	@BeforeClass
	public void prepareData() throws IOException, SQLException {
		LOGGER.log(Level.INFO,
				"=============================================================");
		dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		InputStream is = getClass().getResourceAsStream(
				"/com/futsall/service/reservation/scheduleTD.properties");
		testDataFile = new PropertyResourceBundle(is);
		is.close();

		// get all the countries from the service, faking a client request
		service = new ScheduleService();
		// manage connections and statements
		connection = service.getDbManager().getConnection();
		stmnt = connection.createStatement();
		connection.setAutoCommit(false);
		LOGGER.log(Level.INFO,
				"=============================================================");
	}
	
	@AfterClass
	public void afterClass() throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		if (connection != null) {
			connection.rollback();
			closeDBStatements();
		}
		LOGGER.log(Level.INFO,"=============================================================");
	}
	
	/**
	 * The method tests the service for existing start or end dates and
	 * specified playgrounds 
	 * 
	 * @param date the start or end date we want to find out the schedules
	 * @param playgroundId the playground for which the service is called
	 * @throws SQLException
	 * @throws ParseException
	 */
	@Test(dataProvider="existingInfoProvider")
	public void testGetSchedule(String date,String playgroundId) throws SQLException, ParseException {
		LOGGER.log(Level.INFO,
				"=============================================================");
		List<Schedule> dbSchedules = checkIfInDb(date,playgroundId);
		// check info existence in the database
		Assert.assertNotEquals(dbSchedules.size(),0);
		
		// check the response from the service
		List<Schedule> schedules = executeScheduleRequest(date,playgroundId).getSchedules();
		checkSchedulesEquality(schedules, dbSchedules);

		// check info is still in db
		dbSchedules = checkIfInDb(date,playgroundId);
		Assert.assertNotEquals(dbSchedules.size(),0);
		
		LOGGER.log(Level.INFO,
				"=============================================================");
	}
	
	/**
	 * The method tests the service for a non existing playground
	 * 
	 * @param date the start or end date we want to find out the schedules
	 * @param playgroundId the playground for which the service is called
	 * @return 
	 * @throws SQLException
	 * @throws ParseException
	 */
	@Test(dataProvider="nonExistingInfoProvider",dependsOnMethods={"testGetSchedule"})
	public void testNonExistingPlayground(String date,String playgroundId) throws SQLException, ParseException {
		LOGGER.log(Level.INFO,
				"=============================================================");
		List<Schedule> dbSchedules = checkIfInDb(date,playgroundId);
		// check info existence in the database
		Assert.assertEquals(dbSchedules.size(),0);
		
		// check the response from the service
		List<Schedule> schedules = executeScheduleRequest(date,playgroundId).getSchedules();
		checkSchedulesEquality(schedules, dbSchedules);

		// check info is still in db
		dbSchedules = checkIfInDb(date,playgroundId);
		Assert.assertEquals(dbSchedules.size(),0);
		LOGGER.log(Level.INFO,
				"=============================================================");
	}
	
	@DataProvider(name="nonExistingInfoProvider")
	public Object[][] getNonExistingInfo() {
		return extractPlaygroundReservations("non.existing.dates","non.existing.playground.ids");
	}
	
	@DataProvider(name="existingInfoProvider")
	public Object[][] getExistingInfo() {
		return extractPlaygroundReservations("existing.start.dates","existing.playground.ids");
	}
	
	private Object[][] extractPlaygroundReservations(String datesKey, String playgroundsKey) {
		String[] dates =testDataFile.getString(datesKey).split(ELEMENT_DELIMITER);
		String[] playgroundId = testDataFile.getString(playgroundsKey).split(ELEMENT_DELIMITER);
		
		Object[][] dataProvided = new Object[dates.length][];
		for(int i = 0; i<dates.length; ++i) {
			dataProvided[i] = new Object[]{dates[i],playgroundId[i]};
		}
		
		return dataProvided;
	}
	
	private ListOfSchedules executeScheduleRequest(String inDate, String inPId) {

		return service.getSchedule(inDate, inPId);
	}
	
	/**
	 * Asserts the equality of the elements in two lists
	 * 
	 * @param list1
	 * @param list2
	 */
	private void checkSchedulesEquality(List<Schedule> list1, List<Schedule> list2) {
		Assert.assertEquals(list1.size(), list2.size());
		Assert.assertTrue(isSubList(list1,list2));
		Assert.assertTrue(isSubList(list2,list1));
	}
	
	private boolean isSubList(List<Schedule> list, List<Schedule> sublist) {
		for(Schedule s : list) {
			boolean found = false;
			for(Schedule s2 : sublist) {
				if(s.getUser().getId() == s2.getUser().getId() &&
					s.getPlayGroundId() == s2.getPlayGroundId() &&
					s.getStartTime().getTime() ==s2.getStartTime().getTime() &&
					s.getEndTime().getTime() == s2.getEndTime().getTime() ){
					found = true;
					break;
				}
			}
			
			if(!found) {
				return false;
			}
		}
		return true;
	}
	
	private List<Schedule> checkIfInDb(String time,String playgroundId) throws SQLException, ParseException {
		int pId = Integer.parseInt(playgroundId);
		PreparedStatement pst = connection.prepareStatement(SELECT_EXISTING_RESERVATION);
		pst.setInt(1, pId);
		pst.setString(2, time);
		pst.setString(3, time);
		resultSet = pst.executeQuery();
		
		List<Schedule> list = new ArrayList<>();
		
		while(resultSet.next()) {
			Schedule s = new Schedule();
			User u = new User();
			u.setId(resultSet.getInt(2));
			s.setUser(u);
			s.setPlayGroundId(resultSet.getInt(3));
			s.setStartTime(dateFormatter.parse(resultSet.getString(4)));
			s.setEndTime(dateFormatter.parse(resultSet.getString(5)));
			
			list.add(s);
		}
		
		return list;
	}
	
	private void closeDBStatements() throws SQLException {
		if (resultSet != null) {
			resultSet.close();
		}

		if (stmnt != null) {
			stmnt.close();
		}
	}
}
