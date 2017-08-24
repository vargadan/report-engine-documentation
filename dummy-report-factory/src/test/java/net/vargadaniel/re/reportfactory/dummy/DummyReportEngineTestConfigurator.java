package net.vargadaniel.re.reportfactory.dummy;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

@Configuration
public class DummyReportEngineTestConfigurator {
	
	@Bean
	@Primary
	public ReportEngine reportEngine() {
		return new ReportEngine() {
			
			@Override
			public SubscribableChannel statusUpdates() {
				return Mockito.mock(SubscribableChannel.class);
			}
			
			@Override
			public SubscribableChannel products() {
				return Mockito.mock(SubscribableChannel.class);
			}

			@Override
			public SubscribableChannel orders() {
				return Mockito.mock(SubscribableChannel.class);
			}

			@Override
			public MessageChannel reportFiles() {
				return Mockito.mock(MessageChannel.class);
			}
		};
	}

}
