package home.work.services;

import home.work.dto.request.CreateModuleRequest;
import home.work.dto.request.UpdateModuleRequest;
import home.work.dto.simple.ModuleSimple;
import home.work.entities.Module;
import home.work.mappers.ModuleMapper;
import home.work.repositories.CourseRepository;
import home.work.repositories.ModuleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Сервис для управления модулями (modules) и связанными сущностями.
 */
@Service
@RequiredArgsConstructor
public class ModuleService {
    private static final Logger log = LoggerFactory.getLogger(ModuleService.class);
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    private final ModuleMapper moduleMapper;

    /**
     * Создание нового модуля.
     *
     * @param request данные для создания модуля
     * @return созданный модуль в упрощенном виде
     */
    @Transactional
    public ModuleSimple createModule(CreateModuleRequest request) {
        var course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> {
                    log.error("Course with id {} not found", request.getCourseId());
                    return new RuntimeException("Course not found");
                });

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

    /**
     * Обновление существующего модуля.
     *
     * @param moduleId      идентификатор модуля
     * @param moduleDetails данные для обновления модуля
     * @return обновленный модуль в упрощенном виде
     */
    @Transactional
    public ModuleSimple updateModule(Long moduleId, UpdateModuleRequest moduleDetails) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> {
                    log.error("Module with id {} not found", moduleId);
                    return new RuntimeException("Module not found");
                });

        moduleMapper.updateModule(moduleDetails, module);

        return moduleMapper.toSimple(moduleRepository.save(module));
    }

    /**
     * Получение модуля по идентификатору вместе с его уроками.
     *
     * @param moduleId идентификатор модуля
     * @return модуль в упрощенном виде вместе с уроками
     */
    public ModuleSimple getModuleWithLessons(Long moduleId) {
        return moduleRepository.findByIdWithLessons(moduleId).map(moduleMapper::toSimple)
                .orElseThrow(() -> {
                    log.error("Module with id {} not found", moduleId);
                    return new RuntimeException("Module not found");
                });
    }

    /**
     * Получение всех модулей курса.
     *
     * @param courseId идентификатор курса
     * @return список модулей в упрощенном виде
     */
    public List<ModuleSimple> getCourseModules(Long courseId) {
        return moduleRepository.findByCourseIdOrderByOrderIndex(courseId).stream().map(moduleMapper::toSimple).toList();
    }

    /**
     * Удаление модуля по идентификатору.
     *
     * @param moduleId идентификатор модуля
     */
    @Transactional
    public void deleteModule(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> {
                    log.error("Module with id {} not found", moduleId);
                    return new RuntimeException("Module not found");
                });

        // Check if module has lessons
        if (!module.getLessons().isEmpty()) {
            log.error("Cannot delete module with id {} because it contains lessons", moduleId);
            throw new RuntimeException("Cannot delete module that contains lessons");
        }

        moduleRepository.delete(module);
    }

    /**
     * Переупорядочивание модулей в курсе.
     *
     * @param courseId         идентификатор курса
     * @param moduleIdsInOrder список идентификаторов модулей в новом порядке
     */
    @Transactional
    public void reorderModules(Long courseId, List<Long> moduleIdsInOrder) {
        List<Module> modules = moduleRepository.findByCourseId(courseId);

        if (modules.size() != moduleIdsInOrder.size()) {
            log.error("Invalid module order list for course id {}", courseId);
            throw new RuntimeException("Invalid module order list");
        }

        for (int i = 0; i < moduleIdsInOrder.size(); i++) {
            Long moduleId = moduleIdsInOrder.get(i);
            Module module = modules.stream()
                    .filter(m -> m.getId().equals(moduleId))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("Module with id {} not found in course id {}", moduleId, courseId);
                        return new RuntimeException("Module not found: " + moduleId);
                    });

            module.setOrderIndex(i + 1);
            moduleRepository.save(module);
        }
    }
}