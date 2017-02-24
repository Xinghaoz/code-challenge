package com.shutterfly.www;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.shutterfly.www.event.Event;

/*
 * This class simulate a customer with specific customer id.  It also stores
 * the necessary information that needs to calculate the Lifetime value in 
 * this challenge.  Furthermore, it implements the Comparable interface, which
 * helps us sort the customer by its average Lifetime Value.
 */
public class Customer implements Comparable<Customer> {
	String id, lastname, adrCity, adrState;	// The customer's basic information
	
	// These variables are used for calculate the LTV of this customer.
	Date startTime;
	int totalNumberOfVisit;
	double totalExpenditures;
	double averageLifetimeValue;
	
	/* 
	 * Because the instruction says "You are still required to ingest events 
	 * even if they are not consumed as part of this challenge."  and we 
	 * don't know what these events will be used for in the future, the
	 * best way is to categorize and store them into different kind of list.
	 */
	List<Event> siteVisitEventList;
	List<Event> imageUploadEventList;
	List<Event> orderEventList;
	
	public Customer(String id) {
		this.id = id;
		
		this.totalExpenditures = 0.0;
		this.averageLifetimeValue = 0.0;
		this.totalNumberOfVisit = 0;
		this.siteVisitEventList = new ArrayList<>();
		this.imageUploadEventList = new ArrayList<>();
		this.orderEventList = new ArrayList<>();
	}
	
	public void setLastname(String name) {
		this.lastname = name;
	}
	
	public void setAdrCity(String city) {
		this.adrCity = city;
	}
	
	public void setAdrState(String state) {
		this.adrState = state;
	}
	
	public void addSiteVisitEvent(Event e) {
		this.siteVisitEventList.add(e);
	}
	
	public void addImageUploadEvent(Event e) {
		this.imageUploadEventList.add(e);
	}
	
	public void addOrderEvent(Event e) {
		this.orderEventList.add(e);
	}
	
	public String getId() {
		return this.id;
	}
	
	@Override
	public int compareTo(Customer other) {
		if (other.averageLifetimeValue > this.averageLifetimeValue) {
			return 1;
		} else if (other.averageLifetimeValue < this.averageLifetimeValue) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public void updateTime(Date newTime) {
		if (startTime == null || newTime.before(startTime)) {
			startTime = newTime;
		} 
	}
	
	public void increaseVisit() {
		this.totalNumberOfVisit++;
	}
	
	public void updateExpenditure(double e) {
		this.totalExpenditures += e;
	}
	
	/**
     * This function calculates the average Lifetime Value of this customer.
     * It calculates the Lifetime Value by the following equation:
     *
     * LTV = 52(a) * t
     * 
     * Where a is the customer's Average Lifetime Value and t is the Average
     * Customer Lifespan.  Here t is 10 years.
     * 
     * The start time is the earliest time we can see for this user, while 
     * the end time is the latest time we can see for all the events of 
     * all the users.
     * 
     * @param endTime the latest time we can see for all the events of all
     * 			the users.
     */
	public void updateAverageLifetimeValue(Date endTime) {
		long timeDiffInMilli = endTime.getTime() - startTime.getTime();
		int numOfWeeks = (int)(timeDiffInMilli / (1000 * 60 * 60 * 24 * 7)) + 1;
		this.averageLifetimeValue = Math.round(100.0 * 
				(this.totalExpenditures / this.totalNumberOfVisit) *    // Ave Expenditures per visit.
				((double)this.totalNumberOfVisit / numOfWeeks) * 		// Ave visit per week
				10 * 52)												// Number of weeks in 10 years.
				/ 100.0;												// Round to 2 decimals.
	}
	
	@Override
	public String toString() {
		return "id: " + this.id + "\n" +
			   "LastName: " + lastname + ", adrCity: " + adrCity + 
			   ", adrState: " + adrState + "\n" +
			   "Earliest activity time: " + startTime.toString() + "\n" +
			   "Total number of visit: " + totalNumberOfVisit + "\n" +
			   "Total expenditures: " + totalExpenditures + "\n" + 
			   "Average Lifetime Value: " + averageLifetimeValue + "\n" +
			   "#########################\n";
	}
}
