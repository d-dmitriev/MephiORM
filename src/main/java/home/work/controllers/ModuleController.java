package home.work.controllers;

import home.work.entities.Module;
import home.work.services.ModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {
    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<?> createModule(
            @RequestParam Long courseId,
            @Valid @RequestBody Module module) {
        return ResponseEntity.ok(moduleService.createModule(courseId, module));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getModule(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.getModuleWithLessons(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateModule(
            @PathVariable Long id,
            @Valid @RequestBody Module moduleDetails) {
        return ResponseEntity.ok(moduleService.updateModule(id, moduleDetails));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCourseModules(@PathVariable Long courseId) {
        return ResponseEntity.ok(moduleService.getCourseModules(courseId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/course/{courseId}/reorder")
    public ResponseEntity<?> reorderModules(
            @PathVariable Long courseId,
            @RequestBody List<Long> moduleIds) {
        moduleService.reorderModules(courseId, moduleIds);
        return ResponseEntity.ok().build();
    }
}
