package home.work.mappers;

import home.work.dto.request.CreateAnswerOptionRequest;
import home.work.dto.request.CreateQuestionRequest;
import home.work.dto.request.CreateQuizRequest;
import home.work.dto.simple.AnswerOptionSimple;
import home.work.dto.simple.QuestionSimple;
import home.work.dto.simple.QuizSimple;
import home.work.dto.simple.QuizSubmissionSimple;
import home.work.entities.*;
import home.work.entities.Module;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    QuizSimple toSimple(Quiz request);
    QuestionSimple toSimple(Question request);
    AnswerOptionSimple toSimple(AnswerOption request);
    QuizSubmissionSimple toSimple(QuizSubmission request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "module", source = "module")
    Quiz toEntity(CreateQuizRequest request, Module module);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quiz", source = "quiz")
    Question toEntity(CreateQuestionRequest request, Quiz quiz);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "request.text")
    @Mapping(target = "question", source = "question")
    AnswerOption toEntity(CreateAnswerOptionRequest request, Question question);
}
