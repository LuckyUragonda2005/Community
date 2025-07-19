package CommunityApplication.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateRequestDTO {
    private String firstName;
    private String lastName;
    private String gender;
    private String maritalStatus;
    private LocalDate dob;
    private Float height;
    private Integer weight;
    private String bloodGroup;
    private String fatherName;
    private String address;
    private Long emergencyContact;

}
