package com.knowy.core.user.usercase.update.email;

/**
 * FIXME: Refactor email and password fields to use their respective domain classes
 * ({@code Email} and {@code Password}) instead of {@code String} to ensure type safety
 * and consistent validation logic across the application.
 * <p>
 * Command containing the data required to update a user's email address.
 * <p>
 * This record encapsulates the user identification and the security credentials
 * needed to authorize and perform the email change operation.
 *
 * @param userId   the unique identifier of the user whose email is being updated.
 * @param email    the new email address to be associated with the account.
 * @param password the current password required to verify the user's identity before performing the change.
 */
public record UserUpdateEmailCommand(int userId, String email, String password) {
}
