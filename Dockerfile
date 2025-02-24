FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/dist/todo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
