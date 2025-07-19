package CommunityApplication.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostWithCommentsDTO {
    private Long postId;
    private String imageUrl;
    private String content;
    private List<String> tags;
    private String uploadedBy;
    private String categoryName;
    private LocalDateTime uploadedAt;
    private List<CommentDTO> comments;


}
