package co.edu.sena.seguridad;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** Exige una sesión iniciada para entrar a inicio, criterios y ofertas. */
@WebFilter(filterName = "autenticacionFilter", urlPatterns = {"/inicio", "/criterios/*", "/ofertas/*"})
public class AutenticacionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (SesionUsuario.actual(http).isEmpty()) {
            httpResponse.sendRedirect(http.getContextPath() + "/login");
            return;
        }
        // Evita que el botón "atrás" muestre páginas privadas después de cerrar sesión.
        httpResponse.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }
}
