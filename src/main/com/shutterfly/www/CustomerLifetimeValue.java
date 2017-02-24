package com.shutterfly.www;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.shutterfly.www.event.*;
import com.shutterfly.www.event.Event.*;

/*
 * This is the main class that contains the main method and it servers
 * as a container that stores all the customer information and events.
 */
public class CustomerLifetimeValue {
	private Map<String, Customer> customerMap;
	private String filename; // Store the filename to generate corresponding
							 // name of the output file

	// The endTime is the latest time we can see from all the events.
	private Date endTime;

	public CustomerLifetimeValue() {
		this.customerMap = new HashMap<>();
	}

    /**
     * This method updates the data d by the given event.
     *
     * @param e an Event class generate from the JSON input.
     * @param d a CustomerLifetimeValue instance that hold all the information
     * 			about the events and customers.
     *
     */
	public static void ingest(Event e, CustomerLifetimeValue d) {
		String id = e.getCustomerId();
		Customer customer = null;

		/* Check whether the customer exists.
		 *
		 * It seems that should be done in the "CUSTOMER" event (As I comment
		 * out below).  However, the instruction says that "All events have a
		 * key and event_time, but are received with no guaranteed order and
		 * with fluctuating frequency."  And as the sample_input indicates
		 * that a non-CUSTOMER event could be received before the "NEW" event
		 * of the customer.  Therefore we should check whether the customer
		 * exists no matter what the incoming event is.
		 *
		 */
		if (!d.customerMap.containsKey(id)) {
			customer = new Customer(id);
			d.customerMap.put(id, customer);
		} else {
			customer = d.customerMap.get(id);
		}

		// For all the events, we need to update the date.
		Date eventTime = e.getEventTime();
		customer.updateTime(eventTime);

		// Update the endTime, if it is the latest time we can see currently.
		if (d.endTime == null || eventTime.after(d.endTime)) {
			d.endTime = eventTime;
		}

		if (e.getType() == EventType.CUSTOMER) {
			CustomerEvent event = (CustomerEvent) e;

			/*
			 * I comment out here purposely
			// Check whether the customer already exists.
			if (event.getVerb() == VerbType.NEW) {
				customer = new Customer(id);
				d.customerMap.put(id, customer);
			} else if (event.getVerb() == VerbType.UPDATE) {
				customer = d.customerMap.get(id);
			}
			*/

			if (event.getLastName() != null) {
				customer.setLastname(event.getLastName());
			}
			if (event.getAdrCity() != null) {
				customer.setAdrCity(event.getAdrCity());
			}
			if (event.getAdrState() != null) {
				customer.setAdrState(event.getAdrState());
			}

		}

		else if (e.getType() == EventType.SITE_VISIT) {
			customer.increaseVisit();
			customer.addSiteVisitEvent(e);
		}

		// It seems that we don't need to do anything about this event
		// in this challenge.
		else if (e.getType() == EventType.IMAGE) {
			customer.addImageUploadEvent(e);
		}

		else if (e.getType() == EventType.ORDER) {
			OrderEvent event = (OrderEvent) e;
			customer.updateExpenditure(event.getTotalAmount());
			customer.addOrderEvent(e);
		}
	}

