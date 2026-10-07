package co.edu.sena.criterios.validation;

import co.edu.sena.comun.Textos;
import co.edu.sena.criterios.model.CriterioBusqueda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Valida el formulario de un criterio y llena el modelo con los valores normalizados.
 * El usuario dueño no viene del formulario: lo asigna el servlet desde la sesión.
 */
public class CriterioValidator {
    private static final BigDecimal MAX_PRICE = new BigDecimal("9999999999.99");
    private static final int MAX_PREFERENCES_LENGTH = 16_383;

    public Map<String, String> validate(CriterioBusqueda criterio, Map<String, String> values) {
        Map<String, String> errors = new LinkedHashMap<>();
        parseDestination(values.get("destino"), criterio, errors);
        LocalDate start = parseDate(values.get("fecha_inicio"), "fecha_inicio",
                "La fecha de inicio no es válida.", errors);
        LocalDate end = parseDate(values.get("fecha_fin"), "fecha_fin",
                "La fecha de fin no es válida.", errors);
        criterio.setFechaInicio(start);
        criterio.setFechaFin(end);
        if (start != null && end != null && end.isBefore(start)) {
            errors.put("fecha_fin", "La fecha de fin debe ser igual o posterior a la fecha de inicio.");
        }
        parsePrice(values.get("precio_maximo"), criterio, errors);
        parseSchedule(values.get("horario"), criterio, errors);
        String preferencias = Textos.recortarONulo(values.get("preferencias"));
        if (preferencias != null && preferencias.length() > MAX_PREFERENCES_LENGTH) {
            errors.put("preferencias", "Las preferencias superan el tamaño permitido.");
        }
        criterio.setPreferencias(preferencias);
        criterio.setEstado("true".equals(values.get("estado")));
        return errors;
    }

    private void parseDestination(String value, CriterioBusqueda criterio, Map<String, String> errors) {
        String destination = Textos.recortarONulo(value);
        if (destination == null) {
            errors.put("destino", "El destino es obligatorio.");
        } else if (destination.length() > 100) {
            errors.put("destino", "El destino no puede superar los 100 caracteres.");
        }
        criterio.setDestino(destination);
    }

    private LocalDate parseDate(String value, String field, String error, Map<String, String> errors) {
        try {
            if (value == null || value.isBlank()) {
                throw new DateTimeParseException("La fecha es obligatoria.", "", 0);
            }
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            errors.put(field, error);
            return null;
        }
    }

    private void parsePrice(String value, CriterioBusqueda criterio, Map<String, String> errors) {
        try {
            BigDecimal price = new BigDecimal(value == null ? "" : value.trim());
            if (price.signum() < 0 || price.compareTo(MAX_PRICE) > 0 || price.stripTrailingZeros().scale() > 2) {
                errors.put("precio_maximo",
                        "El precio debe estar entre 0 y 9.999.999.999,99 y tener máximo dos decimales.");
            } else {
                criterio.setPrecioMaximo(price.setScale(2));
            }
        } catch (NumberFormatException exception) {
            errors.put("precio_maximo", "Ingrese un precio numérico válido.");
        }
    }

    private void parseSchedule(String value, CriterioBusqueda criterio, Map<String, String> errors) {
        String schedule = Textos.recortarONulo(value);
        if (schedule != null && schedule.length() > 100) {
            errors.put("horario", "El horario no puede superar los 100 caracteres.");
        }
        criterio.setHorario(schedule);
    }
}
