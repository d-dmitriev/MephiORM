package home.work;

import home.work.entities.*;
import home.work.repositories.CategoryRepository;
import home.work.repositories.CourseRepository;
import home.work.repositories.EnrollmentRepository;
import home.work.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EnrollmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User student;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        // Create student
        student = new User();
        student.setName("Enrollment Student");
        student.setEmail("enrollment.student@example.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);

        // Create teacher and category
        User teacher = new User();
        teacher.setName("Enrollment Teacher");
        teacher.setEmail("enrollment.teacher@example.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Category category = new Category();
        category.setName("Enrollment Category");
        category = categoryRepository.save(category);

        // Create course
        course = new Course();
        course.setTitle("Enrollment Course");
        course.setDescription("Enrollment Course Description");
        course.setTeacher(teacher);
        course.setCategory(category);
        course = courseRepository.save(course);

        // Create enrollment
        enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment = enrollmentRepository.save(enrollment);
    }

    @Test
    void enrollStudent_ShouldCreateEnrollment() throws Exception {
        // Create new student and course for this test
        User newStudent = new User();
        newStudent.setName("New Student");
        newStudent.setEmail("new.student@example.com");
        newStudent.setRole(UserRole.STUDENT);
        newStudent = userRepository.save(newStudent);

        User teacher = new User();
        teacher.setName("New Teacher");
        teacher.setEmail("new.teacher@example.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Category category = new Category();
        category.setName("New Category");
        category = categoryRepository.save(category);

        Course newCourse = new Course();
        newCourse.setTitle("New Course");
        newCourse.setTeacher(teacher);
        newCourse.setCategory(category);
        newCourse = courseRepository.save(newCourse);

        mockMvc.perform(post("/api/enrollments")
                        .param("courseId", newCourse.getId().toString())
                        .param("studentId", newStudent.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getStudentEnrollments_ShouldReturnEnrollments() throws Exception {
        mockMvc.perform(get("/api/enrollments/student/{studentId}", student.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getCourseEnrollments_ShouldReturnEnrollments() throws Exception {
        mockMvc.perform(get("/api/enrollments/course/{courseId}", course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void updateEnrollmentStatus_ShouldUpdateStatus() throws Exception {
        mockMvc.perform(put("/api/enrollments/{enrollmentId}/status", enrollment.getId())
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void updateEnrollmentProgress_ShouldUpdateProgress() throws Exception {
        mockMvc.perform(put("/api/enrollments/{enrollmentId}/progress", enrollment.getId())
                        .param("progress", "0.75"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progress").value(0.75));
    }

    @Test
    void unenrollStudent_ShouldDeleteEnrollment() throws Exception {
        mockMvc.perform(delete("/api/enrollments")
                        .param("courseId", course.getId().toString())
                        .param("studentId", student.getId().toString()))
                .andExpect(status().isNoContent());

        // Verify enrollment is deleted
        mockMvc.perform(get("/api/enrollments/student/{studentId}", student.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
