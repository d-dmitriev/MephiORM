FROM maven:3.9.11-eclipse-temurin-25-alpine AS builder
WORKDIR /build
ARG BUILD_OPTIONS=""
COPY . .
RUN mvn clean package -DskipTests ${BUILD_OPTIONS:+$BUILD_OPTIONS}

FROM eclipse-temurin:25-jre-alpine
WORKDIR /opt/app
COPY --from=builder /build/target/learning-platform-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]