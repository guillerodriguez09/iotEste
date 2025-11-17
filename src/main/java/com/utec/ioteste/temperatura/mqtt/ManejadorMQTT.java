package com.utec.ioteste.temperatura.mqtt;

import com.utec.ioteste.temperatura.api.ControladorTemperatura;
import org.eclipse.paho.client.mqttv3.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utec.ioteste.temperatura.api.impl.ControladorTemperaturaImpl;

public class ManejadorMQTT implements MqttCallback {
    private MqttClient clienteMqtt;
    private final String servidorMqtt;
    private  ControladorTemperatura controlador; // final para evitar null
    private final ObjectMapper mapeador = new ObjectMapper();
    private boolean conectado = false;

    public ManejadorMQTT(String servidorMqtt, ControladorTemperatura controlador) {
        this.servidorMqtt = servidorMqtt;
        this.controlador = controlador;
    }

    public void conectar(String idCliente) throws MqttException {
        clienteMqtt = new MqttClient(servidorMqtt, idCliente + "_" + System.currentTimeMillis());
        MqttConnectOptions opciones = new MqttConnectOptions();
        opciones.setCleanSession(true); //AL ESTAR ESTO EN TRUE SE PIERDEN LAS SUSCRIPCIONES EN CASO DE CAIDA Y RECONEXION, HAY QUE PONERLO EN false
        opciones.setAutomaticReconnect(true);

        clienteMqtt.setCallback(this);
        clienteMqtt.connect(opciones);
        conectado = true;
        System.out.println("[MQTT] Conectado a: " + servidorMqtt);
    }

    public void suscribirse(String tema) throws MqttException {
        if (clienteMqtt != null && clienteMqtt.isConnected()) {
            clienteMqtt.subscribe(tema);
            System.out.println("[MQTT] Suscrito a tema: " + tema);
        }
    }

    public void desconectar() {
        try {
            if (clienteMqtt != null && clienteMqtt.isConnected()) {
                clienteMqtt.disconnect();
                clienteMqtt.close();
                conectado = false;
                System.out.println("[MQTT] Desconectado");
            }
        } catch (MqttException e) {
            System.err.println("[MQTT] Error al desconectar: " + e.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable causa) {
        conectado = false;
        System.err.println("[MQTT] Conexión perdida: " + causa.getMessage());
        System.out.println("[MQTT] Intentando reconectar...");
    }

    @Override
    public void messageArrived(String tema, MqttMessage mensaje) {
        if (controlador == null) {
            System.err.println("[MQTT] Controlador NO inicializado");
            return;
        }

        try {
            String contenido = new String(mensaje.getPayload());
            double temperatura = Double.NaN;

            // Intentar parsear JSON
            JsonNode json = mapeador.readTree(contenido);
            if (json.has("params") && json.get("params").has("temperature:0")) {
                temperatura = json.get("params").get("temperature:0").get("tC").asDouble();
            } else if (json.has("temperature")) {
                temperatura = json.get("temperature").asDouble();
            } else if (json.has("temp")) {
                temperatura = json.get("temp").asDouble();
            } else {
                // Intentar parsear como valor simple
                temperatura = Double.parseDouble(contenido);
            }

            // Validar rango
            if (temperatura >= -50 && temperatura <= 60) {
                controlador.procesarMedicionTemperatura(tema, temperatura);
                System.out.println("[MQTT] Temperatura recibida - Sensor: " + tema + ", Temp: " + temperatura + "°C");
            } else {
                System.err.println("[MQTT] Temperatura fuera de rango: " + temperatura + "°C");
            }

        } catch (Exception e) {
            System.err.println("[MQTT] Error procesando mensaje de " + tema + ": " + e.getMessage());
        }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // No requerido
    }

    public boolean estaConectado() {
        return conectado && clienteMqtt != null && clienteMqtt.isConnected();
    }

    public void setControlador(ControladorTemperaturaImpl controlador) {
        this.controlador = controlador;
    }
}
