package co.edu.sena.criterios.dao;

import co.edu.sena.comun.BaseDatosPrueba;
import co.edu.sena.criterios.model.CriterioBusqueda;
import co.edu.sena.usuarios.dao.UsuarioDaoJdbc;
import co.edu.sena.usuarios.model.Usuario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class CriterioBusquedaDaoTest {
    private final CriterioBusquedaDao dao = new CriterioBusquedaDao();
    private final UsuarioDaoJdbc usuarios = new UsuarioDaoJdbc();
    private int idLaura;
    private int idCarlos;

    @BeforeAll
    static void conectar() {
        assumeTrue(BaseDatosPrueba.preparar(), "MySQL no está disponible: se omiten las pruebas de integración.");
    }

    @BeforeEach
    void preparar() throws SQLException {
        BaseDatosPrueba.limpiar();
        Usuario laura = new Usuario();
        laura.setNombre("Laura");
        laura.setApellido("Pérez");
        laura.setCedula("1010101010");
        laura.setFechaNacimiento(LocalDate.of(1998, 8, 20));
        laura.setCorreo("laura@correo.com");
        laura.setContrasenaHash("hash");
        idLaura = usuarios.insertar(laura);

        Usuario carlos = new Usuario();
        carlos.setNombre("Carlos");
        carlos.setApellido("Gómez");
        carlos.setCedula("2020202020");
        carlos.setFechaNacimiento(LocalDate.of(1990, 1, 5));
        carlos.setCorreo("carlos@correo.com");
        carlos.setContrasenaHash("hash");
        idCarlos = usuarios.insertar(carlos);
    }

    private CriterioBusqueda criterio(int idUsuario, String destino) {
        CriterioBusqueda criterio = new CriterioBusqueda();
        criterio.setIdUsuario(idUsuario);
        criterio.setDestino(destino);
        criterio.setFechaInicio(LocalDate.of(2026, 12, 1));
        criterio.setFechaFin(LocalDate.of(2026, 12, 10));
        criterio.setPrecioMaximo(new BigDecimal("500000.00"));
        criterio.setHorario("Mañana");
        criterio.setPreferencias("Equipaje incluido");
        criterio.setEstado(true);
        return criterio;
    }

    @Test
    void insertaYConsultaUnCriterio() throws SQLException {
        int id = dao.insertar(criterio(idLaura, "Cartagena"));

        CriterioBusqueda guardado = dao.buscarPorIdYUsuario(id, idLaura).orElseThrow();

        assertEquals("Cartagena", guardado.getDestino());
        assertEquals(LocalDate.of(2026, 12, 10), guardado.getFechaFin());
        assertEquals(new BigDecimal("500000.00"), guardado.getPrecioMaximo());
        assertEquals("Mañana", guardado.getHorario());
        assertTrue(guardado.isEstado());
        assertTrue(guardado.getFechaCreacion() != null);
    }

    @Test
    void cadaUsuarioSoloVeSusCriterios() throws SQLException {
        int idDeLaura = dao.insertar(criterio(idLaura, "Cartagena"));
        dao.insertar(criterio(idCarlos, "Medellín"));

        List<CriterioBusqueda> deLaura = dao.buscarPorUsuario(idLaura);

        assertEquals(1, deLaura.size());
        assertEquals("Cartagena", deLaura.get(0).getDestino());
        assertTrue(dao.buscarPorIdYUsuario(idDeLaura, idCarlos).isEmpty());
    }

    @Test
    void actualizaSoloSiElCriterioPerteneceAlUsuario() throws SQLException {
        int id = dao.insertar(criterio(idLaura, "Cartagena"));
        CriterioBusqueda cambio = dao.buscarPorIdYUsuario(id, idLaura).orElseThrow();
        cambio.setDestino("Santa Marta");
        cambio.setEstado(false);

        assertTrue(dao.actualizar(cambio));
        CriterioBusqueda actualizado = dao.buscarPorIdYUsuario(id, idLaura).orElseThrow();
        assertEquals("Santa Marta", actualizado.getDestino());
        assertFalse(actualizado.isEstado());

        cambio.setIdUsuario(idCarlos);
        cambio.setDestino("Intento ajeno");
        assertFalse(dao.actualizar(cambio));
        assertEquals("Santa Marta", dao.buscarPorIdYUsuario(id, idLaura).orElseThrow().getDestino());
    }

    @Test
    void eliminaSoloSiElCriterioPerteneceAlUsuario() throws SQLException {
        int id = dao.insertar(criterio(idLaura, "Cartagena"));

        assertFalse(dao.eliminar(id, idCarlos));
        assertTrue(dao.buscarPorIdYUsuario(id, idLaura).isPresent());

        assertTrue(dao.eliminar(id, idLaura));
        assertTrue(dao.buscarPorIdYUsuario(id, idLaura).isEmpty());
        assertFalse(dao.eliminar(id, idLaura));
    }

    @Test
    void laBaseRechazaUsuariosInexistentes() {
        assertThrows(SQLException.class, () -> dao.insertar(criterio(999_999, "Cartagena")));
    }
}
