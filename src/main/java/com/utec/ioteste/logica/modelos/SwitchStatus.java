package com.utec.ioteste.logica.modelos;


public class SwitchStatus {

    private String source;
    private boolean output;
    private float tC;

    public SwitchStatus(String source, boolean output, float tC) {
        this.source = source;
        this.output = output;
        this.tC = tC;
    }

    public SwitchStatus() {};

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public boolean isOutput() { return output; }
    public void setOutput(boolean output) { this.output = output; }

    public float gettC() { return tC; }
    public void settC(float tC) { this.tC = tC; }

    @Override
    public String toString() {
        return "SwitchStatus{" +
                "source='" + source + '\'' +
                ", output=" + output +
                '}';
    }
}

