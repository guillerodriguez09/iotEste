//package com.utec.ioteste.logica;
//import com.utec.ioteste.logica.modelo.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ControllerTemperaturaTest {
//
//    private IControllerTemperatura controller;
//
//    @BeforeEach
//    void setUp() {
//        controller = new ControllerTemperatura();
//        controller.cargarConfiguracion("src/test/resources/site_config.json");
//    }
//
//    @Test
//    void debeEncenderCuandoTemperaturaBaja() {
//        DataSensor medicion = new DataSensor("shellyhtg3-84fce63ad204",13,200001,false);
//        List<Operacion> acciones = controller.accionHabitacion(medicion,controller.obtenerEstadoActual());
//
//        assertFalse(acciones.isEmpty());
//        assertEquals("shellyhtg3-84fce63ad204", acciones.get(0).getSrc());
//        assertTrue(acciones.get(0).getEncendido());
//        //chequear que solo accione 1 switch
//    }
//
//    //crear el estado actual como querramos
//    //otro test Estado prendido pero temp baja entoces no hace nada
//    //sleep thread para calcular tiempo
//
//    @Test
//    void debeApagarCuandoTemperaturaAlta() {
//
//        DataSensor medicion = new DataSensor("shellyhtg3-84fce63ad204",28,200001,true);
//        List<Operacion> acciones = controller.accionHabitacion(medicion,controller.obtenerEstadoActual());
//
//        assertFalse(acciones.isEmpty());
//        assertEquals("shellyhtg3-84fce63ad204", acciones.get(0).getSrc());
//        assertFalse(acciones.get(0).getEncendido());
//    }
//
//    @Test
//    void noDebeExcederConsumoMaximo() {
//        DataSensor medicion = new DataSensor("shellyhtg3-84fce63ad204",28,200001,true);
//        List<Operacion> acciones = controller.accionHabitacion(medicion, controller.obtenerEstadoActual());
//
//        EstadoSistema estado = controller.obtenerEstadoActual();
//        assertTrue(estado.getConsumoActual() <= estado.getConsumoMaximo());
//
//        // Verificar que el sistema está limitado por consumo
//        if (estado.isLimitadoPorConsumo()) {
//            assertTrue(acciones.isEmpty() || !acciones.get(0).getEncendido());
//        }
//    }
//
//    @Test
//    void debeManetenerTemperaturaEnRango() {
//        DataSensor medicion = new DataSensor("shellyhtg3-84fce63ad204",22,200001,true);
//        List<Operacion> acciones = controller.accionHabitacion(medicion,controller.obtenerEstadoActual());
//
//        // No debería generar acciones si la temperatura está en rango
//        assertTrue(acciones.isEmpty() ||
//                acciones.stream().noneMatch(a -> a.getSrc().equals("shellyhtg3-84fce63ad204")));
//    }
//
//    @Test
//    void debeValidarConsumoMaximoCorrectamente() {
//        Operacion accion1 = new Operacion("hab1", "switch1", true);
//        Operacion accion2 = new Operacion("hab2", "switch2", true);
//        List<Operacion> acciones = List.of(accion1, accion2);
//
//        boolean esValido = controller.validarConsumoMaximo(acciones);
//
//        assertNotNull(esValido);
//    }
//
//    @Test
//    void debeMantenerEncendidoCuandoTemperaturaBaja() {
//        DataSensor medicionFria = new DataSensor("shellyhtg3-84fce63ad204", 13.0f, 200001, true);
//
//        Configuracion config = controller.obtenerEstadoActual().getConfig();
//        EstadoSistema yaEncendido = new EstadoSistema(config);
//
//        EstadoHabitacion hab = yaEncendido.getHabitaciones().stream()
//                .filter(h -> h.getHabitacion().getName().equals("shellyhtg3-84fce63ad204"))
//                .findFirst().orElse(null);
//
//        assertNotNull(hab, "la hab de prueba no se encontro");
//        hab.setSwitchEncendido(true);
//
//        List<Operacion> acciones = controller.accionHabitacion(medicionFria, yaEncendido);
//        assertTrue(acciones.isEmpty(), "No debería generar acciones si el switch ya estaba encendido");
//    }
//
//    @Test
//    void debeActuarSobreHabitacionCorrecta() {
//        // office2 tiene expected temp 21
//        DataSensor medicionFriaO2 = new DataSensor("office2", 15.0f, 200001, true);
//
//        List<Operacion> acciones = controller.accionHabitacion(medicionFriaO2, controller.obtenerEstadoActual());
//
//        assertFalse(acciones.isEmpty()); //ve si genero alguna una accion
//        assertEquals(1, acciones.size()); // ve que sea solo una
//        assertEquals("office2", acciones.get(0).getSrc()); //ve que sea en office2
//        assertTrue(acciones.get(0).getEncendido()); //ve si la prende
//    }
//
//    @Test
//    void debeMantenerApagadoCuandoTemperaturaAlta() {
//
//        //no deberia hacer nada si la temp es alta y el switch ya esta apagado
//        DataSensor medicionCalor = new DataSensor("shellyhtg3-84fce63ad204", 28.0f, 200001, true);
//        EstadoSistema apagado = controller.obtenerEstadoActual();
//
//        EstadoHabitacion hab = apagado.getHabitaciones().stream()
//                .filter(h -> h.getHabitacion().getName().equals("shellyhtg3-84fce63ad204"))
//                .findFirst().get();
//        hab.setSwitchEncendido(false); // aseguramos que esta apagado
//
//        List<Operacion> acciones = controller.accionHabitacion(medicionCalor, apagado);
//
//        assertTrue(acciones.isEmpty());
//    }
//
////    @Test
////    void debeOptimizarAccionesCuandoHayLimitacion() {
////        List<Operacion> acciones = controller.accionHabitacion(controller.obtenerUltimaMedicion(),controller.obtenerEstadoActual());
////
////        assertNotNull(accionesOptimizadas);
////        assertTrue(accionesOptimizadas.size() <= accionesOriginales.size());
////    }
//
//    @Test
//    void debeCagarConfiguracionCorrectamente() {
//        assertDoesNotThrow(() -> {
//            controller.cargarConfiguracion("src/test/resources/site_config.json");
//        });
//        EstadoSistema estado = controller.obtenerEstadoActual();
//        assertNotNull(estado);
//        assertTrue(estado.getConsumoMaximo() > 0);
//    }
//
//}
