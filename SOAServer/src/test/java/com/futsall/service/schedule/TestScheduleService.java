package com.futsall.service.schedule;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.futsall.schedule.ListOfSchedules;
import com.futsall.schedule.Schedule;
import com.futsall.service.restful.schedule.ScheduleService;

public class TestScheduleService {
	
	private ScheduleService service;
	
	@BeforeClass
	public void prepareData()
	{
		service = new ScheduleService();
	}
	
	private ListOfSchedules executeScheduleRequest(String inDate, String inPId) {
		
		return service.getSchedule(inDate, inPId);
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
}
