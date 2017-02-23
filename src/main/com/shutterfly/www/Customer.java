package com.shutterfly.www;

import java.util.Date;

public class Customer implements Comparable<Customer> {
	Date startDate, endDate;
	int totalNumberOfVisit;
	double totalExpenditures;
	double averageLifetimeValue;
	
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
}
