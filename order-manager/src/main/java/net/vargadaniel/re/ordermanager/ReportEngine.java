package net.vargadaniel.re.ordermanager;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface ReportEngine {
	
	String PRODUCTS = "products";
	String ORDERS = "orders";
	String STATUS_UPDATES = "statusUpdates";
	
	@Input(PRODUCTS)
	SubscribableChannel products();
	
	@Output(ORDERS)
	MessageChannel orders();
	
	@Input(STATUS_UPDATES)
	SubscribableChannel statusUpdates();
}
