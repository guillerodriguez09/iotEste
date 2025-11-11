IoTEste - Control de Temperatura

Proyecto para controlar calefacción eléctrica en distintas habitaciones,
recibiendo datos de sensores (MQTT) y actuando sobre switches vía API REST.

# Controlador de Temperatura IoTEste

Controlador de temperatura para sistemas domóticos, con integración **MQTT**, **REST**, y despliegue en **Docker**. Compatible con simulador `cajaNegra` para pruebas de integración.

---

ejecucuion junto al simulador
mvc clean compile
docker compose -f docker-compose-integracion.yml up --build
## Requisitos

- Java 17+
- Maven 3.8+
- Docker + Docker Compose
- Git

---

## Instalación

1. Clonar simulador:

bash
git clone https://github.com/RamosMariano/cajaNegra.git simulador
Compilar proyecto:

bash
Copiar código
cd controlador-temperatura
mvn clean package
Configuración
Archivo principal: config/sitio.json

Ejecución
Local (requiere MQTT)
bash
Copiar código
mvn compile exec:java -Dexec.mainClass="uy.iiss.iot.temperatura.AplicacionControlTemperatura"
Con Docker Compose
bash
Copiar código
chmod +x scripts/run-docker-compose.sh
./scripts/run-docker-compose.sh
Detener sistema:

bash
Copiar código
docker-compose down
Integración con Simulador cajaNegra
Mosquitto Broker para MQTT

Sensores y switches virtuales

Validación de control, limitación de energía y resiliencia

bash
Copiar código
# Ver logs
docker logs -f controlador_temperatura
docker logs -f simulator_iot

# MQTT
docker exec mosquitto-iot mosquitto_sub -t "sim/ht/#"

# REST
curl http://localhost:8080/house/1
curl -X POST http://localhost:8080/switch/1/on
curl -X POST http://localhost:8080/switch/1/off
Casos de Prueba
Sin limitación de energía: Todas las habitaciones alcanzan temperatura deseada.

Con limitación de energía: Prioriza habitaciones según diferencia de temperatura.

Casos borde: Caída/recuperación MQTT, falla de switch, pérdida de mensajes.

Monitoreo
java
Copiar código
var estado = controlador.obtenerEstado();
System.out.printf("Mediciones: %d | Acciones: %d | Errores: %d%n",
    estado.medicionesRecibidas(),
    estado.accionesEjecutadas(),
    estado.erroresRest());

Logs en Docker:

bash
docker-compose logs -f controlador-temperatura

Limpieza

bash
./scripts/clean-all.sh



