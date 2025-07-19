package CommunityApplication.DTO;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
@Data
public class UserFilterRequest {
    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Marital status is required")
    private String maritalStatus;

    @Min(value = 0, message = "Age from must be a positive number")
    @NotBlank
    private Integer ageFrom;

    @Min(value = 0, message = "Age to must be a positive number")
    @NotBlank
    private Integer ageTo;
}

