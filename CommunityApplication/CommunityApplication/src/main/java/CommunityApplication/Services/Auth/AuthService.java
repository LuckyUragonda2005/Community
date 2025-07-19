package CommunityApplication.Services.Auth;

import CommunityApplication.DTO.RegisterRequestDTO;
import CommunityApplication.DTO.UserResponseDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<String> registerUser(RegisterRequestDTO dto);
    ResponseEntity<UserResponseDTO> getCurrentUserDetails();


}
