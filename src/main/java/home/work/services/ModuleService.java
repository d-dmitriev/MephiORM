package home.work.services;

import home.work.dto.request.CreateModuleRequest;
import home.work.dto.request.UpdateModuleRequest;
import home.work.dto.simple.ModuleSimple;
import home.work.entities.Module;
import home.work.mappers.ModuleMapper;
import home.work.repositories.CourseRepository;
import home.work.repositories.LessonRepository;
import home.work.repositories.ModuleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Сервис для управления модулями (modules) и связанными сущностями.
 */
@Service
@RequiredArgsConstructor
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    private final ModuleMapper moduleMapper;

    @Transactional
    public ModuleSimple createModule(CreateModuleRequest request) {
        var course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Set order index if not provided
        if (request.getOrderIndex() == null) {
            var maxOrderIndex = moduleRepository.findByCourseId(request.getCourseId()).stream()
                    .map(Module::getOrderIndex)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);
            request.setOrderIndex(maxOrderIndex + 1);
        }

        Module module = moduleMapper.toEntity(request, course);
        return moduleMapper.toSimple(moduleRepository.save(module));
    }

    @Transactional
    public ModuleSimple updateModule(Long moduleId, UpdateModuleRequest moduleDetails) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        moduleMapper.updateModule(moduleDetails, module);

        return moduleMapper.toSimple(moduleRepository.save(module));
    }

    public ModuleSimple getModuleWithLessons(Long moduleId) {
        return moduleRepository.findByIdWithLessons(moduleId).map(moduleMapper::toSimple)
                .orElseThrow(() -> new RuntimeException("Module not found"));
    }

    public List<ModuleSimple> getCourseModules(Long courseId) {
        return moduleRepository.findByCourseIdOrderByOrderIndex(courseId).stream().map(moduleMapper::toSimple).toList();
    }

    public List<ModuleSimple> getCourseModulesWithLessons(Long courseId) {
        return moduleRepository.findByCourseIdWithLessons(courseId).stream().map(moduleMapper::toSimple).toList();
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