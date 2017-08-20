package net.vargadaniel.re.ordermanager;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

import net.vargadaniel.re.ordermanager.ReportEngine;

@Configuration
public class OrderManagerTestConfigurator {
	
	@Bean(name=ReportEngine.ORDERS)
	@Primary
	public MessageChannel orderTopic() {
		return Mockito.mock(MessageChannel.class);
	}
		
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
			public MessageChannel orders() {
				return Mockito.mock(MessageChannel.class);
			}
		};
	}

}
