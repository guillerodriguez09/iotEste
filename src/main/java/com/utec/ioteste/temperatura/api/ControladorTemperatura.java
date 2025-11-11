package com.utec.ioteste.temperatura.api;

import com.utec.ioteste.temperatura.modelo.AccionTemperatura;
import java.util.List;

public interface ControladorTemperatura {

    void procesarMedicionTemperatura(String idSensor, double temperatura);

    AccionTemperatura obtenerDecision(String idHabitacion);

    void iniciar();

    void detener();

    List<AccionTemperatura> obtenerAccionesPendientes();

    EstadoControlador obtenerEstado();

    record EstadoControlador(
        boolean activo,
        long medicionesRecibidas,
        long accionesEjecutadas,
        long erroresRest
    ) {}
}
