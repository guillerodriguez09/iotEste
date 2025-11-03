package com.utec.ioteste.logica;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utec.ioteste.logica.modelos.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ControllerTemperatura implements IControllerTemperatura {

    /*
    EL CONTRATO ES EL QUE INDICA EL CAMBIO DE TARIFA ALTA A BAJA
    * */

    private EstadoSistema estadoSistema;
    //private static final long LIMITE_INACTIVIDAD = 7_200_000L;

    @Override
    public List<Operacion> accionHabitacion(DataSensor dataSensor, EstadoSistema estadoSistema) {

        if (estadoSistema == null) {
            throw new IllegalStateException("Config no cargada, llamar a cargarConfig");
        }

        List<Operacion> operaciones = new ArrayList<>();

        // si es hora pico, apagarlos todos
        if (estadoSistema.isHoraPico()) {
            for (EstadoHabitacion h : estadoSistema.getHabitaciones()) {
                if (h.isSwitchEncendido()) {
                    operaciones.add(crearOperacion(h.getHabitacion().getSensor(), false));
                    h.setSwitchEncendido(false);
                    h.setUltimaActualizacion(LocalDateTime.now());
                    System.out.println("Hora pico, apagamos " + h.getHabitacion().getSensor());
                }
            }
            return operaciones;
        }

        List<EstadoHabitacion> habitaciones = estadoSistema.getHabitaciones();

        estadoSistema.setConsumoActual(calcularConsumoActual(habitaciones));

        EstadoHabitacion h = buscarHabitacion(estadoSistema.getHabitaciones(), dataSensor.getSrc());

        h.setTemperaturaActual((float) dataSensor.getTemperatura());

//         Verificar inactividad si pasaron mas de 15 min
        //Esto iria en optimizacion o en obtener estado actual con un for para todas las habitaciones
//        if (Duration.between(h.getUltimaActualizacion(), LocalDateTime.now()).toMinutes()>15){
//            //en caso de inactividad apago
//            operaciones.add(crearOperacion(h.getHabitacion().getSensor(), false));
//            h.setConsumo(0);
//            h.setUltimaActualizacion(LocalDateTime.now());
//            h.setSwitchEncendido(false);
//            return operaciones;
//        }
//        h.setUltimaActualizacion(LocalDateTime.now());

        if (h.getHabitacion().getExpectedTemp()>dataSensor.getTemperatura()) {

            //INTENTO OPTIMIZACION
            if(estadoSistema.getConsumoActual() < estadoSistema.getConsumoMaximo()){

                double consumoTot = estadoSistema.getConsumoActual() + h.getConsumo();

                if(consumoTot < estadoSistema.getConsumoMaximo()){

                    operaciones.add(crearOperacion(h.getHabitacion().getName(), true));

                }

            }else{

                //if (validarConsumoMaximo(operaciones)) {
                //aca iria la optimizacion
                //}
            }
            operaciones.add(crearOperacion(h.getHabitacion().getName(), true));
        }
        else if (h.getHabitacion().getExpectedTemp()<dataSensor.getTemperatura()) {
            operaciones.add(crearOperacion(h.getHabitacion().getName(), false));
        }
        return operaciones;
    }


    @Override
    public boolean validarConsumoMaximo(List<Operacion> acciones) {
        return false;
    }


    @Override
    public List<Operacion> optimizacion(List<Operacion> acciones){
        return null;
    }

    //funciones auxiliares
    //para buscar una habitacion por el sensor
    private EstadoHabitacion buscarHabitacion(List<EstadoHabitacion> rooms, String sensorSrc) {
        for (EstadoHabitacion h : rooms) {
            if (h.getHabitacion().getName().equals(sensorSrc))
                return h;
        }
        return null;
    }


   //crea la operacion que va a devolver
    private Operacion crearOperacion(String switchUrl, boolean encender) {
        Operacion oper = new Operacion();
        oper.setSrc(switchUrl);
        oper.setEncendido(encender);
        return oper;
    }

    @Override
    public void cargarConfiguracion(String rutaConfig) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Configuracion config = mapper.readValue(new File(rutaConfig), Configuracion.class);
            estadoSistema = new EstadoSistema(config);
            System.out.println("Configuración cargada correctamente:");
        } catch(IOException e){
            System.err.println("Error al leer configuración: ");
        }
    }

    private double calcularConsumoActual(List<EstadoHabitacion> habitaciones) {

        return habitaciones.stream()
                .filter(EstadoHabitacion::isSwitchEncendido)
                .mapToDouble(EstadoHabitacion::getConsumo)
                .sum();
    }

    @Override
    public EstadoSistema obtenerEstadoActual() {
        return estadoSistema;
    }


}
