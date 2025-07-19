package CommunityApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String gender;
    private String maritalStatus;
    private LocalDate dob;
    private float height;
    private Integer weight;
    private String bloodGroup;
    private String fatherName;
    private String address;
    private Long emergencyContact;

}
