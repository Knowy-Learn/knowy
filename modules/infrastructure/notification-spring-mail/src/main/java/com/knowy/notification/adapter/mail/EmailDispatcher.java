package com.knowy.notification.adapter.mail;

import com.knowy.core.exception.KnowyMailDispatchException;
import com.knowy.core.port.ExternalNotificationDispatcher;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailDispatcher implements ExternalNotificationDispatcher {

	private final JavaMailSender mailSender;

	public EmailDispatcher(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void dispatch(ExternalNotification notification) throws KnowyMailDispatchException {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom("knowy-learn@knowy.com");
			message.setTo(notification.to());
			message.setSubject(notification.subject());
			message.setText(notification.message());

			mailSender.send(message);
		} catch (MailException e) {
			throw new KnowyMailDispatchException("Fail to send email to " + notification.to(), e);
		}
	}
}
