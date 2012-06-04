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
import java.util.Calendar;
import java.util.Date;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.futsall.schedule.ReservationResponse;
import com.futsall.schedule.Schedule;
import com.futsall.service.restful.schedule.ReservationService;
import com.futsall.user.User;

public class ReservationServiceTest {

	private static final String SELECT_SCHEDULE = "SELECT COUNT(*) FROM minifootball.reserved r WHERE r.userId=? "
			+ "AND r.playGroundId=? AND startTime=? AND endTime=?";

	private static final String SELECT_ALREADY_RESERVED_PLAYGROUND = "SELECT * FROM minifootball.reserved r WHERE r.playGroundId=? AND "
			+ "((r.startTime>=? AND r.startTime < ?) OR "
			+ "(r.endTime>? AND r.endTime< ?) OR "
			+ "(r.startTime<= ? AND r.endTime>=?))";

	private static final Logger LOGGER = Logger
			.getLogger(ReservationServiceTest.class.getName());

	private static String DATA_DELIMITER = ",";

	private static String ELEMENT_DELIMITER = ";";

	private static PropertyResourceBundle testDataFile;

	private static ReservationService service;

	private static Connection connection;

	private static Statement stmnt;

	private static ResultSet resultSet;

	private static DateFormat dateFormatter;

	@BeforeClass()
	public void beforeClass() throws IOException, SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		InputStream is = getClass().getResourceAsStream(
				"/com/futsall/service/reservation/reservationTD.properties");
		testDataFile = new PropertyResourceBundle(is);
		is.close();

		// get all the countries from the service, faking a client request
		service = new ReservationService();
		// manage connections and statements
		connection = service.getDbManager().getConnection();
		stmnt = connection.createStatement();
		connection.setAutoCommit(false);
		LOGGER.log(Level.INFO,"=============================================================");
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
	 * Tests some valid input to the service and if it is going to change the
	 * database
	 * 
	 * @param s
	 *            the valid input to be tested
	 * @throws SQLException
	 */
	@Test(dataProvider = "validReservationProvider")
	public void testAddValidReservation(Schedule s) throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		Assert.assertTrue(checkTimeIntervalCorrectness(s.getStartTime(),
				s.getEndTime()));
		Assert.assertFalse(checkTimeInPast(s.getStartTime(), s.getEndTime()));
		Assert.assertFalse(checkPlaygroundTimeAvailability(s.getPlayGroundId(),
				s.getStartTime(), s.getEndTime()));

		// initially no reservation for this time should have been made
		Assert.assertFalse(checkInfoInTable(s));
		ReservationResponse response = service.reservePlayground(s);
		Assert.assertEquals(response, ReservationResponse.RESERVED_SUCCESSFULLY);

