package com.aneeque.controller;

import com.aneeque.exceptions.UserNotFoundException;
import com.aneeque.service.ApplicationUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value= "/api/user")
@AllArgsConstructor
public class ApplicationUserController {

    private final ApplicationUserService userService;

    @GetMapping(value="/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(value= "id") Long id){
        try {
            return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return  new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAllUsers(){
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }
}
