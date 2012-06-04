package com.futsall.service.playground;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.futsall.playGround.PlayGround;
import com.futsall.service.restful.playgrounds.PlayGroundsService;

public class PlayGroundsServiceTest {
	private static final Logger LOGGER = Logger
			.getLogger(PlayGroundsServiceTest.class.getName());

	private static final String SELECT_ALL_DISTINCT_COUNTRIES_STMNT = "SELECT DISTINCT p.country FROM minifootball.playground p";

	private static final String SELECT_FIRST_COMPANY = "SELECT c.id FROM minifootball.company c ORDER BY c.id DESC LIMIT 1";

	private static final String INSERT_PROTO_COMPANY = "INSERT INTO minifootball.company(name,address,email,telephone,city,country) "
			+ "VALUES(?;?,?,?,?,?,?)";

	private static final String INSERT_PLAYGROUND = "INSERT INTO minifootball.playground( "
			+ "companyId,name,address,telephone,email,width,length,flooring,city,country) "
			+ "VALUES";

	private static final String SELECT_COUNTRY_CNT = "SELECT COUNT(*) FROM minifootball.playground p WHERE p.country=";

	private static final String SELECT_CITY_CNT = "SELECT COUNT(*) FROM minifootball.playground p WHERE p.city=";

	private static final String SELECT_PLAYGROUND_CNT = "SELECT COUNT(*) FROM minifootball.playground p WHERE p.name=";

	private static final String UPDATE_NON_EXISTING_COUNTRY = "UPDATE minifootball.playground SET minifootball.playground.country=? "
			+ "WHERE minifootball.playground.country=?";

	private static final String UPDATE_NON_EXISTING_CITY = "UPDATE minifootball.playground SET minifootball.playground.city=? "
			+ "WHERE minifootball.playground.city=?";

	private static final String UPDATE_NON_EXISTING_PLAYGROUND_NAME = "UPDATE minifootball.playground SET minifootball.playground.name=? "
			+ "WHERE minifootball.playground.name=?";

	private static final String SELECT_ALL_CITIES = "SELECT DISTINCT p.city FROM minifootball.playground p "
			+ "WHERE p.country=";

	private static final String SELECT_ALL_PLAYGROUNDS = "SELECT DISTINCT p.name FROM minifootball.playground p "
			+ "WHERE p.country=? AND p.city=?";

	/**
	 * Delimiter between elements of the same type in the properties file
	 */
	private static final String ELEMENT_DELIMITER = ";";

	/**
	 * Delimiter between consequent data information for an element in a
	 * properties file
	 */
	private static final String DATA_DELIMITER = ",";

	private static Connection connection;

	private static Statement stmnt;

	private static ResultSet resultSet;

	private static PlayGroundsService service;

	private static List<String> countries;

	private static List<PlayGround> playGrounds;

	private static PropertyResourceBundle testDataFile;

	@BeforeClass
	public void setUpInitialDbState() throws ClassNotFoundException,
			SQLException, IOException {
		LOGGER.log(Level.INFO,"=============================================================");
		LOGGER.log(Level.INFO, "TestPlayGroundsService#beforeClass() started");
		// load playgrounds from properties file
		playGrounds = new ArrayList<>();

		InputStream is = getClass().getResourceAsStream(
				"playgroundTD.properties");
		testDataFile = new PropertyResourceBundle(is);
		is.close();
		String[] playArr = testDataFile.getString("playground").split(
				ELEMENT_DELIMITER);

		for (String p : playArr) {
			String[] infoHolder = p.split(DATA_DELIMITER);

			playGrounds.add(createPlayGround(infoHolder));
		}

		// get all the countries from the service, faking a client request
		service = new PlayGroundsService();
		countries = service.getCountries().getCountries();

		// manage connections and statements
		connection = service.getDbManager().getConnection();
		stmnt = connection.createStatement();
		LOGGER.log(Level.INFO, "TestPlayGroundsService#beforeClass() ended");
		LOGGER.log(Level.INFO,"=============================================================");
	}

