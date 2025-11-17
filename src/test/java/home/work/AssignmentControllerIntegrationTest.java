package home.work;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.work.entities.Assignment;
import home.work.entities.Category;
import home.work.entities.Course;
import home.work.entities.Lesson;
import home.work.entities.Module;
import home.work.entities.User;
import home.work.entities.UserRole;
import home.work.repositories.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AssignmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Lesson lesson;
    private User student;

    @BeforeEach
    void setUp() {
        // Create course structure
        User teacher = new User();
        teacher.setName("Assignment Teacher");
        teacher.setEmail("assignment.teacher@example.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Category category = new Category();
        category.setName("Assignment Category");
        category = categoryRepository.save(category);

        Course course = new Course();
        course.setTitle("Assignment Course");
        course.setTeacher(teacher);
        course.setCategory(category);
        course = courseRepository.save(course);

        Module module = new Module();
        module.setTitle("Assignment Module");
        module.setCourse(course);
        module = moduleRepository.save(module);

        lesson = new Lesson();
        lesson.setTitle("Assignment Lesson");
        lesson.setModule(module);
        lesson = lessonRepository.save(lesson);

        // Create student
        student = new User();
        student.setName("Assignment Student");
        student.setEmail("assignment.student@example.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);
    }

    @Test
    void createAssignment_ShouldReturnCreatedAssignment() throws Exception {
        Assignment newAssignment = new Assignment();
        newAssignment.setTitle("Test Assignment");
        newAssignment.setDescription("Test Assignment Description");
        newAssignment.setDueDate(LocalDateTime.now().plusDays(7));
        newAssignment.setMaxScore(100);

        mockMvc.perform(post("/api/assignments")
                        .param("lessonId", lesson.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAssignment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Assignment"));
    }

    @Test
    void submitAssignment_ShouldCreateSubmission() throws Exception {
        // First create an assignment
        Assignment assignment = new Assignment();
        assignment.setTitle("Submission Assignment");
        assignment.setLesson(lesson);
        assignment = assignmentRepository.save(assignment);

        mockMvc.perform(post("/api/assignments/{assignmentId}/submit", assignment.getId())
                        .param("studentId", student.getId().toString())
                        .param("content", "My assignment submission"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("My assignment submission"));
    }

    @Test
    void getAssignmentSubmissions_ShouldReturnSubmissions() throws Exception {
        // Create assignment and submission
        Assignment assignment = new Assignment();
        assignment.setTitle("Submissions Assignment");
        assignment.setLesson(lesson);
        assignment = assignmentRepository.save(assignment);

        mockMvc.perform(post("/api/assignments/{assignmentId}/submit", assignment.getId())
                .param("studentId", student.getId().toString())
                .param("content", "Test submission"));

        mockMvc.perform(get("/api/assignments/{assignmentId}/submissions", assignment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("Test submission"));
    }

    @Test
    void gradeSubmission_ShouldUpdateSubmission() throws Exception {
        // Create assignment and submission
        Assignment assignment = new Assignment();
        assignment.setTitle("Grading Assignment");
        assignment.setLesson(lesson);
        assignment = assignmentRepository.save(assignment);

        String submissionResponse = mockMvc.perform(post("/api/assignments/{assignmentId}/submit", assignment.getId())
                        .param("studentId", student.getId().toString())
                        .param("content", "Submission to grade"))
                .andReturn().getResponse().getContentAsString();

        // Extract submission ID from response
        var submissionNode = objectMapper.readTree(submissionResponse);
        Long submissionId = submissionNode.get("id").asLong();

        mockMvc.perform(put("/api/assignments/submissions/{submissionId}/grade", submissionId)
                        .param("score", "85")
                        .param("feedback", "Good work!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(85))
                .andExpect(jsonPath("$.feedback").value("Good work!"));
    }
}
