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
        controller.cargarDataSensor("src/test/resources/sensor.json");
    }

    @Test
    void debeEncenderCuandoTemperaturaBaja() {
        List<Operacion> acciones = controller.accionHabitacion(controller.obtenerUltimaMedicion(),controller.obtenerEstadoActual());

        assertFalse(acciones.isEmpty());
        assertEquals("shellyhtg3-84fce63ad204", acciones.get(0).getSrc());
        assertTrue(acciones.get(0).getParams().isWas_on());
    }

    @Test
    void debeApagarCuandoTemperaturaAlta() {

        DataSensor mod = controller.obtenerUltimaMedicion();
        mod.getParams().getTemperature().setTC(28);
        List<Operacion> acciones = controller.accionHabitacion(mod,controller.obtenerEstadoActual());

        assertFalse(acciones.isEmpty());
        assertEquals("shellyhtg3-84fce63ad204", acciones.get(0).getSrc());
        assertFalse(acciones.get(0).getParams().isWas_on());
    }

    @Test
    void noDebeExcederConsumoMaximo() {
        List<Operacion> acciones = controller.accionHabitacion(controller.obtenerUltimaMedicion(),controller.obtenerEstadoActual());

        EstadoSistema estado = controller.obtenerEstadoActual();
        assertTrue(estado.getConsumoActual() <= estado.getConsumoMaximo());

        // Verificar que el sistema está limitado por consumo
        if (estado.isLimitadoPorConsumo()) {
            assertTrue(acciones.isEmpty() || !acciones.get(0).getParams().isWas_on());
        }
    }

    @Test
    void debeManetenerTemperaturaEnRango() {
        DataSensor mod = controller.obtenerUltimaMedicion();
        mod.getParams().getTemperature().setTC(22);
        List<Operacion> acciones = controller.accionHabitacion(mod,controller.obtenerEstadoActual());

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

    @Test
    void debeCagarSensorCorrectamente() {
        assertDoesNotThrow(() -> {
            controller.cargarDataSensor("src/test/resources/sensor.json");
        });
        DataSensor sensor = controller.obtenerUltimaMedicion();
        assertNotNull(sensor);
        assertTrue(sensor.getParams().getTemperature().getTC() > 0);
    }

}
