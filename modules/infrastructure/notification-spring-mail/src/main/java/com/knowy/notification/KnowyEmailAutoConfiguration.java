package com.knowy.notification;

import com.knowy.core.port.KnowyNotificationDispatcher;
import com.knowy.notification.adapter.mail.EmailDispatcher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@AutoConfiguration
public class KnowyEmailAutoConfiguration {

	@Bean
	public KnowyNotificationDispatcher emailDispatcher(JavaMailSender javaMailSender) {
		return new EmailDispatcher(javaMailSender);
	}

}
