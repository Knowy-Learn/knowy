package com.knowy.server.application.ports;

import com.knowy.server.application.exception.validation.user.KnowyWrongPasswordException;
import com.knowy.server.domain.UserPrivate;

public interface KnowyPasswordEncoder {

    String encode(String password);

    void assertHasPassword(UserPrivate user, String password) throws KnowyWrongPasswordException;

    boolean hasPassword(UserPrivate user, String password);

}