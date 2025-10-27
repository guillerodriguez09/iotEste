IoTEste - Control de Temperatura

Proyecto para controlar calefacción eléctrica en distintas habitaciones,
recibiendo datos de sensores (MQTT) y actuando sobre switches vía API REST.

Estructura:
- com.utec.ioteste.logica.ControllerTemperatura.java: lógica principal del componente. (IController su interfaz)
- modelos/: contiene las clases de datos (Configuracion, Habitacion, etc.)
- tests/: pruebas unitarias JUnit 5 para validar el comportamiento.
