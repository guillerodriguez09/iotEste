FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/iot-temperature-control-1.0.0-jar-with-dependencies.jar app.jar

ENV MQTT_BROKER=mqtt://mosquitto:1883
ENV CONFIG_FILE=/config/sitio.json
ENV LOG_LEVEL=DEBUG

# Carpeta para logs
RUN mkdir -p /app/logs

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# Ejecutar aplicación
ENTRYPOINT ["java", "-Dlog.level=${LOG_LEVEL}", "-jar", "app.jar"]