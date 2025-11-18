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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Сервис для управления викторинами (quizzes), вопросами и связанными сущностями.
 */
@Service
@RequiredArgsConstructor
public class QuizService {
    private static final Logger log = LoggerFactory.getLogger(QuizService.class);
    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserRepository userRepository;

    private final QuizMapper quizMapper;

    /**
     * Создание новой викторины для модуля.
     *
     * @param request данные для создания викторины
     * @return созданная викторина в упрощенном виде
     */
    @Transactional
    public QuizSimple createQuizForModule(CreateQuizRequest request) {
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> {
                    log.error("Module with id {} not found", request.getModuleId());
                    return new RuntimeException("Module not found");
                });

        Quiz quiz = quizMapper.toEntity(request, module);
        return quizMapper.toSimple(quizRepository.save(quiz));
    }

    /**
     * Добавление вопроса к викторине.
     *
     * @param quizId  идентификатор викторины
     * @param request данные для создания вопроса
     * @return созданный вопрос в упрощенном виде
     */
    @Transactional
    public QuestionSimple addQuestionToQuiz(Long quizId, CreateQuestionRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> {
                    log.error("Quiz with id {} not found", quizId);
                    return new RuntimeException("Quiz not found");
                });

        Question question = quizMapper.toEntity(request, quiz);
        return quizMapper.toSimple(questionRepository.save(question));
    }

    /**
     * Добавление варианта ответа к вопросу.
     *
     * @param questionId идентификатор вопроса
     * @param request    данные для создания варианта ответа
     * @return созданный вариант ответа в упрощенном виде
     */
    @Transactional
    public AnswerOptionSimple addAnswerOptionToQuestion(Long questionId, CreateAnswerOptionRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> {
                    log.error("Question with id {} not found", questionId);
                    return new RuntimeException("Question not found");
                });

        AnswerOption answerOption = quizMapper.toEntity(request, question);
        return quizMapper.toSimple(answerOptionRepository.save(answerOption));
    }

    /**
     * Получение викторины по идентификатору вместе с вопросами.
     *
     * @param quizId идентификатор викторины
     * @return викторина в упрощенном виде вместе с вопросами
     */
    @Transactional
    public QuizSimple getByIdWithQuestions(Long quizId) {
        return quizRepository.findByIdWithQuestions(quizId).map(quizMapper::toSimple)
                .orElseThrow(() -> {
                    log.error("Quiz with id {} not found", quizId);
                    return new RuntimeException("Quiz not found");
                });
    }

    /**
     * Отправка ответов студента на викторину и вычисление результата.
     *
     * @param quizId    идентификатор викторины
     * @param studentId идентификатор студента
     * @param answers   карта с идентификаторами вопросов и выбранными вариантами ответов
     * @return информация о результатах отправки в упрощенном виде
     */
    @Transactional
    public QuizSubmissionSimple submitQuiz(Long quizId, Long studentId, Map<Long, List<Long>> answers) {
        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> {
                    log.error("Quiz with id {} not found", quizId);
                    return new RuntimeException("Quiz not found");
                });
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.error("Student with id {} not found", studentId);
                    return new RuntimeException("Student not found");
                });

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

    /**
     * Проверка правильности ответа на вопрос.
     *
     * @param question          вопрос
     * @param selectedOptionIds список идентификаторов выбранных вариантов ответов
     * @return true, если ответ правильный, иначе false
     */
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

    /**
     * Получение всех результатов викторины.
     *
     * @param quizId идентификатор викторины
     * @return список результатов в упрощенном виде
     */
    public List<QuizSubmissionSimple> getQuizResults(Long quizId) {
        return quizSubmissionRepository.findByQuizIdWithDetails(quizId).stream().map(quizMapper::toSimple).toList();
    }

    /**
     * Получение всех результатов студента по викторинам.
     *
     * @param studentId идентификатор студента
     * @return список результатов в упрощенном виде
     */
    public List<QuizSubmissionSimple> getStudentQuizResults(Long studentId) {
        return quizSubmissionRepository.findByStudentIdWithDetails(studentId).stream().map(quizMapper::toSimple).toList();
    }

    /**
     * Вычисление среднего балла по викторине.
     *
     * @param quizId идентификатор викторины
     * @return средний балл
     */
    public Double getQuizAverageScore(Long quizId) {
        return quizSubmissionRepository.findAverageScoreByQuizId(quizId);
    }
}
