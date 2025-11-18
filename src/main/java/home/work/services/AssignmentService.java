package home.work.services;

import home.work.dto.request.CreateAssignmentRequest;
import home.work.dto.simple.AssignmentSimple;
import home.work.dto.simple.SubmissionSimple;
import home.work.entities.Assignment;
import home.work.entities.Lesson;
import home.work.entities.Submission;
import home.work.entities.User;
import home.work.mappers.AssignmentMapper;
import home.work.mappers.SubmissionMapper;
import home.work.repositories.AssignmentRepository;
import home.work.repositories.LessonRepository;
import home.work.repositories.SubmissionRepository;
import home.work.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для управления заданиями и их выполнением.
 */
@Service
@RequiredArgsConstructor
public class AssignmentService {
    private static final Logger log = LoggerFactory.getLogger(AssignmentService.class);

    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    private final AssignmentMapper assignmentMapper;
    private final SubmissionMapper submissionMapper;

    /**
     * Создание нового задания.
     *
     * @param assignment данные для создания задания
     * @return созданное задание в упрощенном виде
     */
    @Transactional
    public AssignmentSimple createAssignment(CreateAssignmentRequest assignment) {
        Lesson lesson = lessonRepository.findById(assignment.getLessonId())
                .orElseThrow(() -> {
                    log.error("Lesson with id {} not found", assignment.getLessonId());
                    return new RuntimeException("Lesson not found");
                });

        Assignment assignmentCreated = assignmentRepository.save(assignmentMapper.toEntity(assignment, lesson));
        return assignmentMapper.toSimple(assignmentCreated);
    }

    /**
     * Отправка задания студентом.
     *
     * @param assignmentId идентификатор задания
     * @param studentId    идентификатор студента
     * @param content      содержимое задания
     * @return отправленное задание в упрощенном виде
     */
    @Transactional
    public SubmissionSimple submitAssignment(Long assignmentId, Long studentId, String content) {
        if (submissionRepository.findByStudentIdAndAssignmentId(studentId, assignmentId).isPresent()) {
            log.error("Student with id {} has already submitted assignment with id {}", studentId, assignmentId);
            throw new RuntimeException("Student has already submitted this assignment");
        }

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    log.error("Assignment with id {} not found", assignmentId);
                    return new RuntimeException("Assignment not found");
                });
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.error("Student with id {} not found", studentId);
                    return new RuntimeException("Student not found");
                });

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent(content);

        return submissionMapper.toSimple(submissionRepository.save(submission));
    }

    /**
     * Оценка отправленного задания.
     *
     * @param submissionId идентификатор отправленного задания
     * @param score        оценка
     * @param feedback     обратная связь
     * @return оцененное задание в упрощенном виде
     */
    @Transactional
    public SubmissionSimple gradeSubmission(Long submissionId, Integer score, String feedback) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> {
                    log.error("Submission with id {} not found", submissionId);
                    return new RuntimeException("Submission not found");
                });

        submission.setScore(score);
        submission.setFeedback(feedback);

        return submissionMapper.toSimple(submissionRepository.save(submission));
    }

    /**
     * Получение всех отправленных заданий для конкретного задания.
     *
     * @param assignmentId идентификатор задания
     * @return список отправленных заданий в упрощенном виде
     */
    public List<SubmissionSimple> getSubmissionsForAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentIdWithDetails(assignmentId).stream().map(submissionMapper::toSimple).toList();
    }

    /**
     * Получение всех отправленных заданий конкретного студента.
     *
     * @param studentId идентификатор студента
     * @return список отправленных заданий в упрощенном виде
     */
    public List<SubmissionSimple> getStudentSubmissions(Long studentId) {
        return submissionRepository.findByStudentIdWithDetails(studentId).stream().map(submissionMapper::toSimple).toList();
    }

    /**
     * Получение всех просроченных заданий для конкретного курса.
     *
     * @param courseId идентификатор курса
     * @return список просроченных заданий в упрощенном виде
     */
    public List<AssignmentSimple> getOverdueAssignments(Long courseId) {
        return assignmentRepository.findOverdueAssignments(LocalDateTime.now(), courseId).stream().map(assignmentMapper::toSimple).toList();
    }

    /**
     * Получение всех заданий для конкретного урока.
     *
     * @param lessonId идентификатор урока
     * @return список заданий в упрощенном виде
     */
    public List<AssignmentSimple> getByLessonId(Long lessonId) {
        return assignmentRepository.findByLessonId(lessonId).stream().map(assignmentMapper::toSimple).toList();
    }

    /**
     * Получение задания по его идентификатору вместе с отправленными заданиями.
     *
     * @param courseId идентификатор задания
     * @return задание в упрощенном виде
     */
    public AssignmentSimple getByIdWithSubmissions(Long courseId) {
        return assignmentRepository.findByIdWithSubmissions(courseId).map(assignmentMapper::toSimple)
                .orElseThrow(() -> {
                    log.error("Assignment with id {} not found", courseId);
                    return new RuntimeException("Assignment not found");
                });
    }
}
