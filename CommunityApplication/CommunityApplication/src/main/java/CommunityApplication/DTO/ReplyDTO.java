package CommunityApplication.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReplyDTO {
    private Long replyId;
    private String text;
    private String commenterName;
    private LocalDateTime commentedAt;
    private List<ReplyDTO> replies;

}
