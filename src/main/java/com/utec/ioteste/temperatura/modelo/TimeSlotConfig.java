package com.utec.ioteste.temperatura.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.utec.ioteste.temperatura.config.MsToIntDeserializer;

public class TimeSlotConfig {

    private final String contractType;
    private final int refreshPeriod; // Aquí recibe "10000 ms" como String

    // Constructor que Jackson usará para crear este objeto
    public TimeSlotConfig(
            @JsonProperty("contractType") String contractType,
            @JsonDeserialize(using = MsToIntDeserializer.class)
            @JsonProperty("refreshPeriod") int refreshPeriod
    ) {
        this.contractType = contractType;
        this.refreshPeriod = refreshPeriod;
    }

    public String getContractType() { return contractType; }
    public int getRefreshPeriod() { return refreshPeriod; }

    // Aquí puedes añadir la lógica de parsing de 'refreshPeriod' si es necesario
    public int obtenerPeriodoRefreshMs() {
        return refreshPeriod;
    }
}