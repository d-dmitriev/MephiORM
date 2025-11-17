package home.work;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.work.entities.User;
import home.work.entities.UserRole;
import home.work.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.STUDENT);
        testUser = userRepository.save(testUser);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("newuser@example.com");
        newUser.setRole(UserRole.TEACHER);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.role").value("TEACHER"));
    }

    @Test
    void getUser_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUser_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 999L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserByEmail_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/email/{email}", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getTeachers_ShouldReturnTeachersList() throws Exception {
        User teacher = new User();
        teacher.setName("Teacher User");
        teacher.setEmail("teacher@example.com");
        teacher.setRole(UserRole.TEACHER);
        userRepository.save(teacher);

        mockMvc.perform(get("/api/users/teachers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].role").value("TEACHER"));
    }

    @Test
    void deleteUser_ShouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId()))
                .andExpect(status().isNoContent());

        // Verify user is deleted
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isBadRequest());
    }
}