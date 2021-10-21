package com.aneeque.controller;

import com.aneeque.dto.LoginRequest;
import com.aneeque.exceptions.AccountNotVerifiedException;
import com.aneeque.exceptions.IncorrectLoginDetailsException;
import com.aneeque.model.ApplicationUser;
import com.aneeque.model.Role;
import com.aneeque.service.UserAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserLoginControllerTest {

    @Mock
    private UserAuthService mockUserAuthService;

    @InjectMocks
    UserLoginController userLoginController;

    @Test
    void login() throws AccountNotVerifiedException, IncorrectLoginDetailsException {
        ApplicationUser mockedUser = new ApplicationUser("Ola", "Omo", "1234", "test_user@gmail.com", Role.USER);
        LoginRequest request = new LoginRequest("test_user@gmail.com", "1234");
        when(mockUserAuthService.login(any(LoginRequest.class))).thenReturn(mockedUser);

        assertThat(userLoginController.login(request)).isEqualTo(new ResponseEntity<>(mockedUser, HttpStatus.OK));
    }
}