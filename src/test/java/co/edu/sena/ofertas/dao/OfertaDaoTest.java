package co.edu.sena.ofertas.dao;

import co.edu.sena.comun.BaseDatosPrueba;
import co.edu.sena.criterios.model.CriterioBusqueda;
import co.edu.sena.ofertas.model.Hospedaje;
import co.edu.sena.ofertas.model.Restaurante;
import co.edu.sena.ofertas.model.Vuelo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class OfertaDaoTest {
    private final OfertaDao dao = new OfertaDao();

    @BeforeAll
    static void conectar() {
        assumeTrue(BaseDatosPrueba.preparar(), "MySQL no está disponible: se omiten las pruebas de integración.");
    }

    @BeforeEach
    void cargarCatalogos() throws SQLException {
        BaseDatosPrueba.limpiar();
        String vuelo = "INSERT INTO vuelo (origen, destino, fecha_salida, fecha_llegada, precio, aerolinea,"
                + " disponibilidad, duracion, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        BaseDatosPrueba.ejecutar(vuelo, "Bogotá", "Cartagena", "2026-12-10 07:00:00", "2026-12-10 08:35:00",
                289000, "Avianca", true, 95, true);
        BaseDatosPrueba.ejecutar(vuelo, "Bogotá", "Cartagena", "2026-12-11 07:00:00", "2026-12-11 08:35:00",
                241000, "Viva Air", true, 95, true);
        BaseDatosPrueba.ejecutar(vuelo, "Bogotá", "Cartagena", "2026-12-10 09:00:00", "2026-12-10 10:35:00",
                150000, "Wingo", false, 95, true);
        BaseDatosPrueba.ejecutar(vuelo, "Bogotá", "Cartagena", "2026-12-10 12:00:00", "2026-12-10 13:35:00",
                100000, "Satena", true, 95, false);
        BaseDatosPrueba.ejecutar(vuelo, "Bogotá", "Cartagena", "2027-02-01 07:00:00", "2027-02-01 08:35:00",
                120000, "LATAM", true, 95, true);
        BaseDatosPrueba.ejecutar(vuelo, "Bogotá", "Cartagena", "2026-12-12 07:00:00", "2026-12-12 08:35:00",
                900000, "Avianca", true, 95, true);
        BaseDatosPrueba.ejecutar(vuelo, "Bogotá", "Medellín", "2026-12-10 07:00:00", "2026-12-10 07:55:00",
                100000, "Avianca", true, 55, true);

        String hospedaje = "INSERT INTO hospedaje (nombre, ubicacion, precio_noche, disponibilidad, calificacion,"
                + " descripcion) VALUES (?, ?, ?, ?, ?, ?)";
        BaseDatosPrueba.ejecutar(hospedaje, "Casa del Mar", "Cartagena", 320000, true, 4.5, "Centro histórico");
        BaseDatosPrueba.ejecutar(hospedaje, "Hostal Muralla", "Cartagena", 95000, true, 4.1, "Económico");
        BaseDatosPrueba.ejecutar(hospedaje, "Hotel Lleno", "Cartagena", 80000, false, 4.0, "Sin cupo");
        BaseDatosPrueba.ejecutar(hospedaje, "Lujo Total", "Cartagena", 2000000, true, 5.0, "Fuera de presupuesto");
        BaseDatosPrueba.ejecutar(hospedaje, "Hotel Poblado", "Medellín", 280000, true, 4.6, "Otro destino");

        String restaurante = "INSERT INTO restaurante (nombre, ubicacion, tipo_comida, precio_promedio,"
                + " disponibilidad, calificacion, descripcion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        BaseDatosPrueba.ejecutar(restaurante, "La Cevichería", "Cartagena", "Mariscos", 78000, true, 4.6, "");
        BaseDatosPrueba.ejecutar(restaurante, "Café del Mural", "Cartagena", "Café", 32000, true, 4.2, "");
        BaseDatosPrueba.ejecutar(restaurante, "Cerrado", "Cartagena", "Variada", 40000, false, 4.9, "");
        BaseDatosPrueba.ejecutar(restaurante, "Hacienda Paisa", "Medellín", "Paisa", 56000, true, 4.5, "");
    }

    private CriterioBusqueda criterio(String destino, String precioMaximo) {
        CriterioBusqueda criterio = new CriterioBusqueda();
        criterio.setDestino(destino);
        criterio.setFechaInicio(LocalDate.of(2026, 12, 1));
        criterio.setFechaFin(LocalDate.of(2026, 12, 31));
        criterio.setPrecioMaximo(new BigDecimal(precioMaximo));
        return criterio;
    }

    @Test
    void losVuelosCumplenDestinoFechasDisponibilidadYPrecio() throws SQLException {
        List<Vuelo> vuelos = dao.buscarVuelos(criterio("cartagena", "500000"));

        assertEquals(2, vuelos.size());
        assertEquals("Viva Air", vuelos.get(0).getAerolinea());
        assertEquals("Avianca", vuelos.get(1).getAerolinea());
        assertEquals("Cartagena", vuelos.get(0).getDestino());
        assertEquals(95, vuelos.get(0).getDuracion());
    }

    @Test
    void lasHorasDeLosVuelosSeLeenTalComoEstanGuardadas() throws SQLException {
        Vuelo avianca = dao.buscarVuelos(criterio("Cartagena", "289000")).get(1);

        assertEquals(LocalDateTime.of(2026, 12, 10, 7, 0), avianca.getFechaSalida());
        assertEquals(LocalDateTime.of(2026, 12, 10, 8, 35), avianca.getFechaLlegada());
    }

    @Test
    void losHospedajesRespetanDisponibilidadYPresupuesto() throws SQLException {
        List<Hospedaje> hospedajes = dao.buscarHospedajes(criterio("Cartagena", "500000"));

        assertEquals(2, hospedajes.size());
        assertEquals("Hostal Muralla", hospedajes.get(0).getNombre());
        assertEquals("Casa del Mar", hospedajes.get(1).getNombre());
    }

    @Test
    void losRestaurantesRespetanDisponibilidadYUbicacion() throws SQLException {
        List<Restaurante> restaurantes = dao.buscarRestaurantes(criterio("Cartagena", "500000"));

        assertEquals(2, restaurantes.size());
        assertEquals("La Cevichería", restaurantes.get(0).getNombre());
    }

    @Test
    void unPresupuestoBajoDejaSoloLasOpcionesBaratas() throws SQLException {
        CriterioBusqueda economico = criterio("Cartagena", "100000");

        assertTrue(dao.buscarVuelos(economico).isEmpty());
        assertEquals(1, dao.buscarHospedajes(economico).size());
        assertEquals(2, dao.buscarRestaurantes(economico).size());
    }

    @Test
    void losComodinesDelDestinoSeTratanComoTextoLiteral() throws SQLException {
        CriterioBusqueda comodin = criterio("%", "5000000");

        assertTrue(dao.buscarVuelos(comodin).isEmpty());
        assertTrue(dao.buscarHospedajes(comodin).isEmpty());
        assertTrue(dao.buscarRestaurantes(comodin).isEmpty());
    }
}
