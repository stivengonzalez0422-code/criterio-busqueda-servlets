package co.edu.sena.ofertas.web;

import co.edu.sena.comun.Textos;
import co.edu.sena.criterios.dao.CriterioBusquedaDao;
import co.edu.sena.criterios.model.CriterioBusqueda;
import co.edu.sena.ofertas.dao.OfertaDao;
import co.edu.sena.seguridad.SesionUsuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Controlador del módulo de ofertas. Recibe el identificador de un criterio del usuario
 * autenticado y muestra los vuelos, hospedajes y restaurantes que lo cumplen.
 */
@WebServlet(name = "ofertaServlet", urlPatterns = "/ofertas/*")
public class OfertaServlet extends HttpServlet {
    private static final String RESULTADOS_VIEW = "/WEB-INF/views/ofertas/resultados.jsp";
    private static final String ERROR_VIEW = "/WEB-INF/views/error.jsp";

    private final CriterioBusquedaDao criterioDao = new CriterioBusquedaDao();
    private final OfertaDao ofertaDao = new OfertaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String ruta = request.getPathInfo();
        if (ruta == null || "/".equals(ruta)) {
            response.sendRedirect(request.getContextPath() + "/criterios");
            return;
        }
        if (!"/criterio".equals(ruta)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            mostrarResultados(request, response);
        } catch (SQLException exception) {
            getServletContext().log("Error de base de datos al buscar ofertas", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("errorGeneral", "No fue posible consultar las ofertas.");
            request.getRequestDispatcher(ERROR_VIEW).forward(request, response);
        }
    }

    private void mostrarResultados(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Optional<Integer> id = Textos.entero(request.getParameter("id"));
        if (id.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El identificador no es válido.");
            return;
        }
        Optional<CriterioBusqueda> criterio =
                criterioDao.buscarPorIdYUsuario(id.get(), SesionUsuario.idActual(request));
        if (criterio.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("criterio", criterio.get());
        request.setAttribute("vuelos", ofertaDao.buscarVuelos(criterio.get()));
        request.setAttribute("hospedajes", ofertaDao.buscarHospedajes(criterio.get()));
        request.setAttribute("restaurantes", ofertaDao.buscarRestaurantes(criterio.get()));
        request.getRequestDispatcher(RESULTADOS_VIEW).forward(request, response);
    }
}
