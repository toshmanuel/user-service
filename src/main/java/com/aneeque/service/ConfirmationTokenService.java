package com.aneeque.service;

import com.aneeque.exceptions.TokenNotFoundException;
import com.aneeque.model.ConfirmationToken;
import com.aneeque.repository.ConfirmationTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;


    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public void saveConfirmationToken(ConfirmationToken confirmationToken){
        confirmationTokenRepository.save(confirmationToken);
    }


    public ConfirmationToken getConfirmationToken(String token) throws TokenNotFoundException {
        return confirmationTokenRepository.findConfirmationTokenByConfirmationToken(token).orElseThrow(() -> new TokenNotFoundException("this token is invalid"));
    }

    public void setConfirmedAt(String token) throws TokenNotFoundException {
        ConfirmationToken confirmationToken = getConfirmationToken(token);
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);
    }
}
