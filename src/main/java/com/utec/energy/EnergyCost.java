package com.utec.energy;
import java.time.LocalDateTime;

/**
 * Clase para determinar tarifas de energía según horario.
 * Basado en estructura de tarifas españolas (Low/Mid/Peak).
 */
public class EnergyCost {

    public static final int LOW = 0;    // Tarifa baja
    public static final int MID = 1;    // Tarifa media
    public static final int PEAK = 2;   // Tarifa pico

    /**
     * Obtiene la zona de energía actual según el contrato.
     */
    public static EnergyZone zonaEnergiActual(String tipoContrato) {
        LocalDateTime ahora = LocalDateTime.now();
        int hora = ahora.getHour();

        if ("DISCRIMINACION_HORARIA".equalsIgnoreCase(tipoContrato)) {
            // Horario típico español
            // 00-08: Tarifa baja
            // 08-14: Tarifa pico
            // 14-22: Tarifa media
            // 22-00: Tarifa baja
            if (hora < 8 || hora >= 22) {
                return new EnergyZone(LOW, "Tarifa baja (noche)");
            } else if (hora >= 8 && hora < 14) {
                return new EnergyZone(PEAK, "Tarifa pico (mañana)");
            } else {
                return new EnergyZone(MID, "Tarifa media (tarde)");
            }
        } else {
            // Contrato simple: siempre tarifa media
            return new EnergyZone(MID, "Tarifa única");
        }
    }

    /**
     * Zona de energía con información de tarifa.
     */
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
