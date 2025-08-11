package com.knowy.server.application.usecase;

import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyImageNotFoundException;
import com.knowy.server.application.exception.validation.user.KnowyInvalidUserException;
import com.knowy.server.application.ports.*;
import com.knowy.server.application.usecase.register.UserSignUpUseCase;
import com.knowy.server.application.usecase.register.UserSingUpCommand;
import com.knowy.server.domain.Email;
import com.knowy.server.domain.ProfileImage;
import com.knowy.server.domain.UserPrivate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserSingUpUseCaseTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserPrivateRepository userPrivateRepository;

	@Mock
	private ProfileImageRepository profileImageRepository;

	@Mock
	private KnowyPasswordEncoder passwordEncoder;

	@Mock
	private KnowyPasswordChecker passwordChecker;

	@InjectMocks
	private UserSignUpUseCase userSignUpUseCase;

	@Test
	void given_userSignUpCommand_when_executeSignUp_then_registerNewUser() throws KnowyInvalidUserException, KnowyImageNotFoundException {
		UserSingUpCommand userSingUpCommand = new UserSingUpCommand(
			"TestNickname", "test@email.com", "ValidPass123@"
		);
		UserPrivate userPrivateResult = new UserPrivate(
			1,
			"TestNickname",
			new ProfileImage(1, "https://knowy/image.png"),
			new HashSet<>(),
			new Email("test@email.com"),
			"ENCODED_PASS",
			true
		);

		Mockito.when(userPrivateRepository.findByEmail(userSingUpCommand.email()))
			.thenReturn(Optional.empty());
		Mockito.when(passwordChecker.isRightPasswordFormat(userSingUpCommand.password()))
			.thenReturn(true);
		Mockito.when(passwordEncoder.encode(userSingUpCommand.password()))
			.thenReturn("ENCODED_PASS");
		Mockito.when(profileImageRepository.findById(1))
			.thenReturn(Optional.of(new ProfileImage(1, "https://knowy/image.png")));
		Mockito.when(userPrivateRepository.save(any(UserPrivate.class)))
			.thenReturn(userPrivateResult);

		UserPrivate newUserPrivate = userSignUpUseCase.execute(userSingUpCommand);
		assertEquals(newUserPrivate, userPrivateResult);
	}

/*	// method create
	@Test
	void given_validEmailAndPassword_when_createNewPrivateUser_then_returnNewPrivateUser() throws Exception {
		NewUserResult newUserResult = new NewUserResult(
			"TestNickname", new ProfileImage(1, "https://knowy/image.png"), new HashSet<>()
		);
		UserPrivate userPrivateResult = new UserPrivate(
			1,
			"TestNickname",
			new ProfileImage(1, "https://knowy/image.png"),
			new HashSet<>(),
			new Email("test@email.com"),
			"ValidPass123@",
			true
		);

		Mockito.when(userPrivateRepository.findByEmail(userPrivateResult.email().value()))
			.thenReturn(Optional.empty());
		Mockito.when(passwordChecker.isRightPasswordFormat(userPrivateResult.password()))
			.thenReturn(true);
		Mockito.when(passwordEncoder.encode(userPrivateResult.password()))
			.thenReturn("ENCODED_PASS");
		Mockito.when(userPrivateRepository.save(any(UserPrivate.class)))
			.thenReturn(userPrivateResult);

		UserPrivate newUserPrivate = userPrivateService.create("test@email.com", "ValidPass123@", newUserResult);
		assertEquals(newUserPrivate, userPrivateResult);
	}

	@Test
	void given_existingEmail_when_createNewPrivateUser_then_throwKnowyInvalidUserEmailException() {
		NewUserResult newUserResult = new NewUserResult(
			"TestNickname", new ProfileImage(1, "https://knowy/image.png"), new HashSet<>()
		);

		User user = new User(
			1, newUserResult.nickname(), newUserResult.profileImage(), newUserResult.categories()
		);
		UserPrivate userPrivate = new UserPrivate(user, "exists@gmail.com", "ValidPass123");

		Mockito.when(userPrivateRepository.findByEmail("exists@gmail.com"))
			.thenReturn(Optional.of(userPrivate));

		assertThrows(
			KnowyInvalidUserEmailException.class,
			() -> userPrivateService.create("exists@gmail.com", "ValidPass123", newUserResult)
		);
	}

	@Test
	void given_invalidPassword_when_createNewPrivateUser_then_throwKnowyInvalidUserPasswordFormatException() {
		NewUserResult newUserResult = new NewUserResult(
			"TestNickname", new ProfileImage(1, "https://knowy/image.png"), new HashSet<>()
		);

		Mockito.when(userPrivateRepository.findByEmail("test@email.com"))
			.thenReturn(Optional.empty());

		assertThrows(
			KnowyInvalidUserPasswordFormatException.class, () ->
				userPrivateService.create("test@email.com", "invalidPassword", newUserResult)
		);
	}*/
}
