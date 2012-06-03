package com.futsall.functional.tests;

import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.SeleneseTestNgHelper;
 
public class HomeTestNavigation extends SeleneseTestNgHelper{
	private SeleniumServer server;
	
	@Override
	public void setUp(String url, String browserString, int port)
			throws Exception {
		RemoteControlConfiguration rcc = new RemoteControlConfiguration();
		rcc.setSingleWindow(true);
		rcc.setPort(4444);
		
		try {
			server = new SeleniumServer(false, rcc);
			server.boot();
			server.start();
		} catch (Exception e) {
			throw new IllegalStateException("Can't start selenium server", e);
		}
		
		super.setUp("http://localhost:8080","*firefox", 4444);
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		server.stop();
		System.out.println("Stopped");
	}
	
	@Test
	public void testHomeTestNavigation() throws Exception {
		selenium.open("/SOAClient/faces/home.xhtml");
		selenium.click("id=playgroundForm:country-selectItem0");
		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
				if (selenium
						.isElementPresent("id=playgroundForm:city-selectItem0"))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}

		selenium.click("id=playgroundForm:city-selectItem0");
		for (int second = 0;; second++) {
			if (second >= 60)
				fail("timeout");
			try {
				if (selenium
						.isElementPresent("id=playgroundForm:playground-selectItem3"))
					break;
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}

		selenium.click("id=playgroundForm:playground-selectItem3");
		selenium.click("id=playgroundForm:j_id_1y:button");
		selenium.waitForPageToLoad("30000");
		selenium.waitForPageToLoad("30000");
		assertEquals(selenium.getTitle(), "Reserve a playground!");
		assertTrue(selenium.isElementPresent("id=reserve-form:playgroundId"));
		assertEquals(selenium.getValue("id=reserve-form:playgroundId"),
				"Strumsko 2");
		assertTrue(selenium.isElementPresent("id=reserve-form:playgroundCity"));
		assertEquals(selenium.getValue("id=reserve-form:playgroundCity"),
				"Blagoevgrad");
		assertTrue(selenium
				.isElementPresent("id=reserve-form:playgroundCountry"));
		assertEquals(selenium.getValue("id=reserve-form:playgroundCountry"),
				"Bulgaria");
	}
}
