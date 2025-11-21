package com.utec.ioteste.temperatura.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class EnergyStringDeserializer extends JsonDeserializer<Double> {

    @Override
    public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String energyStr = p.getValueAsString(); // Obtiene el valor como String (ej. "18 kWh")

        if (energyStr == null || energyStr.isBlank()) {
            return 0.0; // O lanzar una excepción si no se permite nulo/vacío
        }

        String clean = energyStr.trim().toUpperCase();

        // Extrae solo los dígitos y el punto decimal (ej. de "18 kWh" obtiene "18")
        String valorStr = clean.replaceAll("[^0-9.]", "");
        // Extrae solo la unidad (ej. de "18 kWh" obtiene "KWH")
        String unidad = clean.replaceAll("[0-9.]", "").trim();

        if (valorStr.isEmpty()) {
            throw new IOException("Formato de energía inválido: " + energyStr);
        }
        if (unidad.isEmpty()) unidad = "KWH"; // Asume KWH por defecto

        double valor = Double.parseDouble(valorStr);

        // Aplica la conversión de unidades
        return switch (unidad) {
            case "KWH" -> valor;
            case "WH" -> valor / 1000.0;     // Wh → kWh
            case "MWH" -> valor * 1000.0;    // MWh → kWh
            default -> throw new IOException("Unidad de energía desconocida: " + unidad);
        };
    }
}