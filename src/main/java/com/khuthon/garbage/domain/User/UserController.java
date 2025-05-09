package com.khuthon.garbage.domain.User;

import com.khuthon.garbage.domain.User.Dto.AuthRequestDto;
import com.khuthon.garbage.global.dto.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody AuthRequestDto request) {
        return userService.register(request)
                .map(user -> ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", user)))
                .orElseGet(() -> ResponseEntity.status(409)
                        .body(new ApiResponse<>(false, "Username already taken", null)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody AuthRequestDto request, HttpSession session) {
        String result = userService.login(request, session);
        if (result.equals("Login successful")) {
            return ResponseEntity.ok(new ApiResponse<>(true, result, null));
        } else {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, result, null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(new ApiResponse<>(true, "Logged out successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Not logged in", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Current user fetched", user));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable String userId) {
        return userService.findById(userId)
                .map(user -> ResponseEntity.ok(new ApiResponse<>(true, "User fetched successfully", user)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "User not found", null)));
    }
}
