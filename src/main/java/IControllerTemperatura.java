import com.utec.ioteste.modelos.DataSensor;
import com.utec.ioteste.modelos.Habitacion;
import com.utec.ioteste.modelos.Operacion;

import java.util.List;

public interface IControllerTemperatura {

    Operacion accionHabitacion(DataSensor dataSensor, List<Habitacion> habitaciones);

}
