package CommunityApplication.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class    CommentDTO {
    private String commenterName;
    private String text;
    private LocalDateTime commentedAt;

    private List<ReplyDTO> replies;  //  Add replies list

}
