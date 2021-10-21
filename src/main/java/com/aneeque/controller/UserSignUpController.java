package com.aneeque.controller;


import com.aneeque.dto.UserSignUpRequest;
import com.aneeque.exceptions.TokenNotFoundException;
import com.aneeque.exceptions.UserAlreadyExistException;
import com.aneeque.service.UserAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth/signup")
public class UserSignUpController {

    private final UserAuthService userAuthService;

    public UserSignUpController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody @Valid UserSignUpRequest registrationRequest){
        try {
            return new ResponseEntity<>(userAuthService.registerUser(registrationRequest), HttpStatus.OK);
        } catch (UserAlreadyExistException e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam String token){
        try {
            return new ResponseEntity<>(userAuthService.confirmToken(token), HttpStatus.OK);
        } catch (TokenNotFoundException | IllegalStateException e) {
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
