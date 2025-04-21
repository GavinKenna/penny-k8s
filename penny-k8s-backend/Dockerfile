FROM openjdk:17-jdk-slim AS build

# Install Maven
RUN apt-get update && apt-get install -y maven

# Set the working directory in the container
WORKDIR /build

# Copy the entire project including the parent and sibling modules
COPY ../ .

# Move into backend directory
WORKDIR /build/penny-k8s-backend

# Build the backend
RUN mvn clean package -DskipTests

# Stage 2: Minimal image to run
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /build/penny-k8s-backend/target/*-boot.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
