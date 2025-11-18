# 📚 Learning Platform — Учебная платформа на Spring Boot + JPA/Hibernate

[![CI](https://github.com/d-dmitriev/MephiORM/workflows/CI/badge.svg)](https://github.com/d-dmitriev/MephiORM/actions/workflows/ci.yml)

## 🎯 Описание проекта

Проект представляет собой **веб-платформу для онлайн-обучения**, реализованную на **Spring Boot 3.5.6**, **Java 21**, *
*JPA/Hibernate** и **PostgreSQL**.  
Система позволяет:

- Преподавателям: создавать курсы, модули, уроки, задания и тесты;
- Студентам: записываться на курсы, выполнять задания, проходить тесты и оставлять отзывы;
- Администраторам: управлять категориями, тегами и статистикой.

Все сущности связаны через **1:1, 1:N, M:N** отношения, с **ленивой загрузкой (LAZY)** для оптимизации
производительности.  
Платформа включает **REST API**, **валидацию входных данных**, **централизованную обработку ошибок**, **интеграционные
тесты** и **предзаполнение демо-данными**.

---

## 🏗️ Архитектура

### Сущности

- `User`, `Profile`, `UserRole` — пользователи и их профили
- `Category`, `Tag` — классификация курсов
- `Course`, `Module`, `Lesson` — иерархия учебного контента
- `Assignment`, `Submission` — домашние задания и решения
- `Quiz`, `Question`, `AnswerOption`, `QuizSubmission` — система тестирования
- `Enrollment` — связка студент ↔ курс (с прогрессом и статусом)
- `CourseReview` — отзывы о курсах

### Слои приложения

```
src/main/java/home/work/
├── entities/           # JPA-сущности
├── repositories/       # Spring Data JPA репозитории
├── services/           # Бизнес-логика (Service Layer)
├── dto/                # DTO для запросов и ответов
├── mappers/           # MapStruct мапперы между сущностями и DTO
├── controllers/        # REST API (@RestController)
├── exceptions/         # GlobalExceptionHandler
├── configuration/      # WebConfig (CORS)
└── Application.java    # Точка входа
```

### Технологии

| Компонент       | Версия  |
|-----------------|---------|
| Java            | 21      |
| Spring Boot     | 3.5.6   |
| JPA / Hibernate | 6.5+    |
| PostgreSQL      | 42.7.7  |
| H2 (тесты)      | 2.3.232 |
| Lombok          | 1.18.40 |
| Maven           | 3.9+    |
| SpringDoc       | 2.8.13  |

---

## 🚀 Установка и запуск

### 1. Требования

- **Java 21**
- **PostgreSQL 12+**
- **Maven 3.9+**

### 2. Настройка базы данных

Создайте базу данных в PostgreSQL:

```bash
createdb learning_platform
```

Убедитесь, что пользователь `postgres` существует и имеет пароль (по умолчанию `mysecretpassword`).

> 💡 Для изменения параметров подключения — отредактируйте `src/main/resources/application.yml`.

### 3. Запуск приложения

```bash
# Перейдите в корень проекта
cd /path/to/MephiORM

# Соберите проект
mvn clean package

# Запустите приложение
mvn spring-boot:run
# или
java -jar target/learning-platform-1.0-SNAPSHOT.jar
```

Приложение запустится на `http://localhost:8080`.

> ✅ **Важно**: При каждом запуске Hibernate автоматически пересоздаёт схему БД и заполняет её демо-данными из `data.sql`.

---

## 🌐 REST API

Все эндпоинты доступны по префиксу:  
`GET|POST|PUT|DELETE /api/<resource>`

### 📚 Курсы, уроки, модули и записи

| Метод    | Эндпоинт                                   | Описание                                             |
|----------|--------------------------------------------|------------------------------------------------------|
| `POST`   | `/api/courses`                             | Создать курс (требует `teacherId`, `categoryId`)     |
| `GET`    | `/api/courses`                             | Получить все курсы                                   |
| `GET`    | `/api/courses/{id}`                        | Получить курс (ленивая загрузка модулей)             |
| `GET`    | `/api/courses/{id}/full`                   | Получить курс со всеми модулями, уроками и заданиями |
| `GET`    | `/api/courses/category/{name}`             | Курсы по категории                                   |
| `GET`    | `/api/courses/tag/{name}`                  | Курсы по тегу                                        |
| `POST`   | `/api/courses/{courseId}/enroll`           | Записать студента на курс (`studentId`)              |
| `POST`   | `/api/courses/{courseId}/reviews`          | Оставить отзыв (`studentId`, `rating`, `comment`)    |
| `GET`    | `/api/courses/{courseId}/reviews`          | Получить все отзывы                                  |
| `GET`    | `/api/courses/{courseId}/rating`           | Средний рейтинг курса                                |
| `POST`   | `/api/lessons`                             | Создать новый урок                                   |
| `GET`    | `/api/lessons/{id}`                        | Получить урок по идентификатору                      |
| `PUT`    | `/api/lessons/{id}`                        | Обновить существующий урок                           |
| `GET`    | `/api/lessons/module/{moduleId}`           | Получить все уроки для конкретного модуля            |
| `DELETE` | `/api/lessons/{id}`                        | Удалить урок по идентификатору                       |
| `PUT`    | `/api/lessons/module/{moduleId}/reorder`   | Переупорядочить уроки в модуле                       |
| `GET`    | `/api/lessons/search`                      | Поиск уроков по названию                             |
| `POST`   | `/api/modules`                             | Создать новый модуль                                 |
| `GET`    | `/api/modules/{id}`                        | Получить модуль по идентификатору                    |
| `PUT`    | `/api/modules/{id}`                        | Обновить существующий модуль                         |
| `GET`    | `/api/modules/course/{courseId}`           | Получить все модули для конкретного курса            |
| `DELETE` | `/api/modules/{id}`                        | Удалить модуль по идентификатору                     |
| `PUT`    | `/api/modules/course/{courseId}/reorder`   | Удалить модуль по идентификатору                     |
| `POST`   | `/api/enrollments`                         | Зачислить студента на курс                           |
| `DELETE` | `/api/enrollments`                         | Отчислить студента с курса                           |
| `PUT`    | `/api/enrollments/{enrollmentId}/status`   | Обновить статус записи студента на курс              |
| `PUT`    | `/api/enrollments/{enrollmentId}/progress` | Обновить прогресс студента на курсе                  |
| `GET`    | `/api/enrollments/course/{courseId}`       | Получить все записи на конкретный курс               |
| `GET`    | `/api/enrollments/student/{studentId}`     | Получить все записи конкретного студента             |
| `GET`    | `/api/enrollments/progress`                | Рассчитать прогресс студента на курсе                |

### 📝 Задания и решения

| Метод  | Эндпоинт                                  | Описание                                    |
|--------|-------------------------------------------|---------------------------------------------|
| `POST` | `/api/assignments`                        | Создать задание (`lessonId`)                |
| `GET`  | `/api/assignments/{id}`                   | Получить задание                            |
| `GET`  | `/api/assignments/lesson/{id}`            | Получить все задания для урока.             |
| `POST` | `/api/assignments/{id}/submit`            | Отправить решение (`studentId`, `content`)  |
| `PUT`  | `/api/assignments/submissions/{id}/grade` | Оценить решение (`score`, `feedback`)       |
| `GET`  | `/api/assignments/{id}/submissions`       | Получить все решения по заданию             |
| `GET`  | `/api/assignments/student/{id}`           | Получить все решения студента               |
| `GET`  | `/api/assignments/course/{id}/overdue`    | Получить все просроченные задания для курса |

### 🧪 Тесты

| Метод  | Эндпоинт                                   | Описание                                              |
|--------|--------------------------------------------|-------------------------------------------------------|
| `POST` | `/api/quizzes`                             | Создать тест (`moduleId`)                             |
| `GET`  | `/api/quizzes/{id}`                        | Получить викторину по идентификатору                  |
| `POST` | `/api/quizzes/{quizId}/questions`          | Добавить вопрос                                       |
| `POST` | `/api/questions/{questionId}/options`      | Добавить вариант ответа                               |
| `POST` | `/api/quizzes/{quizId}/submit`             | Пройти тест (`studentId`, `{questionId: [optionId]}`) |
| `GET`  | `/api/quizzes/{quizId}/results`            | Получить результаты теста                             |
| `GET`  | `/api/quizzes/student/{studentId}/results` | Получить результаты викторин для конкретного студента |
| `GET`  | `/api/quizzes/{quizId}/average-score`      | Средний балл по тесту                                 |

### 👥 Пользователи

| Метод    | Эндпоинт                      | Описание                                            |
|----------|-------------------------------|-----------------------------------------------------|
| `POST`   | `/api/users`                  | Создать пользователя (`name`, `email`, `role`)      |
| `GET`    | `/api/users/{id}`             | Получить пользователя                               |
| `GET`    | `/api/users/email/{email}`    | Найти пользователя по email                         |
| `GET`    | `/api/users/{id}/profile`     | Получить профиль                                    |
| `PUT`    | `/api/users/{id}/profile`     | Обновить профиль                                    |
| `GET`    | `/api/users/teachers`         | Получить всех преподавателей                        |
| `GET`    | `/api/users/students`         | Получить всех студентов                             |
| `GET`    | `/api/users/{id}/enrollments` | Получить все курсы, на которые записан пользователь |
| `GET`    | `/api/users/{id}/submissions` | Получить все отправленные задания пользователя      |
| `DELETE` | `/api/users/{id}`             | Удалить пользователя                                |

### 🔖 Теги и категории

| Метод    | Эндпоинт                                | Описание                              |
|----------|-----------------------------------------|---------------------------------------|
| `POST`   | `/api/categories`                       | Создать категорию                     |
| `GET`    | `/api/categories`                       | Получить все категории                |
| `GET`    | `/api/categories/{id}`                  | Получить категорию по идентификатору  |
| `GET`    | `/api/categories/name/{name}`           | Получить категорию по названию        |
| `PUT`    | `/api/categories/{id}`                  | Обновить категорию                    |
| `GET`    | `/api/categories/{id}/courses`          | Получить все курсы в категории        |
| `DELETE` | `/api/categories/{id}`                  | Удалить категорию                     |
| `GET`    | `/api/categories/popular`               | Получить популярные категории         |
| `POST`   | `/api/tags`                             | Создать тег                           |
| `GET`    | `/api/tags`                             | Получить все теги                     |
| `GET`    | `/api/tags/{id}`                        | Получить тег по идентификатору        |
| `GET`    | `/api/tags/search`                      | Найти тег по названию                 |
| `POST`   | `/api/tags/courses/{courseId}/multiple` | Привязать теги к курсу                |
| `DELETE` | `/api/tags/{tagId}/courses/{courseId}`  | Удалить тег из курса                  |
| `GET`    | `/api/tags/courses/{courseId}`          | Получить все теги, связанные с курсом |
| `GET`    | `/api/tags/{tagId}/courses`             | Получить курсы по тегу                |
| `DELETE` | `/api/tags/{id}`                        | Удалить тег по идентификатору         |
| `GET`    | `/api/tags/popular`                     | Получает популярные теги              |

### 📊 Аналитика

| Метод | Эндпоинт                          | Описание                                              |
|-------|-----------------------------------|-------------------------------------------------------|
| `GET` | `/api/analytics/platform`         | Статистика платформы (курсы, студенты, записи)        |
| `GET` | `/api/analytics/courses/{id}`     | Статистика курса (записи, оценки, задания)            |
| `GET` | `/api/analytics/progress`         | Прогресс студента в курсе                             |
| `GET` | `api/analytics/enrollments/trend` | Получить тренд по количеству регистраций на платформе |

---

## 🧪 Тестирование

### Интеграционные тесты

Проект включает **набор интеграционных тестов** с использованием `@SpringBootTest` и `H2` в памяти.

**Тесты покрывают:**

- CRUD операции для всех основных сущностей
- Бизнес-логику в сервисах
- Валидацию и обработку ошибок
- Взаимодействие между сущностями (например, запись на курс, отправка заданий, прохождение тестов)

Запуск:

```bash
mvn test
```

Также в проект добавлена коллекция [Postman](postman_collection.json) для ручного тестирования API (содержит тесты с проверкой статуса).

---

## 📄 Swagger/OpenAPI

Документация по REST API автоматически генерируется с помощью `springdoc-openapi`.  
Доступна по адресу:  
`http://localhost:8080/swagger-ui.html`  
или  
`http://localhost:8080/v3/api-docs`

---

## 🚀 CI/CD

Настроен GitHub Actions для автоматического тестирования (CI) при каждом пуше в репозиторий. Вверху файла находится статус сборки.

---

## 📂 Демо-данные

При запуске приложения автоматически инициализируются:

**Категории:**

- `Programming`
- `Data Science`
- `Web Development`

**Теги:**

- `Java`, `Spring Boot`, `Hibernate`, `Python`, `SQL`, `JavaScript`

**Пользователи:**

- `Alice Johnson` (TEACHER)
- `Bob Smith` (TEACHER)
- `Charlie Brown` (STUDENT)
- `Diana Prince` (STUDENT)

**Курсы:**

- 4 курса с привязкой к категориям и преподавателям.
- 1 модуль и 1 урок.
- 2 записанных студента.

> 🔍 Данные находятся в `src/main/resources/data.sql`.

---

## 🛡️ Валидация и обработка ошибок

- **Валидация**: Используется `@Valid` + `spring-boot-starter-validation`.
- **Обработка ошибок**: Централизованный `GlobalExceptionHandler` возвращает:
    - `400 Bad Request` — при невалидных данных (например, дублирующий email)
    - `500 Internal Server Error` — для неожиданных ошибок
- **Формат ответа**:

```json
{
  "status": 400,
  "message": "User with email user@example.com already exists",
  "timestamp": 1734456789000
}
```

---

## 📁 Структура проекта

```
learning-platform/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── home/work/             # Основной код
│   │   ├── resources/
│   │   │   ├── application.yml        # Основная конфигурация
│   │   │   ├── data.sql               # Предзаполнение БД
│   │   └── resources/
│   │       └── application-test.properties  # Конфиг для тестов
│   └── test/
│       ├── java/
│       │   └── home/work/             # Интеграционные тесты
│       └── resources/
│           └── data-test.sql          # Данные для тестов
└── target/
```

---

## 📦 Дополнительно

- **CORS**: Настроен для `http://localhost:3000`, `http://localhost:8080` (для фронтенда).
- **Lombok**: Упрощает код (`@Data`, `@RequiredArgsConstructor`, `@JsonIgnore`).
- **Open-in-view**: Отключён (`false`) — как рекомендовано для продакшена.
- **Lazy Loading**: Все `@OneToMany`, `@ManyToMany` и большинство `@ManyToOne` — с `fetch = FetchType.LAZY`.
- **Транзакции**: Все сервисные методы — `@Transactional`.

---

## 📬 Как проверить проект

1. Установите PostgreSQL и создайте базу `learning_platform`.
2. Запустите: `mvn spring-boot:run`
3. Откройте Postman или curl:
   ```bash
   curl -X POST http://localhost:8080/api/users \
     -H "Content-Type: application/json" \
     -d '{"name":"Test User","email":"test@example.com","role":"STUDENT"}'
   ```
4. Проверьте, что все эндпоинты работают.
5. Запустите тесты: `mvn test`
6. Убедитесь, что все тесты зелёные.