package com.utec.ioteste.temperatura.modelo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConfiguracionSitio {

    private final String nombreSitio;
    private final double energiaMaximaKWh;
    private final int periodoRefreshMs;
    private final String tipoContrato;
    private final List<Habitacion> habitaciones;

    @JsonCreator
    public ConfiguracionSitio(
            @JsonProperty("site") String nombreSitio,
            @JsonProperty("maxEnergy") double energiaMaximaKWh,
            @JsonProperty("refreshPeriod") int periodoRefreshMs,
            @JsonProperty("contractType") String tipoContrato,
            @JsonProperty("rooms") List<Habitacion> habitaciones
    ) {
        this.nombreSitio = nombreSitio;
        this.energiaMaximaKWh = energiaMaximaKWh;
        this.periodoRefreshMs = periodoRefreshMs;
        this.tipoContrato = tipoContrato;
        this.habitaciones = habitaciones;
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
