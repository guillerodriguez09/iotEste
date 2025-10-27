package com.utec.ioteste.modelos;

public class Configuracion {

    public static int consumoMax;
    public static boolean esHoraPico;

    public Configuracion() {
    }

    public static void setConsumoMax(int consumoMax) {
        Configuracion.consumoMax = consumoMax;
    }

    public static void setEsHoraPico(boolean esHoraPico) {
        Configuracion.esHoraPico = esHoraPico;
    }
}
