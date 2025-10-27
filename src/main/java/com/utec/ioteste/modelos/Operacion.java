package com.utec.ioteste.modelos;

public class Operacion {

    String idSwitch;
    boolean prendido;

    public Operacion(String idSwitch, boolean prendido) {
        this.idSwitch = idSwitch;
        this.prendido = prendido;
    }

    public String getIdSwitch() {
        return idSwitch;
    }

    public void setIdSwitch(String idSwitch) {
        this.idSwitch = idSwitch;
    }

    public boolean isPrendido() {
        return prendido;
    }

    public void setPrendido(boolean prendido) {
        this.prendido = prendido;
    }
}
