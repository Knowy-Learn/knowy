package com.knowy.core.user.usercase.manage;

import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.Password;

/**
 * Command containing the necessary information to deactivate a user account.
 * <p>
 * This record ensures that all required security credentials and redirection parameters are present before proceeding
 * with the account deactivation process.
 *
 * @param email           the email address of the account to be deactivated.
 * @param password        the current password for identity verification.
 * @param confirmPassword the password confirmation to ensure accuracy and intent.
 * @param recoveryBaseUrl the base URL used to construct the link for potential account recovery.
 */
public record DeactivateAccountCommand(
	Email email,
	Password password,
	Password confirmPassword,
	String recoveryBaseUrl
) {
}
