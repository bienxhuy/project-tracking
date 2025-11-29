# Use a base image with JDK
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy jar file to container
COPY target/Blog-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 9090

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
#FROM debian:bookworm-slim
#COPY target/Blog /Blog
#RUN chmod +x /Blog
## Add host.docker.internal DNS resolution for Linux
#ENTRYPOINT ["/Blog"]
