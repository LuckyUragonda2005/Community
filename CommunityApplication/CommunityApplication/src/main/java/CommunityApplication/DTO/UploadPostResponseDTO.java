package CommunityApplication.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadPostResponseDTO {
    private Long id;
    private String imageUrl;
    private String content;
    private List<String> tags;
    private String uploadedBy;
    private String categoryName;
    private Long uploadedById;
    private LocalDateTime uploadedAt;
    private Long totalLikes;
//    private List<CommentResponseDTO> comments;
    private int totalComments;
    private boolean liked;


    public UploadPostResponseDTO(Long id, String imageUrl, String content, List<String> tagsList,
                                 String s, String name, LocalDateTime uploadedAt) {
    }
}
