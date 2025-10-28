package com.utec.ioteste.logica.modelos;

public class Operacion {

    public static class Params {
        private boolean was_on;

        public boolean isWas_on() { return was_on; }
        public void setWas_on(boolean was_on) { this.was_on = was_on; }
    }

    private String id;
    private String src;
    private Params params;

    public Operacion(String id, String src, boolean was_on) {
        this.id = id;
        this.src = src;
        this.params = new Params();
        this.params.setWas_on(was_on);
    }

    public Operacion() {
    }

    public String getId() { return id; }
    public String getSrc() { return src; }
    public Params getParams() { return params; }
    public void setId(String id) { this.id = id; }
    public void setSrc(String src) { this.src = src; }
    public void setParams(Params params) { this.params = params; }
}
