package com.careerpath.auth.service;

import com.careerpath.auth.api.AuthController.AuthResponse;
import com.careerpath.auth.domain.AppUser;
import com.careerpath.auth.domain.AppUserRepository;
import com.careerpath.profile.domain.StudentProfile;
import com.careerpath.profile.domain.StudentProfileRepository;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final AppUserRepository userRepository;
    private final StudentProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository userRepository, StudentProfileRepository profileRepository,
            PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(String rawEmail, String password) {
        String email = normalizedEmail(rawEmail);
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account already exists for this email.");
        }

        AppUser user = userRepository.save(new AppUser(email, passwordEncoder.encode(password)));
        profileRepository.save(StudentProfile.forUser(user));
        return new AuthResponse(jwtService.createToken(email), email);
    }

    public AuthResponse login(String rawEmail, String password) {
        String email = normalizedEmail(rawEmail);
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }

        return new AuthResponse(jwtService.createToken(user.getEmail()), user.getEmail());
    }

    private String normalizedEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

