package com.utec.ioteste.temperatura.api.impl;

import com.utec.energy.EnergyCost;
import com.utec.ioteste.temperatura.api.ControladorTemperatura;
import com.utec.ioteste.temperatura.modelo.*;
import com.utec.ioteste.temperatura.rest.ClienteRest;
import com.utec.ioteste.temperatura.mqtt.ManejadorMQTT;

import java.util.*;
import java.util.concurrent.*;

public class ControladorTemperaturaImpl implements ControladorTemperatura {

    private ConfiguracionSitio configuracion;

    private final Map<String, Double> temperaturasActuales;

    private final Map<String, AccionTemperatura> accionesActuales;

    private ManejadorMQTT manejadorMqtt;
    private ClienteRest clienteRest;

    private ScheduledExecutorService ejecutor;

    private long medicionesRecibidas = 0;
    private long accionesEjecutadas = 0;
    private long erroresRest = 0;

    private boolean activo = false;

    private static final double HISTERESIS = 1.0;

    private static final long PERIODO_CONTROL_MS = 5000;

    public ControladorTemperaturaImpl(ConfiguracionSitio config, ManejadorMQTT mqtt, ClienteRest rest) {
        this.configuracion = config;
        this.manejadorMqtt = mqtt;
        this.clienteRest = rest;

        this.temperaturasActuales = new ConcurrentHashMap<>();
        this.accionesActuales = new ConcurrentHashMap<>();

        this.ejecutor = Executors.newScheduledThreadPool(2);
    }

