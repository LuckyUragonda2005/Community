package CommunityApplication.Services.Auth;

import CommunityApplication.DTO.RegisterRequestDTO;
import CommunityApplication.DTO.UserResponseDTO;
import CommunityApplication.Repo.UserRepository;
import CommunityApplication.Services.JWT.JwtServices;
import CommunityApplication.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private JwtServices jwtServices;

    @Override
    public ResponseEntity<String> registerUser(RegisterRequestDTO dto) {
        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone number already exists!");
        }

        User user = new User();
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setGender(dto.getGender());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }


    @Override
    public ResponseEntity<UserResponseDTO> getCurrentUserDetails() {
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByPhoneNumber(phoneNumber)
                .map(user -> {
                    UserResponseDTO dto = new UserResponseDTO();
                    dto.setId(user.getId());
                    dto.setPhoneNumber(user.getPhoneNumber());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setGender(user.getGender());
                    dto.setMaritalStatus(user.getMaritalStatus());
                    dto.setDob(user.getDob());
                    dto.setHeight(user.getHeight());
                    dto.setWeight(user.getWeight());
                    dto.setBloodGroup(user.getBloodGroup());
                    dto.setFatherName(user.getFatherName());
                    dto.setAddress(user.getAddress());
                    dto.setEmergencyContact(user.getEmergencyContact());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}
