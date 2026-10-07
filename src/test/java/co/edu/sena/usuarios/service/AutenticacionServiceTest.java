package co.edu.sena.usuarios.service;

import co.edu.sena.seguridad.PasswordHasher;
import co.edu.sena.usuarios.dao.UsuarioDao;
import co.edu.sena.usuarios.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutenticacionServiceTest {

    /** DAO en memoria: permite probar las reglas del servicio sin MySQL. */
    private static class UsuarioDaoEnMemoria implements UsuarioDao {
        final List<Usuario> usuarios = new ArrayList<>();

        @Override
        public Optional<Usuario> buscarPorCorreo(String correo) {
            return usuarios.stream().filter(u -> u.getCorreo().equals(correo)).findFirst();
        }

        @Override
        public boolean existeCorreo(String correo) {
            return buscarPorCorreo(correo).isPresent();
        }

        @Override
        public boolean existeCedula(String cedula) {
            return usuarios.stream().anyMatch(u -> u.getCedula().equals(cedula));
        }

        @Override
        public int insertar(Usuario usuario) throws SQLException {
            usuarios.add(usuario);
            return usuarios.size();
        }
    }

    private UsuarioDaoEnMemoria dao;
    private AutenticacionService service;

    @BeforeEach
    void preparar() {
        dao = new UsuarioDaoEnMemoria();
        service = new AutenticacionService(dao);
    }

    private Map<String, String> datos(String cedula, String correo) {
        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", "Laura");
        datos.put("apellido", "Pérez");
        datos.put("cedula", cedula);
        datos.put("fecha_nacimiento", "1998-08-20");
        datos.put("correo", correo);
        datos.put("telefono", "");
        datos.put("contrasena", "Clave2026");
        datos.put("confirmacion", "Clave2026");
        return datos;
    }

    @Test
    void registraGuardandoSoloElHashDeLaContrasena() throws SQLException {
        Map<String, String> errores = service.registrar(datos("1010101010", "laura@correo.com"));

        assertTrue(errores.isEmpty());
        assertEquals(1, dao.usuarios.size());
        String guardado = dao.usuarios.get(0).getContrasenaHash();
        assertFalse(guardado.contains("Clave2026"));
        assertTrue(PasswordHasher.verify("Clave2026", guardado));
    }

    @Test
    void noRegistraCorreosNiCedulasRepetidos() throws SQLException {
        service.registrar(datos("1010101010", "laura@correo.com"));

        Map<String, String> errores = service.registrar(datos("1010101010", "LAURA@correo.com"));

        assertTrue(errores.containsKey("correo"));
        assertTrue(errores.containsKey("cedula"));
        assertEquals(1, dao.usuarios.size());
    }

    @Test
    void noGuardaNadaSiLosDatosSonInvalidos() throws SQLException {
        Map<String, String> invalidos = datos("abc", "correo-malo");

        Map<String, String> errores = service.registrar(invalidos);

        assertFalse(errores.isEmpty());
        assertTrue(dao.usuarios.isEmpty());
    }

    @Test
    void autenticaConCorreoEnCualquierCapitalizacion() throws SQLException {
        service.registrar(datos("1010101010", "laura@correo.com"));

        assertTrue(service.autenticar("  Laura@Correo.com ", "Clave2026").isPresent());
    }

    @Test
    void rechazaContrasenaIncorrectaYUsuarioInexistente() throws SQLException {
        service.registrar(datos("1010101010", "laura@correo.com"));

        assertTrue(service.autenticar("laura@correo.com", "Equivocada1").isEmpty());
        assertTrue(service.autenticar("nadie@correo.com", "Clave2026").isEmpty());
        assertTrue(service.autenticar("", "Clave2026").isEmpty());
        assertTrue(service.autenticar("laura@correo.com", "").isEmpty());
    }

    @Test
    void rechazaUsuariosInactivos() throws SQLException {
        service.registrar(datos("1010101010", "laura@correo.com"));
        dao.usuarios.get(0).setEstado(false);

        assertTrue(service.autenticar("laura@correo.com", "Clave2026").isEmpty());
    }
}
