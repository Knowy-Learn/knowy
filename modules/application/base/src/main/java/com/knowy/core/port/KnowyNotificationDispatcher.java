package com.knowy.core.port;

import com.knowy.core.exception.KnowyMailDispatchException;

public interface KnowyNotificationDispatcher {
	void dispatch(String to, String subject, String body) throws KnowyMailDispatchException;
}
