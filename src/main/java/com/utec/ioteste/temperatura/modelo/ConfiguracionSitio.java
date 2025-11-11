package com.utec.ioteste.temperatura.modelo;
import java.util.ArrayList;
import java.util.List;

public class ConfiguracionSitio {
    private final String nombreSitio;
    private final int energiaMaximaWh; // en Wh
    private final int periodoRefreshMs;
    private final List<Habitacion> habitaciones;

    public ConfiguracionSitio(String nombreSitio, int energiaMaximaWh, 
                              int periodoRefreshMs, List<Habitacion> habitaciones) {
        this.nombreSitio = nombreSitio;
        this.energiaMaximaWh = energiaMaximaWh;
        this.periodoRefreshMs = periodoRefreshMs;
        this.habitaciones = new ArrayList<>(habitaciones);
    }

    public String obtenerNombreSitio() { return nombreSitio; }
    public int obtenerEnergiaMaximaWh() { return energiaMaximaWh; }
    public int obtenerPeriodoRefreshMs() { return periodoRefreshMs; }
    public List<Habitacion> obtenerHabitaciones() { return new ArrayList<>(habitaciones); }

    public void agregarHabitacion(Habitacion habitacion) {
        this.habitaciones.add(habitacion);
    }
}
