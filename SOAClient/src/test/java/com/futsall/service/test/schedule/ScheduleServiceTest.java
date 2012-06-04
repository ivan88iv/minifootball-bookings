package com.futsall.service.test.schedule;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.futsall.schedule.ListOfSchedules;
import com.futsall.schedule.Schedule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class ScheduleServiceTest {

	private WebResource resource;

	@BeforeClass
	public void prepareData() {

		ClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);
		resource = client.resource(getBaseUri());
	}

	private ListOfSchedules executeScheduleRequest(String date, String id) {

		return resource.path("/schedule").queryParam("date", date).
				queryParam("playGroundId", id)
				.accept(MediaType.APPLICATION_XML).get(ListOfSchedules.class);
	}

	@Test
	public void testGetSchedule() {
		List<Schedule> schedules = executeScheduleRequest("2012-02-22", "1").getSchedules();

		Assert.assertEquals(schedules.size(), 1, "Size is " + schedules.size());
		Assert.assertEquals(schedules.get(0).getUser().getId(), 1, "User id is " + schedules.get(0).getUser().getId());

		schedules = executeScheduleRequest("2012-03-21", "4").getSchedules();

		Assert.assertEquals(schedules.size(), 1, "Size is " + schedules.size());
		Assert.assertEquals(schedules.get(0).getUser().getId(), 2, "User id is " + schedules.get(0).getUser().getId());
	}

	private static URI getBaseUri() {
		return UriBuilder.fromUri(
				"http://localhost:8090/SOAServer/minifootball").build();
	}
}
