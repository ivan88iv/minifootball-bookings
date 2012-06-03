package com.futsall.service.test.playground;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.futsall.service.restful.playgrounds.PlayGroundsService;

public class TestPlayGroundsService {
	private static final String MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";

	private static final String SELECT_ALL_DISTINCT_COUNTRIES_STMNT = 
			"SELECT DISTINCT p.country FROM minifootball.playground p";

	private static final String SELECT_ALL_COUNTRIES_STMNT = 
			"SELECT DISTINCT p.country FROM minifootball.playground p";

	private static final String DATABASE_NAME = "minifootball";

	private static Connection connection;

	private static Statement stmnt;

	private static ResultSet resultSet;

	private static PlayGroundsService service;

	private static List<String> countries;

	@BeforeClass
	public void setUpInitialDbState() throws ClassNotFoundException,
			SQLException, IOException {
		// get all the countries from the service, faking a client request
		service = new PlayGroundsService();
		countries = service.getCountries().getCountries();

		PropertyResourceBundle bundle = new PropertyResourceBundle(
				getClass().getResourceAsStream("testUsersData.properties"));

		openConnection(DATABASE_NAME, 
				"root","123"
//				bundle.getString("db.username"),
//				bundle.getString("db.password")
				);

		stmnt = connection.createStatement();
	}

	@AfterClass
	public void tearDownDbState() throws SQLException {
		if (connection != null) {
			closeConnection();
		}
	}

	@Test
	public void testAllCountriesInitialState() throws SQLException {
		resultSet = stmnt.executeQuery(SELECT_ALL_COUNTRIES_STMNT);
		List<String> allCountries = readCountries(resultSet);

		checkCountriesEquality(allCountries, countries);
	}

	@Test
	public void testDistinctCountriesInitialState() {

	}

	@Test(dependsOnMethods = { "testAllCountriesInitialState" })
	public void testAllCountriesExtraction() {

	}

	/**
	 * Checks the equality of two lists of countries, element by element
	 * 
	 * @param countries
	 *            the first list of countries
	 * @param countries2
	 *            the second list of countries
	 */
	private void checkCountriesEquality(List<String> countries,
			List<String> countries2) {
		for (String c : countries) {
			Assert.assertTrue(countries2.contains(c));
		}

		for (String c : countries2) {
			Assert.assertTrue(countries.contains(c));
		}
	}

	/**
	 * The method opens a new connection to the database
	 * 
	 * @param inDbName
	 *            the name of the database
	 * @param inUsername
	 *            the name of the user to log to the database
	 * @param inPassword
	 *            the password of the user to log in with
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private void openConnection(String inDbName, String inUsername,
			String inPassword) throws SQLException, ClassNotFoundException {
		Class.forName(MYSQL_DRIVER_CLASS);

		connection = DriverManager.getConnection("jdbc:mysql://localhost/"
				+ inDbName, inUsername, inPassword);
	}

	private void closeConnection() throws SQLException {
		if (resultSet != null) {
			resultSet.close();
		}

		if (stmnt != null) {
			stmnt.close();
		}

		if (connection != null) {
			connection.close();
		}
	}

	/**
	 * The method reads all the countries from the database
	 * 
	 * @param rs
	 *            the ResultSet instance used to read the countries
	 * @return the read countries
	 * @throws SQLException
	 */
	private List<String> readCountries(ResultSet rs) throws SQLException {
		List<String> list = new ArrayList<>();

		while (rs.next()) {
			list.add(rs.getString(1));
		}

		return list;
	}
}
