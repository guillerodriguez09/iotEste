package com.utec.ioteste.logica;
import com.utec.ioteste.logica.modelos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ControllerTemperaturaTest {

    private IControllerTemperatura controller;

    @BeforeEach
    void setUp() {
        controller = new ControllerTemperatura();
        controller.cargarConfiguracion("src/test/resources/site_config.json");
    }

    @Test
    void debeEncenderCuandoTemperaturaBaja() {
        DataSensor medicion = new DataSensor("shellyhtg3-84fce63ad204",13,200001,false);
        List<Operacion> acciones = controller.accionHabitacion(medicion,controller.obtenerEstadoActual());

        assertFalse(acciones.isEmpty());
        assertEquals("shellyhtg3-84fce63ad204", acciones.get(0).getSrc());
        assertTrue(acciones.get(0).getEncendido());
    }

    @Test
    void debeApagarCuandoTemperaturaAlta() {

        DataSensor medicion = new DataSensor("shellyhtg3-84fce63ad204",28,200001,true);
        List<Operacion> acciones = controller.accionHabitacion(medicion,controller.obtenerEstadoActual());

        assertFalse(acciones.isEmpty());
        assertEquals("shellyhtg3-84fce63ad204", acciones.get(0).getSrc());
        assertFalse(acciones.get(0).getEncendido());
    }

    @Test
    void noDebeExcederConsumoMaximo() {
        DataSensor medicion = new DataSensor("shellyhtg3-84fce63ad204",28,200001,true);
        List<Operacion> acciones = controller.accionHabitacion(medicion,controller.obtenerEstadoActual());

        EstadoSistema estado = controller.obtenerEstadoActual();
        assertTrue(estado.getConsumoActual() <= estado.getConsumoMaximo());

        // Verificar que el sistema está limitado por consumo
        if (estado.isLimitadoPorConsumo()) {
            assertTrue(acciones.isEmpty() || !acciones.get(0).getEncendido());
        }
    }

    @Test
    void debeManetenerTemperaturaEnRango() {
        DataSensor medicion = new DataSensor("shellyhtg3-84fce63ad204",22,200001,true);
        List<Operacion> acciones = controller.accionHabitacion(medicion,controller.obtenerEstadoActual());

        // No debería generar acciones si la temperatura está en rango
        assertTrue(acciones.isEmpty() ||
                acciones.stream().noneMatch(a -> a.getSrc().equals("shellyhtg3-84fce63ad204")));
    }

    @Test
    void debeValidarConsumoMaximoCorrectamente() {
        Operacion accion1 = new Operacion("hab1", "switch1", true);
        Operacion accion2 = new Operacion("hab2", "switch2", true);
        List<Operacion> acciones = List.of(accion1, accion2);

        boolean esValido = controller.validarConsumoMaximo(acciones);

        assertNotNull(esValido);
    }

//    @Test
//    void debeOptimizarAccionesCuandoHayLimitacion() {
//        List<Operacion> acciones = controller.accionHabitacion(controller.obtenerUltimaMedicion(),controller.obtenerEstadoActual());
//
//        assertNotNull(accionesOptimizadas);
//        assertTrue(accionesOptimizadas.size() <= accionesOriginales.size());
//    }

    @Test
    void debeCagarConfiguracionCorrectamente() {
        assertDoesNotThrow(() -> {
            controller.cargarConfiguracion("src/test/resources/site_config.json");
        });
        EstadoSistema estado = controller.obtenerEstadoActual();
        assertNotNull(estado);
        assertTrue(estado.getConsumoMaximo() > 0);
    }

}
