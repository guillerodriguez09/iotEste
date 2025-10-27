package com.utec.ioteste.logica.modelos;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Habitacion {


    private String name;
    private boolean prendida;
    private String energy;
    private int expectedTemp;
    @JsonProperty("switch")
    private String switchUrl;
    private String sensor;
    private int tempActual;
    private int ultimaActualizacion;

    public void setName(String name) {this.name = name;}
    public boolean getPrendida() {return prendida;}
    public void setPrendida(boolean prendida) {this.prendida = prendida;}
    public void setEnergy(String energy) {this.energy = energy;}
    public void setExpectedTemp(int expectedTemp) {this.expectedTemp = expectedTemp;}
    public void setSwitchUrl(String switchUrl) {this.switchUrl = switchUrl;}
    public void setSensor(String sensor) {this.sensor = sensor;}
    public void setTempActual(int tempActual) {this.tempActual = tempActual;}
    public void setUltimaActualizacion(int ultimaActualizacion) {this.ultimaActualizacion = ultimaActualizacion;}
    public String getName() {return name;}
    public String getEnergy() {return energy;}
    public int getExpectedTemp() {return expectedTemp;}
    public String getSwitchUrl() {return switchUrl;}
    public String getSensor() {return sensor;}
    public int getTempActual() {return tempActual;}
    public int getUltimaActualizacion() {return ultimaActualizacion;}

    public double getEnergyValue() {
        return Double.parseDouble(energy.replaceAll("[^\\d.]", ""));
    }
}
