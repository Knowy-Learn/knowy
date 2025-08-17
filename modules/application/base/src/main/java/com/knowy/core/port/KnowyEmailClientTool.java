package com.knowy.core.port;

import com.knowy.core.exception.KnowyMailDispatchException;

public interface KnowyEmailClientTool {
	void sendEmail(String to, String subject, String body) throws KnowyMailDispatchException;
}
