FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/app
COPY target/learning-platform-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED", "--enable-native-access=ALL-UNNAMED", "-Dio.netty.noUnsafe=true", "-jar", "app.jar"]