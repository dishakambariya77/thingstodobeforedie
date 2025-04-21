# Stage 1: Build the Spring Boot app with Maven
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the entire source code into the container
COPY . .

# Build the JAR file using Maven (skip tests to speed up the process)
RUN mvn clean package -DskipTests

# Stage 2: Run the application with OpenJDK base image
FROM azul/zulu-openjdk:21

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/thingstodobeforedie-0.0.1-SNAPSHOT.jar thingstodobeforedie.jar

# Expose port 8080 (or your custom port)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "thingstodobeforedie.jar"]
