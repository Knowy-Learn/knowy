package com.knowy.server.application;


import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.exception.validation.user.KnowyWrongPasswordException;
import com.knowy.server.application.model.MailMessage;
import com.knowy.server.application.model.PasswordResetInfo;
import com.knowy.server.application.ports.KnowyPasswordEncoder;
import com.knowy.server.application.ports.KnowyTokenTools;
import com.knowy.server.application.ports.UserPrivateRepository;
import com.knowy.server.application.util.TokenUserPrivateTool;
import com.knowy.server.domain.Email;
import com.knowy.server.domain.Password;
import com.knowy.server.domain.ProfileImage;
import com.knowy.server.domain.UserPrivate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserPrivateServiceTest {

	@Mock
	private TokenUserPrivateTool tokenUserPrivateTool;

	@Mock
	private UserPrivateRepository userPrivateRepository;

	@Mock
	private KnowyPasswordEncoder passwordEncoder;

	@Mock
	private KnowyTokenTools tokenTools;

	@Spy
	@InjectMocks
	private UserPrivateService userPrivateService;

	// mehotd getUserPrivateByEmail
	@Test
	void given_existingEmail_when_getUserPrivateByEmail_then_returnUserPrivate() {
		UserPrivate userPrivate = new UserPrivate(
			11,
			"TestNickname",
			new ProfileImage(1, "https://knowy/image.png"),
			new HashSet<>(),
			new Email("test@email.com"),
			new Password("ValidOldPass123@"),
			true
		);

		Mockito.when(userPrivateRepository.findByEmail("exist@gmail.com"))
			.thenReturn(Optional.of(userPrivate));

		UserPrivate result = assertDoesNotThrow(() -> userPrivateService.getUserPrivateByEmail("exist@gmail.com"));
		assertEquals(userPrivate, result);
	}

	@Test
	void given_nonExistingEmail_when_getUserPrivateByEmail_then_throwKnowyUserNotFoundException() {
		Mockito.when(userPrivateRepository.findByEmail("nonExistg@mail.com"))
			.thenReturn(Optional.empty());

		assertThrows(
			KnowyUserNotFoundException.class,
			() -> userPrivateService.getUserPrivateByEmail("nonExistg@mail.com")
		);
	}
/*




	/*
	// method reactivateUserAccount
	@Test
	void given_validTokenAndUserInactiveExpectActivateAndSave() throws Exception {
		String token = "valid-token";

		PasswordResetInfo passwordResetInfo = new PasswordResetInfo(11, "user@mail.com");
		UserPrivate userPrivate = new UserPrivate(
			11,
			"TestNickname",
			new ProfileImage(1, "https://knowy/image.png"),
			new HashSet<>(),
			new Email("user@mail.com"),
			new Password("ENCODED_PASSWORD"),
			false
		);
		UserPrivate newUserPrivate = new UserPrivate(
			userPrivate.cropToUser(),
			new Email("user@mail.com"), new Password("ENCODED_PASSWORD"),
			true
		);
		Mockito.when(tokenUserPrivateTool.decodeUnverified(token, PasswordResetInfo.class))
			.thenReturn(passwordResetInfo);
		Mockito.when(userPrivateRepository.findById(11))
			.thenReturn(Optional.of(userPrivate));
		Mockito.when(tokenUserPrivateTool.decode(Mockito.anyString(), Mockito.eq(token), Mockito.eq(PasswordResetInfo.class)))
			.thenReturn(passwordResetInfo);

		userRecoveryPasswordUseCase.reactivateUserAccount(token);
		Mockito.verify(userPrivateRepository).save(newUserPrivate);
	}

	@Test
	void given_invalidToken_when_reactivateUserAccount_then_throwKnowyTokenException() throws KnowyTokenException {
		String token = "invalid-token";

		Mockito.doThrow(new KnowyTokenException("Invalid token"))
			.when(tokenTools)
			.decodeUnverified(Mockito.eq(token), Mockito.any());

		assertThrows(
			KnowyTokenException.class,
			() -> userPrivateService.reactivateUserAccount(token)
		);
	}

	@Test
	void given_validTokenButUserNotFound_when_reactivateUserAccount_then_throwKnowyUserNotFoundException() throws KnowyTokenException {
		PasswordResetInfo passwordResetInfo = new PasswordResetInfo(99, "email@example.com");
		Mockito.when(tokenTools.decodeUnverified("valid-token", PasswordResetInfo.class))
			.thenReturn(passwordResetInfo);
		Mockito.when(userPrivateRepository.findById(99))
			.thenReturn(Optional.empty());

		assertThrows(
			KnowyUserNotFoundException.class,
			() -> userPrivateService.reactivateUserAccount("valid-token")
		);
	}

	@Test
	void given_validTokenAndUserAlreadyActive_when_reactivateUserAccount_then_doNothing() throws Exception {
		String token = "valid-token";

		PasswordResetInfo passwordResetInfo = new PasswordResetInfo(11, "email@example.com");
		UserPrivate userPrivate = new UserPrivate(
			11,
			"TestNickname",
			new ProfileImage(1, "https://knowy/image.png"),
			new HashSet<>(),
			new Email("user@mail.com"),
			new Password("ENCODED_PASSWORD"),
			true
		);
		Mockito.when(tokenTools.decodeUnverified(token, PasswordResetInfo.class))
			.thenReturn(passwordResetInfo);
		Mockito.when(userPrivateRepository.findById(11))
			.thenReturn(Optional.of(userPrivate));
		Mockito.when(tokenTools.decode(Mockito.anyString(), Mockito.eq(token), Mockito.eq(PasswordResetInfo.class)))
			.thenReturn(passwordResetInfo);

		userPrivateService.reactivateUserAccount(token);
		Mockito.verify(userPrivateRepository, Mockito.never()).save(Mockito.any());
	}
*/
}
