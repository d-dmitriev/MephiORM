package home.work.services;

import home.work.dto.request.CreateAnswerOptionRequest;
import home.work.dto.request.CreateQuestionRequest;
import home.work.dto.request.CreateQuizRequest;
import home.work.dto.simple.AnswerOptionSimple;
import home.work.dto.simple.QuestionSimple;
import home.work.dto.simple.QuizSimple;
import home.work.dto.simple.QuizSubmissionSimple;
import home.work.entities.*;
import home.work.entities.Module;
import home.work.mappers.QuizMapper;
import home.work.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Сервис для управления викторинами (quizzes), вопросами и связанными сущностями.
 */
@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserRepository userRepository;

    private final QuizMapper quizMapper;

    @Transactional
    public QuizSimple createQuizForModule(CreateQuizRequest request) {
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found"));

        Quiz quiz = quizMapper.toEntity(request, module);
        return quizMapper.toSimple(quizRepository.save(quiz));
    }

    @Transactional
    public QuestionSimple addQuestionToQuiz(Long quizId, CreateQuestionRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Question question = quizMapper.toEntity(request, quiz);
        return quizMapper.toSimple(questionRepository.save(question));
    }

    @Transactional
    public AnswerOptionSimple addAnswerOptionToQuestion(Long questionId, CreateAnswerOptionRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        AnswerOption answerOption = quizMapper.toEntity(request, question);
        return quizMapper.toSimple(answerOptionRepository.save(answerOption));
    }

    @Transactional
    public QuizSimple getByIdWithQuestions(Long quizId) {
        return quizRepository.findByIdWithQuestions(quizId).map(quizMapper::toSimple)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    @Transactional
    public QuizSubmissionSimple submitQuiz(Long quizId, Long studentId, Map<Long, List<Long>> answers) {
        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Calculate score
        int totalQuestions = quiz.getQuestions().size();
        int correctAnswers = 0;

        for (Question question : quiz.getQuestions()) {
            List<Long> selectedOptionIds = answers.get(question.getId());
            if (selectedOptionIds != null && isAnswerCorrect(question, selectedOptionIds)) {
                correctAnswers++;
            }
        }

        int score = (int) ((correctAnswers / (double) totalQuestions) * 100);

        // Get attempt number
        Integer maxAttempt = quizSubmissionRepository.findMaxAttemptNumber(studentId, quizId);
        int attemptNumber = (maxAttempt != null ? maxAttempt : 0) + 1;

        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setStudent(student);
        submission.setScore(score);
        submission.setAttemptNumber(attemptNumber);

        return quizMapper.toSimple(quizSubmissionRepository.save(submission));
    }

    private boolean isAnswerCorrect(Question question, List<Long> selectedOptionIds) {
        List<AnswerOption> correctOptions = answerOptionRepository.findByQuestionIdAndIsCorrect(question.getId(), true);

        if (question.getType() == QuestionType.SINGLE_CHOICE) {
            return selectedOptionIds.size() == 1 &&
                    correctOptions.size() == 1 &&
                    correctOptions.get(0).getId().equals(selectedOptionIds.get(0));
        } else { // MULTIPLE_CHOICE
            if (selectedOptionIds.size() != correctOptions.size()) {
                return false;
            }

            List<Long> correctOptionIds = correctOptions.stream()
                    .map(AnswerOption::getId)
                    .toList();

            return correctOptionIds.containsAll(selectedOptionIds) &&
                    selectedOptionIds.containsAll(correctOptionIds);
        }
    }

    public List<QuizSubmissionSimple> getQuizResults(Long quizId) {
        return quizSubmissionRepository.findByQuizIdWithDetails(quizId).stream().map(quizMapper::toSimple).toList();
    }

    public List<QuizSubmissionSimple> getStudentQuizResults(Long studentId) {
        return quizSubmissionRepository.findByStudentIdWithDetails(studentId).stream().map(quizMapper::toSimple).toList();
    }

    public Double getQuizAverageScore(Long quizId) {
        return quizSubmissionRepository.findAverageScoreByQuizId(quizId);
    }
}
