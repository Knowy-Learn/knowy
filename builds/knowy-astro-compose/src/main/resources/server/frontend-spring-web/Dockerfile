FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY server/frontend-spring-web/*.jar app.jar
EXPOSE 443
ENTRYPOINT ["java", "-jar", "app.jar"]
