package co.edu.sena.criterios.validation;

import co.edu.sena.criterios.model.CriterioBusqueda;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CriterioValidatorTest {
    private final CriterioValidator validator = new CriterioValidator();

    private Map<String, String> datosValidos() {
        Map<String, String> datos = new HashMap<>();
        datos.put("destino", "  Cali  ");
        datos.put("fecha_inicio", "2026-12-01");
        datos.put("fecha_fin", "2026-12-10");
        datos.put("precio_maximo", "350000.50");
        datos.put("horario", " Mañana ");
        datos.put("preferencias", "  Equipaje incluido ");
        datos.put("estado", "true");
        return datos;
    }

    @Test
    void aceptaUnFormularioValidoYRecortaLosTextos() {
        CriterioBusqueda criterio = new CriterioBusqueda();
        Map<String, String> errores = validator.validate(criterio, datosValidos());

        assertTrue(errores.isEmpty());
        assertEquals("Cali", criterio.getDestino());
        assertEquals(new BigDecimal("350000.50"), criterio.getPrecioMaximo());
        assertEquals("Mañana", criterio.getHorario());
        assertEquals("Equipaje incluido", criterio.getPreferencias());
        assertTrue(criterio.isEstado());
    }

    @Test
    void rechazaFechasInvertidasYPrecioNegativo() {
        Map<String, String> datos = datosValidos();
        datos.put("fecha_inicio", "2026-12-10");
        datos.put("fecha_fin", "2026-12-01");
        datos.put("precio_maximo", "-1.00");

        Map<String, String> errores = validator.validate(new CriterioBusqueda(), datos);

        assertTrue(errores.containsKey("fecha_fin"));
        assertTrue(errores.containsKey("precio_maximo"));
    }

    @Test
    void exigeDestinoYFechas() {
        Map<String, String> errores = validator.validate(new CriterioBusqueda(), new HashMap<>());

        assertTrue(errores.containsKey("destino"));
        assertTrue(errores.containsKey("fecha_inicio"));
        assertTrue(errores.containsKey("fecha_fin"));
        assertTrue(errores.containsKey("precio_maximo"));
    }

    @Test
    void rechazaMasDeDosDecimalesYDestinosLargos() {
        Map<String, String> datos = datosValidos();
        datos.put("destino", "D".repeat(101));
        datos.put("precio_maximo", "100.001");

        Map<String, String> errores = validator.validate(new CriterioBusqueda(), datos);

        assertTrue(errores.containsKey("destino"));
        assertTrue(errores.containsKey("precio_maximo"));
    }

    @Test
    void aceptaCerosFinalesYNormalizaAdosDecimales() {
        Map<String, String> datos = datosValidos();
        datos.put("precio_maximo", "100.0000");
        CriterioBusqueda criterio = new CriterioBusqueda();

        assertTrue(validator.validate(criterio, datos).isEmpty());
        assertEquals(new BigDecimal("100.00"), criterio.getPrecioMaximo());
    }

    @Test
    void elCriterioQuedaInactivoSiNoSeEnviaLaCasilla() {
        Map<String, String> datos = datosValidos();
        datos.remove("estado");
        CriterioBusqueda criterio = new CriterioBusqueda();

        validator.validate(criterio, datos);

        assertFalse(criterio.isEstado());
    }
}
