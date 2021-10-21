package com.aneeque.controller;

import com.aneeque.dto.UserSignUpRequest;
import com.aneeque.exceptions.TokenNotFoundException;
import com.aneeque.exceptions.UserAlreadyExistException;
import com.aneeque.service.UserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@Slf4j
class UserSignUpControllerTest {
    @Mock
    private UserAuthService mockerAuth;

    @InjectMocks
    private UserSignUpController userSignUpController;

    @Test
    @DisplayName("User Sign Up ")
    void testToRegisterUser() throws UserAlreadyExistException{
        UserSignUpRequest signUpRequest = new UserSignUpRequest("user", "test", "1234", "test_user@gmail.com");
        String token = UUID.randomUUID().toString();
        String link = "http://localhost:8080/api/auth/signup/confirm?token=" + token;
        when(mockerAuth.registerUser(signUpRequest)).thenReturn(link);
        assertThat(userSignUpController.register(signUpRequest)).isEqualTo(new ResponseEntity<>(link, HttpStatus.OK));
    }

    @Test
    @DisplayName("Throws UserAlreadyExistException")
    void testToThrowUserAlreadyExistExceptionWhenEmailsAlreadyExist() throws UserAlreadyExistException {
        UserSignUpRequest signUpRequest = new UserSignUpRequest("user", "test", "1234", "test_user@gmail.com");

        when(mockerAuth.registerUser(signUpRequest)).thenThrow(new UserAlreadyExistException("this email already exist"));

        assertThat(userSignUpController.register(signUpRequest)).isEqualTo(new ResponseEntity<>("this email already exist", HttpStatus.FORBIDDEN));
    }

    @Test
    @DisplayName("Confirm Email")
    void testToConfirmEmail() throws TokenNotFoundException {
        String token = UUID.randomUUID().toString();
        String confirmationMessage = "Your account has now been confirmed and enabled, Kindly login using your user credentials";
        when(mockerAuth.confirmToken(token)).thenReturn(confirmationMessage);
        assertThat(userSignUpController.confirmEmail(token)).isEqualTo(new ResponseEntity<>(confirmationMessage, HttpStatus.OK));
    }
}