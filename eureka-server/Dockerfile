FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S kyc && adduser -S kyc -G kyc

COPY target/*.jar app.jar
RUN chown kyc:kyc app.jar

USER kyc

EXPOSE 8761

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=5 \
  CMD wget -qO- http://localhost:8761 || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]