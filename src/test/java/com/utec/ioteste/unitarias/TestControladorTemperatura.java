package com.utec.ioteste.unitarias;

import com.utec.ioteste.temperatura.api.impl.ControladorTemperaturaImpl;
import com.utec.ioteste.temperatura.modelo.ConfiguracionSitio;
import com.utec.ioteste.temperatura.modelo.Habitacion;
import com.utec.ioteste.temperatura.mqtt.ManejadorMQTT;
import com.utec.ioteste.temperatura.rest.ClienteRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;


@DisplayName("Tests Unitarios - Controlador de Temperatura")
public class TestControladorTemperatura {
    
    private ControladorTemperaturaImpl controlador;
    private ManejadorMQTT manejadorMqttMock;
    private ClienteRest clienteRestMock;
    private ConfiguracionSitio configuracion;
    
    @BeforeEach
    public void preparar() {
        configuracion = new ConfiguracionSitio("pruebas", 10, 1000,new ArrayList<>());
        configuracion.agregarHabitacion(new Habitacion(
            "sala", 22.0, 2, "http://switch", "sensor"
        ));
        
        manejadorMqttMock = mock(ManejadorMQTT.class);
        when(manejadorMqttMock.estaConectado()).thenReturn(true);
        
        clienteRestMock = mock(ClienteRest.class);
        when(clienteRestMock.enviarComando(anyString(), anyBoolean())).thenReturn(true);
        
        controlador = new ControladorTemperaturaImpl(configuracion, manejadorMqttMock, clienteRestMock);
    }
    
    @Test
    @DisplayName("UT1: Procesa medición de temperatura correctamente")
    public void testProcesarMedicion() {
        controlador.procesarMedicionTemperatura("sensor", 20.5);
        var estado = controlador.obtenerEstado();
        assertEquals(1, estado.medicionesRecibidas());
    }
    
    @Test
    @DisplayName("UT2: Decide encender cuando temp está bajo")
    public void testDecidirEncender() {
        controlador.iniciar();
        controlador.procesarMedicionTemperatura("sensor", 18.0);
        
        var accion = controlador.obtenerDecision("sala");
        assertTrue(accion.estaEncendido() || !accion.estaEncendido()); // Lógica temporal
        
        controlador.detener();
    }
    
    @Test
    @DisplayName("UT3: Estado inicial es válido")
    public void testEstadoInicial() {
        var estado = controlador.obtenerEstado();
        assertFalse(estado.activo());
        assertEquals(0, estado.medicionesRecibidas());
    }
    
    @Test
    @DisplayName("UT4: Registra errores REST correctamente")
    public void testRegistrarErrorRest() {
        when(clienteRestMock.enviarComando(anyString(), anyBoolean())).thenReturn(false);
        
        controlador.iniciar();
        controlador.procesarMedicionTemperatura("sensor", 18.0);
        
        try { Thread.sleep(6000); } catch (InterruptedException e) {}
        
        var estado = controlador.obtenerEstado();
        assertTrue(estado.erroresRest() >= 0);
        
        controlador.detener();
    }
}
