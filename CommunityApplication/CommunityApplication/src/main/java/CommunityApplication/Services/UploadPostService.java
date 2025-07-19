package CommunityApplication.Services;

import CommunityApplication.DTO.*;
import CommunityApplication.Repo.*;
import CommunityApplication.Services.S3Bucket.S3Service;
import CommunityApplication.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UploadPostService {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FedCategoryRepo fedCategoryRepo;

    @Autowired
    private UploadPostRepo uploadPostRepo;

    @Autowired
    private LikeRepo likeRepo;

    @Autowired
    private CommentRepo commentRepo;


    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public UploadPostResponseDTO createPost(MultipartFile image, String content, List<String> tags, Long fedCategoryId,
                                            Long userId) throws Exception {
        //  Compress Image
        File compressedImage = ImageCompressor.compressImage(image, 0.6f); // 60% quality

        //  Upload to S3
        String imageUrl = s3Service.uploadFile((MultipartFile) compressedImage);

        //  Cleanup temp file
        compressedImage.delete();

        //  Get User & Category
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        FedCategory fedCategory = fedCategoryRepo.findById(fedCategoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + fedCategoryId));

        //  Tags handling
        String tagsString = (tags != null && !tags.isEmpty()) ? String.join(",", tags) : "";

        //  Save post
        UploadPost post = new UploadPost();
        post.setImageUrl(imageUrl);
        post.setContent(content);
        post.setTags(tagsString);
        post.setUploadedAt(LocalDateTime.now());
        post.setUser(user);
        post.setFedCategory(fedCategory);
        UploadPost savedPost = uploadPostRepo.save(post);


        List<String> tagsList = tagsString.isEmpty() ? List.of() : List.of(tagsString.split(","));
        return new UploadPostResponseDTO(
                savedPost.getId(),
                savedPost.getImageUrl(),
                savedPost.getContent(),
                tagsList,
                user.getFirstName() + " " + user.getLastName(),
                fedCategory.getName(),
                savedPost.getUploadedAt()
        );
    }


    public List<UploadPostResponseDTO> getAllUploadPosts() {
        List<UploadPost> posts = uploadPostRepo.findAll();

        return posts.stream().map(post -> {
            UploadPostResponseDTO dto = new UploadPostResponseDTO();
            dto.setId(post.getId());
            dto.setImageUrl(post.getImageUrl());
            dto.setContent(post.getContent());
            dto.setTags(post.getTags() != null ? Arrays.asList(post.getTags().split(",")) : Collections.emptyList());
            dto.setUploadedBy(post.getUser().getFirstName() + " " + post.getUser().getLastName());
            dto.setCategoryName(post.getFedCategory().getName());
            dto.setUploadedAt(post.getUploadedAt());
            return dto;
        }).collect(Collectors.toList());
    }

  =



    public List<UploadPostResponseDTO> getUploadPostsByCategoryName(String categoryName, Long userId) {
        List<UploadPost> posts = uploadPostRepo.findByFedCategory_NameIgnoreCase(categoryName);

        return posts.stream().map(post -> {
            UploadPostResponseDTO dto = new UploadPostResponseDTO();
            dto.setId(post.getId());
            dto.setImageUrl(post.getImageUrl());
            dto.setContent(post.getContent());

            dto.setTags(post.getTags() != null ? Arrays.asList(post.getTags().split(",")) : Collections.emptyList());

            dto.setUploadedById(post.getUser().getId());
            dto.setUploadedBy(post.getUser().getFirstName() + " " + post.getUser().getLastName());
            dto.setCategoryName(post.getFedCategory().getName());
            dto.setUploadedAt(post.getUploadedAt());

            // Like details
            dto.setTotalLikes(likeRepo.countByPostId(post.getId()));
            boolean liked = likeRepo.existsByPostIdAndUserId(post.getId(), userId);
            dto.setLiked(liked);

            // Comment count
            dto.setTotalComments(commentRepo.countByPostId(post.getId()));

            return dto;
        }).collect(Collectors.toList());
    }



    =

    public LikeResponseDTO likePost(Long postId, Long userId) {
        UploadPost post = uploadPostRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Like> existingLike = likeRepo.findByPostIdAndUserId(postId, userId);

        boolean liked;
        if (existingLike.isPresent()) {
            likeRepo.delete(existingLike.get());
            liked = false;
        } else {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepo.save(like);
            liked = true;
        }

        Long totalLikes = likeRepo.countByPostId(postId);

        return new LikeResponseDTO(postId, totalLikes, liked);
    }


    public Long getLikeCount(Long postId) {
        if (!uploadPostRepo.existsById(postId)) {
            throw new RuntimeException("Post with ID " + postId + " does not exist");
        }
        return likeRepo.countByPostId(postId);
    }


    public LikeResponseDTO getLikeInfo(Long postId, Long userId) {
        Long count = likeRepo.countByPostId(postId);
        boolean liked = likeRepo.findByPostIdAndUserId(postId, userId).isPresent();

        return new LikeResponseDTO(postId, count, liked);
    }





    public String addComment(Long postId, Long userId, String text) {
        UploadPost post = uploadPostRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setText(text);
        comment.setCommentedAt(LocalDateTime.now());
        commentRepo.save(comment);


        CommentResponseDTO dto = new CommentResponseDTO();
//        dto.setPostId(postId);
        dto.setText(text);
        dto.setCommentedAt(comment.getCommentedAt());
        dto.setCommenterId(user.getId());
        dto.setCommenterName(user.getFirstName() + " " + user.getLastName());

        messagingTemplate.convertAndSend("/topic/comments/" + postId, dto); //  Send to frontend

        return "Comment added successfully";
    }





    public PostWithCommentsDTO getPostWithComments(Long postId) {
        UploadPost post = uploadPostRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<Comment> comments = commentRepo.findByPostId(postId);

        PostWithCommentsDTO dto = new PostWithCommentsDTO();
        dto.setPostId(post.getId());
        dto.setImageUrl(post.getImageUrl());
        dto.setContent(post.getContent());
        dto.setTags(Arrays.asList(post.getTags().split(",")));
        dto.setUploadedBy(post.getUser().getFirstName() + " " + post.getUser().getLastName());
        dto.setCategoryName(post.getFedCategory().getName());
        dto.setUploadedAt(post.getUploadedAt());

        List<CommentDTO> commentDTOs = comments.stream()
                .filter(comment -> comment.getParentComment() == null) // Only top-level comments
                .map(comment -> mapToCommentDTO(comment))
                .collect(Collectors.toList());

        dto.setComments(commentDTOs);
        return dto;
    }

    private CommentDTO mapToCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setText(comment.getText());
        dto.setCommentedAt(comment.getCommentedAt());
        dto.setCommenterName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName());

        // Recursively map replies
        List<ReplyDTO> replyDTOs = comment.getReplies().stream()
                .map(this::mapToReplyDTO)
                .collect(Collectors.toList());

        dto.setReplies(replyDTOs);
        return dto;
    }

    private ReplyDTO mapToReplyDTO(Comment reply) {
        ReplyDTO dto = new ReplyDTO();
        dto.setReplyId(reply.getId());
        dto.setText(reply.getText());
        dto.setCommentedAt(reply.getCommentedAt());
        dto.setCommenterName(reply.getUser().getFirstName() + " " + reply.getUser().getLastName());

        List<ReplyDTO> childReplies = reply.getReplies() != null
                ? reply.getReplies().stream()
                .map(this::mapToReplyDTO)
                .collect(Collectors.toList())
                : new ArrayList<>();

        dto.setReplies(childReplies);
        return dto;
    }




    public CommentResponseDTO deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));

        Long commenterId = comment.getUser().getId();
        Long postOwnerId = comment.getPost().getUser().getId();

        // Authorization check
        if (!userId.equals(commenterId) && !userId.equals(postOwnerId)) {
            throw new RuntimeException("You are not authorized to delete this comment.");
        }

        //  DTO before delete
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setCommentId(comment.getId());
        dto.setText(comment.getText());
        dto.setCommentedAt(comment.getCommentedAt());
        dto.setCommenterName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName());

        
        commentRepo.delete(comment);
        return dto;
    }



    public ReplyDTO replyToComment(Long commentId, Long userId, String text) {
        Comment parentComment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment reply = new Comment();
        reply.setText(text);
        reply.setUser(user);
        reply.setParentComment(parentComment);
        reply.setPost(parentComment.getPost()); 
        reply.setCommentedAt(LocalDateTime.now());

        Comment saved = commentRepo.save(reply);

        ReplyDTO dto = new ReplyDTO();
        dto.setReplyId(saved.getId());
        dto.setText(saved.getText());
        dto.setCommenterName(user.getFirstName() + " " + user.getLastName());
        dto.setCommentedAt(saved.getCommentedAt());
        return dto;
    }


}
