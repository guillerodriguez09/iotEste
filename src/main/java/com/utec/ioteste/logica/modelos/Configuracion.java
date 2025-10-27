package com.utec.ioteste.logica.modelos;

import java.util.List;

public class Configuracion {

    private String site;
    private String maxEnergy;
    private TimeSlot timeSlot;
    private List<Habitacion> rooms;
    private boolean esHoraPico;

    public String getMaxEnergy() { return maxEnergy; }
    public void setMaxEnergy(String maxEnergy) { this.maxEnergy = maxEnergy; }
    public List<Habitacion> getRooms() { return rooms; }
    public void setRooms(List<Habitacion> rooms) { this.rooms = rooms; }
    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }
    public boolean isEsHoraPico() { return esHoraPico; }
    public void setEsHoraPico(boolean esHoraPico) { this.esHoraPico = esHoraPico; }

    //Elimina el khw y devuelve solo el numero
    public double getMaxEnergyValue() {
        return Double.parseDouble(maxEnergy.replaceAll("[^\\d.]", ""));
    }

}
