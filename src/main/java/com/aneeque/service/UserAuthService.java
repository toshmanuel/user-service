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
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserAuthService implements UserDetailsService {
    private final ApplicationUserRepository applicationUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String errorMessage = String.format("user with this email {%s} not found", email);
        return applicationUserRepository.findApplicationUserByEmail(email)
                .orElseThrow(
                    () ->new UsernameNotFoundException(errorMessage)
                );
    }

    public String registerUser(UserSignUpRequest userSignUpRequest) throws UserAlreadyExistException {
        if (!isUserPresent(userSignUpRequest.getEmail())){

            ApplicationUser applicationUser = new ApplicationUser(
                    userSignUpRequest.getFirstName(),
                    userSignUpRequest.getLastName(),
                    bCryptPasswordEncoder.encode(userSignUpRequest.getPassword()),
                    userSignUpRequest.getEmail(),
                    Role.USER
            );
            applicationUserRepository.save(applicationUser);


            return generateLink(applicationUser);
        }else{
            Optional<ApplicationUser> optionalApplicationUser = applicationUserRepository
                    .findApplicationUserByEmail(userSignUpRequest.getEmail());
            if(optionalApplicationUser.isPresent() && !optionalApplicationUser.get().isEnabled()){
                return generateLink(optionalApplicationUser.get());
            }
        }
        throw new UserAlreadyExistException("this email already exist");
    }

    private String generateLink(ApplicationUser applicationUser) {
        String token = generateToken(applicationUser);
        String link = "http://localhost:8080/api/auth/signup/confirm?token=" + token;
        String message = "Click on the link to activate your account --> "+ link;
        emailSender.sendMail(message, applicationUser.getUsername());
        return link;
    }

    private String generateToken(ApplicationUser applicationUser) {
        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                applicationUser
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

    private boolean isUserPresent(String email) {
        Optional<ApplicationUser> optionalApplicationUser = applicationUserRepository
                .findApplicationUserByEmail(email);

        return optionalApplicationUser.isPresent();
    }

    @Transactional
    public String confirmToken(String token) throws TokenNotFoundException {
        String confirmationMessage = "Your account has now been confirmed and enabled, Kindly login using your user credentials";
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(token);

        if (confirmationToken.getConfirmedAt() != null){
            throw new IllegalStateException("email is already verified");
        }

        if(confirmationToken.getExpiredAt().isBefore(LocalDateTime.now())){
            throw new IllegalStateException("Token already expired");
        }
        confirmationTokenService.setConfirmedAt(token);
        ApplicationUser applicationUser = confirmationToken.getApplicationUser();
        applicationUser.setEnabled(true);
        applicationUserRepository.save(applicationUser);
        return confirmationMessage;
    }

    public ApplicationUser login(LoginRequest loginRequest) throws AccountNotVerifiedException, IncorrectLoginDetailsException {
        Optional<ApplicationUser> applicationUser = applicationUserRepository.findApplicationUserByEmail(loginRequest.getUsername());
        if(applicationUser.isPresent()){
            boolean isPassword = bCryptPasswordEncoder.matches(loginRequest.getPassword(), applicationUser.get().getPassword());
            if(!applicationUser.get().isEnabled()){
                throw new AccountNotVerifiedException("this email has not yet been verified, kindly check your email to enable your account");
            }
            if(isPassword){
                return applicationUser.get();
            }
        }

        throw new IncorrectLoginDetailsException("incorrect username or password");
    }
}
