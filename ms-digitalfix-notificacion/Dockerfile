FROM eclipse-temurin:17-jdk-alpine AS construccion
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=construccion /app/target/ms-digitalfix-notificacion-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
