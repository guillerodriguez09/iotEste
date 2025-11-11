package com.utec.ioteste.temperatura.api;

import com.utec.ioteste.temperatura.modelo.AccionTemperatura;
import java.util.List;

public interface ControladorTemperatura {
    
    /**
     * Procesa una medición de temperatura recibida del sensor MQTT.
     * @param idSensor identificador del sensor
     * @param temperatura valor medido en °C
     */
    void procesarMedicionTemperatura(String idSensor, double temperatura);
    
    /**
     * Obtiene la decisión de control para una habitación.
     * @param idHabitacion identificador de la habitación
     * @return AccionTemperatura con la decisión (encender/apagar y razón)
     */
    AccionTemperatura obtenerDecision(String idHabitacion);
    
    /**
     * Inicia el controlador con la configuración del sitio.
     */
    void iniciar();
    
    /**
     * Detiene el controlador y libera recursos.
     */
    void detener();
    
    /**
     * Obtiene todas las acciones pendientes.
     */
    List<AccionTemperatura> obtenerAccionesPendientes();
    
    /**
     * Obtiene estado actual de monitoreo.
     */
    EstadoControlador obtenerEstado();

    record EstadoControlador(
        boolean activo,
        long medicionesRecibidas,
        long accionesEjecutadas,
        long erroresRest
    ) {}
}
