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

    int consumoActual = 0;
    ArrayList<Long> todosLosMili = new ArrayList<>();

    @Override
    public Operacion accionHabitacion(DataSensor dataSensor, List<Habitacion> habitaciones) {

        //Obtiene la timestamp del sensor
        LocalDateTime timestamp = dataSensor.getTimestamp();

        //Le coloca una zona de tiempo
        ZonedDateTime zdt = timestamp.atZone(ZoneId.systemDefault());

        //Lo convierte en un instant
        Instant instant = zdt.toInstant();

        //Convierte a milisegundos
        long milisegundos = instant.toEpochMilli();

        todosLosMili.add(milisegundos);


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
        return null;
    }


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
