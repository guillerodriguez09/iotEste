package com.utec.ioteste.modelos;

import java.util.List;

public class Habitacion {

    public String id;
    public boolean prendida;
    public int consumo;
    public int tempDeseada;
    public int tempActual;
    public int ultimaActualizacion;

    public Habitacion(){}

    public Habitacion(String id, boolean prendida, int consumo, int tempDeseada, int tempActual, int ultimaActualizacion) {
        this.id = id;
        this.prendida = prendida;
        this.consumo = consumo;
        this.tempDeseada = tempDeseada;
        this.tempActual = tempActual;
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getPrendida() {
        return prendida;
    }

    public void setPrendida(boolean prendida) {
        this.prendida = prendida;
    }

    public int getConsumo() {
        return consumo;
    }

    public void setConsumo(int consumo) {
        this.consumo = consumo;
    }

    public int getTempDeseada(){
        return tempDeseada;
    }

    public void setTempDeseada(int tempDeseada){
        this.tempDeseada = tempDeseada;
    }

    public int getTempActual(){
        return tempActual;
    }

    public void setTempActual(int tempActual){
        this.tempActual = tempActual;
    }

    public int getUltimaActualizacion(){
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(int ultimaActualizacion){
        this.ultimaActualizacion = ultimaActualizacion;
    }

}
