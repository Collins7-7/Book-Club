package com.coll.book_manager.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.coll.book_manager.role.RoleRepository;
import com.coll.book_manager.user.Token;
import com.coll.book_manager.user.TokenRepository;
import com.coll.book_manager.user.User;
import com.coll.book_manager.user.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public void register(RegistrationRequest request) {
        
        var userRole = roleRepository.findByName("USER")
                            .orElseThrow(()-> new IllegalStateException("Role 'USER' not initialized"));
    
        var user = User.builder()
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .accountLocked(false)
                        .enabled(false)
                        .roles(List.of(userRole))
                        .build();

        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) {
       var newToken = generateAndSaveActivationToken(user);
       //Send Email
    }

    private String generateAndSaveActivationToken(User user) {
        //generate a token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                            .token(generatedToken)
                            .createdAt(LocalDateTime.now())
                            .expiresAt(LocalDateTime.now().plusMinutes(15))
                            .user(user)
                            .build();

            tokenRepository.save(token);

        return generatedToken;
    }

    private String generateActivationCode(int length) {
        
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for(int i=0; i < length; i++ ){
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }


}
