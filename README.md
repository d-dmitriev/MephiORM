# 📚 Learning Platform — Учебная платформа на Spring Boot + JPA/Hibernate

## 🎯 Описание проекта

Проект представляет собой **веб-платформу для онлайн-обучения**, реализованную на **Spring Boot 3.5.6**, **Java 21**, **JPA/Hibernate** и **PostgreSQL**.  
Система позволяет:

- Преподавателям: создавать курсы, модули, уроки, задания и тесты;
- Студентам: записываться на курсы, выполнять задания, проходить тесты и оставлять отзывы;
- Администраторам: управлять категориями, тегами и статистикой.

Все сущности связаны через **1:1, 1:N, M:N** отношения, с **ленивой загрузкой (LAZY)** для оптимизации производительности.  
Платформа включает **REST API**, **валидацию входных данных**, **централизованную обработку ошибок**, **интеграционные тесты** и **предзаполнение демо-данными**.

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
├── controllers/        # REST API (@RestController)
├── exceptions/         # GlobalExceptionHandler
├── configuration/      # WebConfig (CORS)
└── Application.java    # Точка входа
```

### Технологии
| Компонент | Версия |
|----------|--------|
| Java | 21 |
| Spring Boot | 3.5.6 |
| JPA / Hibernate | 6.5+ |
| PostgreSQL | 42.7.7 |
| H2 (тесты) | 2.3.232 |
| Lombok | 1.18.40 |
| Maven | 3.9+ |

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
cd /path/to/learning-platform

# Соберите проект
mvn clean package

# Запустите приложение
mvn spring-boot:run
```

Приложение запустится на `http://localhost:8080`.

> ✅ **Важно**: При первом запуске Hibernate автоматически создаст схему БД и заполнит её демо-данными из `data.sql`.

---

## 🌐 REST API

Все эндпоинты доступны по префиксу:  
`GET|POST|PUT|DELETE /api/<resource>`

### 📚 Курсы
| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `POST` | `/api/courses` | Создать курс (требует `teacherId`, `categoryId`) |
| `GET` | `/api/courses` | Получить все курсы |
| `GET` | `/api/courses/{id}` | Получить курс (ленивая загрузка модулей) |
| `GET` | `/api/courses/{id}/full` | Получить курс со всеми модулями, уроками и заданиями |
| `GET` | `/api/courses/category/{name}` | Курсы по категории |
| `GET` | `/api/courses/tag/{name}` | Курсы по тегу |
| `POST` | `/api/courses/{courseId}/enroll` | Записать студента на курс (`studentId`) |
| `POST` | `/api/courses/{courseId}/reviews` | Оставить отзыв (`studentId`, `rating`, `comment`) |
| `GET` | `/api/courses/{courseId}/reviews` | Получить все отзывы |
| `GET` | `/api/courses/{courseId}/rating` | Средний рейтинг курса |

### 📝 Задания и решения
| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `POST` | `/api/assignments` | Создать задание (`lessonId`) |
| `POST` | `/api/assignments/{id}/submit` | Отправить решение (`studentId`, `content`) |
| `PUT` | `/api/assignments/submissions/{id}/grade` | Оценить решение (`score`, `feedback`) |
| `GET` | `/api/assignments/{id}/submissions` | Получить все решения по заданию |
| `GET` | `/api/assignments/student/{id}` | Получить все решения студента |

### 🧪 Тесты
| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `POST` | `/api/quizzes` | Создать тест (`moduleId`) |
| `POST` | `/api/quizzes/{quizId}/questions` | Добавить вопрос |
| `POST` | `/api/questions/{questionId}/options` | Добавить вариант ответа |
| `POST` | `/api/quizzes/{quizId}/submit` | Пройти тест (`studentId`, `{questionId: [optionId]}`) |
| `GET` | `/api/quizzes/{quizId}/results` | Получить результаты теста |
| `GET` | `/api/quizzes/{quizId}/average-score` | Средний балл по тесту |

### 👥 Пользователи
| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `POST` | `/api/users` | Создать пользователя (`name`, `email`, `role`) |
| `GET` | `/api/users/{id}` | Получить пользователя |
| `GET` | `/api/users/email/{email}` | Найти пользователя по email |
| `GET` | `/api/users/{id}/profile` | Получить профиль |
| `PUT` | `/api/users/{id}/profile` | Обновить профиль |
| `GET` | `/api/users/teachers` | Получить всех преподавателей |
| `GET` | `/api/users/students` | Получить всех студентов |
| `DELETE` | `/api/users/{id}` | Удалить пользователя |

### 🔖 Теги и категории
| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `POST` | `/api/categories` | Создать категорию |
| `GET` | `/api/categories` | Получить все категории |
| `POST` | `/api/tags` | Создать тег |
| `GET` | `/api/tags` | Получить все теги |
| `POST` | `/api/tags/{tagId}/courses/{courseId}` | Привязать тег к курсу |
| `GET` | `/api/tags/{tagId}/courses` | Получить курсы по тегу |

### 📊 Аналитика
| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `GET` | `/api/analytics/platform` | Статистика платформы (курсы, студенты, записи) |
| `GET` | `/api/analytics/courses/{id}` | Статистика курса (записи, оценки, задания) |
| `GET` | `/api/analytics/progress` | Прогресс студента в курсе |

---

## 🧪 Тестирование

### Интеграционные тесты
Проект включает **полный набор интеграционных тестов** с использованием `@SpringBootTest` и `H2` в памяти.

**Тесты покрывают:**
- CRUD для всех сущностей (`UserControllerIntegrationTest`, `CourseControllerIntegrationTest`, `QuizControllerIntegrationTest` и др.)
- Ленивая загрузка (`IntegrationTest`, `ServiceLayerIntegrationTest`)
- Валидация (`MethodArgumentNotValidException`)
- Обработка ошибок (`GlobalExceptionHandler`)
- Бизнес-логика (запись на курс, проверка дубликатов, оценка заданий)

Запуск:
```bash
mvn test
```

Также в проект добавлена коллекция [Postman](postman_collection.json).

---

## 🚀 CI/CD
Настроен GitHub Actions для автоматического тестирования

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
- 3 курса с привязкой к категориям и преподавателям.

> 🔍 Данные находятся в `src/main/resources/data.sql`.

---

## 🛡️ Валидация и обработка ошибок

- **Валидация**: Используется `@Valid` + `spring-boot-starter-validation`.
- **Обработка ошибок**: Централизованный `GlobalExceptionHandler` возвращает:
    - `400 Bad Request` — при невалидных данных (например, дублирующий email)
    - `404 Not Found` — при отсутствии сущности
    - `500 Internal Server Error` — для неожиданных ошибок
- **Формат ответа**:
```json
{
  "status": 400,
  "message": "User with email already exists",
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