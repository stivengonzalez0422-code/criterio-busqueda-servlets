package co.edu.sena.usuarios.dao;

import co.edu.sena.usuarios.model.Usuario;

import java.sql.SQLException;
import java.util.Optional;

/** Contrato de acceso a la tabla usuario; permite probar el servicio sin base de datos. */
public interface UsuarioDao {
    Optional<Usuario> buscarPorCorreo(String correo) throws SQLException;

    boolean existeCorreo(String correo) throws SQLException;

    boolean existeCedula(String cedula) throws SQLException;

    int insertar(Usuario usuario) throws SQLException;
}
