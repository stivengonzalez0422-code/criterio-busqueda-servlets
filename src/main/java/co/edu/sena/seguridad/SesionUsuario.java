package co.edu.sena.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

/** Operaciones sobre la sesión: iniciar, cerrar, consultar el usuario y el token CSRF. */
public final class SesionUsuario {
    public static final String USUARIO = "usuarioSesion";
    public static final String CSRF = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private SesionUsuario() {
    }

    public static Optional<UsuarioSesion> actual(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }
        Object valor = session.getAttribute(USUARIO);
        return valor instanceof UsuarioSesion usuario ? Optional.of(usuario) : Optional.empty();
    }

    /** Debe llamarse solo desde zonas protegidas por AutenticacionFilter. */
    public static int idActual(HttpServletRequest request) {
        return actual(request)
                .orElseThrow(() -> new IllegalStateException("No hay un usuario autenticado."))
                .getIdUsuario();
    }

    /** Cambia el identificador de sesión y el token CSRF al iniciar sesión (evita fijación de sesión). */
    public static void iniciar(HttpServletRequest request, UsuarioSesion usuario) {
        HttpSession session = request.getSession(true);
        request.changeSessionId();
        session.setAttribute(USUARIO, usuario);
        session.setAttribute(CSRF, nuevoToken());
    }

    public static void cerrar(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public static String nuevoToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
