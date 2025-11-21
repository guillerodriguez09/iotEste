package com.utec.ioteste.temperatura.modelo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Habitacion {
    private final String nombre;
    private final double temperaturaEsperada;
    private final double consumokWh;
    private final String urlSwitch;
    private final String sensor;

    @JsonCreator
    public Habitacion(
            @JsonProperty("name") String nombre,
            @JsonProperty("expectedTemp") double temperaturaEsperada,
            @JsonProperty("energy") double consumokWh,
            @JsonProperty("switch") String urlSwitch,
            @JsonProperty("sensor") String sensor
    ) {
        this.nombre = nombre;
        this.temperaturaEsperada = temperaturaEsperada;
        this.consumokWh = consumokWh;
        this.urlSwitch = urlSwitch;
        this.sensor = sensor;
    }

    public String obtenerNombre() { return nombre; }
    public double obtenerTemperaturaEsperada() { return temperaturaEsperada; }
    public double obtenerConsumoKWh() { return consumokWh; }
    public String obtenerUrlSwitch() { return urlSwitch; }
    public String obtenerSensor() { return sensor; }

}
