package com.utec.ioteste.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.utec.ioteste.temperatura.api.impl.ControladorTemperaturaImpl;
import com.utec.ioteste.temperatura.modelo.*;
import com.utec.ioteste.temperatura.rest.ClienteRest;
import com.utec.ioteste.temperatura.mqtt.ManejadorMQTT;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas del Controlador de Temperatura (versión IoTEste)")
public class TemperaturaControlTest {

    private ControladorTemperaturaImpl controlador;
    private ConfiguracionSitio configuracion;
    private Habitacion oficina1;
    private Habitacion oficina2;

    private ManejadorMQTT mqttMock;
    private ClienteRest restMock;

    @BeforeEach
    void setUp() {
        mqttMock = mock(ManejadorMQTT.class);
        restMock = mock(ClienteRest.class);

        // Crear habitaciones de prueba
        oficina1 = new Habitacion("oficina1", 22.0, 2000, "http://localhost:8080/switch/1", "mqtt:topic1");
        oficina2 = new Habitacion("oficina2", 21.0, 2000, "http://localhost:8080/switch/2", "mqtt:topic2");

        List<Habitacion> habitaciones = Arrays.asList(oficina1, oficina2);
        configuracion = new ConfiguracionSitio("sitio-prueba", 4000, 10000, habitaciones);

        controlador = new ControladorTemperaturaImpl(configuracion, mqttMock, restMock);
    }

    @Test
    @DisplayName("Debe registrar correctamente una medición MQTT")
    void testProcesarMedicion() {
        controlador.procesarMedicionTemperatura("mqtt:topic1", 20.5);
        AccionTemperatura accion = controlador.obtenerDecision("oficina1");
        assertNotNull(accion, "Debe existir una acción registrada para oficina1 (aunque sea vacía)");
    }

    @Test
    @DisplayName("Debe crear una acción al ejecutar control de temperatura")
    void testEjecutarControlTemperaturaBasico() throws Exception {
        // Forzar temperatura baja
        controlador.procesarMedicionTemperatura("mqtt:topic1", 19.0);
        controlador.procesarMedicionTemperatura("mqtt:topic2", 19.5);

        when(restMock.enviarComando(anyString(), anyBoolean())).thenReturn(true);

        // Ejecutamos el ciclo privado a través de reflexión (para probarlo directamente)
        var metodo = ControladorTemperaturaImpl.class.getDeclaredMethod("ejecutarControlTemperatura");
        metodo.setAccessible(true);
        metodo.invoke(controlador);

        AccionTemperatura accion1 = controlador.obtenerDecision("oficina1");
        AccionTemperatura accion2 = controlador.obtenerDecision("oficina2");

        assertNotNull(accion1);
        assertNotNull(accion2);
        assertTrue(accion1.estaEncendido() || accion2.estaEncendido(), "Debe encender al menos un calefactor si hace frío");
    }

    @Test
    @DisplayName("Debe respetar el límite máximo de energía del sitio")
    void testLimiteEnergia() throws Exception {
        oficina1 = new Habitacion("oficina1", 22.0, 3000, "url1", "s1");
        oficina2 = new Habitacion("oficina2", 22.0, 3000, "url2", "s2");

        configuracion = new ConfiguracionSitio("sitio-prueba", 4000, 10000, Arrays.asList(oficina1, oficina2));
        controlador = new ControladorTemperaturaImpl(configuracion, mqttMock, restMock);

        controlador.procesarMedicionTemperatura("s1", 19.0);
        controlador.procesarMedicionTemperatura("s2", 19.0);

        when(restMock.enviarComando(anyString(), anyBoolean())).thenReturn(true);

        var metodo = ControladorTemperaturaImpl.class.getDeclaredMethod("ejecutarControlTemperatura");
        metodo.setAccessible(true);
        metodo.invoke(controlador);

        List<AccionTemperatura> acciones = controlador.obtenerAccionesPendientes();

        long encendidos = acciones.stream().filter(AccionTemperatura::estaEncendido).count();
        assertTrue(encendidos <= 1, "No deben encenderse más habitaciones que el límite energético permite");
    }

    @Test
    @DisplayName("Debe generar un estado del controlador con métricas básicas")
    void testObtenerEstado() {
        var estado = controlador.obtenerEstado();
        assertNotNull(estado, "Debe devolver un objeto de estado");
        assertFalse(estado.activo(), "El sistema debe estar inactivo antes de iniciar");
        assertEquals(0, estado.medicionesRecibidas(), "No debe haber mediciones al inicio");
    }

    @Test
    @DisplayName("Debe responder correctamente al iniciar y detener")
    void testIniciarYDetener() {
        controlador.iniciar();
        var estadoActivo = controlador.obtenerEstado();
        assertTrue(estadoActivo.activo(), "Debe estar activo después de iniciar");

        controlador.detener();
        var estadoFinal = controlador.obtenerEstado();
        assertFalse(estadoFinal.activo(), "Debe quedar inactivo después de detener");
    }
}
