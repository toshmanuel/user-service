package com.aneeque.service;

import com.aneeque.exceptions.TokenNotFoundException;
import com.aneeque.model.ConfirmationToken;
import com.aneeque.repository.ConfirmationTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepository mockerRepository;

    @InjectMocks
    ConfirmationTokenService confirmationTokenService;
    @Test
    @DisplayName("Save Confirmation Token")
    void testToSaveConfirmationToken() {
        String token = "this_is_a_test_token";
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .id(1L)
                .confirmationToken(token)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(1))
                .build();

        when(mockerRepository.save(confirmationToken)).thenReturn(confirmationToken);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        verify(mockerRepository, times(1)).save(confirmationToken);
    }

    @Test
    @DisplayName("Get Confirmation Token")
    void testToGetConfirmationToken() throws TokenNotFoundException {
        String token = "this_is_a_test_token";

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .id(1L)
                .confirmationToken(token)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(1))
                .build();

        when(mockerRepository.findConfirmationTokenByConfirmationToken(token)).thenReturn(Optional.of(confirmationToken));
        assertThat(confirmationTokenService.getConfirmationToken(token)).isEqualTo(confirmationToken);
    }
}