package home.work.services;

import home.work.entities.Lesson;
import home.work.repositories.AssignmentRepository;
import home.work.repositories.LessonRepository;
import home.work.repositories.ModuleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final AssignmentRepository assignmentRepository;

    @Transactional
    public Lesson createLesson(Long moduleId, Lesson lesson) {
        var module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        // Set order index if not provided
        if (lesson.getOrderIndex() == null) {
            Integer maxOrderIndex = lessonRepository.findByModuleId(moduleId).stream()
                    .mapToInt(Lesson::getOrderIndex)
                    .max()
                    .orElse(0);
            lesson.setOrderIndex(maxOrderIndex + 1);
        }

        lesson.setModule(module);
        return lessonRepository.save(lesson);
    }

    @Transactional
    public Lesson updateLesson(Long lessonId, Lesson lessonDetails) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        if (lessonDetails.getTitle() != null) {
            lesson.setTitle(lessonDetails.getTitle());
        }
        if (lessonDetails.getContent() != null) {
            lesson.setContent(lessonDetails.getContent());
        }
        if (lessonDetails.getVideoUrl() != null) {
            lesson.setVideoUrl(lessonDetails.getVideoUrl());
        }
        if (lessonDetails.getDuration() != null) {
            lesson.setDuration(lessonDetails.getDuration());
        }
        if (lessonDetails.getOrderIndex() != null) {
            lesson.setOrderIndex(lessonDetails.getOrderIndex());
        }

        return lessonRepository.save(lesson);
    }

    public Lesson getLessonWithAssignments(Long lessonId) {
        return lessonRepository.findByIdWithAssignments(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
    }

    public List<Lesson> getModuleLessons(Long moduleId) {
        return lessonRepository.findByModuleIdOrderByOrderIndex(moduleId);
    }

    public List<Lesson> getModuleLessonsWithAssignments(Long moduleId) {
        return lessonRepository.findByModuleIdWithAssignments(moduleId);
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

    public Long getTotalCourseDuration(Long courseId) {
        return lessonRepository.countByCourseId(courseId);
    }

    public List<Lesson> searchLessonsByTitle(String title) {
        return lessonRepository.findAll().stream()
                .filter(lesson -> lesson.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
    }

    public Double getAverageLessonDuration(Long courseId) {
        List<Lesson> lessons = lessonRepository.findByModuleIdWithAssignments(courseId).stream()
                .toList();

        if (lessons.isEmpty()) {
            return 0.0;
        }

        return lessons.stream()
                .filter(lesson -> lesson.getDuration() != null)
                .mapToInt(Lesson::getDuration)
                .average()
                .orElse(0.0);
    }
}
