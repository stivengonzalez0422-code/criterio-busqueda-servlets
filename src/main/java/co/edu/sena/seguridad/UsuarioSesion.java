package co.edu.sena.seguridad;

import java.io.Serializable;

/** Datos mínimos del usuario autenticado que se guardan en la sesión HTTP. */
public class UsuarioSesion implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idUsuario;
    private final String nombreCompleto;
    private final String correo;

    public UsuarioSesion(int idUsuario, String nombreCompleto, String correo) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }
}
