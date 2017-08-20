package net.vargadaniel.re.ordermanager;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.vargadaniel.re.ordermanager.ex.OrderExistsException;
import net.vargadaniel.re.ordermanager.model.Order;
import net.vargadaniel.re.ordermanager.model.OrderStatus;
import net.vargadaniel.re.ordermanager.model.Product;

@RestController
public class OrderRestController {
	
	static Logger log = LoggerFactory.getLogger(OrderRestController.class);
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	ReportEngine reportEngine;
	
	@RequestMapping(value="/orders", method=RequestMethod.GET)
	Iterable<Order> findAllOrders() {
		return orderRepository.findAll();
	}
	
	@RequestMapping(value="/products", method=RequestMethod.GET)
	Iterable<Product> findAllProducts() {
		return productRepository.findAll();
	}
	
	Product findProductByName(String productName) {
		return productRepository.findByName(productName);
	}
	
	@RequestMapping(value="/orders", method=RequestMethod.POST)
	Order createOrder(@RequestBody Order order) throws OrderExistsException {
		log.info("Processing order : " + order);
		if (order.getId() != null && orderRepository.exists(order.getId())) {
			throw new OrderExistsException();
		} else {
			order.setTimestamp(System.currentTimeMillis());
			order.getStatuses().add(new OrderStatus(order, System.currentTimeMillis(), "order received"));
			if (order.getProduct() != null) {
				Product product = productRepository.findOne(order.getProduct().getId());
				order.setProduct(product);
			}
			order = orderRepository.save(order);
			if (order.getProduct().getName() == null) {
				throw new NullPointerException("product.name cannot be null!");
			}
			log.info("order.product.name : " + order.getProduct().getName());
			reportEngine.orders().send(MessageBuilder.withPayload(order).setHeader("productName", order.getProduct().getName()).build());
			return order;
		}
	}

	

}
