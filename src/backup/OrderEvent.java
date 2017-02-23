package com.shutterfly.www.event;

import java.util.Date;

public class OrderEvent extends Event {
	String orderId;
	double totalAmount;
	
	public OrderEvent(EventType type, VerbType verb, String customerId, Date eventTime) {
		super(type, verb, customerId, eventTime);	
	}
}
