package com.utec.ioteste.temperatura.modelo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConfiguracionSitio {

    private final String nombreSitio;
    private final double energiaMaximaKWh;
    private final int periodoRefreshMs;
    private final String tipoContrato;
    private final List<Habitacion> habitaciones;

    public ConfiguracionSitio(String nombreSitio,
                              double energiaMaximaKWh,
                              int periodoRefreshMs,
                              String tipoContrato,
                              List<Habitacion> habitaciones) {

        this.nombreSitio = nombreSitio;
        this.energiaMaximaKWh = energiaMaximaKWh;
        this.periodoRefreshMs = periodoRefreshMs;
        this.tipoContrato = tipoContrato;
        this.habitaciones = new ArrayList<>(habitaciones);
    }

    public String obtenerNombreSitio() { return nombreSitio; }
    public double obtenerEnergiaMaximaKWh() { return energiaMaximaKWh; }
    public int obtenerPeriodoRefreshMs() { return periodoRefreshMs; }
    public String obtenerTipoContrato() { return tipoContrato; }

    public List<Habitacion> obtenerHabitaciones() {
        return new ArrayList<>(habitaciones);
    }

    public void agregarHabitacion(Habitacion habitacion) {
        habitaciones.add(habitacion);
    }

    public String getMaxEnergy() {
        return String.format("%.2f", energiaMaximaKWh);
    }


    public String getSite() {
        return nombreSitio;
    }


    public List<Habitacion> getRooms() {
        return habitaciones;
    }
}
