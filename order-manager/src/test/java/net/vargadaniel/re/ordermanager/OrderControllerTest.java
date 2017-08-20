package net.vargadaniel.re.ordermanager;

import java.util.Date;

import javax.validation.ConstraintViolationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import net.vargadaniel.re.ordermanager.OrderManagerApp;
import net.vargadaniel.re.ordermanager.OrderRestController;
import net.vargadaniel.re.ordermanager.OrderStreamListener;
import net.vargadaniel.re.ordermanager.ex.OrderExistsException;
import net.vargadaniel.re.ordermanager.model.Order;
import net.vargadaniel.re.ordermanager.model.Product;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderManagerApp.class)
@WebAppConfiguration
public class OrderControllerTest {
	
	@Autowired
	OrderRestController orderController;
	
	@Autowired
	OrderStreamListener orderListener;
	
	Product product;
	
	@Before
	public void init() {
		orderListener.registerProduct(new Product("hello-product"));
		product = orderController.findProductByName("hello-product");
	}
	
	@Test
	public void testCreate() throws Exception {
		Order order = orderController.createOrder(new Order(System.currentTimeMillis(), product));
		Assert.assertNotNull(order);
		Assert.assertEquals(product.getId(), order.getProduct().getId());
		Assert.assertEquals("hello-product", order.getProduct().getName());
	}
	
	@Test(expected=ConstraintViolationException.class)
	public void testNoCreationDate() throws OrderExistsException {
		orderController.createOrder(new Order(System.currentTimeMillis(), null));
	}

}
