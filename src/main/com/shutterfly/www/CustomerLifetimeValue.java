package com.shutterfly.www;

import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.shutterfly.www.event.*;
import com.shutterfly.www.event.Event.*;

public class CustomerLifetimeValue {
	Map<String, Customer> customerMap;
	
	public CustomerLifetimeValue() {
		this.customerMap = new HashMap<>();
	}
	
	public static void ingest(Event e, CustomerLifetimeValue d) {
		
	}
	
	public static void topXSimpleLTVCustomers(int x, CustomerLifetimeValue D) {
		
	}
	
	public static void main(String[] args) {
		JSONParser parser = new JSONParser();
		CustomerLifetimeValue data = new CustomerLifetimeValue();

		try {
			Object obj = parser.parse(new FileReader("events.txt"));
			JSONArray events = (JSONArray) obj;

			Iterator<JSONObject> iterator = events.iterator();
			
			while (iterator.hasNext()) {
				JSONObject event = iterator.next();
				DateFormat format = new SimpleDateFormat("yyyy-mm-dd:HH:mm:ss.SSSS");
				String typeStr = (String) event.get("type");
				String verbStr = (String) event.get("verb");
				String timeStr = (String) event.get("event_time");
//				Event newEvent;
				Date date = format.parse(timeStr);
				
				System.out.println(timeStr);
				
				if (typeStr.equals("CUSTOMER")) {
					String customerId = (String) event.get("key");
					CustomerEvent newEvent = new CustomerEvent(EventType.valueOf(typeStr), 
													   VerbType.valueOf(verbStr),
													   customerId,
													   date);
					if (event.containsKey("last_name")) {
						newEvent.setLastName((String) event.get("last_name"));
					}
					if (event.containsKey("adr_city")) {
						newEvent.setAdrCity((String) event.get("adr_city"));
					}
					if (event.containsKey("adr_state")) {
						newEvent.setAdrState((String) event.get("adr_state"));
					}
					ingest(newEvent, data);
				} else if (typeStr.equals("SITE_VISIT")) {
					String customerId = (String) event.get("customer_id");
					SiteVisitEvent newEvent = new SiteVisitEvent(EventType.valueOf(typeStr), 
											     VerbType.valueOf(verbStr),
											     customerId,
											     date);
					if (event.containsKey("tags")) {
						System.out.println(event.get("tags"));
//						JSONArray tags = (JSONArray) event.get("tags");
//						Iterator<JSONObject> tagIterator = tags.iterator();
//						while (tagIterator.hasNext()) {
//							System.out.println(tagIterator.next().toString());
//							newEvent.addTag((String) tagIterator.next());
//						}
//						newEvent.setAdrState((String) event.get("adr_state"));
					}
				} else if (typeStr.equals("IMAGE")) {
					String customerId = (String) event.get("customer_id");
					ImageUploadEvent newEvent = new ImageUploadEvent(EventType.valueOf(typeStr), 
											     VerbType.valueOf(verbStr),
											     customerId,
											     date);
				} else if (typeStr.equals("ORDER")) {
					String customerId = (String) event.get("customer_id");
					OrderEvent newEvent = new OrderEvent(EventType.valueOf(typeStr), 
											     VerbType.valueOf(verbStr),
											     customerId,
											     date);
				}
				

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
