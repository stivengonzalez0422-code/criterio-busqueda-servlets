package co.edu.sena.criterios.web;

import co.edu.sena.criterios.dao.CriterioBusquedaDao;
import co.edu.sena.criterios.model.CriterioBusqueda;
import co.edu.sena.criterios.validation.CriterioValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

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
        request.setCharacterEncoding("UTF-8");
        String path = route(request);
        try {
            switch (path) {
                case "/crear" -> create(request, response);
                case "/actualizar" -> update(request, response);
                case "/eliminar" -> delete(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException exception) {
            databaseFailure("No fue posible guardar el cambio. Verifique la conexión y los datos relacionados.", exception, request, response);
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        request.setAttribute("criterios", dao.findAll());
        Object flash = request.getSession().getAttribute("mensaje");
        if (flash != null) {
            request.setAttribute("mensaje", flash);
            request.getSession().removeAttribute("mensaje");
        }
        request.getRequestDispatcher(LIST_VIEW).forward(request, response);
    }

    private void showForm(
            HttpServletRequest request,
            HttpServletResponse response,
            CriterioBusqueda criterio,
            String action,
            boolean editing) throws ServletException, IOException {
        request.setAttribute("criterio", criterio);
        request.setAttribute("action", action);
        request.setAttribute("editing", editing);
        request.getRequestDispatcher(FORM_VIEW).forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Optional<Integer> id = parseId(request.getParameter("id"));
        if (id.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El identificador no es válido.");
            return;
        }
        Optional<CriterioBusqueda> criterio = dao.findById(id.get());
        if (criterio.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        showForm(request, response, criterio.get(), "actualizar", true);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Optional<Integer> id = parseId(request.getParameter("id"));
        if (id.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El identificador no es válido.");
            return;
        }
        Optional<CriterioBusqueda> criterio = dao.findById(id.get());
        if (criterio.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("criterio", criterio.get());
        request.getRequestDispatcher(DETAIL_VIEW).forward(request, response);
    }

    private void create(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        CriterioBusqueda criterio = new CriterioBusqueda();
        Map<String, String> errors = validator.validate(criterio, formValues(request));
        if (!errors.containsKey("id_usuario") && !dao.userExists(criterio.getIdUsuario())) {
            errors.put("id_usuario", "El usuario ingresado no existe. Regístrelo desde el proceso correspondiente.");
        }
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            showForm(request, response, criterio, "crear", false);
            return;
        }
        dao.insert(criterio);
        request.getSession().setAttribute("mensaje", "El criterio se creó correctamente.");
        response.sendRedirect(request.getContextPath() + "/criterios");
    }

    private void update(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Optional<Integer> id = parseId(request.getParameter("id_criterio"));
        if (id.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El identificador no es válido.");
            return;
        }
        Optional<CriterioBusqueda> existing = dao.findById(id.get());
        if (existing.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        CriterioBusqueda criterio = existing.get();
        criterio.setIdCriterio(id.get());
        Map<String, String> errors = validator.validate(criterio, formValues(request));
        if (!errors.containsKey("id_usuario") && !dao.userExists(criterio.getIdUsuario())) {
            errors.put("id_usuario", "El usuario ingresado no existe. Regístrelo desde el proceso correspondiente.");
        }
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            showForm(request, response, criterio, "actualizar", true);
            return;
        }
        if (!dao.update(criterio)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.getSession().setAttribute("mensaje", "El criterio se actualizó correctamente.");
        response.sendRedirect(request.getContextPath() + "/criterios");
    }

    private void delete(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        Optional<Integer> id = parseId(request.getParameter("id_criterio"));
        if (id.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El identificador no es válido.");
            return;
        }
        if (!dao.delete(id.get())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.getSession().setAttribute("mensaje", "El criterio se eliminó correctamente.");
        response.sendRedirect(request.getContextPath() + "/criterios");
    }

    private Map<String, String> formValues(HttpServletRequest request) {
        return Map.of(
                "id_usuario", parameter(request, "id_usuario"),
                "destino", parameter(request, "destino"),
                "fecha_inicio", parameter(request, "fecha_inicio"),
                "fecha_fin", parameter(request, "fecha_fin"),
                "precio_maximo", parameter(request, "precio_maximo"),
                "horario", parameter(request, "horario"),
                "preferencias", parameter(request, "preferencias"),
                "estado", parameter(request, "estado"));
    }

    private String parameter(HttpServletRequest request, String name) {
        return Optional.ofNullable(request.getParameter(name)).orElse("");
    }

    private Optional<Integer> parseId(String value) {
        try {
            int id = Integer.parseInt(value == null ? "" : value);
            return id > 0 ? Optional.of(id) : Optional.empty();
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private String route(HttpServletRequest request) {
        String path = request.getPathInfo();
        return path == null || path.isBlank() ? "/" : path;
    }

    private void databaseFailure(
            String message,
            SQLException exception,
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        getServletContext().log(message, exception);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        request.setAttribute("errorGeneral", message);
        request.getRequestDispatcher(ERROR_VIEW).forward(request, response);
    }
}
