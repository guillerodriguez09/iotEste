package com.utec.ioteste.logica.modelos;

public class DataSensor {

    private String src;
    private float temperatura;
    private double timestamp;
    private boolean connected;

    public DataSensor() {}

    public DataSensor(String src, float temperatura, double timestamp, boolean connected) {
        this.src = src;
        this.temperatura = temperatura;
        this.timestamp = timestamp;
        this.connected = connected;
    }

    public void setSrc(String src) {this.src = src;}
    public String getSrc() {return src;}
    public float getTemperatura() {return temperatura;}
    public double getTimestamp() {return timestamp;}
    public void setConnected(boolean connected) {this.connected = connected;}
    public boolean getConnected() {return connected;}
    public void setTimestamp(double timestamp) {this.timestamp = timestamp;}
    public void setTemperatura(float temperatura) {this.temperatura = temperatura;}
}
