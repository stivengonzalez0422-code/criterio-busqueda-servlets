package co.edu.sena.criterios.web;

import co.edu.sena.comun.Flash;
import co.edu.sena.comun.Textos;
import co.edu.sena.criterios.dao.CriterioBusquedaDao;
import co.edu.sena.criterios.model.CriterioBusqueda;
import co.edu.sena.criterios.validation.CriterioValidator;
import co.edu.sena.seguridad.SesionUsuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador del módulo de criterios. GET muestra listado, detalle y formularios;
 * POST crea, actualiza y elimina. Solo se opera sobre criterios del usuario autenticado.
 */
@WebServlet(name = "criterioBusquedaServlet", urlPatterns = "/criterios/*")
public class CriterioBusquedaServlet extends HttpServlet {
    private static final String LIST_VIEW = "/WEB-INF/views/criterios/list.jsp";
    private static final String FORM_VIEW = "/WEB-INF/views/criterios/form.jsp";
    private static final String DETAIL_VIEW = "/WEB-INF/views/criterios/detail.jsp";
    private static final String ERROR_VIEW = "/WEB-INF/views/error.jsp";

    private final CriterioBusquedaDao dao = new CriterioBusquedaDao();
    private final CriterioValidator validator = new CriterioValidator();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = route(request);
        try {
            switch (path) {
                case "/", "/lista" -> showList(request, response);
                case "/nuevo" -> showForm(request, response, new CriterioBusqueda(), "crear", false);
                case "/ver" -> showDetail(request, response);
                case "/editar" -> showEditForm(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException exception) {
            databaseFailure("No fue posible consultar los criterios.", exception, request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = route(request);
        try {
            switch (path) {
                case "/crear" -> create(request, response);
                case "/actualizar" -> update(request, response);
                case "/eliminar" -> delete(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException exception) {
            databaseFailure("No fue posible guardar el cambio. Verifique la conexión y los datos.", exception,
                    request, response);
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        request.setAttribute("criterios", dao.buscarPorUsuario(SesionUsuario.idActual(request)));
        Flash.consumir(request);
        request.getRequestDispatcher(LIST_VIEW).forward(request, response);
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response,
                          CriterioBusqueda criterio, String action, boolean editing)
            throws ServletException, IOException {
        request.setAttribute("criterio", criterio);
        request.setAttribute("action", action);
        request.setAttribute("editing", editing);
        request.getRequestDispatcher(FORM_VIEW).forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Optional<CriterioBusqueda> criterio = findOwned(request, response, "id");
        if (criterio.isPresent()) {
            showForm(request, response, criterio.get(), "actualizar", true);
        }
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Optional<CriterioBusqueda> criterio = findOwned(request, response, "id");
        if (criterio.isPresent()) {
            request.setAttribute("criterio", criterio.get());
            request.getRequestDispatcher(DETAIL_VIEW).forward(request, response);
        }
    }

    private void create(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        CriterioBusqueda criterio = new CriterioBusqueda();
        criterio.setIdUsuario(SesionUsuario.idActual(request));
        Map<String, String> errors = validator.validate(criterio, formValues(request));
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            showForm(request, response, criterio, "crear", false);
            return;
        }
        dao.insertar(criterio);
        Flash.guardar(request, "El criterio se creó correctamente.");
        response.sendRedirect(request.getContextPath() + "/criterios");
    }

    private void update(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Optional<CriterioBusqueda> existing = findOwned(request, response, "id_criterio");
        if (existing.isEmpty()) {
            return;
        }
        CriterioBusqueda criterio = existing.get();
        Map<String, String> errors = validator.validate(criterio, formValues(request));
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            showForm(request, response, criterio, "actualizar", true);
            return;
        }
        if (!dao.actualizar(criterio)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Flash.guardar(request, "El criterio se actualizó correctamente.");
        response.sendRedirect(request.getContextPath() + "/criterios");
    }

    private void delete(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        Optional<Integer> id = Textos.entero(request.getParameter("id_criterio"));
        if (id.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El identificador no es válido.");
            return;
        }
        if (!dao.eliminar(id.get(), SesionUsuario.idActual(request))) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Flash.guardar(request, "El criterio se eliminó correctamente.");
        response.sendRedirect(request.getContextPath() + "/criterios");
    }

    // Responde 400 o 404 y devuelve vacío cuando el identificador no es válido o el criterio no es del usuario.
    private Optional<CriterioBusqueda> findOwned(HttpServletRequest request, HttpServletResponse response,
                                                 String parameter) throws SQLException, IOException {
        Optional<Integer> id = Textos.entero(request.getParameter(parameter));
        if (id.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El identificador no es válido.");
            return Optional.empty();
        }
        Optional<CriterioBusqueda> criterio = dao.buscarPorIdYUsuario(id.get(), SesionUsuario.idActual(request));
        if (criterio.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return criterio;
    }

    private Map<String, String> formValues(HttpServletRequest request) {
        return Map.of(
                "destino", Textos.parametro(request, "destino"),
                "fecha_inicio", Textos.parametro(request, "fecha_inicio"),
                "fecha_fin", Textos.parametro(request, "fecha_fin"),
                "precio_maximo", Textos.parametro(request, "precio_maximo"),
                "horario", Textos.parametro(request, "horario"),
                "preferencias", Textos.parametro(request, "preferencias"),
                "estado", Textos.parametro(request, "estado"));
    }

    private String route(HttpServletRequest request) {
        String path = request.getPathInfo();
        return path == null || path.isBlank() ? "/" : path;
    }

    private void databaseFailure(String message, SQLException exception, HttpServletRequest request,
                                 HttpServletResponse response) throws ServletException, IOException {
        getServletContext().log(message, exception);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        request.setAttribute("errorGeneral", message);
        request.getRequestDispatcher(ERROR_VIEW).forward(request, response);
    }
}
