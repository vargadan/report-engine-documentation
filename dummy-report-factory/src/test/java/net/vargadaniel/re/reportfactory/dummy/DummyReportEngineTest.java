package net.vargadaniel.re.reportfactory.dummy;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import net.vargadaniel.re.reportfactory.dummy.model.DummyReport;
import net.vargadaniel.re.reportfactory.dummy.model.DummyReportOrder;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DummyReportFactoryApp.class)
@WebAppConfiguration
public class DummyReportEngineTest {
	
	@Autowired
	DummyReportEngine engine;
	
	@Test
	public void testNormalOrder() throws Exception {
		DummyReportOrder order = new DummyReportOrder();
		
		order.setId(1l);
		order.setProperties(new HashMap<>());
		order.getProperties().put(DummyReportMeta.RPOP_CIF, "1");
		order.getProperties().put(DummyReportMeta.PROP_FROM_DATE, "2017-01-01");
		order.getProperties().put(DummyReportMeta.PROP_TO_DATE, "2017-01-31");
		
		DummyReport report = engine.processOrder(order);
		
		Assert.assertNotNull(report);
		Assert.assertEquals("1", report.getCif());
		Assert.assertNotNull(report.getDailyStats());
		Assert.assertEquals(31, report.getDailyStats().size());
	}

}