package com.knowy.core.user.util;

/**
 * FIXME: Implement email using the Email class if the record is deprecated.
 * Represents the essential information required to process a password reset request.
 * <p>
 * This record encapsulates the user's unique identifier and their associated email
 * to ensure the reset process is linked to the correct account.
 *
 * @param userId the unique identifier of the user requesting the password reset.
 * @param email  the email address associated with the user account.
 */
public record PasswordResetInfo(int userId, String email) {
}
