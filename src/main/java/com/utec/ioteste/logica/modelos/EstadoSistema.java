package com.utec.ioteste.logica.modelos;
import java.util.ArrayList;
import java.util.List;

public class EstadoSistema {

    private Configuracion config;
    private List<EstadoHabitacion> habitaciones;
    private double consumoActual;
    private double consumoMaximo;
    private boolean limitadoPorConsumo;
    private boolean horaPico;

    public EstadoSistema(Configuracion config) {
        this.config = config;
        this.habitaciones = new ArrayList<>();
        for (Habitacion h : config.getRooms()) {
            habitaciones.add(new EstadoHabitacion(h));
        }
        this.consumoActual = 0;
        this.consumoMaximo = config.getMaxEnergyValue();
        this.limitadoPorConsumo = false;
        this.horaPico = false;
    }

    public Configuracion getConfig() { return config; }
    public void setConfig(Configuracion config) { this.config = config; }
    public List<EstadoHabitacion> getHabitaciones() { return habitaciones; }
    public void setHabitaciones(List<EstadoHabitacion> habitaciones) { this.habitaciones = habitaciones; }
    public double getConsumoActual() { return consumoActual; }
    public void setConsumoActual(double consumoActual) { this.consumoActual = consumoActual; }
    public double getConsumoMaximo() { return consumoMaximo; }
    public void setConsumoMaximo(double consumoMaximo) { this.consumoMaximo = consumoMaximo; }
    public boolean isLimitadoPorConsumo() { return limitadoPorConsumo; }
    public void setLimitadoPorConsumo(boolean limitadoPorConsumo) { this.limitadoPorConsumo = limitadoPorConsumo; }
    public boolean isHoraPico() { return horaPico; }
    public void setHoraPico(boolean horaPico) { this.horaPico = horaPico; }

}
