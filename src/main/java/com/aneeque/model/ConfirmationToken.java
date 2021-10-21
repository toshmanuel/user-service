package com.aneeque.model;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String confirmationToken;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(
            nullable = false,
            name="application_user_id"
    )
    private ApplicationUser applicationUser;

    public ConfirmationToken(String confirmationToken, LocalDateTime createdAt, LocalDateTime expiredAt, ApplicationUser applicationUser) {
        this.confirmationToken = confirmationToken;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.applicationUser = applicationUser;
    }
}
