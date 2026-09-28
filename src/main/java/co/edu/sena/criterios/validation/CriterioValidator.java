package co.edu.sena.criterios.validation;

import co.edu.sena.criterios.model.CriterioBusqueda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CriterioValidator {
    private static final BigDecimal MAX_PRICE = new BigDecimal("9999999999.99");
    private static final int MAX_PREFERENCES_LENGTH = 16_383;

    public Map<String, String> validate(CriterioBusqueda criterio, Map<String, String> values) {
        Map<String, String> errors = new LinkedHashMap<>();
        parseUserId(values.get("id_usuario"), criterio, errors);
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
        parseOptionalText(values.get("horario"), "horario", 100,
                "El horario no puede superar los 100 caracteres.", criterio, errors);
        String preferencias = trimToNull(values.get("preferencias"));
        if (preferencias != null && preferencias.length() > MAX_PREFERENCES_LENGTH) {
            errors.put("preferencias", "Las preferencias superan el tamaño permitido.");
        }
        criterio.setPreferencias(preferencias);
        criterio.setEstado("true".equals(values.get("estado")));
        return errors;
    }

    private void parseUserId(String value, CriterioBusqueda criterio, Map<String, String> errors) {
        try {
            int id = Integer.parseInt(value == null ? "" : value.trim());
            if (id < 1) {
                throw new NumberFormatException();
            }
            criterio.setIdUsuario(id);
        } catch (NumberFormatException exception) {
            errors.put("id_usuario", "Ingrese un identificador de usuario válido.");
        }
    }

    private void parseDestination(String value, CriterioBusqueda criterio, Map<String, String> errors) {
        String destination = trimToNull(value);
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
            if (price.signum() < 0 || price.compareTo(MAX_PRICE) > 0 || price.scale() > 2) {
                errors.put("precio_maximo", "El precio debe estar entre 0 y 9.999.999.999,99 y tener máximo dos decimales.");
            } else {
                criterio.setPrecioMaximo(price);
            }
        } catch (NumberFormatException exception) {
            errors.put("precio_maximo", "Ingrese un precio numérico válido.");
        }
    }

    private void parseOptionalText(
            String value,
            String field,
            int maxLength,
            String error,
            CriterioBusqueda criterio,
            Map<String, String> errors) {
        String text = trimToNull(value);
        if (text != null && text.length() > maxLength) {
            errors.put(field, error);
        }
        if ("horario".equals(field)) {
            criterio.setHorario(text);
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
