package net.vargadaniel.re.ordermanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

import net.vargadaniel.re.ordermanager.model.Order;
import net.vargadaniel.re.ordermanager.model.OrderStatus;
import net.vargadaniel.re.ordermanager.model.Product;
import net.vargadaniel.re.ordermanager.vo.StatusUpdate;

@EnableBinding(ReportEngine.class)
public class OrderStreamListener {
	
	static Logger log = LoggerFactory.getLogger(OrderStreamListener.class);
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	ReportEngine reportEngine;	
	
	@StreamListener(ReportEngine.PRODUCTS)
	public void registerProduct(Product newProduct) {
		Product oldProduct = productRepository.findByName(newProduct.getName());
		if (oldProduct == null) {
			productRepository.save(newProduct);
		}
	}
	
	@StreamListener(ReportEngine.STATUS_UPDATES)
	public void updateStatus(Message<StatusUpdate> statusUpdateMsg) {
		StatusUpdate statusUpdate = statusUpdateMsg.getPayload();
		log.info("statusUpdate : " + statusUpdate);
		Order order = orderRepository.findOne(Long.valueOf(statusUpdate.getOrderId()));
		Long timestamp = statusUpdateMsg.getHeaders().getTimestamp();
		order.getStatuses().add(new OrderStatus(order, timestamp, statusUpdate.getStatus()));
		orderRepository.save(order);
	}

}
