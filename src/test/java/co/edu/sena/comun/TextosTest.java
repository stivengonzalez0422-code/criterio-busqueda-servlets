package co.edu.sena.comun;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextosTest {

    @Test
    void recortarONuloDevuelveNuloParaTextoVacio() {
        assertNull(Textos.recortarONulo(null));
        assertNull(Textos.recortarONulo("   "));
        assertEquals("Cali", Textos.recortarONulo("  Cali "));
    }

    @Test
    void enteroSoloAceptaNumerosPositivos() {
        assertEquals(7, Textos.entero(" 7 ").orElseThrow());
        assertTrue(Textos.entero("0").isEmpty());
        assertTrue(Textos.entero("-3").isEmpty());
        assertTrue(Textos.entero("abc").isEmpty());
        assertTrue(Textos.entero(null).isEmpty());
    }

    @Test
    void escaparLikeNeutralizaComodines() {
        assertEquals("100!%", Textos.escaparLike("100%"));
        assertEquals("a!_b", Textos.escaparLike("a_b"));
        assertEquals("!!x", Textos.escaparLike("!x"));
    }
}
