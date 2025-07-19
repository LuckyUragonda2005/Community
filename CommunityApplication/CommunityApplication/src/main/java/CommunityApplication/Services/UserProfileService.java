package CommunityApplication.Services;

import CommunityApplication.DTO.UserResponseDTO;
import CommunityApplication.DTO.UserUpdateRequestDTO;
import CommunityApplication.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<UserResponseDTO> updateCurrentUserDetails(UserUpdateRequestDTO requestDTO) {
        String phoneNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByPhoneNumber(phoneNumber)
                .map(user -> {
                    user.setFirstName(requestDTO.getFirstName());
                    user.setLastName(requestDTO.getLastName());
                    user.setGender(requestDTO.getGender());
                    user.setMaritalStatus(requestDTO.getMaritalStatus());
                    user.setDob(requestDTO.getDob());
                    user.setHeight(requestDTO.getHeight());
                    user.setWeight(requestDTO.getWeight());
                    user.setBloodGroup(requestDTO.getBloodGroup());
                    user.setFatherName(requestDTO.getFatherName());
                    user.setAddress(requestDTO.getAddress());
                    user.setEmergencyContact(requestDTO.getEmergencyContact());
                    userRepository.save(user);

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
                }).orElse(ResponseEntity.status(404).build());
    }

}
