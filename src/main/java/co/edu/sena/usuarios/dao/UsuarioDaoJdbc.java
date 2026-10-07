package co.edu.sena.usuarios.dao;

import co.edu.sena.comun.Database;
import co.edu.sena.usuarios.model.Usuario;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;

public class UsuarioDaoJdbc implements UsuarioDao {

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) throws SQLException {
        String sql = """
                SELECT id_usuario, nombre, apellido, cedula, fecha_nacimiento, correo,
                       `contraseña` AS contrasena, telefono, fecha_registro, estado
                FROM usuario
                WHERE correo = ?
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, correo);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapear(result)) : Optional.empty();
            }
        }
    }

    @Override
    public boolean existeCorreo(String correo) throws SQLException {
        return existe("SELECT 1 FROM usuario WHERE correo = ?", correo);
    }

    @Override
    public boolean existeCedula(String cedula) throws SQLException {
        return existe("SELECT 1 FROM usuario WHERE cedula = ?", cedula);
    }

    @Override
    public int insertar(Usuario usuario) throws SQLException {
        String sql = """
                INSERT INTO usuario
                    (nombre, apellido, cedula, fecha_nacimiento, correo, `contraseña`, telefono, estado)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getApellido());
            statement.setString(3, usuario.getCedula());
            statement.setDate(4, Date.valueOf(usuario.getFechaNacimiento()));
            statement.setString(5, usuario.getCorreo());
            statement.setString(6, usuario.getContrasenaHash());
            statement.setString(7, usuario.getTelefono());
            statement.setBoolean(8, usuario.isEstado());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("MySQL no devolvió el identificador del usuario creado.");
                }
                return keys.getInt(1);
            }
        }
    }

    private boolean existe(String sql, String valor) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, valor);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private Usuario mapear(ResultSet result) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(result.getInt("id_usuario"));
        usuario.setNombre(result.getString("nombre"));
        usuario.setApellido(result.getString("apellido"));
        usuario.setCedula(result.getString("cedula"));
        usuario.setFechaNacimiento(result.getDate("fecha_nacimiento").toLocalDate());
        usuario.setCorreo(result.getString("correo"));
        usuario.setContrasenaHash(result.getString("contrasena"));
        usuario.setTelefono(result.getString("telefono"));
        usuario.setFechaRegistro(result.getObject("fecha_registro", LocalDateTime.class));
        usuario.setEstado(result.getBoolean("estado"));
        return usuario;
    }
}
