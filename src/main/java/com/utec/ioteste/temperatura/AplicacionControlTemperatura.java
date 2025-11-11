package com.utec.ioteste.temperatura;

import com.utec.ioteste.temperatura.api.impl.ControladorTemperaturaImpl;
import com.utec.ioteste.temperatura.config.CargadorConfiguracion;
import com.utec.ioteste.temperatura.modelo.ConfiguracionSitio;
import com.utec.ioteste.temperatura.mqtt.ManejadorMQTT;
import com.utec.ioteste.temperatura.rest.ClienteRest;

public class AplicacionControlTemperatura {

    public static void main(String[] args) {
        try {
            System.out.println("╔═══════════════════════════════════════════════════════╗");
            System.out.println("║    Control de Temperatura - IoT Domótica IoTEste      ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝\n");

            // Configuración
            String rutaConfig = System.getenv("CONFIG_FILE");
            if (rutaConfig == null) {
                rutaConfig = "/config/sitio.json";
            }
            String servidorMqtt = System.getenv("MQTT_BROKER");
            if (servidorMqtt == null) {
                servidorMqtt = "tcp://mosquitto:1883";
            }

            System.out.println("Configuración:");
            System.out.println("  - Archivo config: " + rutaConfig);
            System.out.println("  - Servidor MQTT: " + servidorMqtt);

            // Cargar configuración
            CargadorConfiguracion cargador = new CargadorConfiguracion();
            ConfiguracionSitio config = cargador.cargarDesdeArchivo(rutaConfig);
            System.out.println("\n✓ Configuración cargada: " + config.obtenerNombreSitio());
            System.out.println("  - Energía máxima: " + config.obtenerEnergiaMaximaWh() + " kWh");
            System.out.println("  - Habitaciones: " + config.obtenerHabitaciones().size());

            // Crear cliente REST
            ClienteRest clienteRest = new ClienteRest();

            // Crear controlador primero (null temporal para MQTT)
            ControladorTemperaturaImpl controlador = new ControladorTemperaturaImpl(config, null, clienteRest);

            // Crear manejador MQTT pasando el controlador ya creado
            ManejadorMQTT manejadorMqtt = new ManejadorMQTT(servidorMqtt, controlador);
            controlador.setManejadorMqtt(manejadorMqtt);

            // Iniciar sistema
            System.out.println("\n[INICIANDO] Sistema de control de temperatura...");
            controlador.iniciar();

            // Hook para detener graceful
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n[DETENIENDO] Cerrando aplicación...");
                controlador.detener();
                System.out.println("✓ Sistema detenido");
            }));

            System.out.println("✓ Sistema iniciado correctamente\n");
            System.out.println("=".repeat(55));
            System.out.println("Sistema activo. Presione Ctrl+C para detener");
            System.out.println("=".repeat(55) + "\n");

            // Mantener aplicación activa
            while (true) {
                Thread.sleep(60000);
                var estado = controlador.obtenerEstado();
                System.out.printf("[ESTADO] Mediciones: %d | Acciones: %d | Errores: %d%n",
                        estado.medicionesRecibidas(),
                        estado.accionesEjecutadas(),
                        estado.erroresRest());
            }

        } catch (Exception e) {
            System.err.println("\n✗ Error fatal:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
