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
    private Map<String, Double> temperaturasActuales;
    private Map<String, AccionTemperatura> accionesActuales;
    private ManejadorMQTT manejadorMqtt;
    private ClienteRest clienteRest;
    private ScheduledExecutorService ejecutor;
    
    private long medicionesRecibidas = 0;
    private long accionesEjecutadas = 0;
    private long erroresRest = 0;
    private boolean activo = false;
    private static final double HISTERESIS = 0.5;
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
            
            // Conectar MQTT y suscribirse a sensores
            manejadorMqtt.conectar("controlador-temperatura");
            for (Habitacion hab : configuracion.obtenerHabitaciones()) {
                String sensor = hab.obtenerSensor();
                if (sensor != null && !sensor.isEmpty()) {
                    manejadorMqtt.suscribirse(sensor);
                    System.out.println("[MQTT] Suscrito a: " + sensor);
                }
            }
            
            // Iniciar ciclo de control
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
            if (manejadorMqtt != null) {
                manejadorMqtt.desconectar();
            }
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
        // Buscar la habitación en la configuración
        Habitacion hab = configuracion.obtenerHabitaciones().stream()
                .filter(h -> h.obtenerNombre().equals(idHabitacion))
                .findFirst()
                .orElse(null);

        if (hab == null) {
            return new AccionTemperatura(idHabitacion, false, "Habitación no encontrada");
        }

        double tempActual = temperaturasActuales.getOrDefault(hab.obtenerNombre(), hab.obtenerTemperaturaEsperada());

        double tempEsperada = hab.obtenerTemperaturaEsperada();

        boolean encender = necesitaCalefaccion(tempActual, tempEsperada);

        String motivo = encender
                ? "Temperatura actual (" + tempActual + ") menor a la esperada (" + tempEsperada + ")"
                : "Temperatura en rango";

        return new AccionTemperatura(
                hab.obtenerNombre(),
                encender,
                motivo
        );
    }



    @Override
    public List<AccionTemperatura> obtenerAccionesPendientes() {
        return new ArrayList<>(accionesActuales.values());
    }

    @Override
    public ControladorTemperatura.EstadoControlador obtenerEstado() {
        return new ControladorTemperatura.EstadoControlador(activo, medicionesRecibidas, accionesEjecutadas, erroresRest);
    }

    private void ejecutarControlTemperatura() {
        try {
            boolean tarifaAlta = esHoraAltaTarifa(configuracion.obtenerTipoContrato());
            
            if (tarifaAlta) {
                // TARIFA ALTA: Apagar TODO para evitar consumo
                System.out.println("[TARIFA] *** TARIFA ALTA *** Apagando todos los calefactores");
                for (Habitacion hab : configuracion.obtenerHabitaciones()) {
                    ejecutarAccion(hab, false, "TARIFA ALTA - Sistema pausado");
                }
            } else {
                // TARIFA NORMAL: Control de temperatura normal
                System.out.println("[TARIFA] Tarifa normal - Control activo");
                
                // Calcular consumo total actual
                double consumoTotal = 0;
                List<Habitacion> habitacionesAEncender = new ArrayList<>();
                
                for (Habitacion hab : configuracion.obtenerHabitaciones()) {
                    double tempActual = temperaturasActuales.getOrDefault(hab.obtenerNombre(), hab.obtenerTemperaturaEsperada());
                    
                    if (necesitaCalefaccion(tempActual, hab.obtenerTemperaturaEsperada())) {
                        consumoTotal += hab.obtenerConsumoWh();
                        habitacionesAEncender.add(hab);
                    }
                }
                
                // Ajustar si hay limitación de energía
                if (consumoTotal > configuracion.obtenerEnergiaMaximaKWh()) {
                    ajustarPorLimitacionEnergia(habitacionesAEncender);
                } else {
                    // Ejecutar acciones sin limitación
                    for (Habitacion hab : habitacionesAEncender) {
                        ejecutarAccion(hab, true, "Temperatura baja");
                    }
                    for (Habitacion hab : configuracion.obtenerHabitaciones()) {
                        if (!habitacionesAEncender.contains(hab)) {
                            ejecutarAccion(hab, false, "Temperatura en rango");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[CONTROLADOR] Error ejecutando control: " + e.getMessage());
        }
    }

    private void ejecutarAccion(Habitacion hab, boolean encender, String razon) {
        try {
            String urlSwitch = hab.obtenerUrlSwitch();
            boolean exito = clienteRest.enviarComando(urlSwitch, encender);
            
            AccionTemperatura accion = new AccionTemperatura(
                hab.obtenerNombre(),
                encender,
                razon
            );
            accionesActuales.put(hab.obtenerNombre(), accion);
            
            System.out.println("[REST] " + hab.obtenerNombre() + " -> " + (encender ? "ENCENDER" : "APAGAR") + " (" + razon + ")");
            
            if (exito) {
                accionesEjecutadas++;
            } else {
                erroresRest++;
            }
        } catch (Exception e) {
            erroresRest++;
            System.err.println("[CONTROLADOR] Error ejecutando acción: " + e.getMessage());
        }
    }

    private void ajustarPorLimitacionEnergia(List<Habitacion> habitaciones) {
        System.out.println("[ENERGÍA] Limitación activa: priorizando habitaciones");
        
        // Priorizar por diferencia de temperatura
        habitaciones.sort((h1, h2) -> {
            double diff1 = Math.abs(temperaturasActuales.getOrDefault(h1.obtenerNombre(), h1.obtenerTemperaturaEsperada()) - h1.obtenerTemperaturaEsperada());
            double diff2 = Math.abs(temperaturasActuales.getOrDefault(h2.obtenerNombre(), h2.obtenerTemperaturaEsperada()) - h2.obtenerTemperaturaEsperada());
            return Double.compare(diff2, diff1);
        });
        
        double consumoAcumulado = 0;
        Set<String> habitacionesAEncender = new HashSet<>();
        
        for (Habitacion hab : habitaciones) {
            if (consumoAcumulado + hab.obtenerConsumoWh() <= configuracion.obtenerEnergiaMaximaKWh()) {
                consumoAcumulado += hab.obtenerConsumoWh();
                habitacionesAEncender.add(hab.obtenerNombre());
            }
        }
        
        // Ejecutar acciones finales
        for (Habitacion hab : configuracion.obtenerHabitaciones()) {
            boolean debeEncender = habitacionesAEncender.contains(hab.obtenerNombre());
            ejecutarAccion(hab, debeEncender, debeEncender ? "Priorizada por diferencia" : "Limitación de energía");
        }
    }

    private boolean necesitaCalefaccion(double actual, double esperada) {
        return (esperada - actual) > HISTERESIS;
    }

    /* Esto con la clase que nos paso el profe, o directamente se puede llamar a energyZone al inicio de
    la funcion ejecutarControlTemperatura pasandole tanto el tipo de contrato y el ts actual
    private boolean esHoraPico(String contrato, long ts){
        int res = energyZone(contrato, ts);
        if(res == 1) {
            return true;
        }else{
            return false;
        }
    }
    */
    private boolean esHoraAltaTarifa(String tipoContrato) {
        // Obtener zona energética según el tipo de contrato
        EnergyCost.EnergyZone zona = EnergyCost.zonaEnergiActual(tipoContrato);

        // Consideramos tarifa alta cuando la zona es PEAK
        boolean esAlta = zona.actual() == EnergyCost.PEAK;

        // Logging
        if (esAlta) {
            System.out.println("[TARIFA] " + zona.obtenerDescripcion() + " → TARIFA ALTA");
        } else {
            System.out.println("[TARIFA] " + zona.obtenerDescripcion() + " → Tarifa normal");
        }

        return esAlta;
    }


    public void setManejadorMqtt(ManejadorMQTT manejadorMqtt) {
        this.manejadorMqtt = manejadorMqtt;
    }
}
