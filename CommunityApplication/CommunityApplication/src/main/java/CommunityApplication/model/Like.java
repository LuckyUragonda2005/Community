package CommunityApplication.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Table(name = "likes")
@Data
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private UploadPost post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime likedAt = LocalDateTime.now();
}
