FROM maven:3.9.9-eclipse-temurin-24-alpine AS builder
WORKDIR /build
ARG BUILD_OPTIONS
COPY . .
RUN mvn clean package -DskipTests $BUILD_OPTIONS

FROM eclipse-temurin:24-jre-alpine
EXPOSE 8080
WORKDIR /opt/app
ENV SPRING_ACTIVE_PROFILE=test
COPY --from=builder /build/target/learning-platform-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED", "--enable-native-access=ALL-UNNAMED", "-Dio.netty.noUnsafe=true", "-jar", "app.jar"]