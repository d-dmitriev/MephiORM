package home.work;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.work.dto.request.CreateCourseRequest;
import home.work.entities.Category;
import home.work.entities.Course;
import home.work.entities.User;
import home.work.entities.UserRole;
import home.work.repositories.CategoryRepository;
import home.work.repositories.CourseRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User teacher;
    private Category category;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Create teacher
        teacher = new User();
        teacher.setName("Course Teacher");
        teacher.setEmail("course.teacher@example.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        // Create category
        category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Category Description");
        category = categoryRepository.save(category);

        // Create course
        testCourse = new Course();
        testCourse.setTitle("Test Course");
        testCourse.setDescription("Test Course Description");
        testCourse.setTeacher(teacher);
        testCourse.setCategory(category);
        testCourse = courseRepository.save(testCourse);
    }

    @Test
    void createCourse_ShouldReturnCreatedCourse() throws Exception {
        CreateCourseRequest newCourse = new CreateCourseRequest();
        newCourse.setTitle("New Course");
        newCourse.setDescription("New Course Description");
        newCourse.setTeacherId(1L);
        newCourse.setCategoryId(1L);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Course"))
                .andExpect(jsonPath("$.description").value("New Course Description"));
    }

    @Test
    void getAllCourses_ShouldReturnCoursesList() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].title").value("Title 1"));
    }

    @Test
    void getCourse_ShouldReturnCourse() throws Exception {
        mockMvc.perform(get("/api/courses/{id}", testCourse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCourse.getId()))
                .andExpect(jsonPath("$.title").value("Test Course"));
    }

    @Test
    void getCoursesByCategory_ShouldReturnFilteredCourses() throws Exception {
        mockMvc.perform(get("/api/courses/category/{categoryName}", "Test Category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Course"));
    }

    @Test
    void enrollStudent_ShouldCreateEnrollment() throws Exception {
        User student = new User();
        student.setName("Test Student");
        student.setEmail("student@example.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        mockMvc.perform(post("/api/courses/{courseId}/enroll", testCourse.getId())
                        .param("studentId", student.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void addCourseReview_ShouldCreateReview() throws Exception {
        User student = new User();
        student.setName("Review Student");
        student.setEmail("review.student@example.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        // Enroll student first
        mockMvc.perform(post("/api/courses/{courseId}/enroll", testCourse.getId())
                .param("studentId", student.getId().toString()));

        mockMvc.perform(post("/api/courses/{courseId}/reviews", testCourse.getId())
                        .param("studentId", student.getId().toString())
                        .param("rating", "5")
                        .param("comment", "Great course!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Great course!"));
    }
}
