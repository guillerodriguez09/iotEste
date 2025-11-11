package com.utec.ioteste.temperatura.modelo;

public class AccionTemperatura {
    private final String idHabitacion;
    private final boolean encender;
    private final String razon;
    private final long timestamp;

    public AccionTemperatura(String idHabitacion, boolean encender, String razon) {
        this.idHabitacion = idHabitacion;
        this.encender = encender;
        this.razon = razon;
        this.timestamp = System.currentTimeMillis();
    }

    public String obtenerIdHabitacion() { return idHabitacion; }
    public boolean estaEncendido() { return encender; }
    public String obtenerRazon() { return razon; }
    public long obtenerTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("AccionTemperatura{habitacion='%s', encender=%s, razon='%s'}",
                idHabitacion, encender, razon);
    }
}
