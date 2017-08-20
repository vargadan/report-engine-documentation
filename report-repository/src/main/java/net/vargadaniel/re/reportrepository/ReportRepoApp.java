package net.vargadaniel.re.reportrepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.vargadaniel.re.reportrepository.model.Report;

@SpringBootApplication
@RestController
@EnableBinding(ReportEngine.class)
public class ReportRepoApp {
	
	@Autowired
	ReportRepository reportRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(ReportRepoApp.class, args);
	}
	
	@StreamListener(ReportEngine.REPORT_FILES)
	void saveReport(Message<String> message) {
		Object orderId = message.getHeaders().get("orderId");
		String payLoad = message.getPayload();
		
		Report report = new Report();
		report.setOrderId(Long.valueOf(orderId.toString()));	
		report.setContent(payLoad.getBytes());
		
		reportRepository.save(report);
	}
	
	@RequestMapping(path="/hello/{id}", method=RequestMethod.GET, produces="text/plain")
	ResponseEntity<String> dummy(@PathVariable("id") Long id) {
		return new ResponseEntity<>("hello : " + id, HttpStatus.OK);
	}
	
	@RequestMapping(path="/files/{id}", method=RequestMethod.GET, produces="text/plain")
	ResponseEntity<String> getFile(@PathVariable("id") Long id) {
		Report report = reportRepository.findOne(id);
		if (report == null) {
			return new ResponseEntity<>("No report found with ID " + id, HttpStatus.NOT_FOUND);
		} 
		String content = new String(report.getContent());
		return new ResponseEntity<>(content, HttpStatus.OK);
	}

}
