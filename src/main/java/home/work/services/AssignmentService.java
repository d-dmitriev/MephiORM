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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для управления заданиями и их выполнением.
 */
@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    private final AssignmentMapper assignmentMapper;
    private final SubmissionMapper submissionMapper;

    @Transactional
    public AssignmentSimple createAssignment(CreateAssignmentRequest assignment) {
        Lesson lesson = lessonRepository.findById(assignment.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        Assignment assignmentCreated = assignmentRepository.save(assignmentMapper.toEntity(assignment, lesson));
        return assignmentMapper.toSimple(assignmentCreated);
    }

    @Transactional
    public SubmissionSimple submitAssignment(Long assignmentId, Long studentId, String content) {
        if (submissionRepository.findByStudentIdAndAssignmentId(studentId, assignmentId).isPresent()) {
            throw new RuntimeException("Student has already submitted this assignment");
        }

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setContent(content);

        return submissionMapper.toSimple(submissionRepository.save(submission));
    }

    @Transactional
    public SubmissionSimple gradeSubmission(Long submissionId, Integer score, String feedback) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        submission.setScore(score);
        submission.setFeedback(feedback);

        return submissionMapper.toSimple(submissionRepository.save(submission));
    }

    public List<SubmissionSimple> getSubmissionsForAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentIdWithDetails(assignmentId).stream().map(submissionMapper::toSimple).toList();
    }

    public List<SubmissionSimple> getStudentSubmissions(Long studentId) {
        return submissionRepository.findByStudentIdWithDetails(studentId).stream().map(submissionMapper::toSimple).toList();
    }

    public List<AssignmentSimple> getOverdueAssignments(Long courseId) {
        return assignmentRepository.findOverdueAssignments(LocalDateTime.now(), courseId).stream().map(assignmentMapper::toSimple).toList();
    }

    public List<AssignmentSimple> getByLessonId(Long lessonId) {
        return assignmentRepository.findByLessonId(lessonId).stream().map(assignmentMapper::toSimple).toList();
    }

    public AssignmentSimple getByIdWithSubmissions(Long courseId) {
        return assignmentRepository.findByIdWithSubmissions(courseId).map(assignmentMapper::toSimple)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
    }
}
