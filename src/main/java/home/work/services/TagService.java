package home.work.services;

import home.work.dto.request.CreateTagRequest;
import home.work.dto.simple.CourseSimple;
import home.work.dto.simple.TagSimple;
import home.work.entities.Course;
import home.work.entities.Tag;
import home.work.mappers.CourseMapper;
import home.work.mappers.TagMapper;
import home.work.repositories.CourseRepository;
import home.work.repositories.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Сервис для управления тегами (tags) и связанными сущностями.
 */
@Service
@RequiredArgsConstructor
public class TagService {
    private static final Logger log = LoggerFactory.getLogger(TagService.class);
    private final TagRepository tagRepository;
    private final CourseRepository courseRepository;

    private final CourseMapper courseMapper;
    private final TagMapper tagMapper;

    /**
     * Создание нового тега.
     *
     * @param request данные для создания тега
     * @return созданный тег в упрощенном виде
     */
    @Transactional
    public TagSimple createTag(CreateTagRequest request) {
        if (tagRepository.findByName(request.getName()).isPresent()) {
            log.error("Tag with name {} already exists", request.getName());
            throw new RuntimeException("Tag with name " + request.getName() + " already exists");
        }
        Tag tag = tagMapper.toEntity(request);
        return tagMapper.toSimple(tagRepository.save(tag));
    }

    /**
     * Получение тега по идентификатору.
     *
     * @param tagId идентификатор тега
     * @return тег в упрощенном виде
     */
    public TagSimple getTagById(Long tagId) {
        return tagRepository.findById(tagId).map(tagMapper::toSimple)
                .orElseThrow(() -> {
                    log.error("Tag with id {} not found", tagId);
                    return new RuntimeException("Tag not found");
                });
    }

    /**
     * Получение всех тегов.
     *
     * @return список всех тегов в упрощенном виде
     */
    public List<TagSimple> getAllTags() {
        return tagRepository.findAll().stream().map(tagMapper::toSimple).toList();
    }

    /**
     * Поиск тегов по имени.
     *
     * @param query строка поиска
     * @return список найденных тегов в упрощенном виде
     */
    public List<TagSimple> searchTags(String query) {
        return tagRepository.findByNameContainingIgnoreCase(query).stream().map(tagMapper::toSimple).toList();
    }

    /**
     * Добавление тегов к курсу.
     *
     * @param courseId идентификатор курса
     * @param tagIds   набор идентификаторов тегов
     */
    @Transactional
    public void addTagsToCourse(Long courseId, Set<Long> tagIds) {
        if (!courseRepository.existsById(courseId)) {
            log.error("Course with id {} not found", courseId);
            throw new RuntimeException("Course not found");
        }

        List<Tag> existingTags = tagRepository.findAllById(tagIds);
        if (existingTags.size() != tagIds.size()) {
            log.error("Some tags not found for ids: {}", tagIds);
            throw new RuntimeException("Some tags not found");
        }

        for (Tag tag : existingTags)
            courseRepository.addTagsToCourseWithClear(courseId, tag.getId());
    }

    /**
     * Удаление тега из курса.
     *
     * @param courseId идентификатор курса
     * @param tagId    идентификатор тега
     */
    @Transactional
    public void removeTagFromCourse(Long courseId, Long tagId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("Course with id {} not found", courseId);
                    return new RuntimeException("Course not found");
                });
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> {
                    log.error("Tag with id {} not found", tagId);
                    return new RuntimeException("Tag not found");
                });

        courseRepository.removeTagFromCourse(courseId, tagId);
    }

    /**
     * Получение всех тегов курса.
     *
     * @param courseId идентификатор курса
     * @return список тегов в упрощенном виде
     */
    public List<TagSimple> getCourseTags(Long courseId) {
        return tagRepository.findByCourseId(courseId).stream().map(tagMapper::toSimple).toList();
    }

    /**
     * Получение всех курсов с заданным тегом.
     *
     * @param tagId идентификатор тега
     * @return список курсов в упрощенном виде
     */
    public List<CourseSimple> getCoursesByTag(Long tagId) {
        return courseRepository.findByTagId(tagId).stream().map(courseMapper::toSimple).toList();
    }

    /**
     * Удаление тега.
     *
     * @param tagId идентификатор тега
     */
    @Transactional
    public void deleteTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> {
                    log.error("Tag with id {} not found", tagId);
                    return new RuntimeException("Tag not found");
                });

        // Remove tag from all courses
        for (Course course : tag.getCourses()) {
            course.getTags().remove(tag);
            courseRepository.save(course);
        }

        tagRepository.delete(tag);
    }

    /**
     * Получение популярных тегов вместе с количеством курсов, связанных с каждым тегом.
     *
     * @return список массивов объектов, где каждый массив содержит тег и количество связанных курсов
     */
    public List<Object[]> getPopularTagsWithCounts() {
        return tagRepository.findPopularTagsWithCourseCount();
    }
}
