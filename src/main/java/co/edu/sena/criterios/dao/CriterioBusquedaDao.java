package co.edu.sena.criterios.dao;

import co.edu.sena.comun.Database;
import co.edu.sena.criterios.model.CriterioBusqueda;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acceso a criterio_busqueda. Toda consulta se limita al usuario dueño del criterio. */
public class CriterioBusquedaDao {
    private static final String SELECT_COLUMNS = """
            SELECT id_criterio, id_usuario, destino, fecha_inicio, fecha_fin,
                   precio_maximo, horario, preferencias, fecha_creacion, estado
            FROM criterio_busqueda
            """;

    public List<CriterioBusqueda> buscarPorUsuario(int idUsuario) throws SQLException {
        String sql = SELECT_COLUMNS + " WHERE id_usuario = ? ORDER BY fecha_creacion DESC, id_criterio DESC";
        List<CriterioBusqueda> criterios = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idUsuario);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    criterios.add(mapear(result));
                }
            }
        }
        return criterios;
    }

    public Optional<CriterioBusqueda> buscarPorIdYUsuario(int idCriterio, int idUsuario) throws SQLException {
        String sql = SELECT_COLUMNS + " WHERE id_criterio = ? AND id_usuario = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idCriterio);
            statement.setInt(2, idUsuario);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapear(result)) : Optional.empty();
            }
        }
    }

    public int insertar(CriterioBusqueda criterio) throws SQLException {
        String sql = """
                INSERT INTO criterio_busqueda
                    (id_usuario, destino, fecha_inicio, fecha_fin, precio_maximo,
                     horario, preferencias, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, criterio.getIdUsuario());
            asignarDatos(statement, criterio, 2);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("MySQL no devolvió el identificador del criterio creado.");
                }
                return keys.getInt(1);
            }
        }
    }

    /** Actualiza solo si el criterio pertenece al usuario indicado en el modelo. */
    public boolean actualizar(CriterioBusqueda criterio) throws SQLException {
        String sql = """
                UPDATE criterio_busqueda
                SET destino = ?, fecha_inicio = ?, fecha_fin = ?, precio_maximo = ?,
                    horario = ?, preferencias = ?, estado = ?
                WHERE id_criterio = ? AND id_usuario = ?
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            asignarDatos(statement, criterio, 1);
            statement.setInt(8, criterio.getIdCriterio());
            statement.setInt(9, criterio.getIdUsuario());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean eliminar(int idCriterio, int idUsuario) throws SQLException {
        String sql = "DELETE FROM criterio_busqueda WHERE id_criterio = ? AND id_usuario = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idCriterio);
            statement.setInt(2, idUsuario);
            return statement.executeUpdate() == 1;
        }
    }

    // Asigna destino ... estado (7 parámetros) a partir de la posición indicada.
    private void asignarDatos(PreparedStatement statement, CriterioBusqueda criterio, int posicion)
            throws SQLException {
        statement.setString(posicion, criterio.getDestino());
        statement.setDate(posicion + 1, Date.valueOf(criterio.getFechaInicio()));
        statement.setDate(posicion + 2, Date.valueOf(criterio.getFechaFin()));
        statement.setBigDecimal(posicion + 3, criterio.getPrecioMaximo());
        statement.setString(posicion + 4, criterio.getHorario());
        statement.setString(posicion + 5, criterio.getPreferencias());
        statement.setBoolean(posicion + 6, criterio.isEstado());
    }

    private CriterioBusqueda mapear(ResultSet result) throws SQLException {
        CriterioBusqueda criterio = new CriterioBusqueda();
        criterio.setIdCriterio(result.getInt("id_criterio"));
        criterio.setIdUsuario(result.getInt("id_usuario"));
        criterio.setDestino(result.getString("destino"));
        criterio.setFechaInicio(result.getDate("fecha_inicio").toLocalDate());
        criterio.setFechaFin(result.getDate("fecha_fin").toLocalDate());
        criterio.setPrecioMaximo(result.getBigDecimal("precio_maximo"));
        criterio.setHorario(result.getString("horario"));
        criterio.setPreferencias(result.getString("preferencias"));
        criterio.setFechaCreacion(result.getObject("fecha_creacion", LocalDateTime.class));
        criterio.setEstado(result.getBoolean("estado"));
        return criterio;
    }
}
