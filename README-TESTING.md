# 🧪 Guía de Pruebas con Diferentes Configuraciones

Este documento explica cómo probar el controlador de temperatura con las diferentes configuraciones disponibles en el simulador.

## 📋 Configuraciones Disponibles

El simulador tiene las siguientes configuraciones en `simulador/blackBox/config/`:

- `config_default.json` - Configuración por defecto
- `config_1.json` - 2 habitaciones, 60 kWh máximo
- `config_2.json` - 5 habitaciones, 100 kWh máximo
- `config_3.json` - Varias habitaciones con diferentes configuraciones
- `config_4.json` - Otra variante de configuración
- `config_5.json` - Otra variante de configuración
- `config_6.json` - Otra variante de configuración
- `config_7.json` - Otra variante de configuración

## 🚀 Uso de los Scripts de Prueba

### Probar una Configuración Específica

```bash
./test-configs.sh [número|nombre]
```

**Ejemplos:**
```bash
# Probar con config_1.json
./test-configs.sh 1

# Probar con config_default.json
./test-configs.sh default

# Listar todas las configuraciones disponibles
./test-configs.sh --list
```

### Probar Todas las Configuraciones

```bash
./test-all-configs.sh
```

Este script probará automáticamente todas las configuraciones y generará un resumen al final.

## 📊 Qué Verifica el Script

El script `test-configs.sh` realiza las siguientes verificaciones:

1. ✅ Copia la configuración seleccionada al archivo `config/configuracion.json`
2. ✅ Reinicia el simulador y el controlador
3. ✅ Verifica que el simulador responde en `http://localhost:8080/site-config`
4. ✅ Verifica que el controlador está recibiendo mediciones MQTT
5. ✅ Muestra un resumen de la configuración (sitio, energía máxima, habitaciones)

## 🔍 Ver Logs en Tiempo Real

Después de ejecutar una prueba, puedes ver los logs en tiempo real:

```bash
# Logs del controlador
docker logs -f controlador-temp

# Logs del simulador
docker logs -f simulator-iot

# Logs de MQTT (opcional)
docker exec mosquitto-iot mosquitto_sub -t "sim/ht/#"
```

## 📝 Ejemplo de Salida

```
[INFO] Resumen de la configuración:

  Sitio: oficinaA
  Energía máxima: 60 kWh
  Habitaciones: 2
  Escenario: 1

  Habitaciones:
    - office1: temp esperada 22°C, energía 80 kWh
    - office2: temp esperada 21°C, energía 80 kWh

[INFO] Copiando configuración: config_1
[INFO] Configuración copiada a config/configuracion.json
[INFO] Reiniciando servicios...
[INFO] Esperando a que el simulador esté listo...
[INFO] Esperando a que el controlador esté listo...
[INFO] Verificando sistema...
[INFO] ✓ Sistema funcionando correctamente - Recibiendo mediciones
[INFO] ✓ Prueba completada exitosamente
```

## ⚠️ Notas

- El script detiene y reinicia los servicios, por lo que puede tomar unos segundos
- Asegúrate de que Docker esté corriendo antes de ejecutar los scripts
- Si una prueba falla, revisa los logs en `/tmp/test_config_[número].log`

## 🛠️ Solución de Problemas

### El simulador no responde
```bash
docker logs simulator-iot
docker restart simulator-iot
```

### El controlador no recibe mediciones
```bash
# Verificar que está suscrito a los temas correctos
docker logs controlador-temp | grep "Suscrito"

# Verificar que MQTT está funcionando
docker exec mosquitto-iot mosquitto_sub -t "sim/ht/#"
```

### Error al copiar configuración
Asegúrate de que el archivo existe:
```bash
ls -la simulador/blackBox/config/config_*.json
```

