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
        
        String maxEnergyStr = json.get("maxEnergy").getAsString();
        int energiaMaximaWh = analizarEnergia(maxEnergyStr);
        
        JsonObject franjaHoraria = json.getAsJsonObject("timeSlot");
        int periodoRefreshMs = Integer.parseInt(franjaHoraria.get("refreshPeriod").getAsString().split(" ")[0]) * 1000;
        
        List<Habitacion> habitaciones = new ArrayList<>();
        JsonArray habitacionesArray = json.getAsJsonArray("rooms");
        
        for (int i = 0; i < habitacionesArray.size(); i++) {
            JsonObject habitacionJson = habitacionesArray.get(i).getAsJsonObject();
            habitaciones.add(analizarHabitacion(habitacionJson));
        }
        
        return new ConfiguracionSitio(nombreSitio, energiaMaximaWh, periodoRefreshMs, habitaciones);
    }

    private static Habitacion analizarHabitacion(JsonObject json) {
        String nombre = json.get("name").getAsString();
        double tempEsperada = Double.parseDouble(json.get("expectedTemp").getAsString());

        String energyStr = json.get("energy").getAsString();
        int energiaWh = analizarEnergia(energyStr);

        String urlSwitch = json.get("switch").getAsString();
        String sensor = json.get("sensor").getAsString();

        if (urlSwitch.contains("localhost")) {
            System.out.println("[INFO] Corrigiendo URL de switch: " + urlSwitch + " → " +
                    urlSwitch.replace("localhost", "simulator"));
            urlSwitch = urlSwitch.replace("localhost", "simulator");
        }

        return new Habitacion(nombre, tempEsperada, energiaWh, urlSwitch, sensor);
    }


    private static int analizarEnergia(String energyStr) {
        if (energyStr == null || energyStr.isBlank()) {
            throw new IllegalArgumentException("Cadena de energía vacía o nula");
        }

        energyStr = energyStr.trim().toUpperCase();

        String valorStr = energyStr.replaceAll("[^0-9.]", "");
        String unidad = energyStr.replaceAll("[0-9.]", "").trim();

        if (valorStr.isEmpty()) {
            throw new IllegalArgumentException("Formato de energía inválido: " + energyStr);
        }
        if (unidad.isEmpty()) {
            unidad = "WH";
        }

        double valor = Double.parseDouble(valorStr);
        return (int) (valor * obtenerMultiplicadorUnidad(unidad));
    }




    private static double obtenerMultiplicadorUnidad(String unidad) {
        return switch (unidad) {
            case "WH" -> 1;
            case "KWH" -> 1000;
            case "MWH" -> 1000000;
            default -> throw new IllegalArgumentException("Unidad desconocida: " + unidad);
        };
    }
}
