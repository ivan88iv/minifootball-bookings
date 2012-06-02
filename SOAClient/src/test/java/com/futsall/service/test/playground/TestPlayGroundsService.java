package com.futsall.service.test.playground;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestPlayGroundsService {
	private static final String SELECT_ALL_COUNTRIES_STMNT=
			"SELECT DISTINCT p.country FROM minifootball.playground p";
	
	@BeforeClass
	public void setUpInitialDbState() {
		
	}
	
	@Test
	public void testAllCountriesInitialState() {
 	}

	@Test
	public void testDistinctCountriesInitialState() {
		
	}
	
	@Test(dependsOnMethods={"testAllCountriesInitialState"})
	public void testAllCountriesExtraction() {

	}
}
