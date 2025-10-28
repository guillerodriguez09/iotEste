package com.utec.ioteste.logica.modelos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataSensor {

    public static class Params {
        private double ts;
        @JsonProperty("temperature:0")
        private Temperature temperature;

        public void setTs(double ts) {this.ts = ts;}
        public double getTs() {return ts;}
        public Temperature getTemperature() { return temperature; }
        public void setTemperature(Temperature temperature) { this.temperature = temperature; }
        public long getTsMillis() {
            return (long) (ts * 1000);
        }
    }

    public static class Temperature {
        @JsonProperty("tC")
        private float tC;

        public float getTC() { return tC; }
        public void setTC(float tC) { this.tC = tC; }
    }

    private String src;
    private Params params;

    public void setSrc(String src) {this.src = src;}
    public String getSrc() {return src;}
    public Params getParams() { return params; }
    public void setParams(DataSensor.Params params) { this.params = params; }

}
