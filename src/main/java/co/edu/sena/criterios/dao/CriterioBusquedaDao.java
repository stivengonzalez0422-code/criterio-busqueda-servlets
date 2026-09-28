package co.edu.sena.criterios.dao;

import co.edu.sena.criterios.model.CriterioBusqueda;
import co.edu.sena.criterios.util.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CriterioBusquedaDao {
    private static final String SELECT_COLUMNS = """
            SELECT id_criterio, id_usuario, destino, fecha_inicio, fecha_fin,
                   precio_maximo, horario, preferencias, fecha_creacion, estado
            FROM criterio_busqueda
            """;

    public List<CriterioBusqueda> findAll() throws SQLException {
        String sql = SELECT_COLUMNS + " ORDER BY fecha_creacion DESC, id_criterio DESC";
        List<CriterioBusqueda> criterios = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                criterios.add(map(result));
            }
        }
        return criterios;
    }

    public Optional<CriterioBusqueda> findById(int id) throws SQLException {
        String sql = SELECT_COLUMNS + " WHERE id_criterio = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        }
    }

    public boolean userExists(int idUsuario) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE id_usuario = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idUsuario);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public int insert(CriterioBusqueda criterio) throws SQLException {
        String sql = """
                INSERT INTO criterio_busqueda
                    (id_usuario, destino, fecha_inicio, fecha_fin, precio_maximo,
                     horario, preferencias, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(statement, criterio);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("MySQL no devolvió el identificador del criterio creado.");
                }
                return keys.getInt(1);
            }
        }
    }

    public boolean update(CriterioBusqueda criterio) throws SQLException {
        String sql = """
                UPDATE criterio_busqueda
                SET id_usuario = ?, destino = ?, fecha_inicio = ?, fecha_fin = ?,
                    precio_maximo = ?, horario = ?, preferencias = ?, estado = ?
                WHERE id_criterio = ?
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, criterio);
            statement.setInt(9, criterio.getIdCriterio());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM criterio_busqueda WHERE id_criterio = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    private void bind(PreparedStatement statement, CriterioBusqueda criterio) throws SQLException {
        statement.setInt(1, criterio.getIdUsuario());
        statement.setString(2, criterio.getDestino());
        statement.setDate(3, Date.valueOf(criterio.getFechaInicio()));
        statement.setDate(4, Date.valueOf(criterio.getFechaFin()));
        statement.setBigDecimal(5, criterio.getPrecioMaximo());
        statement.setString(6, criterio.getHorario());
        statement.setString(7, criterio.getPreferencias());
        statement.setBoolean(8, criterio.isEstado());
    }

    private CriterioBusqueda map(ResultSet result) throws SQLException {
        CriterioBusqueda criterio = new CriterioBusqueda();
        criterio.setIdCriterio(result.getInt("id_criterio"));
        criterio.setIdUsuario(result.getInt("id_usuario"));
        criterio.setDestino(result.getString("destino"));
        criterio.setFechaInicio(result.getDate("fecha_inicio").toLocalDate());
        criterio.setFechaFin(result.getDate("fecha_fin").toLocalDate());
        criterio.setPrecioMaximo(result.getBigDecimal("precio_maximo"));
        criterio.setHorario(result.getString("horario"));
        criterio.setPreferencias(result.getString("preferencias"));
        Timestamp fechaCreacion = result.getTimestamp("fecha_creacion");
        criterio.setFechaCreacion(fechaCreacion == null ? null : fechaCreacion.toLocalDateTime());
        criterio.setEstado(result.getBoolean("estado"));
        return criterio;
    }
}
