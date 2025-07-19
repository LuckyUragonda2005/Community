package CommunityApplication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeResponseDTO {
    private Long postId;
    private Long totalLikes;
    private boolean liked;


}
