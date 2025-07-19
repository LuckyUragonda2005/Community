package CommunityApplication.Controller;

import CommunityApplication.DTO.LoginRequest;
import CommunityApplication.DTO.LoginResponseDTO;
import CommunityApplication.DTO.RegisterApiResponse;
import CommunityApplication.DTO.UserResponseDTO;
import CommunityApplication.Repo.UserRepository;
import CommunityApplication.Services.Auth.AuthService;
import CommunityApplication.Services.CustomUserDetailsService;
import CommunityApplication.Services.JWT.JwtService;
import CommunityApplication.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private  AuthenticationManager authManager;
    @Autowired
    private  UserRepository userRepo;
    @Autowired
    private  PasswordEncoder encoder;
    @Autowired
    private  JwtService jwtService;
    @Autowired
    private   CustomUserDetailsService userDetailsService;
    @Autowired
    private  AuthService authService;
    @Autowired
    private  UserRepository userRepository;



    @PostMapping("/register")
    public ResponseEntity<RegisterApiResponse> register(@RequestBody User user) {
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank() ||
                user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new RegisterApiResponse(false, "Phone number and password are required!!!"));
        }
        if (userRepo.existsByPhoneNumber(user.getPhoneNumber())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new RegisterApiResponse(false, "Phone number already exists!!!"));
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("USER");
        userRepo.save(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RegisterApiResponse(true, "User registered successfully!!!"));
    }

 



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getPhoneNumber() == null || loginRequest.getPhoneNumber().isBlank() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Phone number and password must not be empty!!!");
        }

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getPhoneNumber(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid phone number or password!!!");
        }

        User user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user.getPhoneNumber());

        return ResponseEntity.ok(new LoginResponseDTO(token, user.getId()));
    }


    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getLoggedInUser() {
        return authService.getCurrentUserDetails();
    }






}
