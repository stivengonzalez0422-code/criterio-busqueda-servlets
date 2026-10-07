package co.edu.sena.ofertas.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VueloTest {

    @Test
    void formateaLaDuracionEnHorasYMinutos() {
        Vuelo vuelo = new Vuelo();

        vuelo.setDuracion(45);
        assertEquals("45 min", vuelo.getDuracionTexto());
        vuelo.setDuracion(60);
        assertEquals("1 h", vuelo.getDuracionTexto());
        vuelo.setDuracion(95);
        assertEquals("1 h 35 min", vuelo.getDuracionTexto());
    }

    @Test
    void formateaLasFechas() {
        Vuelo vuelo = new Vuelo();
        vuelo.setFechaSalida(LocalDateTime.of(2026, 12, 10, 7, 5));

        assertEquals("10/12/2026 07:05", vuelo.getFechaSalidaTexto());
        assertEquals("", vuelo.getFechaLlegadaTexto());
    }
}