	/**
     * This method return the top x customers with the highest Simple
     * Lifetime Value from data D.
     *
     * @param x the number of customer with top Lifetime Value that the user
     * 			wants to return.
     * @param d a CustomerLifetimeValue instance that hold all the information
     * 			about the events and customers.
   	 *
     */
	public static void topXSimpleLTVCustomers(int x, CustomerLifetimeValue d) {
		PriorityQueue<Customer> heap = new PriorityQueue<>();

		for (Map.Entry<String, Customer> entry : d.customerMap.entrySet()) {
			Customer customer = entry.getValue();;
			customer.updateAverageLifetimeValue(d.endTime);
			heap.offer(customer);
		}

		File faker = new File("");
		String outputPath = faker.getAbsoluteFile().getParentFile().getPath() + "/output/";
		int index = 1;

		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(outputPath + "output_" + d.filename), "utf-8"));
			while (!heap.isEmpty() && x > 0) {
				x--;
				Customer top = heap.poll();
				System.out.println("####### " + index + " #######");
				System.out.println(top);
				writer.write("####### " + index + " #######\n");
				writer.write(top.toString() + "\n");
				index++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			   try {writer.close();} catch (Exception e) {e.printStackTrace();}
		}
	}

    /**
     * This method read the file of the given file name in the "input" folder.
     *
     * @param filename the filename of the file in the "input" folder.
     * 			the file must be in the "input" folder.
     * @return a CustomerLifetimeValue instance that contains all the
     * 			events and all information needs to get the Lifetime
     * 			value of all users.
     */
	public static CustomerLifetimeValue readFile(String filename) {
		CustomerLifetimeValue data = new CustomerLifetimeValue();
		data.filename = filename;
		JSONParser parser = new JSONParser();

		try {
			File faker = new File("");
			String inputPath = faker.getAbsoluteFile().getParentFile().getPath() + "/input/";
			Object obj = parser.parse(new FileReader(inputPath + filename));
			JSONArray events = (JSONArray) obj;
			Iterator<JSONObject> iterator = events.iterator();

			/*
			 * Iterate the events to generate a specific Event instance
			 * for each event in the JSON file.  Then pass this instance
			 * into the ingest method.
			 */
			while (iterator.hasNext()) {
				JSONObject event = iterator.next();
				DateFormat format = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss.SSSS");
				String typeStr = (String) event.get("type");
				String verbStr = (String) event.get("verb");
				String timeStr = (String) event.get("event_time");
				Date date = format.parse(timeStr);

				if (typeStr.equals("CUSTOMER")) {
					String customerId = (String) event.get("key");
					CustomerEvent newEvent = new CustomerEvent(EventType.valueOf(typeStr),
													   VerbType.valueOf(verbStr),
													   customerId,
													   date);

					// Handle non-required field.
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
				}

				else if (typeStr.equals("SITE_VISIT")) {
					String customerId = (String) event.get("customer_id");
					SiteVisitEvent newEvent = new SiteVisitEvent(EventType.valueOf(typeStr),
											     VerbType.valueOf(verbStr),
											     customerId,
											     date);

					// Handle non-required field.
					newEvent.setPageId((String) event.get("page_id"));
					if (event.containsKey("tags")) {
						JSONObject tagJson = (JSONObject) event.get("tags");
						for (Object keyObj : tagJson.keySet()) {
							String key = (String) keyObj;
							String value = (String) tagJson.get(keyObj);
							newEvent.addTag(key, value);
						};
					}
					ingest(newEvent, data);
				}

				else if (typeStr.equals("IMAGE")) {
					String customerId = (String) event.get("customer_id");
					ImageUploadEvent newEvent = new ImageUploadEvent(EventType.valueOf(typeStr),
											     VerbType.valueOf(verbStr),
											     customerId,
											     date);

					// Handle non-required field.
					newEvent.setImageId((String) event.get("image_id"));
					if (event.containsKey("camera_make")) {
						newEvent.setCameraMake((String) event.get("camera_make"));
					}
					if (event.containsKey("camera_model")) {
						newEvent.setCameraModel((String) event.get("camera_model"));
					}
					ingest(newEvent, data);
				}

				else if (typeStr.equals("ORDER")) {
					String customerId = (String) event.get("customer_id");
					OrderEvent newEvent = new OrderEvent(EventType.valueOf(typeStr),
											     VerbType.valueOf(verbStr),
											     customerId,
											     date);
					newEvent.setOrderId((String) event.get("key"));

					// Use regex to extract the double value.
					Pattern pattern = Pattern.compile("[-+]?[0-9]*\\.?[0-9]*");
					Matcher matcher = pattern.matcher((String) event.get("total_amount"));
					if (matcher.find()) {
						newEvent.setTotalAmount(Double.valueOf(matcher.group()));
					}
					ingest(newEvent, data);
				}


			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	public static void main(String[] args) {
		CustomerLifetimeValue test1 = readFile("events.txt");
		topXSimpleLTVCustomers(10, test1);

		CustomerLifetimeValue test2 = readFile("testCustomerWithoutExpenditure.txt");
		topXSimpleLTVCustomers(10, test2);

		CustomerLifetimeValue test3 = readFile("testTimeSpanExactOneWeek.txt");
		topXSimpleLTVCustomers(10, test3);

		CustomerLifetimeValue test4 = readFile("testTimeSpanExactTwoWeeks.txt");
		topXSimpleLTVCustomers(10, test4);

		CustomerLifetimeValue test5 = readFile("testTop5OutOf10Customers.txt");
		topXSimpleLTVCustomers(5, test5);

		CustomerLifetimeValue test6 = readFile("testTop-1OutOf10Customers.txt");
		topXSimpleLTVCustomers(-1, test6);

		CustomerLifetimeValue test7 = readFile("testTop14OutOf10Customers.txt");
		topXSimpleLTVCustomers(14, test7);

		CustomerLifetimeValue test8 = readFile("random50.txt");
		topXSimpleLTVCustomers(25, test8);

		CustomerLifetimeValue test9 = readFile("random500.txt");
		topXSimpleLTVCustomers(25, test8);

		CustomerLifetimeValue test10 = readFile("random2500.txt");
		topXSimpleLTVCustomers(25, test10);

		CustomerLifetimeValue test11 = readFile("random10000.txt");
		topXSimpleLTVCustomers(25, test11);
	}

}
