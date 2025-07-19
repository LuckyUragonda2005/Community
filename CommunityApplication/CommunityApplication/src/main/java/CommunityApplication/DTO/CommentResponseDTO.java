package CommunityApplication.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentResponseDTO {
    private Long commenterId;
    private Long commentId;
    private String commenterName;
    private String text;
    private LocalDateTime commentedAt;




}
