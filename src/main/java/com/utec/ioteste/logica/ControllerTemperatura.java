package com.utec.ioteste.logica;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.utec.ioteste.logica.modelos.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

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
        Map<String, Double> difTempAllRooms = new HashMap<>();
        Map<String, Double> minDiff = conseguirDiffMin(difTempAllRooms);

        for(EstadoHabitacion r :  habitaciones) {

            if(r.isSwitchEncendido()) {
                double difTem = r.getHabitacion().getExpectedTemp() - r.getTemperaturaActual();
                difTempAllRooms.put(r.getHabitacion().getName(), difTem);
            }
        }

        if (h.getHabitacion().getExpectedTemp()>dataSensor.getTemperatura()) {

            double difTemp = h.getHabitacion().getExpectedTemp() - dataSensor.getTemperatura();
            //double difTempMin = minDiff.values().iterator().next();
            EstadoHabitacion maxRoom = conseguirMax(habitaciones);
            //double difTempMax = maxRoom.getHabitacion().getExpectedTemp() - maxRoom.getTemperaturaActual();

            //INTENTO OPTIMIZACION
            if(estadoSistema.getConsumoActual() < estadoSistema.getConsumoMaximo()){

                double consumoTot = estadoSistema.getConsumoActual() + h.getConsumo();

                if(consumoTot < estadoSistema.getConsumoMaximo()){

                    operaciones.add(crearOperacion(h.getHabitacion().getName(), true));

                }

            }else if(difTemp > minDiff.values().iterator().next() + 2){ /*(si diferencia habitacionActual es mayor a diferenciaMenor + 2, apagar habitacion con menor diferencia y prender la que recien llego)*/

                String key = "Pan";
                for (Map.Entry<String, Double> entry : minDiff.entrySet()) {
                    key = entry.getKey();
                }

                operaciones.add(crearOperacion(key, false));
                operaciones.add(crearOperacion(h.getHabitacion().getName(), true));

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

    @Override
    public double calcularConsumoActual(List<EstadoHabitacion> habitaciones) {

        return habitaciones.stream()
                .filter(EstadoHabitacion::isSwitchEncendido)
                .mapToDouble(EstadoHabitacion::getConsumo)
                .sum();
    }

    @Override
    public EstadoHabitacion conseguirMax(List<EstadoHabitacion> habitaciones) {

        return habitaciones.stream()
                .filter(EstadoHabitacion::isSwitchEncendido)
                .max(Comparator.comparingDouble(EstadoHabitacion::getTemperaturaActual))
                .orElse(null);
    }

    //Esto seria para conseguir la diferencia mas chica de todas las habitaciones provistas.
    public Map<String, Double> conseguirDiffMin(Map<String, Double> diff) {

        Optional<Map.Entry<String, Double>> minDiff = diff.entrySet()
                .stream().
                min(Map.Entry.comparingByValue());

        return minDiff.map(entry -> Map.of(entry.getKey(), entry.getValue()))
                .orElseGet(Map::of);
    }

    @Override
    public EstadoSistema obtenerEstadoActual() {
        return estadoSistema;
    }


}
