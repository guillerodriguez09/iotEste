package com.utec.ioteste.temperatura.modelo;

public class Habitacion {
    private final String nombre;
    private final double temperaturaEsperada;
    private final int consumokWh; // en Wh
    private final String urlSwitch;
    private final String sensor;

    public Habitacion(String nombre, double tempEsperada, int consumokWh,
                      String urlSwitch, String sensor) {
        this.nombre = nombre;
        this.temperaturaEsperada = tempEsperada;
        this.consumokWh = consumokWh;
        this.urlSwitch = urlSwitch;
        this.sensor = sensor;
    }

    public String obtenerNombre() { return nombre; }
    public double obtenerTemperaturaEsperada() { return temperaturaEsperada; }
    public int obtenerConsumoWh() { return consumokWh; }
    public String obtenerUrlSwitch() { return urlSwitch; }
    public String obtenerSensor() { return sensor; }

}
