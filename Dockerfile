# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
RUN mkdir /app
WORKDIR /app
COPY . /app
RUN ./mvnw package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]