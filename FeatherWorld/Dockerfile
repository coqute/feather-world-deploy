FROM gradle:8.6-jdk21-alpine AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test --no-daemon

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
VOLUME ["/app/uploads"]
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]