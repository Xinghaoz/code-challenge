package com.shutterfly.www.event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * This class is the abstraction of the events.  There are 4 subclass that 
 * extends this class to represent the 4 actual events respectively.  
 */
public abstract class Event {
	private EventType type;
	private VerbType verb;
	private String customerId;
	private Date eventTime;
	
	/**
     * This constructor generate a general event that its 4 arguments are the 
     * fields that every kind of event shares.
     * 
     * @param type a enum that indicates the type of the event.
     * @param verb a enum that indicates the type of the verb.
   	 * @param customerId the id that related to the specific customer. 
   	 * @param eventTime a Date instance that stores that time of the event.
     */
	public Event(EventType type, VerbType verb, String customerId, Date eventTime) {
		this.type = type;
		this.verb = verb;
		this.customerId = customerId;
		this.eventTime = eventTime;
	}
	
	public EventType getType() {
		return this.type;
	}
	
	public VerbType getVerb() {
		return this.verb;
	}
	
	public String getCustomerId() {
		return this.customerId;
	}
	
	public Date getEventTime() {
		return this.eventTime;
	}
	
	public enum EventType {
		CUSTOMER("CUSTOMER"),
		SITE_VISIT("SITE_VISIT"),
		IMAGE("IMAGE"),
		ORDER("ORDER");
		
		private final String str;
		
		EventType(String event) {
			this.str = event;
		}
		
		public static EventType fromString(String str) {
			switch (str) {
			case "CUSTOMER": return EventType.CUSTOMER;
			case "SITE_VISIT": return EventType.SITE_VISIT;
			case "IMAGE": return EventType.IMAGE;
			case "ORDER": return EventType.ORDER;
			default:
				throw new RuntimeException("Invalid string value for conversion to EventType");
			}
		}
	}
	
	public enum VerbType {
		NEW,
		UPDATE,
		UPLOAD;
	}	
	
    /**
     * Each subclass here represents a specific kind of event.
     */
	public static class CustomerEvent extends Event {
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
			this.adrState = state;
		}
		
		public String getLastName() {
			return this.lastName;
		}
		
		public String getAdrCity() {
			return this.adrCity;
		}
		
		public String getAdrState() {
			return this.adrState;
		}
	}
	
	public static class SiteVisitEvent extends Event {
		String pageId;
		Map<String, String> tags;
		
		public SiteVisitEvent(EventType type, VerbType verb, String customerId, Date eventTime) {
			super(type, verb, customerId, eventTime);	
			this.tags = new HashMap<>();
		}
				
		public void setPageId(String id) {
			this.pageId = id;
		}
		
		public void addTag(String key, String value) {
			this.tags.put(key, value);
		}
		
		public String getPageId() {
			return this.pageId;
		}
				
		public Map<String, String> getTags() {
			return this.tags;
		}
	}
	
	public static class ImageUploadEvent extends Event {
		String imageId;
		String cameraMake;
		String cameraModel;
		
		public ImageUploadEvent(EventType type, VerbType verb, String customerId, Date eventTime) {
			super(type, verb, customerId, eventTime);	
		}
		
		public void setImageId(String id) {
			this.imageId = id;
		}
		
		public void setCameraMake(String cameraMake) {
			this.cameraMake = cameraMake;
		}
		
		public void setCameraModel(String cameraModel) {
			this.cameraModel = cameraModel;
		}
		
		public String getImageId() {
			return this.imageId;
		}
		
		public String getCameraMake() {
			return this.cameraMake;
		}
		
		public String getCameraModel() {
			return this.cameraModel;
		}
	}
	
	public static class OrderEvent extends Event {
		String orderId;
		double totalAmount;
		
		public OrderEvent(EventType type, VerbType verb, String customerId, Date eventTime) {
			super(type, verb, customerId, eventTime);	
		}
		
		public void setOrderId(String id) {
			this.orderId = id;
		}
		
		public void setTotalAmount(double amount) {
			this.totalAmount = amount;
		}
		
		public String getOrderId() {
			return this.orderId;
		}
		
		public double getTotalAmount() {
			return this.totalAmount;
		}
	}
	
}
