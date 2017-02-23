package com.shutterfly.www.event;

import java.util.Date;
import java.util.List;

public class SiteVisitEvent extends Event {
	String pageId;
	List<String> tags;
	
	public SiteVisitEvent(EventType type, VerbType verb, String customerId, Date eventTime) {
		super(type, verb, customerId, eventTime);	
	}
			
	public void addTag(String tag) {
		this.tags.add(tag);
	}
}