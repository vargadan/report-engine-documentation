package net.vargadaniel.re.reportrepository;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ReportEngine {
	
	String REPORT_FILES = "reportFiles"; 
	
	@Input(REPORT_FILES)
	SubscribableChannel reportFileTopic();
}
