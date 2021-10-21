package com.aneeque.service;


import com.aneeque.exceptions.UserNotFoundException;
import com.aneeque.model.ApplicationUser;
import com.aneeque.repository.ApplicationUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ApplicationUserService {


    private final ApplicationUserRepository applicationUserRepository;


    @Cacheable(value = "ApplicationUser", key = "#id")
    public ApplicationUser findUserById(Long id) throws UserNotFoundException {
        String errorMessage = String.format("User with id %d not found", id);

        return applicationUserRepository.findById(id).orElseThrow(() -> new UserNotFoundException(errorMessage));
    }

    @Cacheable(value="ApplicationUsers")
    public List<ApplicationUser> findAll(){
        return applicationUserRepository.findAll();
    }
}
