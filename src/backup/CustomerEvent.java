package com.shutterfly.www.event;

import java.util.Date;

public class CustomerEvent extends Event {
	String lastName;
	String adrCity;
	String adrState;
	
	public CustomerEvent(EventType type, VerbType verb, String customerId, Date eventTime) {
		super(type, verb, customerId, eventTime);	
	}
	
	public void setLastName(String name) {
		this.lastName = name;
	}
	
	public void setAdrCity(String city) {
		this.adrCity = city;
	}
	
	public void setAdrState(String state) {
		this.adrCity = state;
	}
}