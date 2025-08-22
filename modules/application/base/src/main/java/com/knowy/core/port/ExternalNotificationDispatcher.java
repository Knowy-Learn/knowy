package com.knowy.core.port;

import com.knowy.core.exception.KnowyMailDispatchException;

public interface ExternalNotificationDispatcher {

	void dispatch(ExternalNotification externalNotification) throws KnowyMailDispatchException;

	record ExternalNotification(String to, String subject, String message) {
	}
}
