package com.futsall.client;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.futsall.playGround.ListOfPlayGrounds;
import com.futsall.playGround.PlayGround;
import com.futsall.schedule.ListOfSchedules;
import com.futsall.schedule.Schedule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class ClientApp {
	public static void main(String[] args) {
		ClientConfig clientConfig = new DefaultClientConfig();
		Client client = Client.create(clientConfig);
		WebResource resource = client.resource(getBaseUri());
		
		ListOfPlayGrounds playGrounds =
				resource.path("/playgrounds").queryParam("city", "Blagoevgrad").
					queryParam("country", "Bulgaria").accept(MediaType.APPLICATION_XML).get(
					ListOfPlayGrounds.class);
		List<PlayGround> list = playGrounds.getPlayGrounds();
		for(PlayGround pg : list) {
			System.out.println("Name " + pg.getName());
		}
		
		ListOfSchedules schedules = 
				resource.path("/schedule").queryParam("date", "2012-02-22").
					queryParam("playGroundId", "1").accept(MediaType.APPLICATION_XML).
					get(ListOfSchedules.class);
		List<Schedule> sList = schedules.getSchedules();
		for(Schedule s : sList) {
			System.out.println("Start " + s.getStartTime() + " / End  " + s.getEndTime());
		}
//		
//		ListOfCountries countries = 
//				resource.path("/playgrounds/countries").accept(MediaType.APPLICATION_XML).
//					get(ListOfCountries.class);
//		List<String> cList = countries.getCountries();
//		for(String c : cList) {
//			System.out.println(c);
//		}
	}
	
	private static URI getBaseUri() {
		return UriBuilder.fromUri("http://localhost:8090/SOAServer/minifootball").build();
	}
}
