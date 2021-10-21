package com.aneeque.service;

import com.aneeque.dto.LoginRequest;
import com.aneeque.dto.UserSignUpRequest;
import com.aneeque.email.EmailSender;
import com.aneeque.exceptions.AccountNotVerifiedException;
import com.aneeque.exceptions.IncorrectLoginDetailsException;
import com.aneeque.exceptions.TokenNotFoundException;
import com.aneeque.exceptions.UserAlreadyExistException;
import com.aneeque.model.ApplicationUser;
import com.aneeque.model.ConfirmationToken;
import com.aneeque.model.Role;
import com.aneeque.repository.ApplicationUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {

    @Mock
    private ApplicationUserRepository mockApplicationUserRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private EmailSender emailSender;

    @InjectMocks
    UserAuthService userAuthService;

    @Test
    @DisplayName("Load User by Email")
    void testToLoadUserByEmail() {
        ApplicationUser mockedUser = new ApplicationUser("Ola", "Omo", "1234", "ola.omo@gmail.com", Role.USER);
        when(mockApplicationUserRepository
                .findApplicationUserByEmail(
                        any(String.class)
                )
        ).thenReturn(Optional.of(mockedUser));

        log.info("mocked application user details -> {}", mockApplicationUserRepository.findApplicationUserByEmail("ola.omo@gmail.com"));

        assertThat(
                userAuthService.
                        loadUserByUsername("ola.omo@gmail.com")
        ).isEqualTo(mockedUser);
    }

    @Test
    @DisplayName("Cannot find user with specified email")
    void testToThrowExceptionWhenUserWithSpecifiedEmailIsNotFound() {
        when(mockApplicationUserRepository
                .findApplicationUserByEmail(
                        any(String.class)
                )
        ).thenReturn(Optional.empty());

        log.info("mocked application user details -> {}", mockApplicationUserRepository.findApplicationUserByEmail("ola.omo@gmail.com"));

        UsernameNotFoundException errorMessage = assertThrows(UsernameNotFoundException.class, () -> {
            userAuthService.loadUserByUsername("ola.omo@gmail.com");
        });

        assertThat(errorMessage.getLocalizedMessage()).isEqualTo("user with this email {ola.omo@gmail.com} not found");
    }

    @Test
    @DisplayName("Register Application User")
    void testToRegisterApplicationUser() throws UserAlreadyExistException {
        UserSignUpRequest signUpRequest = new UserSignUpRequest("user", "test", "1234", "test_user@gmail.com");
        when(mockApplicationUserRepository.save(any(ApplicationUser.class)))
                .thenReturn(
                        new ApplicationUser(
                                signUpRequest.getLastName(),
                                signUpRequest.getFirstName(),
                                signUpRequest.getPassword(),
                                signUpRequest.getEmail(),
                                Role.USER
                        )
                );
        assertThat(userAuthService.registerUser(signUpRequest)).isExactlyInstanceOf(String.class);
        log.info("confirmation link is -->{}", userAuthService.registerUser(signUpRequest));
    }

    @Test
    @DisplayName("Confirm token sent to user")
    void testThatConfirmTokenSentToUserAfterRegistration() throws TokenNotFoundException {
        ApplicationUser mockedUser = new ApplicationUser("Ola", "Omo", "1234", "ola.omo@gmail.com", Role.USER);
        String token = "this_is_a_test_token";
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .id(1L)
                .confirmationToken(token)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(1))
                .applicationUser(mockedUser)
                .build();
        given(confirmationTokenService.getConfirmationToken(token)).willReturn(confirmationToken);
        assertThat(userAuthService.confirmToken(confirmationToken.getConfirmationToken())).isEqualTo("Your account has now been confirmed and enabled, Kindly login using your user credentials");
    }


    @Test
    @DisplayName("Throws TokenNotFoundException")
    void testThatTokenNotFoundExceptionIsThrownWhenTokeIsNotFound() throws TokenNotFoundException {
        String token = "this_is_a_test_token";
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .id(1L)
                .confirmationToken(token)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(1))
                .build();

        given(confirmationTokenService.getConfirmationToken(token)).willThrow(new TokenNotFoundException("this token is invalid"));
        TokenNotFoundException exception = assertThrows(TokenNotFoundException.class, () -> {
            userAuthService.confirmToken(confirmationToken.getConfirmationToken());
        });
        assertThat(exception.getMessage()).isEqualTo("this token is invalid");

    }

    @Test
    @DisplayName("Login Confirmed Registered User")
    void testToLoginUser() throws AccountNotVerifiedException, IncorrectLoginDetailsException {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        ApplicationUser response = ApplicationUser.builder()
                .email("test_user@gmail.com")
                .password("12345")
                .enabled(true)
                .firstName("test")
                .lastName("user")
                .role(Role.USER)
                .build();

        when(mockApplicationUserRepository.findApplicationUserByEmail(anyString()))
                .thenReturn(
                        Optional.of(response)
        );
        LoginRequest request = new LoginRequest("test_user@gmail.com", "12345");

        assertThat(userAuthService.login(request)).isEqualTo(response);
    }

    @Test
    @DisplayName("Incorrect username or password")
    void testToThrowIncorrectLoginDetailsExceptionWhenPasswordIsWrong() {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        ApplicationUser response = ApplicationUser.builder()
                .email("test_user@gmail.com")
                .password("12345")
                .enabled(true)
                .firstName("test")
                .lastName("user")
                .role(Role.USER)
                .build();

        when(mockApplicationUserRepository.findApplicationUserByEmail(anyString()))
                .thenReturn(
                        Optional.of(response)
                );
        LoginRequest request = new LoginRequest("test_user@gmail.com", "123456");

        IncorrectLoginDetailsException exception = assertThrows(IncorrectLoginDetailsException.class, () -> {
           userAuthService.login(request);
        });

        assertThat(exception.getLocalizedMessage()).isEqualTo("incorrect username or password");
    }

    @Test
    @DisplayName("User not yet verified")
    void testAccountNotVerifiedExceptionWhenTheUserIsNotYetEnabled(){

        ApplicationUser response = ApplicationUser.builder()
                .email("test_user@gmail.com")
                .password("12345")
                .enabled(false)
                .firstName("test")
                .lastName("user")
                .role(Role.USER)
                .build();

        when(mockApplicationUserRepository.findApplicationUserByEmail(anyString()))
                .thenReturn(
                        Optional.of(response)
                );
        LoginRequest request = new LoginRequest("test_user@gmail.com", "123456");

        AccountNotVerifiedException exception = assertThrows(AccountNotVerifiedException.class, () -> {
            userAuthService.login(request);
        });

        assertThat(exception.getLocalizedMessage()).isEqualTo("this email has not yet been verified, kindly check your email to enable your account");
    }


}