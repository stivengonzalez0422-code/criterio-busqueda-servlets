package co.edu.sena.usuarios.dao;

import co.edu.sena.comun.BaseDatosPrueba;
import co.edu.sena.usuarios.model.Usuario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class UsuarioDaoJdbcTest {
    private final UsuarioDaoJdbc dao = new UsuarioDaoJdbc();

    @BeforeAll
    static void conectar() {
        assumeTrue(BaseDatosPrueba.preparar(), "MySQL no está disponible: se omiten las pruebas de integración.");
    }

    @BeforeEach
    void limpiar() throws SQLException {
        BaseDatosPrueba.limpiar();
    }

    static Usuario usuario(String cedula, String correo) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Laura");
        usuario.setApellido("Pérez");
        usuario.setCedula(cedula);
        usuario.setFechaNacimiento(LocalDate.of(1998, 8, 20));
        usuario.setCorreo(correo);
        usuario.setContrasenaHash("pbkdf2_sha256$1$c2Fs$aGFzaA==");
        usuario.setTelefono("3001234567");
        return usuario;
    }

    @Test
    void insertaYRecuperaUnUsuarioPorCorreo() throws SQLException {
        int id = dao.insertar(usuario("1010101010", "laura@correo.com"));

        Optional<Usuario> encontrado = dao.buscarPorCorreo("laura@correo.com");

        assertTrue(encontrado.isPresent());
        assertEquals(id, encontrado.get().getIdUsuario());
        assertEquals("Laura Pérez", encontrado.get().getNombreCompleto());
        assertEquals("pbkdf2_sha256$1$c2Fs$aGFzaA==", encontrado.get().getContrasenaHash());
        assertEquals(LocalDate.of(1998, 8, 20), encontrado.get().getFechaNacimiento());
        assertNotNull(encontrado.get().getFechaRegistro());
        assertTrue(encontrado.get().isEstado());
    }

    @Test
    void indicaSiExisteUnCorreoOUnaCedula() throws SQLException {
        dao.insertar(usuario("1010101010", "laura@correo.com"));

        assertTrue(dao.existeCorreo("laura@correo.com"));
        assertTrue(dao.existeCedula("1010101010"));
        assertFalse(dao.existeCorreo("otro@correo.com"));
        assertFalse(dao.existeCedula("999999999"));
        assertTrue(dao.buscarPorCorreo("otro@correo.com").isEmpty());
    }

    @Test
    void laBaseRechazaCorreosDuplicados() throws SQLException {
        dao.insertar(usuario("1010101010", "laura@correo.com"));

        assertThrows(SQLException.class, () -> dao.insertar(usuario("2020202020", "laura@correo.com")));
    }
}
