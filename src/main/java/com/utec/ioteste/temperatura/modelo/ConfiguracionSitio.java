package com.utec.ioteste.temperatura.modelo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.utec.ioteste.temperatura.config.EnergyStringDeserializer;
import com.utec.ioteste.temperatura.modelo.TimeSlotConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConfiguracionSitio {

    private final String nombreSitio;
    private final double energiaMaximaKWh;
    private final TimeSlotConfig timeSlot;
    private final List<Habitacion> habitaciones;

    @JsonCreator
    public ConfiguracionSitio(
            @JsonProperty("site") String nombreSitio,
            @JsonDeserialize(using = EnergyStringDeserializer.class)
            @JsonProperty("maxEnergy") double energiaMaximaKWh,
            @JsonProperty("timeSlot") TimeSlotConfig timeSlot,
            @JsonProperty("rooms") List<Habitacion> habitaciones
    ) {
        this.nombreSitio = nombreSitio;
        this.energiaMaximaKWh = energiaMaximaKWh;
        this.habitaciones = habitaciones;
        this.timeSlot = timeSlot;
    }

    public String obtenerNombreSitio() { return nombreSitio; }
    public double obtenerEnergiaMaximaKWh() { return energiaMaximaKWh; }

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

    public int obtenerPeriodoRefreshMs() {
        return timeSlot.obtenerPeriodoRefreshMs();
    }
    public String obtenerTipoContrato() {
        return timeSlot.getContractType();
    }
}
