package home.work.controllers;

import home.work.dto.request.CreateModuleRequest;
import home.work.dto.request.UpdateModuleRequest;
import home.work.services.ModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления модулями в курсах.
 */
@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;

    /**
     * Создать новый модуль.
     *
     * @param module Данные модуля
     * @return Созданный модуль
     */
    @PostMapping
    public ResponseEntity<?> createModule(
            @Valid @RequestBody CreateModuleRequest module) {
        return ResponseEntity.ok(moduleService.createModule(module));
    }

    /**
     * Получить модуль по идентификатору вместе с его уроками.
     *
     * @param id Идентификатор модуля
     * @return Модуль с уроками
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getModule(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.getModuleWithLessons(id));
    }

    /**
     * Обновить существующий модуль.
     *
     * @param id            Идентификатор модуля
     * @param moduleDetails Обновленные данные модуля
     * @return Обновленный модуль
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateModule(
            @PathVariable Long id,
            @Valid @RequestBody UpdateModuleRequest moduleDetails) {
        return ResponseEntity.ok(moduleService.updateModule(id, moduleDetails));
    }

    /**
     * Получить все модули для конкретного курса.
     *
     * @param courseId Идентификатор курса
     * @return Список модулей
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCourseModules(@PathVariable Long courseId) {
        return ResponseEntity.ok(moduleService.getCourseModules(courseId));
    }

    /**
     * Удалить модуль по идентификатору.
     *
     * @param id Идентификатор модуля
     * @return Ответ об успешном удалении
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Переупорядочить модули в курсе.
     *
     * @param courseId  Идентификатор курса
     * @param moduleIds Новый порядок идентификаторов модулей
     * @return Ответ об успешном переупорядочивании
     */
    @PutMapping("/course/{courseId}/reorder")
    public ResponseEntity<?> reorderModules(
            @PathVariable Long courseId,
            @RequestBody List<Long> moduleIds) {
        moduleService.reorderModules(courseId, moduleIds);
        return ResponseEntity.ok().build();
    }
}
