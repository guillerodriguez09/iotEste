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

            //   Configuración por variables
            String rutaConfig = System.getenv("CONFIG_FILE");
            if (rutaConfig == null) rutaConfig = "/config/sitio.json";

            String servidorMqtt = System.getenv("MQTT_BROKER");
            if (servidorMqtt == null) servidorMqtt = "tcp://mosquitto:1883";

            String urlApi = System.getenv("SITE_API");
            if (urlApi == null) urlApi = "http://simulator-iot:8080";

            System.out.println("Configuración:");
            System.out.println("  - Archivo config: " + rutaConfig);
            System.out.println("  - Servidor MQTT: " + servidorMqtt);
            System.out.println("  - API Site-config: " + urlApi);

            //   Cargar configuración inicial
            CargadorConfiguracion cargador = new CargadorConfiguracion();
            ConfiguracionSitio config = cargador.cargarDesdeArchivo(rutaConfig);

            System.out.println("\n✓ Configuración LOCAL cargada: " + config.obtenerNombreSitio());
            System.out.println("  - Energía máxima: " + config.obtenerEnergiaMaximaKWh() + " kWh");
            System.out.println("  - Habitaciones: " + config.obtenerHabitaciones().size());

            //   Intentar obtener /site-config
            ClienteRest clienteRest = new ClienteRest();
            ConfiguracionSitio configRemota = clienteRest.obtenerSiteConfig(urlApi);

            if (configRemota != null) {
                System.out.println("\n✓ Configuración REMOTA obtenida desde /site-config");
                System.out.println("  - Sitio: " + configRemota.getSite());
                System.out.println("  - Máxima energía: " + configRemota.getMaxEnergy());
                System.out.println("  - Habitaciones: " + configRemota.getRooms().size());
                config = configRemota;  // Reemplazar config local
            } else {
                System.err.println("\n⚠ No se pudo obtener configuración remota. Se usa la del archivo.");
            }

            //   Crear controlador
            ControladorTemperaturaImpl controlador =
                    new ControladorTemperaturaImpl(config, null, clienteRest);

            //   Crear manejador MQTT
            ManejadorMQTT manejadorMqtt = new ManejadorMQTT(servidorMqtt, controlador);
            controlador.setManejadorMqtt(manejadorMqtt);

            //   Iniciar sistema
            System.out.println("\n[INICIANDO] Sistema de control de temperatura...");
            controlador.iniciar();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n[DETENIENDO] Cerrando aplicación...");
                controlador.detener();
                System.out.println("✓ Sistema detenido");
            }));

            System.out.println("✓ Sistema iniciado correctamente\n");
            System.out.println("=".repeat(55));
            System.out.println("Sistema activo. Presione Ctrl+C para detener");
            System.out.println("=".repeat(55) + "\n");

            //   Loop principal
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
