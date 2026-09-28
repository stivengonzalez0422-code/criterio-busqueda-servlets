package co.edu.sena.criterios.validation;

import co.edu.sena.criterios.model.CriterioBusqueda;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CriterioValidatorTest {
    private final CriterioValidator validator = new CriterioValidator();

    @Test
    void acceptsValidFormAndTrimsOptionalValues() {
        CriterioBusqueda criterio = new CriterioBusqueda();
        Map<String, String> errors = validator.validate(criterio, Map.of(
                "id_usuario", "12",
                "destino", "  Cali  ",
                "fecha_inicio", "2026-12-01",
                "fecha_fin", "2026-12-10",
                "precio_maximo", "350000.50",
                "horario", " Mañana ",
                "preferencias", "  Equipaje incluido ",
                "estado", "true"));

        assertTrue(errors.isEmpty());
        assertEquals(12, criterio.getIdUsuario());
        assertEquals("Cali", criterio.getDestino());
        assertEquals(new BigDecimal("350000.50"), criterio.getPrecioMaximo());
        assertEquals("Mañana", criterio.getHorario());
        assertEquals("Equipaje incluido", criterio.getPreferencias());
        assertTrue(criterio.isEstado());
    }

    @Test
    void rejectsInvalidUserDateOrderAndPrice() {
        CriterioBusqueda criterio = new CriterioBusqueda();
        Map<String, String> errors = validator.validate(criterio, Map.of(
                "id_usuario", "0",
                "destino", "Bogotá",
                "fecha_inicio", "2026-12-10",
                "fecha_fin", "2026-12-01",
                "precio_maximo", "-1.00"));

        assertTrue(errors.containsKey("id_usuario"));
        assertTrue(errors.containsKey("fecha_fin"));
        assertTrue(errors.containsKey("precio_maximo"));
    }

    @Test
    void rejectsPriceWithMoreThanTwoDecimalsAndLongDestination() {
        CriterioBusqueda criterio = new CriterioBusqueda();
        Map<String, String> errors = validator.validate(criterio, Map.of(
                "id_usuario", "2",
                "destino", "D".repeat(101),
                "fecha_inicio", "2026-12-01",
                "fecha_fin", "2026-12-02",
                "precio_maximo", "100.001"));

        assertTrue(errors.containsKey("destino"));
        assertTrue(errors.containsKey("precio_maximo"));
    }

    @Test
    void leavesCriteriaInactiveWhenCheckboxIsNotSubmitted() {
        CriterioBusqueda criterio = new CriterioBusqueda();
        validator.validate(criterio, Map.of(
                "id_usuario", "2",
                "destino", "Pasto",
                "fecha_inicio", "2026-12-01",
                "fecha_fin", "2026-12-02",
                "precio_maximo", "100"));

        assertFalse(criterio.isEstado());
    }
}
