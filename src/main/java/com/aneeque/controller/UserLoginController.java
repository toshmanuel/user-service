package com.aneeque.controller;

import com.aneeque.dto.LoginRequest;
import com.aneeque.exceptions.AccountNotVerifiedException;
import com.aneeque.exceptions.IncorrectLoginDetailsException;
import com.aneeque.model.ApplicationUser;
import com.aneeque.service.UserAuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/auth/login")
@AllArgsConstructor
public class UserLoginController {


    private final UserAuthService userAuthService;

    @GetMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){

        try{
            ApplicationUser applicationUser = userAuthService.login(loginRequest);

            return new ResponseEntity<>(applicationUser, HttpStatus.OK);
        }catch(IncorrectLoginDetailsException | AccountNotVerifiedException e){
            String errorMessage = e.getLocalizedMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
        }
    }
}
