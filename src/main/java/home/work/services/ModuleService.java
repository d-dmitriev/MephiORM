package home.work.services;

import home.work.entities.Module;
import home.work.repositories.CourseRepository;
import home.work.repositories.LessonRepository;
import home.work.repositories.ModuleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    @Transactional
    public Module createModule(Long courseId, Module module) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Set order index if not provided
        if (module.getOrderIndex() == null) {
            Integer maxOrderIndex = moduleRepository.findByCourseId(courseId).stream()
                    .mapToInt(Module::getOrderIndex)
                    .max()
                    .orElse(0);
            module.setOrderIndex(maxOrderIndex + 1);
        }

        module.setCourse(course);
        return moduleRepository.save(module);
    }

    @Transactional
    public Module updateModule(Long moduleId, Module moduleDetails) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        if (moduleDetails.getTitle() != null) {
            module.setTitle(moduleDetails.getTitle());
        }
        if (moduleDetails.getDescription() != null) {
            module.setDescription(moduleDetails.getDescription());
        }
        if (moduleDetails.getOrderIndex() != null) {
            module.setOrderIndex(moduleDetails.getOrderIndex());
        }

        return moduleRepository.save(module);
    }

    public Module getModuleWithLessons(Long moduleId) {
        return moduleRepository.findByIdWithLessons(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));
    }

    public List<Module> getCourseModules(Long courseId) {
        return moduleRepository.findByCourseIdOrderByOrderIndex(courseId);
    }

    public List<Module> getCourseModulesWithLessons(Long courseId) {
        return moduleRepository.findByCourseIdWithLessons(courseId);
    }

    @Transactional
    public void deleteModule(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        // Check if module has lessons
        if (!module.getLessons().isEmpty()) {
            throw new RuntimeException("Cannot delete module that contains lessons");
        }

        moduleRepository.delete(module);
    }

    @Transactional
    public void reorderModules(Long courseId, List<Long> moduleIdsInOrder) {
        List<Module> modules = moduleRepository.findByCourseId(courseId);

        if (modules.size() != moduleIdsInOrder.size()) {
            throw new RuntimeException("Invalid module order list");
        }

        for (int i = 0; i < moduleIdsInOrder.size(); i++) {
            Long moduleId = moduleIdsInOrder.get(i);
            Module module = modules.stream()
                    .filter(m -> m.getId().equals(moduleId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Module not found: " + moduleId));

            module.setOrderIndex(i + 1);
            moduleRepository.save(module);
        }
    }

    public Long getModuleCountForCourse(Long courseId) {
        return (long) moduleRepository.findByCourseId(courseId).size();
    }

    public Double getAverageLessonsPerModule(Long courseId) {
        List<Module> modules = moduleRepository.findByCourseIdWithLessons(courseId);
        if (modules.isEmpty()) {
            return 0.0;
        }

        return modules.stream()
                .mapToInt(module -> module.getLessons().size())
                .average()
                .orElse(0.0);
    }
}