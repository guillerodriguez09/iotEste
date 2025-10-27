package com.utec.ioteste.logica;

import com.utec.ioteste.logica.modelos.DataSensor;
import com.utec.ioteste.logica.modelos.Habitacion;
import com.utec.ioteste.logica.modelos.Operacion;
import java.util.List;

public interface IControllerTemperatura {

    public abstract Operacion accionHabitacion(DataSensor dataSensor, List<Habitacion> habitaciones);

    public abstract void optimizacion(List<Habitacion> habitaciones);

    public abstract void cargarConfiguracion(String rutaConfig);
}
