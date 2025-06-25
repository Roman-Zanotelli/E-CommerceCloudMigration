# Use Maven to build the Spring Boot app
FROM amazoncorretto:17-alpine AS build-env
WORKDIR /app

# Copy source code and pom.xml
COPY . /app
RUN apk update && apk add --no-cache maven git bash

# Build the app and skip tests for faster builds
RUN mvn clean package -DskipTests

# Final image using a lightweight JDK
FROM amazoncorretto:17-alpine
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build-env /app/target/*.jar app.jar

# Expose the port Spring Boot listens on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar", "--server.address=0.0.0.0"]