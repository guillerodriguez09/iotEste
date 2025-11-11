package com.utec.ioteste.temperatura.modelo;
import java.util.ArrayList;
import java.util.List;

public class ConfiguracionSitio {
    private final String nombreSitio;
    private final int energiaMaximakWh; // en Wh
    private final int periodoRefreshMs;
    private final List<Habitacion> habitaciones;

    public ConfiguracionSitio(String nombreSitio, int energiaMaximakWh,
                              int periodoRefreshMs, List<Habitacion> habitaciones) {
        this.nombreSitio = nombreSitio;
        this.energiaMaximakWh = energiaMaximakWh;
        this.periodoRefreshMs = periodoRefreshMs;
        this.habitaciones = new ArrayList<>(habitaciones);
    }

    public String obtenerNombreSitio() { return nombreSitio; }
    public int obtenerEnergiaMaximaWh() { return energiaMaximakWh; }
    public int obtenerPeriodoRefreshMs() { return periodoRefreshMs; }
    public List<Habitacion> obtenerHabitaciones() { return new ArrayList<>(habitaciones); }

    public void agregarHabitacion(Habitacion habitacion) {
        this.habitaciones.add(habitacion);
    }
}
