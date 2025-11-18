package home.work.controllers;

import home.work.dto.request.CreateAnswerOptionRequest;
import home.work.dto.request.CreateQuestionRequest;
import home.work.dto.request.CreateQuizRequest;
import home.work.services.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Контроллер для управления викторинами и вопросами.
 */
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;

    /**
     * Создать новую викторину для модуля.
     *
     * @param quiz Данные викторины
     * @return Созданная викторина
     */
    @PostMapping
    public ResponseEntity<?> createQuiz(
            @Valid @RequestBody CreateQuizRequest quiz) {
        return ResponseEntity.ok(quizService.createQuizForModule(quiz));
    }

    /**
     * Получить викторину по идентификатору вместе с вопросами и вариантами ответов.
     *
     * @param id Идентификатор викторины
     * @return Викторина с вопросами и вариантами ответов
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuiz(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getByIdWithQuestions(id));
    }

    /**
     * Добавить вопрос к викторине.
     *
     * @param quizId   Идентификатор викторины
     * @param question Данные вопроса
     * @return Обновленная викторина с новым вопросом
     */
    @PostMapping("/{quizId}/questions")
    public ResponseEntity<?> addQuestion(
            @PathVariable Long quizId,
            @Valid @RequestBody CreateQuestionRequest question) {
        return ResponseEntity.ok(quizService.addQuestionToQuiz(quizId, question));
    }

    /**
     * Добавить вариант ответа к вопросу.
     *
     * @param questionId   Идентификатор вопроса
     * @param answerOption Данные варианта ответа
     * @return Обновленный вопрос с новым вариантом ответа
     */
    @PostMapping("/questions/{questionId}/options")
    public ResponseEntity<?> addAnswerOption(
            @PathVariable Long questionId,
            @Valid @RequestBody CreateAnswerOptionRequest answerOption) {
        return ResponseEntity.ok(quizService.addAnswerOptionToQuestion(questionId, answerOption));
    }

    /**
     * Отправить ответы студента на викторину.
     *
     * @param quizId    Идентификатор викторины
     * @param studentId Идентификатор студента
     * @param answers   Карта вопросов и выбранных вариантов ответов
     * @return Результат отправки ответов
     */
    @PostMapping("/{quizId}/submit")
    public ResponseEntity<?> submitQuiz(
            @PathVariable Long quizId,
            @RequestParam Long studentId,
            @RequestBody Map<Long, List<Long>> answers) {
        return ResponseEntity.ok(quizService.submitQuiz(quizId, studentId, answers));
    }

    /**
     * Получить результаты викторины для всех студентов.
     *
     * @param quizId Идентификатор викторины
     * @return Список результатов викторины
     */
    @GetMapping("/{quizId}/results")
    public ResponseEntity<?> getQuizResults(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizResults(quizId));
    }

    /**
     * Получить результаты викторин для конкретного студента.
     *
     * @param studentId Идентификатор студента
     * @return Список результатов викторин студента
     */
    @GetMapping("/student/{studentId}/results")
    public ResponseEntity<?> getStudentQuizResults(@PathVariable Long studentId) {
        return ResponseEntity.ok(quizService.getStudentQuizResults(studentId));
    }

    /**
     * Получить средний балл по викторине.
     *
     * @param quizId Идентификатор викторины
     * @return Средний балл
     */
    @GetMapping("/{quizId}/average-score")
    public ResponseEntity<?> getQuizAverageScore(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizAverageScore(quizId));
    }
}
