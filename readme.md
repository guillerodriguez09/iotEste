# IoTEste - Control de Temperatura

Proyecto para controlar calefacción eléctrica en distintas habitaciones,
recibiendo datos de sensores (**MQTT**) y actuando sobre switches vía **API REST**.

Controlador compatible con simulador `cajaNegra` para pruebas de integración.

---

## 🚀 Ejecución junto al Simulador

### 1. Clonar el simulador

```bash
git clone https://github.com/RamosMariano/cajaNegra.git simulador
```

---

### 2. Compilar el proyecto

Desde la carpeta principal del controlador:

```bash
mvn clean compile
```

---

### 3. Levantar el sistema completo con Docker Compose

```bash
docker compose -f docker-compose-integracion.yml up --build
```

Esto inicia:
- **Mosquitto** (broker MQTT)
- **Simulador IoT (`simulator-iot`)**
- **Controlador de temperatura (`controlador-temp`)**

---

### 4. Ver logs en tiempo real

```bash
docker logs -f controlador-temp
docker logs -f simulator-iot
```

---

## 🧠 Requisitos

- Java **17+**
- Maven **3.8+**
- Docker + Docker Compose
- Git

---

## ⚙️ Configuración

Archivo principal de configuración:

```
/config/sitio.json
```

Ejemplo:

```json
{
  "site": "oficina_pruebas",
  "maxEnergy": "14kWh",
  "rooms": [
    {
      "id": "hab1",
      "name": "office1",
      "expectedTemp": "22",
      "energy": "2kWh",
      "switch": "http://simulator-iot:8080/switch/1",
      "sensor": "sim/ht/1"
    }
  ]
}
```

---

## 🔍 Comprobación manual

### MQTT

Ver mensajes publicados por los sensores simulados:

```bash
docker exec mosquitto-iot mosquitto_sub -t "sim/ht/#"
```

### REST

Consultar y controlar switches desde el simulador:

```bash
curl http://localhost:8080/switch/1
curl -X POST http://localhost:8080/switch/1 -H "Content-Type: application/json" -d '{"state": true}'
curl -X POST http://localhost:8080/switch/1 -H "Content-Type: application/json" -d '{"state": false}'
```

---

## 🧪 Casos de Prueba

- **Sin limitación de energía** → todas las habitaciones alcanzan la temperatura deseada.
- **Con limitación** → prioriza habitaciones con mayor diferencia respecto al objetivo.
- **Casos borde** → caída MQTT, falla de switch, pérdida de mensajes, reconexión.

---

## 🧹 Limpieza

Para detener y limpiar todo el entorno:

```bash
docker compose down
```

---

## 📜 Créditos

Proyecto **IoTEste - Control de Temperatura**  
Desarrollo y pruebas de integración sobre simulador **cajaNegra**.
