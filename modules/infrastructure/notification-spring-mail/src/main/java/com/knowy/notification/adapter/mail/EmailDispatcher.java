package com.knowy.notification.adapter.mail;

import com.knowy.core.exception.KnowyMailDispatchException;
import com.knowy.core.port.KnowyNotificationDispatcher;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailDispatcher implements KnowyNotificationDispatcher {

	private final JavaMailSender mailSender;

	public EmailDispatcher(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void dispatch(String to, String subject, String body) throws KnowyMailDispatchException {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom("knowy-learn@knowy.com");
			message.setTo(to);
			message.setSubject(subject);
			message.setText(body);

			mailSender.send(message);
		} catch (MailException e) {
			throw new KnowyMailDispatchException("Fail to send email to " + to, e);
		}
	}
}
