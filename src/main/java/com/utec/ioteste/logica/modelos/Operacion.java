package com.utec.ioteste.logica.modelos;

public class Operacion {

    private String id;
    private String src;
    private boolean encendido;

    public Operacion(String id, String src, boolean encendido) {
        this.id = id;
        this.src = src;
        this.encendido = encendido;
    }

    public Operacion() {
    }

    public String getId() { return id; }
    public String getSrc() { return src; }
    public boolean getEncendido() { return encendido; }
    public void setId(String id) { this.id = id; }
    public void setSrc(String src) { this.src = src; }
    public void setEncendido(boolean encendido) { this.encendido = encendido; }
}
