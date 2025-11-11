package com.utec.energy;

public class EnergyCost {

    public static class EnergyZone {
        private final int tarifa;
        private final String descripcion;
        
        public EnergyZone(int tarifa, String descripcion) {
            this.tarifa = tarifa;
            this.descripcion = descripcion;
        }
        
        public int actual() {
            return tarifa;
        }
        
        public String obtenerDescripcion() {
            return descripcion;
        }
        
        @Override
        public String toString() {
            return descripcion + " (" + tarifa + ")";
        }
    }
}
