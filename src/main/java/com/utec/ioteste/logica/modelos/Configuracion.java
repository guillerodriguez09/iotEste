package com.utec.ioteste.logica.modelos;

import java.util.List;

public class Configuracion {

    private String site;
    private String maxEnergy;
    private TimeSlot timeSlot;
    private List<Habitacion> rooms;

    public Configuracion(String site, String maxEnergy, TimeSlot timeSlot, List<Habitacion> rooms) {
        this.site = site;
        this.maxEnergy = maxEnergy;
        this.timeSlot = timeSlot;
        this.rooms = rooms;
    }

    public Configuracion() {};

    public String getMaxEnergy() { return maxEnergy; }
    public void setMaxEnergy(String maxEnergy) { this.maxEnergy = maxEnergy; }
    public List<Habitacion> getRooms() { return rooms; }
    public void setRooms(List<Habitacion> rooms) { this.rooms = rooms; }
    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }

    public double getMaxEnergyValue() {
        return Double.parseDouble(maxEnergy.replaceAll("[^\\d.]", ""));
    }

}
