package co.edu.sena.comun;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/** Utilidades pequeñas de texto y parámetros compartidas por los módulos. */
public final class Textos {
    private Textos() {
    }

    /** Recorta el texto; devuelve null si es nulo o está en blanco. */
    public static String recortarONulo(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    public static String parametro(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor;
    }

    public static Optional<Integer> entero(String valor) {
        try {
            int numero = Integer.parseInt(valor == null ? "" : valor.trim());
            return numero > 0 ? Optional.of(numero) : Optional.empty();
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    /** Escapa %, _ y el propio carácter de escape para usar el texto dentro de un LIKE ... ESCAPE '!'. */
    public static String escaparLike(String texto) {
        return texto.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }
}
