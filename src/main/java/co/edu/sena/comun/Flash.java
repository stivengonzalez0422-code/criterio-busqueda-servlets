package co.edu.sena.comun;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Mensaje de una sola lectura que sobrevive a una redirección (patrón Post/Redirect/Get). */
public final class Flash {
    private static final String ATRIBUTO = "mensaje";

    private Flash() {
    }

    public static void guardar(HttpServletRequest request, String mensaje) {
        request.getSession().setAttribute(ATRIBUTO, mensaje);
    }

    /** Pasa el mensaje pendiente al request para que la vista lo muestre y lo elimina de la sesión. */
    public static void consumir(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object mensaje = session.getAttribute(ATRIBUTO);
        if (mensaje != null) {
            request.setAttribute(ATRIBUTO, mensaje);
            session.removeAttribute(ATRIBUTO);
        }
    }
}
