package CommunityApplication.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class RegisterApiResponse {
    private boolean success;
    private String message;
}