    @Override
    public void iniciar() {
        try {
            System.out.println("[CONTROLADOR] Iniciando control de temperatura...");

            manejadorMqtt.conectar("controlador-temperatura");

            for (Habitacion hab : configuracion.obtenerHabitaciones()) {
                String sensor = hab.obtenerSensor();
                if (sensor != null && !sensor.isEmpty()) {
                    manejadorMqtt.suscribirse(sensor);
                    System.out.println("[MQTT] Suscrito a: " + sensor);
                }
            }

            ejecutor.scheduleAtFixedRate(
                    this::ejecutarControlTemperatura,
                    0,
                    PERIODO_CONTROL_MS,
                    TimeUnit.MILLISECONDS
            );

            activo = true;
            System.out.println("[CONTROLADOR] Sistema activo");

        } catch (Exception e) {
            System.err.println("[CONTROLADOR] Error iniciando: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void detener() {
        try {
            activo = false;
            if (manejadorMqtt != null) manejadorMqtt.desconectar();
            if (ejecutor != null) {
                ejecutor.shutdown();
                ejecutor.awaitTermination(5, TimeUnit.SECONDS);
            }
            System.out.println("[CONTROLADOR] Sistema detenido");
        } catch (Exception e) {
            System.err.println("[CONTROLADOR] Error deteniendo: " + e.getMessage());
        }
    }

    @Override
    public void procesarMedicionTemperatura(String idSensor, double temperatura) {
        for (Habitacion hab : configuracion.obtenerHabitaciones()) {
            if (hab.obtenerSensor().equals(idSensor)) {
                temperaturasActuales.put(hab.obtenerNombre(), temperatura);
                medicionesRecibidas++;
                System.out.println("[MQTT] Temperatura " + hab.obtenerNombre() + ": " + temperatura + "°C");
                break;
            }
        }
    }

    @Override
    public AccionTemperatura obtenerDecision(String idHabitacion) {
        Habitacion hab = configuracion.obtenerHabitaciones().stream()
                .filter(h -> h.obtenerNombre().equals(idHabitacion))
                .findFirst()
                .orElse(null);

        if (hab == null) {
            return new AccionTemperatura(idHabitacion, false, "Habitación no encontrada");
        }

        double tempActual = temperaturasActuales.getOrDefault(
                hab.obtenerNombre(),
                Double.NaN // Si no hay medición, no deberíamos tomar decisiones
        );

        if (Double.isNaN(tempActual)) {
            return new AccionTemperatura(hab.obtenerNombre(), false, "Sin medición disponible");
        }

        boolean encender = necesitaCalefaccion(tempActual, hab.obtenerTemperaturaEsperada());
        String motivo = encender ?
                "Temperatura actual (" + tempActual + ") menor a la esperada" :
                "Temperatura en rango";

        return new AccionTemperatura(hab.obtenerNombre(), encender, motivo);
    }

    @Override
    public List<AccionTemperatura> obtenerAccionesPendientes() {
        return new ArrayList<>(accionesActuales.values());
    }

    @Override
    public EstadoControlador obtenerEstado() {
        return new EstadoControlador(activo, medicionesRecibidas, accionesEjecutadas, erroresRest);
    }

    private void ejecutarControlTemperatura() {
        try {
            boolean tarifaAlta = esHoraAltaTarifa(configuracion.obtenerTipoContrato());

            if (tarifaAlta) {
                System.out.println("[TARIFA] *** TARIFA ALTA *** Apagando todos los calefactores");
                for (Habitacion hab : configuracion.obtenerHabitaciones()) {
                    enviarAccionSoloSiCambia(hab, false, "TARIFA ALTA");
                }
                return;
            }

            System.out.println("[TARIFA] Tarifa normal - Control activo");

            double consumoTotalKWh = 0;
            List<Habitacion> habitacionesAEncender = new ArrayList<>();

            for (Habitacion hab : configuracion.obtenerHabitaciones()) {
                Double tempActual = temperaturasActuales.get(hab.obtenerNombre());

                if (tempActual == null) {
                    System.out.println("[WARN] Sin medición para " + hab.obtenerNombre());
                    continue;
                }

                if (necesitaCalefaccion(tempActual, hab.obtenerTemperaturaEsperada())) {
                    consumoTotalKWh += hab.obtenerConsumoKWh();
                    habitacionesAEncender.add(hab);
                }
            }

            double limiteKWh = configuracion.obtenerEnergiaMaximaKWh();

            if (consumoTotalKWh > limiteKWh) {
                ajustarPorLimitacionEnergia(habitacionesAEncender, limiteKWh);
            } else {
                for (Habitacion h : configuracion.obtenerHabitaciones()) {
                    boolean enc = habitacionesAEncender.contains(h);
                    enviarAccionSoloSiCambia(h, enc, enc ? "Temperatura baja" : "Temperatura OK");
                }
            }

        } catch (Exception e) {
            System.err.println("[CONTROLADOR] Error ejecutando control: " + e.getMessage());
        }
    }

    private void enviarAccionSoloSiCambia(Habitacion hab, boolean encender, String razon) {
        AccionTemperatura previo = accionesActuales.get(hab.obtenerNombre());

        if (previo != null && previo.isEncender() == encender) {
            return;
        }

        ejecutarAccion(hab, encender, razon);
    }

    private void ejecutarAccion(Habitacion hab, boolean encender, String razon) {
        try {
            boolean exito = clienteRest.enviarComando(hab.obtenerUrlSwitch(), encender);

            AccionTemperatura accion = new AccionTemperatura(hab.obtenerNombre(), encender, razon);
            accionesActuales.put(hab.obtenerNombre(), accion);

            System.out.println("[REST] " + hab.obtenerNombre() + " -> " +
                    (encender ? "ENCENDER" : "APAGAR") + " (" + razon + ")");

            if (exito) accionesEjecutadas++;
            else erroresRest++;

        } catch (Exception e) {
            erroresRest++;
            System.err.println("[CONTROLADOR] Error ejecutando acción: " + e.getMessage());
        }
    }

    private void ajustarPorLimitacionEnergia(List<Habitacion> habitaciones, double limiteKWh) {
        System.out.println("[ENERGÍA] Limitación activa: priorizando habitaciones");

        habitaciones.sort((h1, h2) -> {
            double diff1 = Math.max(0,
                    h1.obtenerTemperaturaEsperada() -
                            temperaturasActuales.getOrDefault(h1.obtenerNombre(), h1.obtenerTemperaturaEsperada()));

            double diff2 = Math.max(0,
                    h2.obtenerTemperaturaEsperada() -
                            temperaturasActuales.getOrDefault(h2.obtenerNombre(), h2.obtenerTemperaturaEsperada()));

            return Double.compare(diff2, diff1);
        });

        double acumulado = 0;
        Set<String> prender = new HashSet<>();

        for (Habitacion hab : habitaciones) {
            double consumo = hab.obtenerConsumoKWh();
            if (acumulado + consumo <= limiteKWh) {
                acumulado += consumo;
                prender.add(hab.obtenerNombre());
            }
        }

        for (Habitacion hab : configuracion.obtenerHabitaciones()) {
            boolean debe = prender.contains(hab.obtenerNombre());
            enviarAccionSoloSiCambia(hab, debe, debe ? "Priorizada" : "Limitación energía");
        }
    }

    private boolean necesitaCalefaccion(double actual, double esperada) {
        return (esperada - actual) > HISTERESIS;
    }

    private boolean esHoraAltaTarifa(String tipoContrato) {
        EnergyCost.EnergyZone zona = EnergyCost.currentEnergyZone(tipoContrato);
        return zona.current() == EnergyCost.HIGH;
    }

    public void setManejadorMqtt(ManejadorMQTT manejadorMqtt) {
        this.manejadorMqtt = manejadorMqtt;
    }
}
