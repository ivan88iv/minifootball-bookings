package com.futsall.service.schedule;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PropertyResourceBundle;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.futsall.schedule.ReservationResponse;
import com.futsall.schedule.Schedule;
import com.futsall.service.restful.schedule.ReservationService;
import com.futsall.user.User;

public class TestReservationService {

	private static final String SELECT_SCHEDULE = 
			"SELECT COUNT(*) FROM minifootball.reserved r WHERE r.userId=? "+
			"AND r.playGroundId=? AND startTime=? AND endTime=?";
	
	private static PropertyResourceBundle testDataFile;
	
	private static ReservationService service;
	
	private static Connection connection;
	
	private static Statement stmnt;
	
	private static ResultSet resultSet;
	
	@BeforeClass
	public void beforeClass() throws IOException, SQLException {
		testDataFile = new PropertyResourceBundle(
				getClass().getResourceAsStream("/com/futsall/service/testData.properties"));

		// get all the countries from the service, faking a client request
		service = new ReservationService();
		// manage connections and statements
		connection = service.getDbManager().getConnection();
		stmnt = connection.createStatement();
		connection.setAutoCommit(false);
	}

	@AfterClass
	public void afterClass() throws SQLException {
		if (connection != null) {
			connection.rollback();
			closeDBStatements();
		}
	}

	/**
	 * Tests some valid input to the service and if it is going to change the database
	 * 
	 * @param s
	 * @throws SQLException
	 */
	@Test(dataProvider="validReservationProvider")
	public void testAddValidReservation(Schedule s) throws SQLException {
		Assert.assertFalse(checkInfoInTable(s));
		ReservationResponse response =
				service.reservePlayground(s);
		Assert.assertEquals(response, ReservationResponse.RESERVED_SUCCESSFULLY);
		
		// check the database
		Assert.assertTrue(checkInfoInTable(s));
	}
	
	private boolean checkInfoInTable(Schedule s) throws SQLException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		PreparedStatement pst = connection.prepareStatement(SELECT_SCHEDULE);
		pst.setInt(1, s.getUser().getId());
		pst.setInt(2, s.getPlayGroundId());
		pst.setString(3, df.format(s.getStartTime()));
		pst.setString(4, df.format(s.getEndTime()));
		resultSet = pst.executeQuery();
		
		if(resultSet.next()) {
			return resultSet.getInt(1)==1;
		}
		
		return false;
	}
	
	@DataProvider(name="validReservationProvider")
	public Object[][] getValidReservationInfo() throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		int playGroundId = Integer.parseInt(testDataFile.getString("new.schedule.playground.id"));
		Date startDate = df.parse(testDataFile.getString("new.schedule.start"));
		Date endDate = df.parse(testDataFile.getString("new.schedule.end"));
		int userId =  Integer.parseInt(testDataFile.getString("new.schedule.user.id"));
		
		//create schedule for reservation
		Schedule s = new Schedule();
		User u = new User();
		u.setId(userId);
		s.setUser(u);
		s.setPlayGroundId(playGroundId);
		s.setStartTime(startDate);
		s.setEndTime(endDate);
		
		return new Object[][]{new Object[]{s}};
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
