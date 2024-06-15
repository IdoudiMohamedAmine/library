package org.example.librarybackend.auth;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.example.librarybackend.Entitys.Token;
import org.example.librarybackend.Entitys.User;
import org.example.librarybackend.email.EmailService;
import org.example.librarybackend.email.EmailTemplateName;
import org.example.librarybackend.repository.RoleRepository;
import org.example.librarybackend.repository.TokenRepository;
import org.example.librarybackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder   passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${application.security.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                //could use a better exception handeling
                .orElseThrow(()-> new IllegalStateException("Role user not initialised"));
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(true)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToksen=genarateAndSaveActivationToken(user);
        //send email
        emailService.sendEmail(
                user.getEmail(),
                user.FullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToksen,
                "Activate your account"
        );


    }

    private String genarateAndSaveActivationToken(User user) {
        //generate the token
        String generatedToken=generateActivationcode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(20))
                .user(user)
                .build();

        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationcode(int len) {
        String Charachters="0123456789";
        StringBuilder code=new StringBuilder(len);
        SecureRandom random=new SecureRandom();
        for (int i = 0; i < len; i++) {
            int rand=random.nextInt(Charachters.length());
            code.append(Charachters.charAt(rand));
        }
        return code.toString();
    }
}
