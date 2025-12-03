# Use JRE instead of JDK for smaller image size
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy jar file to container
COPY target/project-tracker-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render will override this with PORT env variable)
EXPOSE 9090

# Run the jar with optimized JVM settings for faster startup
# Container memory management
# -XX:+UseContainerSupport: Auto-detect container memory limits
# -XX:MaxRAMPercentage=75.0: Use max 75% of available memory
# -XX:InitialRAMPercentage=50.0: Start with 50% of available memory
#
# Compilation optimization
# -XX:+TieredCompilation: Enable tiered compilation (C1 + C2 compilers)
# -XX:TieredStopAtLevel=1: Use C1 only (faster startup, lower peak performance)
# -XX:TieredStopAtLevel=4: Use C1+C2 (slower startup, better peak performance) - for production
#
# Class Data Sharing (CDS) for faster startup
# -Xshare:auto: Use shared class data if available
#
# Garbage Collection
# -XX:+UseG1GC: Use G1 garbage collector (default in Java 21, explicit for clarity)
# -XX:MaxGCPauseMillis=200: Target max GC pause time
ENTRYPOINT ["sh", "-c", "java \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -XX:+TieredCompilation \
    -XX:TieredStopAtLevel=1 \
    -Xshare:auto \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -Dserver.port=${PORT:-9090} \
    -jar app.jar"]
#FROM debian:bookworm-slim
#COPY target/Blog /Blog
#RUN chmod +x /Blog
## Add host.docker.internal DNS resolution for Linux
#ENTRYPOINT ["/Blog"]
