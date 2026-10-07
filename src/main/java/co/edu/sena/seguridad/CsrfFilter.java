package co.edu.sena.seguridad;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Filtro transversal: fija UTF-8, añade cabeceras de seguridad y valida el token CSRF
 * de toda solicitud POST. Las vistas incluyen el token en el campo oculto "_csrf".
 */
@WebFilter(filterName = "csrfFilter", urlPatterns = "/*")
public class CsrfFilter implements Filter {
    public static final String PARAMETRO = "_csrf";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Debe hacerse antes de leer cualquier parámetro del formulario.
        request.setCharacterEncoding("UTF-8");
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("Referrer-Policy", "same-origin");

        if (http.getServletPath().startsWith("/assets/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = http.getSession(true);
        if (session.getAttribute(SesionUsuario.CSRF) == null) {
            session.setAttribute(SesionUsuario.CSRF, SesionUsuario.nuevoToken());
        }

        if ("POST".equalsIgnoreCase(http.getMethod()) && !tokenValido(http, session)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "La solicitud no es válida o la sesión expiró. Vuelva a cargar la página.");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean tokenValido(HttpServletRequest request, HttpSession session) {
        Object esperado = session.getAttribute(SesionUsuario.CSRF);
        String recibido = request.getParameter(PARAMETRO);
        if (!(esperado instanceof String token) || recibido == null) {
            return false;
        }
        return MessageDigest.isEqual(
                token.getBytes(StandardCharsets.UTF_8), recibido.getBytes(StandardCharsets.UTF_8));
    }
}
