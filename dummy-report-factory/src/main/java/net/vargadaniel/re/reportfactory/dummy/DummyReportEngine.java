package net.vargadaniel.re.reportfactory.dummy;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import net.vargadaniel.re.reportfactory.dummy.model.DummyReport;
import net.vargadaniel.re.reportfactory.dummy.model.DummyReport.DailiyDummyReportStats;
import net.vargadaniel.re.reportfactory.dummy.model.DummyReportOrder;
import net.vargadaniel.re.reportfactory.dummy.model.DummyTransaction;

@EnableBinding(ReportEngine.class)
public class DummyReportEngine {

	static Logger log = LoggerFactory.getLogger(DummyReportEngine.class);

	static class Double2DecimalConverter implements Converter {

		public Double2DecimalConverter(Converter delegate) {
			super();
			this.delegate = delegate;
		}

		Converter delegate;

		@Override
		public <T> T convert(Class<T> clazz, Object val) {
			if (BigDecimal.class.equals(clazz)) {
				BigDecimal bd = new BigDecimal((Double) val);
				return (T) bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				return delegate.convert(clazz, val);
			}
		}
	}

	static {
		ConvertUtils.register(new Double2DecimalConverter(new DoubleConverter(0)), Double.class);
	}

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

	@StreamListener(target = ReportEngine.ORDERS, condition = "headers['productName']=='" + DummyReportMeta.NAME + "'")
	public void processOrderMessage(Message<DummyReportOrder> orderMsg) {
		log.debug("order received, headers : " + orderMsg.getHeaders());
		DummyReportOrder order = orderMsg.getPayload();
		try {
			this.publishReport(order.getId(), processOrder(order));
		} catch (Exception e) {
			log.error("There was an error", e);
			this.statusUpdate(order.getId(), "error");
		}
	}

	protected DummyReport processOrder(DummyReportOrder order)
			throws IllegalAccessException, InvocationTargetException {
		this.statusUpdate(order.getId(), "preparing data");
		this.prepareData(order);
		this.statusUpdate(order.getId(), "generating report");
		DummyReport report = this.produceReport(order);
		this.statusUpdate(order.getId(), "report generated");
		return report;
	}

	void prepareData(DummyReportOrder order) {

		String cif = order.getCif();
		LocalDate fromDate = order.getFromDate();
		LocalDate toDate = order.getToDate();

		for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
			Iterable<DummyTransaction> dayTransactions = transactionRepo.findByCifAndDate(cif, date);
			if (!dayTransactions.iterator().hasNext()) {
				Random random = new Random();
				order.getProperties();
				{
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
				}
			}
		}

	}

	DummyReport produceReport(DummyReportOrder order) throws IllegalAccessException, InvocationTargetException {
		DummyReport report = new DummyReport();

		Iterable<DummyTransaction> transactions = transactionRepo.findByCifAndDate(order.getCif(), order.getFromDate(),
				order.getToDate());

		DoubleSummaryStatistics totalStats = StreamSupport.stream(transactions.spliterator(), false).mapToDouble(DummyTransaction::getAmountAsDoube)
				.summaryStatistics();

		BeanUtils.copyProperties(report, totalStats);
		report.setCif(order.getCif());

		LocalDate fromDate = order.getFromDate();
		LocalDate toDate = order.getToDate();

		SortedSet<DailiyDummyReportStats> dailyStats = new TreeSet<>();

		for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
			final LocalDate tdate = date;
			DoubleSummaryStatistics dailySummaryStats = StreamSupport.stream(transactions.spliterator(), false).filter(t -> t.getDate().equals(tdate))
					.mapToDouble(DummyTransaction::getAmountAsDoube).summaryStatistics();
			DailiyDummyReportStats dailyReportStats = new DailiyDummyReportStats();
			BeanUtils.copyProperties(dailyReportStats, dailySummaryStats);
			dailyReportStats.setDate(tdate);
			dailyStats.add(dailyReportStats);
		}

		report.setDailyStats(dailyStats);

		return report;
	}

	void statusUpdate(Long orderId, String status) {
		log.info("statusupdate for orderId " + orderId + " status : " + status);
		reportEngine.statusUpdates()
				.send(MessageBuilder.withPayload(new StatusUpdate(Long.toString(orderId), status)).build());
	}
	
	String convertReportToXml(DummyReport report) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(DummyReport.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		jaxbMarshaller.marshal(report, os);

		String xml = os.toString();
		return xml;
	}

	void publishReport(Long orderId, DummyReport report) throws JAXBException {
		String xml = convertReportToXml(report);
		log.info("XML report for order {} is : \n{}", orderId, xml);
		Message<String> reportFileMsg = MessageBuilder.withPayload(xml).setHeader("orderId", orderId)
				.setHeader("productName", DummyReportMeta.NAME).build();
		reportEngine.reportFiles().send(reportFileMsg);
	}

	private BigDecimal getRandomAmount(int max) {
		BigDecimal bd = new BigDecimal(Math.random() * max);
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return bd;
	}

}
