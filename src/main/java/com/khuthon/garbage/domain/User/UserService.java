package com.khuthon.garbage.domain.User;

import com.khuthon.garbage.domain.User.Dto.AuthRequestDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<User> register(AuthRequestDto request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return Optional.empty();
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setTrustScore(0);
        user.setPoint(0);
        user.setCreatedAt(LocalDateTime.now());

        return Optional.of(userRepository.save(user));
    }

    public String login(AuthRequestDto request, HttpSession session) {
        return userRepository.findByUsername(request.getUsername())
                .map(user -> {
                    if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        session.setAttribute("user", user);
                        return "Login successful";
                    } else {
                        return "Invalid password";
                    }
                })
                .orElse("User not found");
    }

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }
}