# Lightweight runtime Dockerfile — NO multi-stage build
# REQUIREMENT: Run "mvn clean package -DskipTests" from project root BEFORE "docker-compose build"
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S kyc && adduser -S kyc -G kyc
COPY target/*.jar app.jar
RUN chown kyc:kyc app.jar
USER kyc
EXPOSE 8083
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=5   CMD wget -qO- http://localhost:8083/actuator/health || exit 1
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
