package com.utec.ioteste.logica.modelos;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Habitacion {


    private String name;
    private String energy;
    private int expectedTemp;
    @JsonProperty("switch")
    private String switchUrl;
    private String sensor;

    public Habitacion() {}

    public Habitacion(String name, String energy, int expectedTemp, String switchUrl, String sensor) {
        this.name = name;
        this.energy = energy;
        this.expectedTemp = expectedTemp;
        this.switchUrl = switchUrl;
        this.sensor = sensor;
    }

    public void setName(String name) {this.name = name;}
    public void setEnergy(String energy) {this.energy = energy;}
    public void setExpectedTemp(int expectedTemp) {this.expectedTemp = expectedTemp;}
    public void setSwitchUrl(String switchUrl) {this.switchUrl = switchUrl;}
    public void setSensor(String sensor) {this.sensor = sensor;}
    public String getName() {return name;}
    public String getEnergy() {return energy;}
    public int getExpectedTemp() {return expectedTemp;}
    public String getSwitchUrl() {return switchUrl;}
    public String getSensor() {return sensor;}

    public double getEnergyValue() {
        return Double.parseDouble(energy.replaceAll("[^\\d.]", ""));
    }
}
