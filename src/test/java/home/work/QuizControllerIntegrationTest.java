package home.work;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.work.dto.request.CreateQuestionRequest;
import home.work.dto.request.CreateQuizRequest;
import home.work.entities.AnswerOption;
import home.work.entities.Category;
import home.work.entities.Course;
import home.work.entities.Module;
import home.work.entities.User;
import home.work.entities.UserRole;
import home.work.entities.Question;
import home.work.entities.QuestionType;
import home.work.entities.Quiz;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class QuizControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Module module;
    private User student;
    private Quiz quiz;
    private Question question;

    @BeforeEach
    void setUp() {
        // Create course structure
        User teacher = new User();
        teacher.setName("Quiz Teacher");
        teacher.setEmail("quiz.teacher@example.com");
        teacher.setRole(UserRole.TEACHER);
        teacher = userRepository.save(teacher);

        Category category = new Category();
        category.setName("Quiz Category");
        category = categoryRepository.save(category);

        Course course = new Course();
        course.setTitle("Quiz Course");
        course.setTeacher(teacher);
        course.setCategory(category);
        course = courseRepository.save(course);

        module = new Module();
        module.setTitle("Quiz Module");
        module.setCourse(course);
        module = moduleRepository.save(module);

        // Create quiz
        quiz = new Quiz();
        quiz.setTitle("Test Quiz");
        quiz.setDescription("Test Quiz Description");
        quiz.setModule(module);
        quiz = quizRepository.save(quiz);

        // Create question
        question = new Question();
        question.setText("What is 2+2?");
        question.setType(QuestionType.SINGLE_CHOICE);
        question.setQuiz(quiz);
        question = questionRepository.save(question);

        // Create answer options
        AnswerOption correctOption = new AnswerOption();
        correctOption.setText("4");
        correctOption.setIsCorrect(true);
        correctOption.setQuestion(question);
        answerOptionRepository.save(correctOption);

        AnswerOption wrongOption = new AnswerOption();
        wrongOption.setText("5");
        wrongOption.setIsCorrect(false);
        wrongOption.setQuestion(question);
        answerOptionRepository.save(wrongOption);

        // Create student
        student = new User();
        student.setName("Quiz Student");
        student.setEmail("quiz.student@example.com");
        student.setRole(UserRole.STUDENT);
        student = userRepository.save(student);
    }

    @Test
    void createQuiz_ShouldReturnCreatedQuiz() throws Exception {
        CreateQuizRequest newQuiz = new CreateQuizRequest();
        newQuiz.setTitle("New Quiz");
        newQuiz.setDescription("New Quiz Description");
        newQuiz.setModuleId(1L);

        mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newQuiz)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Quiz"));
    }

    @Test
    void addQuestionToQuiz_ShouldCreateQuestion() throws Exception {
        CreateQuestionRequest newQuestion = new CreateQuestionRequest();
        newQuestion.setText("What is the capital of France?");
        newQuestion.setType("SINGLE_CHOICE");
        newQuestion.setPoints(2);

        mockMvc.perform(post("/api/quizzes/{quizId}/questions", quiz.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newQuestion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("What is the capital of France?"));
    }

    @Test
    void submitQuiz_ShouldCreateQuizSubmission() throws Exception {
        // Get the correct answer option ID
        Long correctOptionId = answerOptionRepository.findByQuestionIdAndIsCorrect(question.getId(), true)
                .get(0).getId();

        Map<Long, List<Long>> answers = new HashMap<>();
        answers.put(question.getId(), List.of(correctOptionId));

        mockMvc.perform(post("/api/quizzes/{quizId}/submit", quiz.getId())
                        .param("studentId", student.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answers)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(0));
    }

    @Test
    void getQuizResults_ShouldReturnSubmissions() throws Exception {
        // First submit a quiz
        Long correctOptionId = answerOptionRepository.findByQuestionIdAndIsCorrect(question.getId(), true)
                .get(0).getId();

        Map<Long, List<Long>> answers = new HashMap<>();
        answers.put(question.getId(), List.of(correctOptionId));

        mockMvc.perform(post("/api/quizzes/{quizId}/submit", quiz.getId())
                .param("studentId", student.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answers)));

        mockMvc.perform(get("/api/quizzes/{quizId}/results", quiz.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].score").value(0));
    }
}
