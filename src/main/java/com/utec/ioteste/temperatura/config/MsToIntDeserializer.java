package com.utec.ioteste.temperatura.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class MsToIntDeserializer extends JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String refreshStr = p.getValueAsString(); // Obtiene el valor como String (ej. "10000 ms")

        if (refreshStr == null || refreshStr.trim().isEmpty()) {
            return 0;
        }

        try {
            // Elimina " ms" y cualquier espacio, y convierte a entero
            String clean = refreshStr.replace(" ms", "").trim();
            return Integer.parseInt(clean);

        } catch (NumberFormatException e) {
            throw new IOException("No se pudo convertir '" + refreshStr + "' a entero.", e);
        }
    }
}