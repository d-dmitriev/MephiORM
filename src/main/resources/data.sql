INSERT INTO categories (name, description) VALUES
('Programming', 'Software development courses'),
('Data Science', 'Data analysis and machine learning'),
('Web Development', 'Frontend and backend web technologies');

INSERT INTO tags (name) VALUES
('Java'), ('Spring Boot'), ('Hibernate'),
('Python'), ('SQL'), ('JavaScript');

INSERT INTO users (name, email, role, created_at, updated_at) VALUES
('Alice Johnson', 'alice@example.com', 'TEACHER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bob Smith', 'bob@example.com', 'TEACHER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Charlie Brown', 'charlie@example.com', 'STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Diana Prince', 'diana@example.com', 'STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO profiles (user_id, bio, avatar_url, created_at, updated_at) VALUES
(2, 'Data science expert with 10 years experience', '/avatars/bob.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Experienced Java developer and instructor', '/avatars/alice.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO courses (title, description, duration, category_id, teacher_id, start_date, created_at, updated_at) VALUES
('Title 1', 'Description 1', 7,  1, 1, NOW() + INTERVAL '7 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Title 2', 'Description 2', 14, 2, 2, NOW() + INTERVAL '14 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Title 3', 'Description 3', 30, 3, 1, NOW() + INTERVAL '30 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Title 4', 'Description 4', 30, 3, 1, NOW() + INTERVAL '30 days', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO course_tag (course_id, tag_id) VALUES
(1, 1),
(1, 3),
(2, 4);

INSERT INTO modules (title, course_id) VALUES
('Module 1', 1);

INSERT INTO lessons (title, module_id) VALUES
('Lesson 1', 1);

INSERT INTO assignments (title, due_date, lesson_id) VALUES
('Assignment 1', NOW() - INTERVAL '1 days', 1),
('Assignment 2', NOW() + INTERVAL '1 days', 1);