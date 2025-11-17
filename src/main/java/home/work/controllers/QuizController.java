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

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<?> createQuiz(
            @Valid @RequestBody CreateQuizRequest quiz) {
        return ResponseEntity.ok(quizService.createQuizForModule(quiz));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuiz(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getByIdWithQuestions(id));
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<?> addQuestion(
            @PathVariable Long quizId,
            @Valid @RequestBody CreateQuestionRequest question) {
        return ResponseEntity.ok(quizService.addQuestionToQuiz(quizId, question));
    }

    @PostMapping("/questions/{questionId}/options")
    public ResponseEntity<?> addAnswerOption(
            @PathVariable Long questionId,
            @Valid @RequestBody CreateAnswerOptionRequest answerOption) {
        return ResponseEntity.ok(quizService.addAnswerOptionToQuestion(questionId, answerOption));
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<?> submitQuiz(
            @PathVariable Long quizId,
            @RequestParam Long studentId,
            @RequestBody Map<Long, List<Long>> answers) {
        return ResponseEntity.ok(quizService.submitQuiz(quizId, studentId, answers));
    }

    @GetMapping("/{quizId}/results")
    public ResponseEntity<?> getQuizResults(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizResults(quizId));
    }

    @GetMapping("/student/{studentId}/results")
    public ResponseEntity<?> getStudentQuizResults(@PathVariable Long studentId) {
        return ResponseEntity.ok(quizService.getStudentQuizResults(studentId));
    }

    @GetMapping("/{quizId}/average-score")
    public ResponseEntity<?> getQuizAverageScore(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizAverageScore(quizId));
    }
}
