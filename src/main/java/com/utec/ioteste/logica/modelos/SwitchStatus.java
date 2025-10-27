package com.utec.ioteste.logica.modelos;


public class SwitchStatus {

    private String source;
    private boolean output;

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public boolean isOutput() { return output; }
    public void setOutput(boolean output) { this.output = output; }

    @Override
    public String toString() {
        return "SwitchStatus{" +
                "source='" + source + '\'' +
                ", output=" + output +
                '}';
    }
}

