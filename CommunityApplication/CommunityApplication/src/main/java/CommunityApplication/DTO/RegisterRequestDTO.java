package CommunityApplication.DTO;

import lombok.Data;


@Data
public class RegisterRequestDTO {
    private String phoneNumber;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String gender;


}
