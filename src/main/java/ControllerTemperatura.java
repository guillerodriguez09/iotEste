import com.utec.ioteste.modelos.Configuracion;
import com.utec.ioteste.modelos.DataSensor;
import com.utec.ioteste.modelos.Habitacion;
import com.utec.ioteste.modelos.Operacion;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ControllerTemperatura {

    boolean prendido = false;
    int consumoActual = 0;
    Habitacion habitacion = new Habitacion();
    ArrayList<Long> todosLosMili = new ArrayList<>();

    public List<Operacion> accionHabitacion(List<DataSensor> dataSensor, List<Habitacion> habitaciones){

        for(DataSensor ds : dataSensor){

            //Obtiene la timestamp del sensor
            LocalDateTime timestamp = ds.getTimestamp();

            //Le coloca una zona de tiempo
            ZonedDateTime zdt = timestamp.atZone(ZoneId.systemDefault());

            //Lo convierte en un instant
            Instant instant = zdt.toInstant();

            //Convierte a milisegundos
            long milisegundos = instant.toEpochMilli();

            todosLosMili.add(milisegundos);

        }

        for(Habitacion fila : habitaciones){

            if(fila.getPrendida()) {
                consumoActual += fila.getConsumo();

                if(consumoActual >= Configuracion.consumoMax){

                    optimizacion(habitaciones);
                    return null;
                }
            }

        }

        return oper;

    }

    public void optimizacion(List<Habitacion> habitaciones) {

        for(Habitacion fila : habitaciones){

            if(fila.getPrendida()) {

                fila.getTempDeseada();

                if(consumoActual >= Configuracion.consumoMax){

                    fila.optimizacion(habitaciones);
                }
            }

        }

    }

}
