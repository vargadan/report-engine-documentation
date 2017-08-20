package net.vargadaniel.re.reportfactory.dummy;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import net.vargadaniel.re.reportfactory.dummy.model.DummyReport;
import net.vargadaniel.re.reportfactory.dummy.model.DummyReportOrder;
import net.vargadaniel.re.reportfactory.dummy.model.DummyTransaction;

@Component
@EnableBinding(ReportEngine.class)
public class DummyReportEngine {
	
	static Logger log = LoggerFactory.getLogger(DummyReportEngine.class);
	
	static class StatusUpdate {

		final String orderId;
		
		final String status;
		
		public StatusUpdate(String orderId, String status) {
			super();
			this.orderId = orderId;
			this.status = status;
		}

		public String getOrderId() {
			return orderId;
		}

		public String getStatus() {
			return status;
		}	
	}
	
	@Autowired
	DummyTransactionRepo transactionRepo;
	
	@Autowired
	ReportEngine reportEngine;
	
	@StreamListener(target=ReportEngine.ORDERS, condition="headers['productName']=='" + DummyReportMeta.NAME + "'")
	public void processDummyReportOrder(Message<DummyReportOrder> orderMsg) {
		log.debug("order received, headers : " + orderMsg.getHeaders());
		DummyReportOrder order = orderMsg.getPayload(); 
		try {
			this.statusUpdate(order.getId(), "preparing data");
			this.prepareData(order);
			this.statusUpdate(order.getId(), "generating report");
			DummyReport report = this.produceReport(order);
			this.statusUpdate(order.getId(), "report generated");
			this.publishReport(order.getId(), report);
		} catch (Exception e) {
			log.error("There was an error", e);
			this.statusUpdate(order.getId(), "error");
		}
	}
	
	void prepareData(DummyReportOrder order)  {
		
		Iterable<DummyTransaction> existintTrs = transactionRepo.findByCifAndDate(order.getCif(), order.getFromDate(), order.getToDate());
		if (existintTrs.iterator().hasNext()) {
			transactionRepo.delete(existintTrs);
		}
		
		Random random = new Random();
		order.getProperties();
		LocalDate date = order.getFromDate();
		do {
			int transNo = random.nextInt(1000);
			List<DummyTransaction> transactions = new ArrayList<>();
			for (int i = 0; i < transNo; i++) {
				DummyTransaction transaction = new DummyTransaction();
				transaction.setDate(date);
				transaction.setCif(order.getCif());
				transaction.setAmount(getRandomAmount(10000));
				transactions.add(transaction);
			}
			transactionRepo.save(transactions);
			date = date.plusDays(1);
		} while (!date.isAfter(order.getToDate()));
	}
	
	DummyReport produceReport(DummyReportOrder order) {
		DummyReport report = new DummyReport();
		
		Iterable<DummyTransaction> transactions = transactionRepo.findByCifAndDate(order.getCif(), order.getFromDate(), order.getToDate());
		Stream<DummyTransaction> trStream = StreamSupport.stream(transactions.spliterator(), false);
		
		DoubleSummaryStatistics totalStats = trStream.mapToDouble(DummyTransaction::getAmountAsDoube).summaryStatistics();
		BigDecimal tatalAvg = new BigDecimal(totalStats.getAverage());
		BigDecimal tatalSum = new BigDecimal(totalStats.getSum());
		report.setTotalAverage(tatalAvg.setScale(2, BigDecimal.ROUND_HALF_UP));
		report.setTotalSum(tatalSum.setScale(2, BigDecimal.ROUND_HALF_UP));
		
		return report;
	}
	

	void statusUpdate(Long orderId, String status) {
		log.info("statusupdate for orderId " + orderId + " status : " + status);
		reportEngine.statusUpdates().send(MessageBuilder.withPayload(new StatusUpdate(Long.toString(orderId), status)).build());
	}
	
	void publishReport(Long orderId, DummyReport report) throws JAXBException {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(DummyReport.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		jaxbMarshaller.marshal(report, os);
		
		String xml = os.toString();
		log.info("XML report for order {} is : \n{}", orderId, xml);
		Message<String> reportFileMsg = MessageBuilder.withPayload(xml)
				.setHeader("orderId", orderId)
				.setHeader("productName", DummyReportMeta.NAME).build();
		reportEngine.reportFiles().send(reportFileMsg);
	}
	
	private BigDecimal getRandomAmount(int max) {
		BigDecimal bd = new BigDecimal(Math.random() * max);
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return bd;
	}
	

}
