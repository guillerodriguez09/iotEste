import com.utec.ioteste.modelos.Configuracion;
import com.utec.ioteste.modelos.DataSensor;
import com.utec.ioteste.modelos.Habitacion;
import com.utec.ioteste.modelos.Operacion;

import java.util.ArrayList;
import java.util.List;

public class ControllerTemperatura {

    Operacion oper = new Operacion();
    int consumoActual = 0;

    public Operacion accionHabitacion(DataSensor dataSensor, List<Habitacion> habitaciones){

        for(Habitacion fila : habitaciones){

            if(!fila.getPrendida()) {
                consumoActual += fila.getConsumo();

                if(consumoActual >= Configuracion.consumoMax){
                    return null;
                }
            }

        }

        return oper;

    }

}
