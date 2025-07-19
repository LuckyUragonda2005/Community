package CommunityApplication.DTO;

import lombok.Data;
import java.util.List;

@Data
public class CommunityNameResponseDTO {
    private List<String> communities;


    public CommunityNameResponseDTO(List<String> communities) {
        this.communities = communities;
    }

}
