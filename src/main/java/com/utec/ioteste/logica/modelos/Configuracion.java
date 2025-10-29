package com.utec.ioteste.logica.modelos;

import java.util.List;

public class Configuracion {

    private String site;
    private int maxEnergy;
    private TimeSlot timeSlot;
    private List<Habitacion> rooms;

    public Configuracion(String site, int maxEnergy, TimeSlot timeSlot, List<Habitacion> rooms) {
        this.site = site;
        this.maxEnergy = maxEnergy;
        this.timeSlot = timeSlot;
        this.rooms = rooms;
    }

    public Configuracion() {};

    public int getMaxEnergy() { return maxEnergy; }
    public void setMaxEnergy(int maxEnergy) { this.maxEnergy = maxEnergy; }
    public List<Habitacion> getRooms() { return rooms; }
    public void setRooms(List<Habitacion> rooms) { this.rooms = rooms; }
    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }

}