		// check the database
		Assert.assertTrue(checkInfoInTable(s));
		LOGGER.log(Level.INFO,"=============================================================");
	}

	/**
	 * The method tries to reserve the playground when it has already been
	 * reserved
	 * 
	 * @param s
	 *            the schedule to be reserved
	 * @throws SQLException
	 */
	@Test(dataProvider = "alreadyExistingReservation", dependsOnMethods = { "testAddValidReservation" })
	public void testAlredyExistingReservation(Schedule s) throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		Assert.assertTrue(checkTimeIntervalCorrectness(s.getStartTime(),
				s.getEndTime()));
		Assert.assertFalse(checkTimeInPast(s.getStartTime(), s.getEndTime()));
		Assert.assertTrue(checkPlaygroundTimeAvailability(s.getPlayGroundId(),
				s.getStartTime(), s.getEndTime()));

		ReservationResponse response = service.reservePlayground(s);
		Assert.assertEquals(response, ReservationResponse.ALREADY_RESERVED);
		LOGGER.log(Level.INFO,"=============================================================");
	}

	/**
	 * Checks the bahaviour of the service when incorrect time information is supplied
	 * 
	 * @param s the time information
	 * @throws SQLException
	 */
	@Test(dataProvider="incorrectTimeIntervalsProvider")
	public void testIncorrectTimeInterval(Schedule s) throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		// check the input data - it should be incorrect but not in the past
		Assert.assertFalse(checkTimeIntervalCorrectness(s.getStartTime(),
				s.getEndTime()));
		Assert.assertFalse(checkTimeInPast(s.getStartTime(), s.getEndTime()));
		// assert that the time is not in the database before the service
		Assert.assertFalse(checkInfoInTable(s));
		
		// call the service and assert its response
		ReservationResponse response = service.reservePlayground(s);
		Assert.assertEquals(response, ReservationResponse.START_TIME_BEFORE_END_TIME_OR_EQUAL);
		
		// check nothing is added to the database
		Assert.assertFalse(checkInfoInTable(s));
		
		LOGGER.log(Level.INFO,"=============================================================");
	}

	/**
	 * Checks if the supplied time information is in the past
	 * 
	 * @param s the time information
	 * @throws SQLException
	 */
	@Test(dataProvider="pastSchedulesProvider")
	public void testPastTime(Schedule s) throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		// check the input data - it should be correct but in the past
		Assert.assertTrue(checkTimeIntervalCorrectness(s.getStartTime(),
				s.getEndTime()));
		Assert.assertTrue(checkTimeInPast(s.getStartTime(), s.getEndTime()));
		// assert that the time is not in the database before the service
		Assert.assertFalse(checkInfoInTable(s));
		
		// call the service and assert its response
		ReservationResponse response = service.reservePlayground(s);
		Assert.assertEquals(response, ReservationResponse.TIME_INFO_IN_PAST);
		
		// check nothing is added to the database
		Assert.assertFalse(checkInfoInTable(s));
		
		LOGGER.log(Level.INFO,"=============================================================");
	}
	
	/**
	 * The method checks if it is possible to reserve another playground for the same time
	 * 
	 * @param s the time information and the playground information
	 * @throws SQLException
	 */
	@Test(dependsOnMethods={"testAddValidReservation"},
			dataProvider = "validReservationProvider")
	public void testAnotherPlaygroundSameTime(Schedule s) throws SQLException {
		int existingPlaygroundId = Integer.parseInt(testDataFile.getString("existing.playground.id"));
		// check the id is different from the one in the database
		Assert.assertNotEquals(existingPlaygroundId, s.getPlayGroundId());
		
		s.setPlayGroundId(existingPlaygroundId);
		// check the info is not in the database
		Assert.assertFalse(checkInfoInTable(s));
		
		// call the service
		ReservationResponse response = service.reservePlayground(s);
		Assert.assertEquals(response, ReservationResponse.RESERVED_SUCCESSFULLY);
		
		// check the info is in the database
		Assert.assertTrue(checkInfoInTable(s));
	}
	
	/**
	 * The method returns true if the startTime is before the endTime
	 * 
	 * @param startTime
	 *            the start value for the time interval
	 * @param endTime
	 *            the end value for the time interval
	 * @return true if start time is before end time
	 */
	private boolean checkTimeIntervalCorrectness(Date startTime, Date endTime) {
		return startTime.getTime() <= endTime.getTime();
	}

	@DataProvider(name="pastSchedulesProvider")
	public Object[][] getPastTime() throws ParseException {
		return createSchedulesByKey("past.time.interval");
	}
	
	@DataProvider(name = "alreadyExistingReservation")
	public Object[][] getExistingReservations() throws ParseException {
		return createSchedulesByKey("reserved.schedules");
	}
	
	@DataProvider(name="incorrectTimeIntervalsProvider")
	public Object[][] getIncorrectTimeIntervals() throws ParseException {
		return createSchedulesByKey("incorrect.time.interval");
	}
	
	@DataProvider(name = "validReservationProvider")
	public Object[][] getValidReservationInfo() throws ParseException {
		int playGroundId = Integer.parseInt(testDataFile
				.getString("new.schedule.playground.id"));
		Date startDate = dateFormatter.parse(testDataFile.getString("new.schedule.start"));
		Date endDate = dateFormatter.parse(testDataFile.getString("new.schedule.end"));
		int userId = Integer.parseInt(testDataFile
				.getString("new.schedule.user.id"));

		// create schedule for reservation
		Schedule s = new Schedule();
		User u = new User();
		u.setId(userId);
		s.setUser(u);
		s.setPlayGroundId(playGroundId);
		s.setStartTime(startDate);
		s.setEndTime(endDate);

		return new Object[][] { new Object[] { s } };
	}
	
	/**
	 * Returns true if the time interval refers to time in the past
	 * 
	 * @param startTime
	 *            the start of the time interval
	 * @param endTime
	 *            the end of the time interval
	 * @return true if the time interval refers to time in the past
	 */
	private boolean checkTimeInPast(Date startTime, Date endTime) {
		Calendar cal = Calendar.getInstance();

		if (startTime.getTime() <= cal.getTime().getTime()
				|| endTime.getTime() <= cal.getTime().getTime()) {
			return true;
		}

		return false;
	}

	/**
	 * The method checks if the specified time interval [startTime; endTime] is
	 * already present in the database for a specified playground.
	 * 
	 * @param playgroundId
	 *            the id of the playground for which the check is performed
	 * @param startTime
	 *            the start of the time interval
	 * @param endTime
	 *            the end of the time interval
	 * @return true if the playground with id playgroundId is not free for the
	 *         time [startTime;endTime] an false otherwise
	 * 
	 * @throws SQLException
	 */
	private boolean checkPlaygroundTimeAvailability(int playgroundId,
			Date startTime, Date endTime) throws SQLException {
		PreparedStatement pst = connection
				.prepareStatement(SELECT_ALREADY_RESERVED_PLAYGROUND);
		pst.setInt(1, playgroundId);
		pst.setString(2, dateFormatter.format(startTime));
		pst.setString(3, dateFormatter.format(endTime));
		pst.setString(4, dateFormatter.format(startTime));
		pst.setString(5, dateFormatter.format(endTime));
		pst.setString(6, dateFormatter.format(startTime));
		pst.setString(7, dateFormatter.format(endTime));

		resultSet = pst.executeQuery();
		if (resultSet.next()) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if the schedule is already in the database
	 * 
	 * @param s
	 *            the schedule to be checked
	 * @return true if the schedule information is in the database
	 * @throws SQLException
	 */
	private boolean checkInfoInTable(Schedule s) throws SQLException {
		PreparedStatement pst = connection.prepareStatement(SELECT_SCHEDULE);
		pst.setInt(1, s.getUser().getId());
		pst.setInt(2, s.getPlayGroundId());
		pst.setString(3, dateFormatter.format(s.getStartTime()));
		pst.setString(4, dateFormatter.format(s.getEndTime()));
		resultSet = pst.executeQuery();

		if (resultSet.next()) {
			return resultSet.getInt(1) == 1;
		}

		return false;
	}

	private void closeDBStatements() throws SQLException {
		if (resultSet != null) {
			resultSet.close();
		}

		if (stmnt != null) {
			stmnt.close();
		}
	}
	
	/**
	 * The method generates schedules given a key in the testDataProperties file
	 * 
	 * @param key the key
	 * @return the schedules
	 * @throws ParseException 
	 */
	private Object[][] createSchedulesByKey(String key) throws ParseException {
		String[] schedules = testDataFile.getString(key)
				.split(ELEMENT_DELIMITER);
		Object[][] dataProvided = new Object[schedules.length][];
		int index = 0;

		for (String s : schedules) {
			String[] info = s.split(DATA_DELIMITER);

			Schedule newSchedule = new Schedule();
			User u = new User();
			u.setId(Integer.parseInt(info[3]));
			newSchedule.setUser(u);
			newSchedule.setPlayGroundId(Integer.parseInt(info[0]));
			newSchedule.setStartTime(dateFormatter.parse(info[1]));
			newSchedule.setEndTime(dateFormatter.parse(info[2]));
			dataProvided[index++] = new Object[] { newSchedule };
		}

		return dataProvided;
	}
}
