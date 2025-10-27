package com.utec.ioteste.logica;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utec.ioteste.logica.modelos.Configuracion;
import com.utec.ioteste.logica.modelos.DataSensor;
import com.utec.ioteste.logica.modelos.Habitacion;
import com.utec.ioteste.logica.modelos.Operacion;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ControllerTemperatura implements IControllerTemperatura {

    private Configuracion config; //para guardar la configuracion
    double consumoActual = 0;
    private static final long LIMITE_INACTIVIDAD = 7_200_000L;
    ArrayList<Long> todosLosMili = new ArrayList<>();

    @Override
    public List<Operacion> accionHabitacion(List<DataSensor> sensores, List<Habitacion> habitaciones) {
        //todosLosMili.add(dataSensor.getTsMillis());

        if (config == null) {
            throw new IllegalStateException("Config no cargada, llamar a cargarConfig");
        }

        List<Operacion> operaciones = new ArrayList<>();

        consumoActual = calcularConsumoActual(habitaciones);

        // si es hora pico, apagarlos todos
        if (config.isEsHoraPico()) {
            for (Habitacion h : habitaciones) {
                if (h.getPrendida()) {
                    operaciones.add(crearOperacion(h.getSwitchUrl(), false));
                    h.setPrendida(false);
                    System.out.println("Hora pico, apagamos " + h.getName());
                }
            }
            return operaciones;
        }

        //procesamos los sensores
        for (DataSensor sensor : sensores) {
            Habitacion hab = buscarHabitacion(habitaciones, sensor.getSrc());
            if (hab == null) continue;

            // actualizar estado de la habitación
            hab.setTempActual((int) sensor.getTC());
            hab.setUltimaActualizacion((int) sensor.getTsMillis());

            // habria que hacer una funcion para ver si hay inactividad
            // luego controlar las temperaturas y comparar con las deseadas
            // usar la funcion optimización si quedamos al límite del consumo

            if (consumoActual >= config.getMaxEnergyValue()) {
                //aca iria la optimizacion
            }

            return operaciones;
        }
        return operaciones;
    }



//        for(Habitacion fila : habitaciones){
//
//            if(fila.getPrendida()) {
//                consumoActual += fila.getEnergy();
//
//                if(consumoActual >= Configuracion.maxEnergy){
//
//                    optimizacion(habitaciones);
//                    return null;
//                }
//            }
//
//        }

        //return oper;
       // return null }


    @Override
    public void optimizacion(List<Habitacion> habitaciones) {
        for(Habitacion fila : habitaciones){

            if(fila.getPrendida()) {

                fila.getExpectedTemp();

                //if(consumoActual >= Configuracion.maxEnergy){

                  //  fila.optimizacion(habitaciones);
                //}
            }

        }
    }

    //funciones auxiliares
    //para buscar una habitacion por el sensor
    private Habitacion buscarHabitacion(List<Habitacion> rooms, String sensorSrc) {
        for (Habitacion h : rooms) {
            if (h.getSensor().equals(sensorSrc))
                return h;
        }
        return null;
    }

    //calcula el consumo actual
    private double calcularConsumoActual(List<Habitacion> habitaciones) {
        return habitaciones.stream()
                .filter(Habitacion::getPrendida)
                .mapToDouble(Habitacion::getEnergyValue)
                .sum();
    }

   //crea la operacion que va a devolver
    private Operacion crearOperacion(String switchUrl, boolean encender) {
        Operacion oper = new Operacion();
        oper.setSrc(switchUrl);
        Operacion.Params params = new Operacion.Params();
        params.setWas_on(encender);
        oper.setParams(params);
        return oper;
    }

    public void cargarConfiguracion(String rutaConfig) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Configuracion config = mapper.readValue(new File(rutaConfig), Configuracion.class);
            System.out.println("Configuración cargada correctamente:");
        } catch(IOException e){
            System.err.println("Error al leer configuración: ");
        }
    }



}
