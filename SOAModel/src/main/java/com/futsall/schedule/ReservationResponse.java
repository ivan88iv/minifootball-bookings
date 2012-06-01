package com.futsall.schedule;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public enum ReservationResponse {
	DB_ERROR,START_TIME_BEFORE_END_TIME_OR_EQUAL,TIME_INFO_IN_PAST,ALREADY_RESERVED,RESERVED_SUCCESSFULLY 
}
