package com.knowy.server.domain;

import com.knowy.server.domain.exception.KnowyUserEmailFormatException;
import com.knowy.server.domain.exception.KnowyUserEmailFormatRuntimeException;

import java.io.Serializable;
import java.util.Objects;

public record Email(String value) implements Serializable {

	public Email {
		Objects.requireNonNull(value, "An email value can't be null");
		if (!isValid(value)) {
			throw new KnowyUserEmailFormatRuntimeException("An email must match the expression <user>@<domain>");
		}
	}

    public static boolean isValid(String email) {
        return email != null && email.chars()
                .filter(ch -> ch == '@')
                .count() == 1;
    }

    public static void assertValid(String email) throws KnowyUserEmailFormatException {
        if (!Email.isValid(email)) {
            throw new KnowyUserEmailFormatException("An email must match the expression <user>@<domain>");
        }
    }
}
