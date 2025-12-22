package com.knowy.core.user.usercase.update.password;

/**
 * FIXME: Refactor password and confirmPassword fields to use the {@code Password}
 * domain class instead of {@code String} to ensure validation consistency and type safety.
 * <p>
 * Command containing the data required to update a user's password using a security token.
 * <p>
 * This record is typically utilized in password recovery or reset flows where
 * a token has been previously generated and sent to the user.
 *
 * @param token           the unique security token used to authorize the password reset.
 * @param password        the new password to be established for the account.
 * @param confirmPassword the confirmation of the new password to prevent accidental mismatch or typing errors.
 */
public record UserUpdatePasswordCommand(String token, String password, String confirmPassword) {
}
