package com.utec.ioteste.temperatura.rest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClienteRest {
    private final ObjectMapper mapeador = new ObjectMapper();
    private final int tiempoEsperaMs = 5000;

    public boolean enviarComando(String urlSwitch, boolean encendido) {
        try {
            URL url;
            String payload;

            if (urlSwitch.contains("simulator")) {
                url = new URL(urlSwitch);
                payload = String.format("{\"state\": %s}", encendido);
            } else {
                // Para los Shelly reales
                url = new URL(urlSwitch);
                payload = crearPayloadSwitch(encendido);
            }


            //borrar
            System.out.println("[DEBUG] Enviando a URL: " + url + " con payload: " + payload);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("POST");
            conexion.setRequestProperty("Content-Type", "application/json");
            conexion.setConnectTimeout(tiempoEsperaMs);
            conexion.setReadTimeout(tiempoEsperaMs);
            conexion.setDoOutput(true);

            try (OutputStream salida = conexion.getOutputStream()) {
                salida.write(payload.getBytes());
                salida.flush();
            }

            int codigoRespuesta = conexion.getResponseCode();
            boolean exitoso = codigoRespuesta >= 200 && codigoRespuesta < 300;

            if (exitoso) {
                System.out.println("[REST] Comando enviado a " + urlSwitch + " - Encendido: " + encendido);
            } else {
                System.err.println("[REST] Error en respuesta. Código: " + codigoRespuesta);
            }

            conexion.disconnect();
            return exitoso;

        } catch (Exception e) {
            System.err.println("[REST] Error enviando comando a " + urlSwitch + ": " + e.getMessage());
            return false;
        }
    }


    public boolean obtenerEstado(String urlSwitch) {
        try {
            URL url = new URL(urlSwitch);
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(tiempoEsperaMs);
            conexion.setReadTimeout(tiempoEsperaMs);

            int codigoRespuesta = conexion.getResponseCode();
            if (codigoRespuesta == 200) {
                String respuesta = new String(conexion.getInputStream().readAllBytes());
                var json = mapeador.readTree(respuesta);

                boolean estado;
                if (json.has("state")) {
                    estado = json.get("state").asBoolean(false);
                } else if (json.has("ison")) {
                    estado = json.get("ison").asBoolean(false);
                } else if (json.has("output")) {
                    estado = json.get("output").asBoolean(false);
                } else {
                    estado = false;
                }
                conexion.disconnect();
                return estado;
            }

            conexion.disconnect();
            return false;
        } catch (Exception e) {
            System.err.println("[REST] Error obteniendo estado de " + urlSwitch + ": " + e.getMessage());
            return false;
        }
    }


    private String crearPayloadSwitch(boolean encendido) throws Exception {
        String json = String.format(
            "{\"id\":1,\"method\":\"Switch.Set\",\"params\":{\"id\":0,\"on\":%s}}",
            encendido
        );
        return json;
    }
}
