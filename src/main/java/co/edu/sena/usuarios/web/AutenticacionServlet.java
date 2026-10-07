package co.edu.sena.usuarios.web;

import co.edu.sena.comun.Flash;
import co.edu.sena.comun.Textos;
import co.edu.sena.seguridad.SesionUsuario;
import co.edu.sena.seguridad.UsuarioSesion;
import co.edu.sena.usuarios.dao.UsuarioDaoJdbc;
import co.edu.sena.usuarios.model.Usuario;
import co.edu.sena.usuarios.service.AutenticacionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

/** Controlador del módulo de usuarios: registro, inicio y cierre de sesión. */
@WebServlet(name = "autenticacionServlet", urlPatterns = {"/login", "/registro", "/logout"})
public class AutenticacionServlet extends HttpServlet {
    private static final String LOGIN_VIEW = "/WEB-INF/views/usuarios/login.jsp";
    private static final String REGISTRO_VIEW = "/WEB-INF/views/usuarios/registro.jsp";
    private static final String ERROR_VIEW = "/WEB-INF/views/error.jsp";

    private final AutenticacionService service = new AutenticacionService(new UsuarioDaoJdbc());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        switch (request.getServletPath()) {
            case "/login" -> mostrarLogin(request, response);
            case "/registro" -> mostrarRegistro(request, response);
            default -> response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            switch (request.getServletPath()) {
                case "/login" -> iniciarSesion(request, response);
                case "/registro" -> registrar(request, response);
                case "/logout" -> cerrarSesion(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException exception) {
            getServletContext().log("Error de base de datos en autenticación", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("errorGeneral", "No fue posible completar la operación. Intente más tarde.");
            request.getRequestDispatcher(ERROR_VIEW).forward(request, response);
        }
    }

    private void mostrarLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SesionUsuario.actual(request).isPresent()) {
            response.sendRedirect(request.getContextPath() + "/inicio");
            return;
        }
        Flash.consumir(request);
        request.getRequestDispatcher(LOGIN_VIEW).forward(request, response);
    }

    private void mostrarRegistro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SesionUsuario.actual(request).isPresent()) {
            response.sendRedirect(request.getContextPath() + "/inicio");
            return;
        }
        request.getRequestDispatcher(REGISTRO_VIEW).forward(request, response);
    }

    private void iniciarSesion(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Optional<Usuario> usuario = service.autenticar(
                Textos.parametro(request, "correo"), Textos.parametro(request, "contrasena"));
        if (usuario.isEmpty()) {
            request.setAttribute("errorGeneral", "Correo o contraseña incorrectos.");
            request.getRequestDispatcher(LOGIN_VIEW).forward(request, response);
            return;
        }
        Usuario encontrado = usuario.get();
        SesionUsuario.iniciar(request, new UsuarioSesion(
                encontrado.getIdUsuario(), encontrado.getNombreCompleto(), encontrado.getCorreo()));
        response.sendRedirect(request.getContextPath() + "/inicio");
    }

    private void registrar(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        Map<String, String> valores = Map.of(
                "nombre", Textos.parametro(request, "nombre"),
                "apellido", Textos.parametro(request, "apellido"),
                "cedula", Textos.parametro(request, "cedula"),
                "fecha_nacimiento", Textos.parametro(request, "fecha_nacimiento"),
                "correo", Textos.parametro(request, "correo"),
                "telefono", Textos.parametro(request, "telefono"),
                "contrasena", Textos.parametro(request, "contrasena"),
                "confirmacion", Textos.parametro(request, "confirmacion"));
        Map<String, String> errores = service.registrar(valores);
        if (!errores.isEmpty()) {
            request.setAttribute("errors", errores);
            request.getRequestDispatcher(REGISTRO_VIEW).forward(request, response);
            return;
        }
        Flash.guardar(request, "Cuenta creada correctamente. Ya puede iniciar sesión.");
        response.sendRedirect(request.getContextPath() + "/login");
    }

    private void cerrarSesion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SesionUsuario.cerrar(request);
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