	@AfterClass(alwaysRun = true)
	public void tearDownDbState() throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		LOGGER.log(Level.INFO, "TestPlayGroundsService#afterClass() started");
		if (connection != null) {
			closeDBStatements();
		}
		LOGGER.log(Level.INFO, "TestPlayGroundsService#afterClass() ended");
		LOGGER.log(Level.INFO,"=============================================================");
	}

	/**
	 * The method tests if the service method finds all the countries in the
	 * database and just them
	 * 
	 * @throws SQLException
	 *             if a database operation exception occurs
	 */
	@Test(groups = { "countryTest", "playgroundServices" })
	public void testAllCountriesExtraction() throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		LOGGER.log(Level.INFO,
				"TestPlayGroundsService#testAllCountriesExtraction() started");
		resultSet = stmnt.executeQuery(SELECT_ALL_DISTINCT_COUNTRIES_STMNT);
		List<String> allCountries = readStringEntities(resultSet);

		checkStringEntitiesEquality(allCountries, countries);
		LOGGER.log(Level.INFO,
				"TestPlayGroundsService#testAllCountriesExtraction() ended");
		LOGGER.log(Level.INFO,"=============================================================");
	}

	/**
	 * The method depends on the method testAllCountriesExtraction. It tests if
	 * after new countries are added to the database they are found by the
	 * service method
	 * 
	 * @throws SQLException
	 *             if a error occurs in a database operation
	 */
	@Test(dependsOnMethods = { "testAllCountriesExtraction" }, groups = {
			"countryTest", "playgroundServices" })
	public void testNewAddingNewCountries() throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		LOGGER.log(Level.INFO,
				"TestPlayGroundsService#testNewAddingNewCountries() started");
		connection.setAutoCommit(false);
		temporaryInsertCompany(true);

		// extract the the old and the new countries
		List<String> newCountries = service.getCountries().getCountries();
		List<String> unionCountries = new ArrayList<>();

		// roll the changes to the database back
		connection.rollback();
		connection.setAutoCommit(true);

		for (String c : countries) {
			unionCountries.add(c);
		}

		for (PlayGround p : playGrounds) {
			unionCountries.add(p.getCountry());
		}

		checkStringEntitiesEquality(newCountries, unionCountries);

		// extract data after the playgrounds deletion and compare it to the
		// firstly extracted countries
		newCountries = service.getCountries().getCountries();

		checkStringEntitiesEquality(newCountries, countries);
		LOGGER.log(Level.INFO,
				"TestPlayGroundsService#testNewAddingNewCountries() started");
		LOGGER.log(Level.INFO,"=============================================================");
	}

	/**
	 * The method tests if a non existing country in the database is contained
	 * in the service response. The method temporarily changes the database,
	 * while replacing an existing country with a non-existing country and
	 * checks again if the response from the service is updated
	 * 
	 * @param nonExistingCountry
	 *            the non-existing country name
	 * @param existingCountry
	 *            the existing country name
	 * @throws SQLException
	 *             if a database connection error occurs
	 */
	@Test(dataProvider = "nonexistingCountriesProvider", dependsOnMethods = { "testAllCountriesExtraction" }, groups = {
			"countryTest", "playgroundServices" })
	public void testNonExistingCountry(String nonExistingCountry,
			String existingCountry) throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		LOGGER.log(Level.INFO,
				"TestPlayGroundsService#testNonExistingCountry(String nonExistingCountry,"
						+ "String existingCountry) started");
		// check the server response
		assertCountriesExistance(nonExistingCountry, existingCountry, countries);
		connection.setAutoCommit(false);
		try {
			updateEntity(nonExistingCountry, existingCountry,
					UPDATE_NON_EXISTING_COUNTRY);
			// the new service response contains the previously non existing
			// country
			// and does not contain the previously existing service
			assertCountriesExistance(existingCountry, nonExistingCountry,
					service.getCountries().getCountries());
		} finally {
			connection.rollback();
			connection.setAutoCommit(false);
		}
		LOGGER.log(Level.INFO,
				"TestPlayGroundsService#testNonExistingCountry(String nonExistingCountry,"
						+ "String existingCountry) ended");
		LOGGER.log(Level.INFO,"=============================================================");
	}

	/**
	 * The method tests if the cities for a given country found by the
	 * PlayGroundsService#getCities() are identical to the cities in the
	 * database
	 * 
	 * @throws SQLException
	 */
	@Test(dataProvider = "countryServiceProvider", groups = { "cityTest",
			"playgroundServices" }, dependsOnGroups = { "countryTest" })
	public void testCitiesInDB(String country) throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		LOGGER.log(Level.INFO,
				"TestPlayGroundsService# testCitiesInDB(String country) started");
		List<String> countriesForCurrentCity = readStringEntities(stmnt
				.executeQuery(SELECT_ALL_CITIES + "\'" + country + "\'"));
		// call the service for the current country
		List<String> citiesByService = service.getCities(country).getCities();

		checkStringEntitiesEquality(countriesForCurrentCity, citiesByService);
		LOGGER.log(Level.INFO,
				"TestPlayGroundsService# testCitiesInDB(String country) ended");
		LOGGER.log(Level.INFO,"=============================================================");
	}

	/**
	 * The method adds new playgrounds which are related to the company with the
	 * greatest id or to a new company if none exists. Then the method checks if
	 * the service response is updated and reverts the changes to the database.
	 * 
	 * @throws SQLException
	 *             if a database error occurs
	 */
	@Test(dependsOnGroups = { "countryTest" }, groups = { "cityTest",
			"playgroundServices" }, dependsOnMethods = { "testCitiesInDB", })
	public void testNewCities() throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		LOGGER.log(Level.INFO,
				"TestPlayGroundsService# testNewCities() started");
		connection.setAutoCommit(false);
		temporaryInsertCompany(true);

		// extract the the old and the new countries
		List<String> newCountries = service.getCountries().getCountries();
		for (String c : newCountries) {
			testCitiesInDB(c);
		}

		// roll the changes to the database back
		connection.rollback();
		connection.setAutoCommit(true);
		LOGGER.log(Level.INFO, "TestPlayGroundsService# testNewCities() ended");
		LOGGER.log(Level.INFO,"=============================================================");
	}

	/**
	 * The method changes a non-existing city with an existing city in the
	 * database and checks if the service response is updated
	 * 
	 * @param nonExistingCity
	 *            the non-existing city
	 * @param existingCity
	 *            the existing city
	 * @throws SQLException
	 */
	@Test(dataProvider = "citiesExistanceProvider", groups = { "cityTest",
			"playgroundServices" }, dependsOnGroups = { "countryTest" }, dependsOnMethods = { "testCitiesInDB" })
	public void testCitiesExistance(String nonExistingCity, String existingCity)
			throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		LOGGER.log(
				Level.INFO,
				"TestPlayGroundsService#testCitiesExistance(String nonExistingCity,String existingCity) started");
		assertCitiesExistence(nonExistingCity, existingCity, countries);

		connection.setAutoCommit(false);
		try {
			updateEntity(nonExistingCity, existingCity,
					UPDATE_NON_EXISTING_CITY);
			// the new service response contains the previously non existing
			// country
			// and does not contain the previously existing service
			assertCitiesExistence(existingCity, nonExistingCity, service
					.getCountries().getCountries());
		} finally {
			connection.rollback();
			connection.setAutoCommit(false);
		}
		LOGGER.log(
				Level.INFO,
				"TestPlayGroundsService#testCitiesExistance(String nonExistingCity,String existingCity) ended");
		LOGGER.log(Level.INFO,"=============================================================");
	}

	/**
	 * The method checks if the playgrounds from the server response are the
	 * same as the data ones in the database
	 * 
	 * @param country
	 *            the name of the country which we search playgrounds for
	 * @param city
	 *            the name of the city which we search playgrounds for
	 * @throws SQLException
	 */
	@Test(groups = { "playgroundServices" }, dependsOnGroups = { "cityTest" }, dataProvider = "playGroundsProvider")
	public void testAllPlayGrounds(String country, String city)
			throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		LOGGER.log(
				Level.INFO,
				"TestPlayGroundsService#testAllPlayGrounds(String country, String city) started");
		// get playgrounds from the database
		PreparedStatement pst = connection
				.prepareStatement(SELECT_ALL_PLAYGROUNDS);
		pst.setString(1, country);
		pst.setString(2, city);
		List<String> dbPlaygrounds = readStringEntities(pst.executeQuery());
		// get playgrounds from the service
		List<PlayGround> servicePlaygrounds = service.getPlayGrounds(city,
				country).getPlayGrounds();
		List<String> servicePlaygroundsNames = getPlaygroundNames(servicePlaygrounds);

		// check if playgrounds from the database are equal to those from the
		// service
		checkStringEntitiesEquality(dbPlaygrounds, servicePlaygroundsNames);
		LOGGER.log(
				Level.INFO,
				"TestPlayGroundsService#testAllPlayGrounds(String country, String city) ended");
		LOGGER.log(Level.INFO,"=============================================================");
	}

	/**
	 * The method inserts a new company and checks if the playgrounds return
	 * from the server are the same as the ones in the database.
	 * 
	 * @throws SQLException
	 */
	@Test(groups = { "playgroundServices" }, dependsOnGroups = { "cityTest" }, dependsOnMethods = { "testAllPlayGrounds" })
	public void testNewPlaygrounds() throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		LOGGER.log(Level.INFO,
				"TestPlayGroundsService#testNewPlaygrounds() started");
		connection.setAutoCommit(false);
		// add the new company
		temporaryInsertCompany(true);

		// extract the countries
		List<String> newCountries = service.getCountries().getCountries();
		for (String c : newCountries) {
			// extract the new cities for the current country
			List<String> newCities = service.getCities(c).getCities();

			// test the playgrounds for each country and city pair
			for (String city : newCities) {
				testAllPlayGrounds(c, city);
			}
		}

		// roll the changes to the database back
		connection.rollback();
		connection.setAutoCommit(true);
		LOGGER.log(Level.INFO,
				"TestPlayGroundsService#testNewPlaygrounds() ended");
		LOGGER.log(Level.INFO,"=============================================================");
	}

	/**
	 * The method replaces the existing playground name with a non-existing one
	 * in the database and checks if the service updates its response
	 * 
	 * @param existingPlayground
	 *            the name of the existing playground
	 * @param nonExistingPlayground
	 *            the name of the non-existing playground
	 * @throws SQLException
	 */
	@Test(groups = { "playgroundServices" }, dependsOnGroups = { "cityTest" }, dataProvider = "updatePlaygroundsProvider")
	public void testUpdatedPlayground(String existingPlayground,
			String nonExistingPlayground) throws SQLException {
		LOGGER.log(Level.INFO,"=============================================================");
		LOGGER.log(
				Level.INFO,
				"TestPlayGroundsService#testUpdatedPlayground(String existingPlayground,"
				+"String nonExistingPlayground) started");
		// check the initial state in the database and in the service response
		assertPlaygroundsExistence(nonExistingPlayground, existingPlayground,
				countries);
		// update the database, by substituting the existing with the
		// non-existing playground name
		connection.setAutoCommit(false);
		try {
			updateEntity(nonExistingPlayground, existingPlayground,
					UPDATE_NON_EXISTING_PLAYGROUND_NAME);
			// the new service response contains the previously non existing
			// country
			// and does not contain the previously existing service
			assertPlaygroundsExistence(existingPlayground,
					nonExistingPlayground, service.getCountries()
							.getCountries());
		} finally {
			connection.rollback();
			connection.setAutoCommit(false);
		}
		LOGGER.log(
				Level.INFO,
				"TestPlayGroundsService#testUpdatedPlayground(String existingPlayground,"
				+"String nonExistingPlayground) ended");
		LOGGER.log(Level.INFO,"=============================================================");
	}

	@DataProvider(name = "updatePlaygroundsProvider")
	public Object[][] getNonExistingPlaygrounds() {
		String[] nonExistingPlaygrounds = testDataFile.getString(
				"non.existing.playgrounds").split(ELEMENT_DELIMITER);
		String existingPlayground = testDataFile
				.getString("existing.playground");
		Object[][] dataProvided = new Object[nonExistingPlaygrounds.length][];
		int index = 0;
		for (String nep : nonExistingPlaygrounds) {
			dataProvided[index++] = new Object[] { existingPlayground, nep };
		}

		return dataProvided;
	}

	/**
	 * The method supplies the tests with every possible pair country-city from
	 * the service response
	 * 
	 * @return an iterator of country-city array objects
	 */
	@DataProvider(name = "playGroundsProvider")
	public Iterator<Object[]> getPlaygroundLocation() {
		List<Object[]> countryCityList = new ArrayList<>();

		for (String country : countries) {
			List<String> cities = service.getCities(country).getCities();
			for (String city : cities) {
				String[] strArr = new String[2];
				strArr[0] = country;
				strArr[1] = city;

				countryCityList.add(strArr);
			}
		}

		return countryCityList.iterator();
	}

	@DataProvider(name = "citiesExistanceProvider")
	public Object[][] existingCitiesProvider() {
		String[] nonExistingCities = testDataFile.getString(
				"non.existing.cities").split(ELEMENT_DELIMITER);
		String existingCity = testDataFile.getString("existing.city");
		Object[][] providedData = new Object[nonExistingCities.length][];

		int index = 0;
		for (String s : nonExistingCities) {
			providedData[index++] = new Object[] { s, existingCity };
		}

		return providedData;
	}

	@DataProvider(name = "countryServiceProvider")
	public Object[][] countryServiceExtractor() {
		Object[][] providedCountries = new Object[countries.size()][];

		int index = 0;
		for (String c : countries) {
			providedCountries[index++] = new Object[] { c };
		}

		return providedCountries;
	}

	/**
	 * The method asserts the non-existence and existence of the first and the
	 * second countries correspondingly
	 * 
	 * @param nonExistingCountry
	 *            the non-existing country
	 * @param existingCountry
	 *            the existing country
	 * @param elements
	 *            the collection where the elements are asserted
	 * @throws SQLException
	 */
	private void assertCountriesExistance(String nonExistingCountry,
			String existingCountry, Collection<String> elements)
			throws SQLException {
		// The country must not exist
		Assert.assertTrue(searchNonExistingEntryInDB(SELECT_COUNTRY_CNT + "\'"
				+ nonExistingCountry + "\'"), nonExistingCountry
				+ " exists in the database!");
		// The country must exist
		Assert.assertFalse(searchNonExistingEntryInDB(SELECT_COUNTRY_CNT + "\'"
				+ existingCountry + "\'"), existingCountry
				+ " does not exist in the database!");

		Assert.assertFalse(elements.contains(nonExistingCountry));
		Assert.assertTrue(elements.contains(existingCountry));
	}

	@DataProvider(name = "nonexistingCountriesProvider")
	public Object[][] countryNamesProvider() {
		String existingCountryName = testDataFile.getString("existing.country");
		String[] nonExistingCountryNames = testDataFile.getString(
				"non.existing.countries").split(ELEMENT_DELIMITER);

		Object[][] dataProvider = new Object[nonExistingCountryNames.length][];
		int index = 0;
		for (String s : nonExistingCountryNames) {
			dataProvider[index++] = new Object[] { s, existingCountryName };
		}

		return dataProvider;
	}

	private void updateEntity(String newValue, String oldValue, String query)
			throws SQLException {
		PreparedStatement pstmnt = connection.prepareStatement(query);
		pstmnt.setString(1, newValue);
		pstmnt.setString(2, oldValue);
		pstmnt.executeUpdate();
	}

	private void assertPlaygroundsExistence(String nonExistionPlayground,
			String existingPlayground, Collection<String> countries)
			throws SQLException {
		// Check the database
		Assert.assertTrue(searchNonExistingEntryInDB(SELECT_PLAYGROUND_CNT
				+ "\'" + nonExistionPlayground + "\'"), nonExistionPlayground
				+ " exists in the database!");
		Assert.assertFalse(searchNonExistingEntryInDB(SELECT_PLAYGROUND_CNT
				+ "\'" + existingPlayground + "\'"), existingPlayground
				+ " does not exist in the database!");

		// Check the service
		boolean isExistingPlaygroundFound = false;
		List<String> citiesForCurrentCountry = null;
		List<String> currentPlaygrounds = null;

		for (String country : countries) {
			citiesForCurrentCountry = service.getCities(country).getCities();

			for (String city : citiesForCurrentCountry) {
				currentPlaygrounds = getPlaygroundNames(service.getPlayGrounds(
						city, country).getPlayGrounds());
				Assert.assertFalse(currentPlaygrounds
						.contains(nonExistionPlayground));

				if (!isExistingPlaygroundFound) {
					isExistingPlaygroundFound = currentPlaygrounds
							.contains(existingPlayground);
				}
			}
		}
		Assert.assertTrue(isExistingPlaygroundFound);
	}

	private void assertCitiesExistence(String nonExistingCity,
			String existingCity, Collection<String> elements)
			throws SQLException {
		// The city must not exist
		Assert.assertTrue(searchNonExistingEntryInDB(SELECT_CITY_CNT + "\'"
				+ nonExistingCity + "\'"), nonExistingCity
				+ " exists in the database!");
		// The city must exist
		Assert.assertFalse(searchNonExistingEntryInDB(SELECT_CITY_CNT + "\'"
				+ existingCity + "\'"), existingCity
				+ " does not exist in the database!");

		boolean isExistingCityFound = false;
		List<String> citiesForCurrentCountry = null;

		for (String country : elements) {
			citiesForCurrentCountry = service.getCities(country).getCities();

			Assert.assertFalse(citiesForCurrentCountry
					.contains(nonExistingCity));
			if (!isExistingCityFound) {
				isExistingCityFound = citiesForCurrentCountry
						.contains(existingCity);
			}
		}
		Assert.assertTrue(isExistingCityFound);
	}

	/**
	 * Constructs a list of the names of the given playgrounds
	 * 
	 * @param playgrounds
	 *            the playgrounds which names we get
	 * @return the list of names
	 */
	private List<String> getPlaygroundNames(List<PlayGround> playgrounds) {
		List<String> names = new ArrayList<>();

		for (PlayGround p : playgrounds) {
			names.add(p.getName());
		}

		return names;
	}

	/**
	 * The method checks if a country exists in the database and if not it
	 * returns true
	 * 
	 * @param inCountry
	 *            the name of the country to be searched for
	 * @return true if the country does not exist in the database
	 * @throws SQLException
	 *             if a problem while reading from the database occurs
	 */
	private boolean searchNonExistingEntryInDB(String query)
			throws SQLException {
		resultSet = stmnt.executeQuery(query);
		if (resultSet.next()) {
			if (resultSet.getInt(1) == 0) {
				// no country
				return true;
			}
		}

		return false;
	}

	private void addPlaygrounds(int companyId) throws SQLException {
		StringBuilder builder = new StringBuilder(INSERT_PLAYGROUND);
		int playgroundsCnt = playGrounds.size();

		for (int i = 0; i < playgroundsCnt; ++i) {
			PlayGround p = playGrounds.get(i);

			builder.append("(");
			builder.append(companyId).append(",");
			builder.append("\'").append(p.getName()).append("\'").append(",");
			builder.append("\'").append(p.getAddress()).append("\'")
					.append(",");
			builder.append("\'").append(p.getTelephone()).append("\'")
					.append(",");
			builder.append("\'").append(p.getEmail()).append("\'").append(",");
			builder.append(p.getWidth()).append(",");
			builder.append(p.getLength()).append(",");
			builder.append("\'").append(p.getFlooring()).append("\'")
					.append(",");
			builder.append("\'").append(p.getCity()).append("\'").append(",");
			builder.append("\'").append(p.getCountry()).append("\'");
			builder.append(")");

			if (i < playgroundsCnt - 1) {
				builder.append(",");
			}
		}

		String query = builder.toString();
		stmnt.executeUpdate(query);
	}

	/**
	 * Checks the equality of two lists of strings, element by element
	 * 
	 * @param stringList1
	 *            the first list of strings
	 * @param stringList2
	 *            the second list of strings
	 */
	private void checkStringEntitiesEquality(List<String> stringList1,
			List<String> stringList2) {
		for (String c : stringList1) {
			Assert.assertTrue(stringList2.contains(c));
		}

		for (String c : stringList2) {
			Assert.assertTrue(stringList1.contains(c));
		}
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
	 * The method reads all the strings from a result set and parses them into a
	 * list of strings
	 * 
	 * @param rs
	 *            the ResultSet instance used to read the countries
	 * @return the read elements
	 * @throws SQLException
	 */
	private List<String> readStringEntities(ResultSet rs) throws SQLException {
		List<String> list = new ArrayList<>();

		while (rs.next()) {
			list.add(rs.getString(1));
		}

		return list;
	}

	/**
	 * Creates a play ground instance given an array containing its information
	 * in the form
	 * name,address,telephone,email,width,length,flooring,city,country
	 * 
	 * @param infoHolder
	 *            the arraay of strings with playground information
	 * @return the new playground instance
	 */
	private PlayGround createPlayGround(String[] infoHolder) {
		PlayGround pG = new PlayGround();
		pG.setName(infoHolder[0]);
		pG.setAddress(infoHolder[1]);
		pG.setTelephone(infoHolder[2]);
		pG.setEmail(infoHolder[3]);
		pG.setWidth(Double.valueOf(infoHolder[4]));
		pG.setLength(Double.valueOf(infoHolder[5]));
		pG.setFlooring(infoHolder[6]);
		pG.setCity(infoHolder[7]);
		pG.setCountry(infoHolder[8]);

		return pG;
	}

	/**
	 * Inserts (temporary) in the database a company and some playgrounds for it
	 * if no company exists at all, or adds the playgrounds to the last company
	 * in the table(with greatest id)
	 * 
	 * @param isRollBackAllowed
	 *            if true roll back is allowed on exception occurs
	 * @return id the id of the company that is changed
	 * @throws SQLException
	 *             if there are errors while communicating with the database
	 */
	private int temporaryInsertCompany(boolean isRollBackAllowed)
			throws SQLException {
		resultSet = stmnt.executeQuery(SELECT_FIRST_COMPANY);
		int id = 1;

		if (resultSet.next()) {
			id = resultSet.getInt(1);
		} else {
			PreparedStatement pstmnt = null;
			pstmnt = connection.prepareStatement(INSERT_PROTO_COMPANY);
			String[] infoHolder = testDataFile.getString("company").split(
					DATA_DELIMITER);

			try {
				pstmnt.setInt(1, id);

				for (int i = 2; i < 8; ++i) {
					pstmnt.setString(i, infoHolder[i]);
				}
				// create a new country
				pstmnt.executeUpdate();
				pstmnt.close();
			} catch (SQLException sqle) {
				// rollback the chagnes to the database and propagate the
				// exception
				if (isRollBackAllowed) {
					connection.rollback();
					connection.setAutoCommit(true);
				}
				pstmnt.close();
				throw sqle;
			}
		}

		try {
			addPlaygrounds(id);
		} catch (SQLException sqle) {
			// rollback the chagnes to the database and propagate the exception
			connection.rollback();
			connection.setAutoCommit(true);
			throw sqle;
		}

		return id;
	}
}
