package CommunityApplication.Controller;

import CommunityApplication.DTO.*;
import CommunityApplication.Repo.*;
import CommunityApplication.Services.S3Bucket.S3Service;
import CommunityApplication.Services.UploadPostService;
import CommunityApplication.Services.UserProfileService;
import CommunityApplication.Services.UserService;
import CommunityApplication.model.Comment;
import CommunityApplication.model.Like;
import CommunityApplication.model.UploadPost;
import CommunityApplication.model.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserService userService;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FedCategoryRepo fedCategoryRepo;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UploadPostRepo uploadPostRepo;

    @Autowired
    private UploadPostService uploadPostService;



    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateUserProfile(@RequestBody UserUpdateRequestDTO requestDTO) {
        logger.info("Received PUT /profile request with data: {}", requestDTO);
        ResponseEntity<UserResponseDTO> response = userProfileService.updateCurrentUserDetails(requestDTO);
        logger.info("Profile updated successfully. Response: {}", response.getBody());
        return response;
    }





    @GetMapping("/filter")
    public List<UserResponse> filterUsers(
            @RequestParam @NotBlank(message = "Gender is required") String gender,
            @RequestParam @NotBlank(message = "Marital Status is required") String maritalStatus,
            @RequestParam @NotNull(message = "ageFrom is required") @Min(value = 0, message = "ageFrom must be >= 0") Integer ageFrom,
            @RequestParam @NotNull(message = "ageTo is required") @Min(value = 0, message = "ageTo must be >= 0") Integer ageTo,
            Principal principal
    )
    {

        // principal.getName() will return the phoneNumber from JWT token
        String phoneNumber = principal.getName();
        User loggedInUser = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userService.filterUsers(gender, maritalStatus, ageFrom, ageTo, loggedInUser.getId());

    }


    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }


    @PostMapping("/upload-post")
    public ResponseEntity<?> uploadPost(
            @RequestParam("image") MultipartFile image,
            @RequestParam("content") String content,
            @RequestParam("tags") List<String> tags,
            @RequestParam("fedCategoryId") Long fedCategoryId,
            @RequestParam("userId") Long userId
    ) {
        try {
            UploadPostResponseDTO response = uploadPostService.createPost(image, content, tags, fedCategoryId, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload post: " + e.getMessage());
        }
    }





    @GetMapping("/getUploaded-post")
    public ResponseEntity<List<UploadPostResponseDTO>> getAllUploadPosts() {
        List<UploadPostResponseDTO> dtos = uploadPostService.getAllUploadPosts();
        return ResponseEntity.ok(dtos);
    }





    @GetMapping("/getUploaded-post/{categoryName}")
    public ResponseEntity<?> getPostsByCategory(@PathVariable String categoryName, Principal principal) {
        String phoneNumber = principal.getName();
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<UploadPostResponseDTO> posts = uploadPostService.getUploadPostsByCategoryName(categoryName, user.getId());

        return ResponseEntity.ok(posts);
    }






    @PostMapping("/like/{postId}")
    public ResponseEntity<LikeResponseDTO> likePost(
            @PathVariable Long postId,
            @RequestBody LikeRequestDTO dto) {

        LikeResponseDTO response = uploadPostService.likePost(postId, dto.getUserId());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/likes/{postId}")
    public ResponseEntity<LikeResponseDTO> getLikeCount(@PathVariable Long postId,
                                                        @RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ResponseEntity.ok(uploadPostService.getLikeInfo(postId, userId));
        }
        Long count = uploadPostService.getLikeCount(postId);
        return ResponseEntity.ok(new LikeResponseDTO(postId, count, false));
    }




    @PostMapping("/comment/{postId}")
    public ResponseEntity<String> addComment(@PathVariable Long postId,
                                             @RequestBody CommentRequestDTO dto,
                                             Principal principal) {
        // Extract loggedin user from JWT
        String phoneNumber = principal.getName();
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Use logged-in user ID
        return ResponseEntity.ok(uploadPostService.addComment(postId, user.getId(), dto.getText()));

    }





    // get number of Comments on each post
    @GetMapping("/comments/{postId}")
    public ResponseEntity<PostWithCommentsDTO> getCommentsWithPostInfo(@PathVariable Long postId) {
        return ResponseEntity.ok(uploadPostService.getPostWithComments(postId));
    }




    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<CommentApiResponseDTO<CommentResponseDTO>> deleteComment(
            @PathVariable Long commentId,
            Principal principal
    ) {
        // Extract phoneNumber from JWT token
        String phoneNumber = principal.getName();

        // Fetch user from DB
        User loggedInUser = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Call service with commentId and logged-in userId
        CommentResponseDTO deletedComment = uploadPostService.deleteComment(commentId, loggedInUser.getId());

        CommentApiResponseDTO<CommentResponseDTO> response =
                new CommentApiResponseDTO<>("Comment deleted", deletedComment);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/reply/{commentId}")
    public ResponseEntity<ReplyDTO> replyToComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDTO dto) {
        ReplyDTO replyDTO = uploadPostService.replyToComment(commentId, dto.getUserId(), dto.getText());
        return ResponseEntity.ok(replyDTO);
    }




}
