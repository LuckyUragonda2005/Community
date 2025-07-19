package CommunityApplication.Services;

import CommunityApplication.DTO.UserResponse;
import CommunityApplication.DTO.UserResponseDTO;
import CommunityApplication.ExceptionHandler.NoMatchFoundException;
import CommunityApplication.Repo.UserRepository;
import CommunityApplication.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public List<UserResponse> filterUsers(String gender, String maritalStatus, Integer ageFrom, Integer ageTo, Long loggedInUserId) {
        LocalDate today = LocalDate.now();

        // Youngest
        LocalDate dobFrom = (ageFrom != null) ? today.minusYears(ageFrom) : today;
        // Oldest
        LocalDate dobTo = (ageTo != null) ? today.minusYears(ageTo) : LocalDate.MIN;
        List<User> users = userRepository.filterUsers(gender, maritalStatus, dobTo, dobFrom);
        users = users.stream()
                .filter(user -> !user.getId().equals(loggedInUserId))
                .toList();
        if (users.isEmpty()) {
            throw new NoMatchFoundException("No matches found for the given filters.");
        }
        return users.stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getGender(),
                        user.getMaritalStatus(),
                        Period.between(user.getDob(), today).getYears()
                ))
                .toList();
    }




    // METHOD FOR GET USER THAT MATCHES
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoMatchFoundException("User not found with id: " + id));

           //        LocalDate today = LocalDate.now();
        return new UserResponseDTO(
                user.getId(),
                user.getPhoneNumber(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getMaritalStatus(),
                user.getDob(),
                user.getHeight(),
                user.getWeight(),
                user.getBloodGroup(),
                user.getFatherName(),
                user.getAddress(),
                user.getEmergencyContact()

        );
    }
}
