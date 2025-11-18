package home.work.services;

import home.work.dto.request.CreateCategoryRequest;
import home.work.dto.request.UpdateCategoryRequest;
import home.work.dto.simple.CategorySimple;
import home.work.dto.simple.CourseSimple;
import home.work.entities.Category;
import home.work.mappers.CategoryMapper;
import home.work.mappers.CourseMapper;
import home.work.repositories.CategoryRepository;
import home.work.repositories.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления категориями курсов.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    private final CategoryMapper categoryMapper;
    private final CourseMapper courseMapper;

    @Transactional
    public CategorySimple createCategory(CreateCategoryRequest category) {
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new RuntimeException("Category with name " + category.getName() + " already exists");
        }
        return categoryMapper.toSimple(categoryRepository.save(categoryMapper.toEntity(category)));
    }

    @Transactional
    public CategorySimple updateCategory(Long categoryId, UpdateCategoryRequest categoryDetails) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (categoryDetails.getName() != null) {
            // Check if name is already taken by another category
            categoryRepository.findByName(categoryDetails.getName())
                    .ifPresent(existingCategory -> {
                        if (!existingCategory.getId().equals(categoryId)) {
                            throw new RuntimeException("Category name already taken");
                        }
                    });
        }

        categoryMapper.updateCategory(categoryDetails, category);

        return categoryMapper.toSimple(categoryRepository.save(category));
    }

    public CategorySimple getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).map(categoryMapper::toSimple)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public CategorySimple getCategoryByName(String name) {
        return categoryRepository.findByName(name).map(categoryMapper::toSimple)
                .orElseThrow(() -> new RuntimeException("Category not found: " + name));
    }

    public List<CategorySimple> getAllCategories() {
        return categoryRepository.findAll().stream().map(categoryMapper::toSimple).toList();
    }

    public List<CourseSimple> getCoursesByCategory(Long categoryId) {
        Category category = categoryRepository.findByIdWithCourses(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return category.getCourses().stream().map(courseMapper::toSimple).toList();
    }

//    public List<Course> getCoursesByCategoryName(String categoryName) {
//        return courseRepository.findByCategoryName(categoryName);
//    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Check if category has courses
        if (!category.getCourses().isEmpty()) {
            throw new RuntimeException("Cannot delete category that contains courses");
        }

        categoryRepository.delete(category);
    }

    public Long getCourseCountByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return (long) category.getCourses().size();
    }

    public List<CategorySimple> getPopularCategories(int limit) {
        return categoryRepository.findAllWithCourses().stream()
                .sorted((c1, c2) -> Long.compare(
                        c2.getCourses().size(),
                        c1.getCourses().size()
                ))
                .limit(limit)
                .map(categoryMapper::toSimple)
                .toList();
    }
}
