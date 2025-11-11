package com.utec.ioteste.temperatura.health;

import com.utec.ioteste.temperatura.api.ControladorTemperatura;

public class VerificadorSalud {
    private final ControladorTemperatura controlador;
    private final long umbralMedicionesMs;

    public VerificadorSalud(ControladorTemperatura controlador, long umbralMedicionesMs) {
        this.controlador = controlador;
        this.umbralMedicionesMs = umbralMedicionesMs;
    }

    public boolean estaEnSalud() {
        ControladorTemperatura.EstadoControlador estado = controlador.obtenerEstado();
        
        if (!estado.activo()) {
            return false;
        }
        
        // Permitir algunos errores pero no demasiados (máximo 10% de fallos)
        long totalOperaciones = estado.accionesEjecutadas() + estado.erroresRest();
        if (totalOperaciones > 0) {
            double tasaError = (double) estado.erroresRest() / totalOperaciones;
            if (tasaError > 0.1) {
                return false;
            }
        }
        
        return true;
    }

    public String obtenerReporteEstado() {
        ControladorTemperatura.EstadoControlador estado = controlador.obtenerEstado();
        
        return String.format(
            "[SALUD] Activo: %s | Mediciones: %d | Acciones: %d | Errores: %d",
            estado.activo() ? "SÍ" : "NO",
            estado.medicionesRecibidas(),
            estado.accionesEjecutadas(),
            estado.erroresRest()
        );
    }
}
