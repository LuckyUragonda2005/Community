package CommunityApplication.DTO;

import lombok.Data;

@Data
public class CommentRequestDTO {
    private Long userId;
    private String text;

}
