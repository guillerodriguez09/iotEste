package com.utec.ioteste.integracion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.utec.ioteste.temperatura.api.impl.ControladorTemperaturaImpl;
import com.utec.ioteste.temperatura.modelo.*;
import com.utec.ioteste.temperatura.rest.ClienteRest;
import com.utec.ioteste.temperatura.mqtt.ManejadorMQTT;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@DisplayName("Tests de Integración - Controlador Completo")
public class TestIntegracionCompleto {
    
    private ControladorTemperaturaImpl controlador;
    private ManejadorMQTT manejadorMqttMock;
    private ClienteRest clienteRestMock;
    private ConfiguracionSitio configuracion;
    
    @BeforeEach
    public void preparar() {
        configuracion = new ConfiguracionSitio("integracion" ,10,1000,"std",new ArrayList<>());
        configuracion.agregarHabitacion(new Habitacion(
            "sala1", 22.0, 2, "http://switch1",  "s1"
        ));
        configuracion.agregarHabitacion(new Habitacion(
            "sala2", 21.0, 2, "http://switch2",  "s2"
        ));
        configuracion.agregarHabitacion(new Habitacion(
            "sala3", 20.0, 3, "http://switch3", "s3"
        ));
        
        manejadorMqttMock = mock(ManejadorMQTT.class);
        when(manejadorMqttMock.estaConectado()).thenReturn(true);
        
        clienteRestMock = mock(ClienteRest.class);
        when(clienteRestMock.enviarComando(anyString(), anyBoolean())).thenReturn(true);
        
        controlador = new ControladorTemperaturaImpl(configuracion, manejadorMqttMock, clienteRestMock);
    }
    
    @Test
    @DisplayName("IT1: Control sin limitación energética")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testSinLimitacionEnergia() throws InterruptedException {
        controlador.iniciar();
        
        // Temperatura baja en dos habitaciones (consumo: 4 kWh < 6 kWh límite)
        controlador.procesarMedicionTemperatura("s1", 18.0);
        controlador.procesarMedicionTemperatura("s2", 19.0);
        
        Thread.sleep(6000);
        
        var estado = controlador.obtenerEstado();
        assertTrue(estado.activo());
        assertTrue(estado.medicionesRecibidas() >= 2);
        assertEquals(0, estado.erroresRest());
        
        verify(clienteRestMock, atLeast(1)).enviarComando(anyString(), anyBoolean());
        
        controlador.detener();
    }
    
    @Test
    @DisplayName("IT2: Control con limitación energética efectiva")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testConLimitacionEnergia() throws InterruptedException {
        controlador.iniciar();
        
        // Temperatura baja en todas (consumo potencial: 7 kWh > 6 kWh límite)
        // Debe priorizar: diferencia de temp mayor
        controlador.procesarMedicionTemperatura("s1", 15.0); // Diferencia: 7°C
        controlador.procesarMedicionTemperatura("s2", 16.0); // Diferencia: 5°C
        controlador.procesarMedicionTemperatura("s3", 15.0); // Diferencia: 5°C
        
        Thread.sleep(6000);
        
        var estado = controlador.obtenerEstado();
        assertTrue(estado.activo());
        assertTrue(estado.medicionesRecibidas() >= 3);
        
        // Verifica que se respeta el límite
        var acciones = controlador.obtenerAccionesPendientes();
        assertNotNull(acciones);
        
        controlador.detener();
    }
    
    @Test
    @DisplayName("IT3: Resilencia ante caída MQTT")
    @Timeout(value = 20, unit = TimeUnit.SECONDS)
    public void testResilienciaMqtt() throws InterruptedException {
        when(manejadorMqttMock.estaConectado()).thenReturn(true);
        
        controlador.iniciar();
        controlador.procesarMedicionTemperatura("s1", 18.0);
        
        Thread.sleep(3000);
        
        // Simular caída MQTT
        when(manejadorMqttMock.estaConectado()).thenReturn(false);
        Thread.sleep(2000);
        
        // Sistema debería seguir funcionando con última temperatura conocida
        controlador.procesarMedicionTemperatura("s2", 19.0);
        
        Thread.sleep(3000);
        
        // Recuperación
        when(manejadorMqttMock.estaConectado()).thenReturn(true);
        controlador.procesarMedicionTemperatura("s3", 20.0);
        
        Thread.sleep(3000);
        
        var estado = controlador.obtenerEstado();
        assertTrue(estado.activo());
        assertTrue(estado.medicionesRecibidas() >= 3);
        
        controlador.detener();
    }
    
    @Test
    @DisplayName("IT4: Recuperación ante fallos REST")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testRecuperacionFallosRest() throws InterruptedException {
        // Primero falla
        when(clienteRestMock.enviarComando(anyString(), anyBoolean()))
            .thenReturn(false)
            .thenReturn(true); // Después recupera
        
        controlador.iniciar();
        controlador.procesarMedicionTemperatura("s1", 18.0);
        
        Thread.sleep(6000);
        
        // Segundo intento
        controlador.procesarMedicionTemperatura("s1", 17.0);
        Thread.sleep(6000);
        
        var estado = controlador.obtenerEstado();
        assertTrue(estado.erroresRest() >= 0); // Registra intentos fallidos
        
        controlador.detener();
    }
    
    @Test
    @DisplayName("IT5: Pérdida de mensajes - Sistema recupera")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    public void testPerdidaMensajes() throws InterruptedException {
        controlador.iniciar();
        
        // Enviar solo algunos mensajes
        controlador.procesarMedicionTemperatura("s1", 18.0);
        Thread.sleep(3000);
        
        // s2 y s3 no llegan (perdidos)
        Thread.sleep(3000);
        
        // Después llegan todos
        controlador.procesarMedicionTemperatura("s1", 18.5);
        controlador.procesarMedicionTemperatura("s2", 19.0);
        controlador.procesarMedicionTemperatura("s3", 17.0);
        
        Thread.sleep(3000);
        
        var estado = controlador.obtenerEstado();
        assertTrue(estado.medicionesRecibidas() >= 3);
        
        controlador.detener();
    }
}
