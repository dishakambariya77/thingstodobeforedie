# Use official OpenJDK base image
FROM azul/zulu-openjdk:21

# Set working directory inside the container
WORKDIR /app

# Copy the built JAR file from target folder to /app inside the container
COPY target/thingstodobeforedie-0.0.1-SNAPSHOT.jar thingstodobeforedie.jar

# Expose port 8080 (or your custom port)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "thingstodobeforedie.jar"]
