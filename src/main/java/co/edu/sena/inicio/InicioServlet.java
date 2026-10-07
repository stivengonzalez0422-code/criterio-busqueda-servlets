package co.edu.sena.inicio;

import co.edu.sena.criterios.dao.CriterioBusquedaDao;
import co.edu.sena.seguridad.SesionUsuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Página de entrada. La raíz del sitio redirige a /inicio (si hay sesión) o a /login;
 * /inicio es el panel que enlaza los módulos de la aplicación.
 */
@WebServlet(name = "inicioServlet", urlPatterns = {"", "/inicio"})
public class InicioServlet extends HttpServlet {
    private final CriterioBusquedaDao criterioDao = new CriterioBusquedaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean autenticado = SesionUsuario.actual(request).isPresent();
        if (!"/inicio".equals(request.getServletPath())) {
            response.sendRedirect(request.getContextPath() + (autenticado ? "/inicio" : "/login"));
            return;
        }
        try {
            int total = criterioDao.buscarPorUsuario(SesionUsuario.idActual(request)).size();
            request.setAttribute("totalCriterios", total);
        } catch (SQLException exception) {
            getServletContext().log("No se pudo contar los criterios del usuario", exception);
        }
        request.getRequestDispatcher("/WEB-INF/views/inicio.jsp").forward(request, response);
    }
}
