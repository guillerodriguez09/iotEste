package com.utec.ioteste.logica.modelos;

import java.time.LocalDateTime;

public class DataSensor {

    private String src;
    private String id;
    private double ts;
    private float tC;
    private String connected;

    public void setConnected(String connected) {this.connected = connected;}
    public void setSrc(String src) {this.src = src;}
    public void setId(String id) {this.id = id;}
    public void setTs(double ts) {this.ts = ts;}
    public void setTC(float tC) {this.tC = tC;}
    public String getConnected() {return connected;}
    public String getSrc() {return src;}
    public String getId() {return id;}
    public double getTs() {return ts;}
    public float getTC() {return tC;}

    //convierte ts a ms
    public long getTsMillis() {
        return (long) (ts * 1000);
    }
}
