package com.utec.ioteste.temperatura.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.utec.ioteste.temperatura.modelo.Habitacion;
import com.utec.ioteste.temperatura.modelo.ConfiguracionSitio;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CargadorConfiguracion {

    private static final Gson gson = new Gson();

    public static ConfiguracionSitio cargarDesdeArchivo(String rutaArchivo) throws IOException {
        try (FileReader lector = new FileReader(rutaArchivo)) {
            JsonObject json = gson.fromJson(lector, JsonObject.class);
            return analizarConfiguracion(json);
        }
    }

    private static ConfiguracionSitio analizarConfiguracion(JsonObject json) {

        String nombreSitio = json.get("site").getAsString();

        // --- ENERGÍA ---
        // ahora analizarEnergia() retorna SIEMPRE kWh
        String maxEnergyStr = json.get("maxEnergy").getAsString();
        double energiaMaximaKWh = analizarEnergia(maxEnergyStr);

        // --- REFRESH ---
        JsonObject franjaHoraria = json.getAsJsonObject("timeSlot");
        String refreshRaw = franjaHoraria.get("refreshPeriod").getAsString();
        int periodoRefreshMs = Integer.parseInt(refreshRaw.replace(" ms", "").trim());

        // --- HABITACIONES ---
        List<Habitacion> habitaciones = new ArrayList<>();
        JsonArray habitacionesArray = json.getAsJsonArray("rooms");

        for (int i = 0; i < habitacionesArray.size(); i++) {
            JsonObject habitacionJson = habitacionesArray.get(i).getAsJsonObject();
            habitaciones.add(analizarHabitacion(habitacionJson));
        }

        return new ConfiguracionSitio(
                nombreSitio,
                energiaMaximaKWh,
                periodoRefreshMs,
                franjaHoraria.get("contractType").getAsString(),
                habitaciones
        );
    }

    private static Habitacion analizarHabitacion(JsonObject json) {

        String nombre = json.get("name").getAsString();
        double tempEsperada = json.get("expectedTemp").getAsDouble();

        // energía SIEMPRE en kWh
        String energyStr = json.get("energy").getAsString();
        double consumoKWh = analizarEnergia(energyStr);

        String urlSwitch = json.get("switch").getAsString();
        String sensor = json.get("sensor").getAsString();

        if (urlSwitch.contains("localhost")) {
            String corregida = urlSwitch.replace("localhost", "simulator");
            System.out.println("[INFO] Corrigiendo URL de switch: " + urlSwitch + " -> " + corregida);
            urlSwitch = corregida;
        }

        return new Habitacion(nombre, tempEsperada, consumoKWh, urlSwitch, sensor);
    }


    private static double analizarEnergia(String energyStr) {

        if (energyStr == null || energyStr.isBlank()) {
            throw new IllegalArgumentException("Cadena de energía vacía o nula");
        }

        String clean = energyStr.trim().toUpperCase();

        String valorStr = clean.replaceAll("[^0-9.]", "");
        String unidad   = clean.replaceAll("[0-9.]", "").trim();

        if (valorStr.isEmpty()) throw new IllegalArgumentException("Formato inválido: " + energyStr);
        if (unidad.isEmpty()) unidad = "KWH";  // default

        double valor = Double.parseDouble(valorStr);

        return switch (unidad) {
            case "KWH" -> valor;
            case "WH"  -> valor / 1000.0;      // Wh → kWh
            case "MWH" -> valor * 1000.0;      // MWh → kWh
            default -> throw new IllegalArgumentException("Unidad desconocida: " + unidad);
        };
    }

}
