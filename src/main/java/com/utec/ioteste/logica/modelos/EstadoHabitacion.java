package com.utec.ioteste.logica.modelos;

import java.time.LocalDateTime;

public class EstadoHabitacion {
    private Habitacion habitacion;
    private float temperaturaActual;
    private boolean switchEncendido;
    private double consumo;
    private LocalDateTime ultimaActualizacion;

    public EstadoHabitacion(){}

    public EstadoHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
        this.consumo = getConsumo();
        this.switchEncendido = consumo > 0;
        this.switchEncendido = false;
        this.temperaturaActual = 0;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public Habitacion getHabitacion() {return habitacion;}
    public void setHabitacion(Habitacion habitacion) {this.habitacion = habitacion;}
    public float getTemperaturaActual() { return temperaturaActual; }
    public void setTemperaturaActual(float temperaturaActual) { this.temperaturaActual = temperaturaActual; }
    public boolean isSwitchEncendido() { return switchEncendido; }
    public void setSwitchEncendido(boolean switchEncendido) { this.switchEncendido = switchEncendido; }
    public double getConsumo() { return consumo; }
    public void setConsumo(double consumo) { this.consumo = consumo; }
    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }

}
