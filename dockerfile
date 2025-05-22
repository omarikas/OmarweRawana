# Use official OpenJDK 23 image
FROM eclipse-temurin:23-jdk

# Set working directory
WORKDIR /app

# Copy Maven build output
COPY target/demo-0.0.1-SNAPSHOT.jar user-app.jar

# Expose port
EXPOSE 8081

# Run application
ENTRYPOINT ["java", "-jar", "user-app.jar"]
