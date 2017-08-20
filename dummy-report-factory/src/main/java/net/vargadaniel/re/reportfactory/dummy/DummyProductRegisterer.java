package net.vargadaniel.re.reportfactory.dummy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@EnableBinding(ReportEngine.class)
public class DummyProductRegisterer {
	
	static final Logger logger = LoggerFactory.getLogger(DummyProductRegisterer.class);

	@RequestMapping("/register")
	@InboundChannelAdapter(channel=ReportEngine.PRODUCTS, poller= @Poller(fixedDelay="1"))
	public DummyReportMeta registerProduct() {
		DummyReportMeta drMeta = new DummyReportMeta();
		return drMeta;
	}
}
