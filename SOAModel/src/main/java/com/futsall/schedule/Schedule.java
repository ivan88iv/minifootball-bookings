package com.futsall.schedule;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.futsall.user.User;

@XmlRootElement
public class Schedule implements Serializable {
/**
	 * 
	 */
	private static final long serialVersionUID = 3960952239425289969L;

	private int playGroundId;
	
	private User user;
	
	private Date startTime;
	
	private Date endTime;

	public int getPlayGroundId() {
		return playGroundId;
	}

	public void setPlayGroundId(int playGroundId) {
		this.playGroundId = playGroundId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
