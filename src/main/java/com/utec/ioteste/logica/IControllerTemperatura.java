package com.utec.ioteste.logica;

import com.utec.ioteste.logica.modelos.DataSensor;
import com.utec.ioteste.logica.modelos.EstadoSistema;
import com.utec.ioteste.logica.modelos.Operacion;
import java.util.List;

public interface IControllerTemperatura {

    public abstract List<Operacion> accionHabitacion(DataSensor dataSensor, EstadoSistema estadoSistema);

    EstadoSistema obtenerEstadoActual();

    boolean validarConsumoMaximo(List<Operacion> acciones);

    public abstract List<Operacion> optimizacion(List<Operacion> acciones);

    public abstract void cargarConfiguracion(String rutaConfig);

    public abstract void cargarDataSensor(String rutaConfig);

    public abstract DataSensor obtenerUltimaMedicion();
}
