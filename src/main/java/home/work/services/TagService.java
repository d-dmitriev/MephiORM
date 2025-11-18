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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Сервис для управления тегами (tags) и связанными сущностями.
 */
@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final CourseRepository courseRepository;

    private final CourseMapper courseMapper;
    private final TagMapper tagMapper;

    @Transactional
    public TagSimple createTag(CreateTagRequest request) {
        if (tagRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Tag with name " + request.getName() + " already exists");
        }
        Tag tag = tagMapper.toEntity(request);
        return tagMapper.toSimple(tagRepository.save(tag));
    }

    public TagSimple getTagById(Long tagId) {
        return tagRepository.findById(tagId).map(tagMapper::toSimple)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
    }

    public List<TagSimple> getAllTags() {
        return tagRepository.findAll().stream().map(tagMapper::toSimple).toList();
    }

    public List<TagSimple> searchTags(String query) {
        return tagRepository.findByNameContainingIgnoreCase(query).stream().map(tagMapper::toSimple).toList();
    }

    @Transactional
    public void addTagsToCourse(Long courseId, Set<Long> tagIds) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Course not found");
        }

        List<Tag> existingTags = tagRepository.findAllById(tagIds);
        if (existingTags.size() != tagIds.size()) {
            throw new RuntimeException("Some tags not found");
        }

        for (Tag tag : existingTags)
            courseRepository.addTagsToCourseWithClear(courseId, tag.getId());
    }

    @Transactional
    public void removeTagFromCourse(Long courseId, Long tagId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));

        courseRepository.removeTagFromCourse(courseId, tagId);
    }

    public List<TagSimple> getCourseTags(Long courseId) {
        return tagRepository.findByCourseId(courseId).stream().map(tagMapper::toSimple).toList();
    }

    public List<CourseSimple> getCoursesByTag(Long tagId) {
        return courseRepository.findByTagId(tagId).stream().map(courseMapper::toSimple).toList();
    }

    @Transactional
    public void deleteTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));

        // Remove tag from all courses
        for (Course course : tag.getCourses()) {
            course.getTags().remove(tag);
            courseRepository.save(course);
        }

        tagRepository.delete(tag);
    }

    public List<Object[]> getPopularTagsWithCounts() {
        return tagRepository.findPopularTagsWithCourseCount();
    }
}
