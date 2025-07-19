package CommunityApplication;

import CommunityApplication.Repo.UserRepository;
import CommunityApplication.Services.CustomUserDetailsService;
import CommunityApplication.Services.JWT.JwtService;
import CommunityApplication.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authManager;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

//    @Test
//    void testRegister() throws Exception {
//        User user = new User();
//        user.setPhoneNumber("80807698944");
//        user.setPassword("lucky");
//
//        when(encoder.encode(any(String.class))).thenReturn("encodedPassword");
//        when(userRepo.save(any(User.class))).thenReturn(user);
//
//        mockMvc.perform(post("/api/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isOk())
//                .andExpect(content().string("User registered successfully!!!"));
//    }


    @Test
    void testRegisterSuccess() throws Exception {
        User user = new User();
        user.setPhoneNumber("80807698944");
        user.setPassword("lucky");

        when(userRepo.existsByPhoneNumber(user.getPhoneNumber())).thenReturn(false);
        when(encoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(user);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully!!!"));
    }


    @Test
    void testRegisterWithMissingFields() throws Exception {
        User user = new User();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Phone number and password are required!!!"));
    }

    @Test
    void testRegisterPhoneNumberAlreadyExists() throws Exception {
        User user = new User();
        user.setPhoneNumber("80807698944");
        user.setPassword("lucky");

        when(userRepo.existsByPhoneNumber(user.getPhoneNumber())).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Phone number already exists!!!"));
    }




    @Test
    void testLogin() throws Exception {
        User user = new User();
        user.setPhoneNumber("8087698944");
        user.setPassword("lucky");

        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("8087698944")
                .password("lucky")
                .authorities("ROLE_USER")
                .build();

        // Mocking authenticationManager.authenticate
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);

        // Mocking userDetailsService.loadUserByUsername(PHONE NUMBER)
        when(customUserDetailsService.loadUserByUsername("8087698944")).thenReturn(mockUserDetails);

        // Mocking jwtService.generateToken
        when(jwtService.generateToken("8087698944")).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("fake-jwt-token"));
    }
}
