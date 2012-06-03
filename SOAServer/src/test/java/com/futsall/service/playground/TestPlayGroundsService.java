//package com.futsall.service.playground;
//
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.PropertyResourceBundle;
//
//import org.testng.Assert;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//import com.futsall.playGround.PlayGround;
//import com.futsall.service.restful.playgrounds.PlayGroundsService;
//
//public class TestPlayGroundsService {
//	private static final String MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";
//
//	private static final String SELECT_ALL_DISTINCT_COUNTRIES_STMNT = 
//			"SELECT DISTINCT p.country FROM minifootball.playground p";
//	
//	private static final String SELECT_FIRST_COMPANY =
//			"SELECT c.id FROM minifootball.company c ORDER BY c.id DESC LIMIT 1";
//	
//	private static final String INSERT_PROTO_COMPANY=
//			"INSERT INTO minifootball.company(name,address,email,telephone,city,country) " +
//			"VALUES(?;?,?,?,?,?,?)";
//
//	private static final String INSERT_PLAYGROUND=
//			"INSERT INTO minifootball.playground( "+
//			"companyId,name,address,telephone,email,width,length,flooring,city,country) "+
//			"VALUES";
//	
//	private static final String DATABASE_NAME = "minifootball";
//	
//	/**
//	 * Delimiter between elements of the same type in the properties file
//	 */
//	private static final String ELEMENT_DELIMITER = ";";
//	
//	/**
//	 * Delimiter between consequent data information for an element in a properties file
//	 */
//	private static final String DATA_DELIMITER= ",";
//
//	private static Connection connection;
//
//	private static Statement stmnt;
//
//	private static ResultSet resultSet;
//
//	private static PlayGroundsService service;
//
//	private static List<String> countries;
//	
//	private static List<PlayGround> playGrounds; 
//
//	private static PropertyResourceBundle testDataFile;
//	
//	@BeforeClass
//	public void setUpInitialDbState() throws ClassNotFoundException,
//			SQLException, IOException {
//		// load playgrounds from properties file
//		playGrounds = new ArrayList<>();
//		testDataFile = new PropertyResourceBundle(
//				getClass().getResourceAsStream("testData.properties"));
//		
//		String[] playArr = testDataFile.getString("playground").split(ELEMENT_DELIMITER);
//		
//		for(String p : playArr) {
//			String[] infoHolder = p.split(DATA_DELIMITER);
//			
//			playGrounds.add(createPlayGround(infoHolder));
//		}
//		
//		
//		// get all the countries from the service, faking a client request
//		service = new PlayGroundsService();
//		countries = service.getCountries().getCountries();
//
//		PropertyResourceBundle bundle = new PropertyResourceBundle(
//				getClass().getResourceAsStream("/com/futsall/configuration.properties"));
//
//		openConnection(DATABASE_NAME,
//				bundle.getString("db.username"),
//				bundle.getString("db.password")
//				);
//
//		stmnt = connection.createStatement();
//	}
//
//	@AfterClass(alwaysRun=true)
//	public void tearDownDbState() throws SQLException {
//		if (connection != null) {
//			closeConnection();
//		}
//	}
//
//	@Test
//	public void testAllCountriesInitialState() throws SQLException {
//		resultSet = stmnt.executeQuery(SELECT_ALL_DISTINCT_COUNTRIES_STMNT);
//		List<String> allCountries = readCountries(resultSet);
//
//		checkCountriesEquality(allCountries, countries);
//	}
//
//	@Test(dependsOnMethods = { "testAllCountriesInitialState" })
//	public void testAllCountriesExtraction() throws SQLException {
//		resultSet = stmnt.executeQuery(SELECT_FIRST_COMPANY);
//		int id = 1;
//		
//		connection.setAutoCommit(false);
//		
//		if(resultSet.next()) {
//			id = resultSet.getInt(1); 
//		} else {
//			PreparedStatement pstmnt = null;
//			pstmnt = connection.prepareStatement(INSERT_PROTO_COMPANY);
//			String[] infoHolder =testDataFile.getString("company").split(DATA_DELIMITER);
//			
//			try{
//				pstmnt.setInt(1, id);
//				
//				for(int i=2;i<8;++i) {
//					pstmnt.setString(i, infoHolder[i]);
//				}
//				// create a new country
//				pstmnt.executeUpdate();
//				pstmnt.close();
//			} catch (SQLException sqle) {
//				// rollback the chagnes to the database and propagate the exception
//				connection.rollback();
//				pstmnt.close();
//				throw sqle;
//			} 
//		}
//		
//		try {
//			addPlaygrounds(id);
//		} catch(SQLException sqle) {
//			// rollback the chagnes to the database and propagate the exception
//			connection.rollback();
//			throw sqle;
//		}
//		
//		// extract the the old and the new countries
//		List<String> newCountries = service.getCountries().getCountries();
//		List<String> unionCountries = new ArrayList<>();
//		
////		if(id>1) {
////			removeAddedCompany(id);
////		}
//		
//		connection.rollback();
//		
//		for(String c : countries) {
//			unionCountries.add(c);
//		}
//		
//		for(PlayGround p : playGrounds) {
//			unionCountries.add(p.getCountry());
//		}
//		
//		checkCountriesEquality(newCountries, unionCountries);
//		
//		// extract data after the playgrounds deletion and compare it to the firstly extracted countries
//		newCountries = service.getCountries().getCountries();
//		
//		checkCountriesEquality(newCountries, countries);
//	}
//
//	/**
//	 * The company is deleted and with it all of its playgrounds
//	 * 
//	 * @param id the id of the company to be deleted
//	 * @throws SQLException
//	 */
//	private void removeAddedCompany(int id) throws SQLException {
//		stmnt.executeUpdate("DELETE FROM minifootball.company WHERE minifootball.company.id ="+id);
//	}
//	
//	private void addPlaygrounds(int companyId) throws SQLException {
//		StringBuilder builder =new StringBuilder(INSERT_PLAYGROUND);
//		int playgroundsCnt = playGrounds.size();
//		
//		for(int i=0;i<playgroundsCnt;++i ) {
//			PlayGround p = playGrounds.get(i);
//			
//			builder.append("(");
//			builder.append(companyId).append(",");
//			builder.append("\'").append(p.getName()).append("\'").append(",");
//			builder.append("\'").append(p.getAddress()).append("\'").append(",");
//			builder.append("\'").append(p.getTelephone()).append("\'").append(",");
//			builder.append("\'").append(p.getEmail()).append("\'").append(",");
//			builder.append(p.getWidth()).append(",");
//			builder.append(p.getLength()).append(",");
//			builder.append("\'").append(p.getFlooring()).append("\'").append(",");
//			builder.append("\'").append(p.getCity()).append("\'").append(",");
//			builder.append("\'").append(p.getCountry()).append("\'");
//			builder.append(")");
//			
//			if(i<playgroundsCnt-1) {
//				builder.append(",");
//			}
//		}
//		
//		String query = builder.toString();
//		stmnt.executeUpdate(query);
//	}
//	
//	/**
//	 * Checks the equality of two lists of countries, element by element
//	 * 
//	 * @param countries
//	 *            the first list of countries
//	 * @param countries2
//	 *            the second list of countries
//	 */
//	private void checkCountriesEquality(List<String> countries,
//			List<String> countries2) {
//		for (String c : countries) {
//			Assert.assertTrue(countries2.contains(c));
//		}
//
//		for (String c : countries2) {
//			Assert.assertTrue(countries.contains(c));
//		}
//	}
//
//	/**
//	 * The method opens a new connection to the database
//	 * 
//	 * @param inDbName
//	 *            the name of the database
//	 * @param inUsername
//	 *            the name of the user to log to the database
//	 * @param inPassword
//	 *            the password of the user to log in with
//	 * @throws SQLException
//	 * @throws ClassNotFoundException
//	 */
//	private void openConnection(String inDbName, String inUsername,
//			String inPassword) throws SQLException, ClassNotFoundException {
//		Class.forName(MYSQL_DRIVER_CLASS);
//
//		connection = DriverManager.getConnection("jdbc:mysql://localhost/"
//				+ inDbName, inUsername, inPassword);
//	}
//
//	private void closeConnection() throws SQLException {
//		if (resultSet != null) {
//			resultSet.close();
//		}
//
//		if (stmnt != null) {
//			stmnt.close();
//		}
//
//		if (connection != null) {
//			connection.close();
//		}
//	}
//
//	/**
//	 * The method reads all the countries from the database
//	 * 
//	 * @param rs
//	 *            the ResultSet instance used to read the countries
//	 * @return the read countries
//	 * @throws SQLException
//	 */
//	private List<String> readCountries(ResultSet rs) throws SQLException {
//		List<String> list = new ArrayList<>();
//
//		while (rs.next()) {
//			list.add(rs.getString(1));
//		}
//
//		return list;
//	}
//	
//	/**
//	 * Creates a play ground instance given an array containing its information in 
//	 * the form name,address,telephone,email,width,length,flooring,city,country
//	 * 
//	 * @param infoHolder the arraay of strings with playground information
//	 * @return the new playground instance
//	 */
//	private PlayGround createPlayGround(String[] infoHolder) {
//		PlayGround pG = new PlayGround();
//		pG.setName(infoHolder[0]);
//		pG.setAddress(infoHolder[1]);
//		pG.setTelephone(infoHolder[2]);
//		pG.setEmail(infoHolder[3]);
//		pG.setWidth(Double.valueOf(infoHolder[4]));
//		pG.setLength(Double.valueOf(infoHolder[5]));
//		pG.setFlooring(infoHolder[6]);
//		pG.setCity(infoHolder[7]);
//		pG.setCountry(infoHolder[8]);
//		
//		return pG;
//	}
//}
