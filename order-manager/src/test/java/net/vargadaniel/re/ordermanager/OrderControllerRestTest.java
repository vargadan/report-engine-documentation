package net.vargadaniel.re.ordermanager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.vargadaniel.re.ordermanager.OrderManagerApp;
import net.vargadaniel.re.ordermanager.OrderRestController;
import net.vargadaniel.re.ordermanager.OrderStreamListener;
import net.vargadaniel.re.ordermanager.model.Order;
import net.vargadaniel.re.ordermanager.model.Product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderManagerApp.class)
@WebAppConfiguration
public class OrderControllerRestTest {

	MockMvc mockMvc;
	
	@Autowired
	OrderRestController orderController;
	
	@Autowired
	OrderStreamListener orderListener;
	
	Product product;
	
	@Before
	public void init() {
		orderListener.registerProduct(new Product("hello-product"));
		product = orderController.findProductByName("hello-product");
		
		mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
	}
	
	@Test
	public void testOrderCreate() throws Exception {
		Order order = new Order(null, product);
		MvcResult result = mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(asJsonString(order))).andReturn();
		Order resultOrder = asObject(result.getResponse().getContentAsByteArray(), Order.class);
		Assert.assertNotNull(resultOrder);
		Assert.assertNotNull(resultOrder.getTimestamp());
		Assert.assertNotNull(resultOrder.getStatuses());
		Assert.assertNotNull(resultOrder.getProduct());
		Assert.assertEquals(product.getId(), resultOrder.getProduct().getId());
		Assert.assertEquals(product.getName(), resultOrder.getProduct().getName());
	}
	
    /*
     * converts a Java object into JSON representation
     */
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> T asObject(final byte[] json, Class<T> clazz) {
        try {
            return new ObjectMapper().readerFor(clazz).readValue(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
}
