package com.utec.ioteste.logica.modelos;

import java.time.LocalDateTime;

public class DataSensor {

    String id;
    LocalDateTime timestamp;
    float temperatura;

    public DataSensor(String id, LocalDateTime timestamp, float temperatura) {
        this.id = id;
        this.timestamp = timestamp;
        this.temperatura = temperatura;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(float temperatura) {
        this.temperatura = temperatura;
    }
}
