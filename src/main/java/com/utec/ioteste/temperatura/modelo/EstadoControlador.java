package com.utec.ioteste.temperatura.modelo;

public class EstadoControlador {
    private final boolean activo;
    private final long medicionesRecibidas;
    private final long accionesEjecutadas;
    private final long erroresRest;
    
    public EstadoControlador(boolean activo, long mediciones, long acciones, long errores) {
        this.activo = activo;
        this.medicionesRecibidas = mediciones;
        this.accionesEjecutadas = acciones;
        this.erroresRest = errores;
    }
    
    public boolean esActivo() {
        return activo;
    }
    
    public long obtenerMedicionesRecibidas() {
        return medicionesRecibidas;
    }
    
    public long obtenerAccionesEjecutadas() {
        return accionesEjecutadas;
    }
    
    public long obtenerErroresRest() {
        return erroresRest;
    }
    
    @Override
    public String toString() {
        return String.format(
            "EstadoControlador{activo=%s, mediciones=%d, acciones=%d, errores=%d}",
            activo, medicionesRecibidas, accionesEjecutadas, erroresRest
        );
    }
}
