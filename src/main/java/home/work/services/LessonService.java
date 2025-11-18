package home.work.services;

import home.work.dto.request.CreateLessonRequest;
import home.work.dto.request.UpdateLessonRequest;
import home.work.dto.simple.LessonSimple;
import home.work.entities.Lesson;
import home.work.mappers.LessonMapper;
import home.work.repositories.LessonRepository;
import home.work.repositories.ModuleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Сервис для управления уроками (lessons) и связанными сущностями.
 */
@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    private final LessonMapper lessonMapper;

    @Transactional
    public LessonSimple createLesson(CreateLessonRequest request) {
        var module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found"));

        // Set order index if not provided
        if (request.getOrderIndex() == null) {
            var maxOrderIndex = lessonRepository.findByModuleId(request.getModuleId()).stream()
                    .map(Lesson::getOrderIndex)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);
            request.setOrderIndex(maxOrderIndex + 1);
        }
        Lesson lesson = lessonMapper.toEntity(request, module);

        return lessonMapper.toSimple(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonSimple updateLesson(Long lessonId, UpdateLessonRequest lessonDetails) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        lessonMapper.updateLesson(lessonDetails, lesson);

        return lessonMapper.toSimple(lessonRepository.save(lesson));
    }

    public LessonSimple getLessonWithAssignments(Long lessonId) {
        return lessonRepository.findByIdWithAssignments(lessonId).map(lessonMapper::toSimple)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
    }

    public List<LessonSimple> getModuleLessons(Long moduleId) {
        return lessonRepository.findByModuleIdOrderByOrderIndex(moduleId).stream().map(lessonMapper::toSimple).toList();
    }

    @Transactional
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        // Check if lesson has assignments
        if (!lesson.getAssignments().isEmpty()) {
            throw new RuntimeException("Cannot delete lesson that contains assignments");
        }

        lessonRepository.delete(lesson);
    }

    @Transactional
    public void reorderLessons(Long moduleId, List<Long> lessonIdsInOrder) {
        List<Lesson> lessons = lessonRepository.findByModuleId(moduleId);

        if (lessons.size() != lessonIdsInOrder.size()) {
            throw new RuntimeException("Invalid lesson order list");
        }

        for (int i = 0; i < lessonIdsInOrder.size(); i++) {
            Long lessonId = lessonIdsInOrder.get(i);
            Lesson lesson = lessons.stream()
                    .filter(l -> l.getId().equals(lessonId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Lesson not found: " + lessonId));

            lesson.setOrderIndex(i + 1);
            lessonRepository.save(lesson);
        }
    }

    public List<LessonSimple> searchLessonsByTitle(String title) {
        return lessonRepository.findAll().stream()
                .filter(lesson -> lesson.getTitle().toLowerCase().contains(title.toLowerCase()))
                .map(lessonMapper::toSimple)
                .toList();
    }
}
