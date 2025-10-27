package com.utec.ioteste.modelos;

import java.util.List;

public class Habitacion {

    public String id;
    public boolean prendida;
    public int consumo;
    public int tempDeseada;

    public Habitacion(){}

    public Habitacion(String id, boolean prendida, int consumo, int tempDeseada) {
        this.id = id;
        this.prendida = prendida;
        this.consumo = consumo;
        this.tempDeseada = tempDeseada;
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

}
