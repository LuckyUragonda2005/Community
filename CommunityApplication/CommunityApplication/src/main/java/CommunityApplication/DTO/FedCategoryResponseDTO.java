package CommunityApplication.DTO;

import lombok.Data;

import java.util.List;

@Data
public class FedCategoryResponseDTO {

    private List<String> categories;

    public FedCategoryResponseDTO(List<String> categories) {
        this.categories = categories;
    }
}
