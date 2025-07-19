package CommunityApplication;

import CommunityApplication.DTO.UploadPostResponseDTO;
import CommunityApplication.Repo.FedCategoryRepo;
import CommunityApplication.Repo.UploadPostRepo;
import CommunityApplication.Repo.UserRepository;
import CommunityApplication.Services.S3Bucket.S3Service;
import CommunityApplication.Services.UploadPostService;
import CommunityApplication.model.FedCategory;
import CommunityApplication.model.UploadPost;
import CommunityApplication.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class UploadPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private S3Service s3Service;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private FedCategoryRepo fedCategoryRepo;

    @MockBean
    private UploadPostRepo uploadPostRepo;

    @MockBean
    private UploadPostService uploadPostService;


    @WithMockUser(username = "8087698944", roles = {"USER"})
    @Test
    public void testUploadPostWithMockUser() throws Exception {
        // Mock image
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "dummy image content".getBytes());

        //  Mock AWS S3 upload
        Mockito.when(s3Service.uploadFile(Mockito.any()))
                .thenReturn("https://s3.mock.com/test.jpg");

        //  Mock user
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("Lucky");
        mockUser.setLastName("Uragonda");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        //  Mock category
        FedCategory mockCategory = new FedCategory();
        mockCategory.setId(1L);
        mockCategory.setName("HOME");
        Mockito.when(fedCategoryRepo.findById(1L)).thenReturn(Optional.of(mockCategory));

        //  Mock saving post
        Mockito.when(uploadPostRepo.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Perform request
        mockMvc.perform(multipart("/api/users/upload-post")
                        .file(imageFile)
                        .param("content", "Sample post content")
                        .param("tags", "tag1")
                        .param("tags", "tag2")
                        .param("fedCategoryId", "1")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @WithMockUser(username = "8087698944", roles = {"USER"})
    @Test
    public void testGetAllUploadPosts() throws Exception {
        // Create mock response DTO
        UploadPostResponseDTO mockDto = new UploadPostResponseDTO();
        mockDto.setId(1L);
        mockDto.setImageUrl("https://s3.mock.com/test.jpg");
        mockDto.setContent("Sample post content");
        mockDto.setTags(List.of("tag1", "tag2"));
        mockDto.setUploadedBy("Lucky Uragonda");
        mockDto.setCategoryName("HOME");
        mockDto.setUploadedAt(LocalDateTime.now());

        // Mock service response
        Mockito.when(uploadPostService.getAllUploadPosts())
                .thenReturn(List.of(mockDto));

        // Perform GET request to correct endpoint
        mockMvc.perform(get("/getUploaded-post"))
                .andExpect(status().isOk())
                .andDo(print());
    }



}
