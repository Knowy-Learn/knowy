package com.knowy.core.user.port;

import com.knowy.core.user.exception.KnowyWrongPasswordException;
import com.knowy.core.user.domain.UserPrivate;

public interface KnowyPasswordEncoder {

    String encode(String password);

    void assertHasPassword(UserPrivate user, String password) throws KnowyWrongPasswordException;

    boolean hasPassword(UserPrivate user, String password);

}