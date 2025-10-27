package com.utec.ioteste.logica.modelos;

public class TimeSlot {
    private String contractType;
    private String refreshPeriod;

    public void setContractType(String contractType) {this.contractType = contractType;}
    public void setRefreshPeriod(String refreshPeriod) {this.refreshPeriod = refreshPeriod;}
    public String getContractType() {return contractType;}
    public String getRefreshPeriod() {return refreshPeriod;}

    public double getrefreshPeriodValue() {
        return Double.parseDouble(refreshPeriod.replaceAll("[^\\d.]", ""));
    }
}


