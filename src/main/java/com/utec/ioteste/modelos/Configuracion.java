package com.utec.ioteste.modelos;

import java.util.List;

public class Configuracion {

    public static int consumoMax;
    public static boolean esHoraPico;
    public static List<Habitacion> habitaciones;

    public Configuracion() {
    }

    public static void setConsumoMax(int consumoMax) {
        Configuracion.consumoMax = consumoMax;
    }

    public static void setEsHoraPico(boolean esHoraPico) {
        Configuracion.esHoraPico = esHoraPico;
    }
}
