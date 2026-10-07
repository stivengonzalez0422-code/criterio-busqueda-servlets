package co.edu.sena.ofertas.dao;

import co.edu.sena.comun.Database;
import co.edu.sena.comun.Textos;
import co.edu.sena.criterios.model.CriterioBusqueda;
import co.edu.sena.ofertas.model.Hospedaje;
import co.edu.sena.ofertas.model.Restaurante;
import co.edu.sena.ofertas.model.Vuelo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Busca en los catálogos de vuelo, hospedaje y restaurante las opciones que cumplen un criterio
 * de búsqueda: mismo destino (o ubicación), disponibles, activas y dentro del precio máximo.
 */
public class OfertaDao {
    private static final int LIMITE = 20;

    public List<Vuelo> buscarVuelos(CriterioBusqueda criterio) throws SQLException {
        String sql = """
                SELECT id_vuelo, origen, destino, fecha_salida, fecha_llegada, precio, aerolinea, duracion
                FROM vuelo
                WHERE estado = TRUE AND disponibilidad = TRUE
                  AND destino LIKE ? ESCAPE '!'
                  AND DATE(fecha_salida) BETWEEN ? AND ?
                  AND precio <= ?
                ORDER BY precio, fecha_salida
                LIMIT ?
                """;
        List<Vuelo> vuelos = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, patron(criterio.getDestino()));
            statement.setDate(2, Date.valueOf(criterio.getFechaInicio()));
            statement.setDate(3, Date.valueOf(criterio.getFechaFin()));
            statement.setBigDecimal(4, criterio.getPrecioMaximo());
            statement.setInt(5, LIMITE);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Vuelo vuelo = new Vuelo();
                    vuelo.setIdVuelo(result.getInt("id_vuelo"));
                    vuelo.setOrigen(result.getString("origen"));
                    vuelo.setDestino(result.getString("destino"));
                    // getObject(LocalDateTime) devuelve la hora tal como está guardada, sin conversión de zona horaria.
                    vuelo.setFechaSalida(result.getObject("fecha_salida", LocalDateTime.class));
                    vuelo.setFechaLlegada(result.getObject("fecha_llegada", LocalDateTime.class));
                    vuelo.setPrecio(result.getBigDecimal("precio"));
                    vuelo.setAerolinea(result.getString("aerolinea"));
                    vuelo.setDuracion(result.getInt("duracion"));
                    vuelos.add(vuelo);
                }
            }
        }
        return vuelos;
    }

    public List<Hospedaje> buscarHospedajes(CriterioBusqueda criterio) throws SQLException {
        String sql = """
                SELECT id_hospedaje, nombre, ubicacion, precio_noche, calificacion, descripcion
                FROM hospedaje
                WHERE estado = TRUE AND disponibilidad = TRUE
                  AND ubicacion LIKE ? ESCAPE '!'
                  AND precio_noche <= ?
                ORDER BY precio_noche
                LIMIT ?
                """;
        List<Hospedaje> hospedajes = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, patron(criterio.getDestino()));
            statement.setBigDecimal(2, criterio.getPrecioMaximo());
            statement.setInt(3, LIMITE);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Hospedaje hospedaje = new Hospedaje();
                    hospedaje.setIdHospedaje(result.getInt("id_hospedaje"));
                    hospedaje.setNombre(result.getString("nombre"));
                    hospedaje.setUbicacion(result.getString("ubicacion"));
                    hospedaje.setPrecioNoche(result.getBigDecimal("precio_noche"));
                    hospedaje.setCalificacion(result.getBigDecimal("calificacion"));
                    hospedaje.setDescripcion(result.getString("descripcion"));
                    hospedajes.add(hospedaje);
                }
            }
        }
        return hospedajes;
    }

    public List<Restaurante> buscarRestaurantes(CriterioBusqueda criterio) throws SQLException {
        String sql = """
                SELECT id_restaurante, nombre, ubicacion, tipo_comida, precio_promedio, calificacion, descripcion
                FROM restaurante
                WHERE estado = TRUE AND disponibilidad = TRUE
                  AND ubicacion LIKE ? ESCAPE '!'
                  AND precio_promedio <= ?
                ORDER BY calificacion DESC, precio_promedio
                LIMIT ?
                """;
        List<Restaurante> restaurantes = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, patron(criterio.getDestino()));
            statement.setBigDecimal(2, criterio.getPrecioMaximo());
            statement.setInt(3, LIMITE);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Restaurante restaurante = new Restaurante();
                    restaurante.setIdRestaurante(result.getInt("id_restaurante"));
                    restaurante.setNombre(result.getString("nombre"));
                    restaurante.setUbicacion(result.getString("ubicacion"));
                    restaurante.setTipoComida(result.getString("tipo_comida"));
                    restaurante.setPrecioPromedio(result.getBigDecimal("precio_promedio"));
                    restaurante.setCalificacion(result.getBigDecimal("calificacion"));
                    restaurante.setDescripcion(result.getString("descripcion"));
                    restaurantes.add(restaurante);
                }
            }
        }
        return restaurantes;
    }

    private String patron(String destino) {
        return "%" + Textos.escaparLike(destino.trim()) + "%";
    }
}
