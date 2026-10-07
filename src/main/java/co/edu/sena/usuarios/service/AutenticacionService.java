package co.edu.sena.usuarios.service;

import co.edu.sena.seguridad.PasswordHasher;
import co.edu.sena.usuarios.dao.UsuarioDao;
import co.edu.sena.usuarios.model.Usuario;
import co.edu.sena.usuarios.validation.UsuarioValidator;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

/** Reglas de negocio del registro y del inicio de sesión. */
public class AutenticacionService {
    // Hash de relleno: permite gastar el mismo tiempo cuando el correo no existe.
    private static final String HASH_RELLENO = PasswordHasher.hash("relleno-contraseña-inexistente-1");

    private final UsuarioDao dao;
    private final UsuarioValidator validator = new UsuarioValidator();

    public AutenticacionService(UsuarioDao dao) {
        this.dao = dao;
    }

    /** Devuelve los errores por campo; el mapa vacío significa que el usuario quedó registrado. */
    public Map<String, String> registrar(Map<String, String> valores) throws SQLException {
        Usuario usuario = new Usuario();
        Map<String, String> errores = validator.validar(usuario, valores);
        if (!errores.containsKey("correo") && dao.existeCorreo(usuario.getCorreo())) {
            errores.put("correo", "Ya existe una cuenta con ese correo.");
        }
        if (!errores.containsKey("cedula") && dao.existeCedula(usuario.getCedula())) {
            errores.put("cedula", "Ya existe una cuenta con esa cédula.");
        }
        if (!errores.isEmpty()) {
            return errores;
        }
        usuario.setContrasenaHash(PasswordHasher.hash(valores.get("contrasena")));
        usuario.setEstado(true);
        dao.insertar(usuario);
        return errores;
    }

    public Optional<Usuario> autenticar(String correo, String contrasena) throws SQLException {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isEmpty()) {
            return Optional.empty();
        }
        Optional<Usuario> encontrado = dao.buscarPorCorreo(correo.trim().toLowerCase());
        if (encontrado.isEmpty()) {
            PasswordHasher.verify(contrasena, HASH_RELLENO);
            return Optional.empty();
        }
        Usuario usuario = encontrado.get();
        boolean valido = PasswordHasher.verify(contrasena, usuario.getContrasenaHash());
        return valido && usuario.isEstado() ? Optional.of(usuario) : Optional.empty();
    }
}
