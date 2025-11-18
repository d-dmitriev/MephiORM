package home.work;

import home.work.entities.Assignment;
import home.work.entities.Course;
import home.work.entities.Submission;
import home.work.entities.User;
import home.work.entities.Lesson;
import home.work.entities.Module;
import home.work.repositories.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SubmissionJpaTest {
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private LessonRepository lessonRepository;

    private Assignment assignment;

    @BeforeEach
    void initAssignment() {
        // инициализируем assignment один раз перед каждым тестом
        assignment = assignmentRepository.findById(1L).orElseGet(() -> {
            // если в БД нет Course с id=1 — создаём минимальный Course
            Course course = courseRepository.findById(1L).orElseGet(() -> {
                Course c = new Course();
                c.setTitle("Auto Created Course");
                c.setDescription("Automatically created for tests");
                return courseRepository.save(c);
            });

            // создаём Module -> Lesson -> Assignment и сохраняем Assignment
            Module module = moduleRepository.findById(1L).orElseGet(() -> {
                Module m = new Module();
                m.setTitle("Auto Module");
                m.setCourse(course);
                return moduleRepository.save(m);
            });

            Lesson lesson = lessonRepository.findById(1L).orElseGet(() -> {
                Lesson l = new Lesson();
                l.setTitle("Auto Lesson");
                l.setModule(module);
                return lessonRepository.save(l);
            });

            Assignment a = new Assignment();
            a.setTitle("Auto Assignment");
            a.setDescription("Auto-created assignment for tests");
            a.setLesson(lesson);

            return assignmentRepository.save(a);
        });
    }

    @Test
    void createSubmission_and_verifyPersistence() {
        // используем прединицилизированный assignment
        User student = userRepository.findById(3L).orElseThrow();

        Submission s = new Submission();
        s.setAssignment(assignment);
        s.setStudent(student);
        s.setContent("Answer text");
        s.setScore(85);
        s.setFeedback("Good");

        submissionRepository.save(s);

        assertNotNull(s.getId());
        assertNotNull(s.getSubmittedAt());
        assertFalse(s.getSubmittedAt().isAfter(LocalDateTime.now().plusSeconds(1)));

        Submission fromDb = submissionRepository.findById(s.getId()).orElseThrow();
        assertEquals(85, fromDb.getScore());
        assertEquals("Answer text", fromDb.getContent());
        assertEquals(student.getId(), fromDb.getStudent().getId());
        assertEquals(assignment.getId(), fromDb.getAssignment().getId());
    }

    @Test
    void findByStudentIdAndAssignmentId_returnsSavedSubmission() {
        User student = userRepository.findById(3L).orElseThrow();

        Submission s = new Submission();
        s.setAssignment(assignment);
        s.setStudent(student);
        s.setContent("Lookup test");
        submissionRepository.save(s);

        Optional<Submission> opt = submissionRepository.findByStudentIdAndAssignmentId(student.getId(), assignment.getId());
        assertTrue(opt.isPresent());
        assertEquals("Lookup test", opt.get().getContent());
    }

    @Test
    void findByAssignmentIdWithDetails_and_findByStudentIdWithDetails() {
        User s2 = userRepository.findById(2L).orElseThrow();
        User s3 = userRepository.findById(3L).orElseThrow();

        Submission a = new Submission();
        a.setAssignment(assignment);
        a.setStudent(s2);
        a.setContent("Detail 1");

        Submission b = new Submission();
        b.setAssignment(assignment);
        b.setStudent(s3);
        b.setContent("Detail 2");

        submissionRepository.saveAll(Arrays.asList(a, b));

        List<Submission> byAssign = submissionRepository.findByAssignmentIdWithDetails(assignment.getId());
        assertFalse(byAssign.isEmpty());
        assertTrue(byAssign.stream().anyMatch(x -> x.getStudent() != null && x.getAssignment() != null));

        List<Submission> byStudentDetails = submissionRepository.findByStudentIdWithDetails(s2.getId());
        assertFalse(byStudentDetails.isEmpty());
        assertTrue(byStudentDetails.stream().allMatch(x -> x.getStudent() != null && x.getAssignment() != null));
    }

    @Test
    void countByCourseId_and_findUngradedSubmissionsByCourseId_and_complexPathSearch() {
        Course course = courseRepository.findById(1L).orElseThrow();

        User u2 = userRepository.findById(2L).orElseThrow();
        User u3 = userRepository.findById(3L).orElseThrow();

        Submission s1 = new Submission();
        s1.setAssignment(assignment);
        s1.setStudent(u2);
        s1.setScore(null); // ungraded

        Submission s2 = new Submission();
        s2.setAssignment(assignment);
        s2.setStudent(u3);
        s2.setScore(75); // graded

        submissionRepository.saveAll(Arrays.asList(s1, s2));

        Long cnt = submissionRepository.countByCourseId(course.getId());
        assertNotNull(cnt);
        assertTrue(cnt >= 2); // может быть больше, проверяем минимум

        List<Submission> ungraded = submissionRepository.findUngradedSubmissionsByCourseId(course.getId());
        assertTrue(ungraded.stream().anyMatch(x -> x.getScore() == null));

        List<Submission> byComplexPath = submissionRepository.findByStudentIdAndAssignmentLessonModuleCourseId(u2.getId(), course.getId());
        // метод возвращает submissions для указанного student и course через путь assignment.lesson.module.course
        assertTrue(byComplexPath.stream().anyMatch(x -> x.getStudent().getId().equals(u2.getId())));
    }
}
