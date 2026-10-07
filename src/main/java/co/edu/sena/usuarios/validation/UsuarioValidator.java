package co.edu.sena.usuarios.validation;

import co.edu.sena.comun.Textos;
import co.edu.sena.usuarios.model.Usuario;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Valida el formulario de registro y llena el modelo Usuario con los datos normalizados.
 * La contraseña no se asigna aquí: el servicio la convierte en hash.
 */
public class UsuarioValidator {
    public static final int CONTRASENA_MINIMA = 8;
    public static final int CONTRASENA_MAXIMA = 72;

    private static final Pattern NOMBRE = Pattern.compile("^\\p{L}[\\p{L} '\\-]*$");
    private static final Pattern CEDULA = Pattern.compile("^\\d{5,20}$");
    private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern TELEFONO = Pattern.compile("^\\+?\\d{7,15}$");

    public Map<String, String> validar(Usuario usuario, Map<String, String> valores) {
        Map<String, String> errores = new LinkedHashMap<>();
        usuario.setNombre(texto(valores.get("nombre"), "nombre", "El nombre", 100, NOMBRE, errores));
        usuario.setApellido(texto(valores.get("apellido"), "apellido", "El apellido", 100, NOMBRE, errores));
        validarCedula(valores.get("cedula"), usuario, errores);
        validarFechaNacimiento(valores.get("fecha_nacimiento"), usuario, errores);
        validarCorreo(valores.get("correo"), usuario, errores);
        validarTelefono(valores.get("telefono"), usuario, errores);
        validarContrasena(valores.get("contrasena"), valores.get("confirmacion"), errores);
        return errores;
    }

    private String texto(String valor, String campo, String etiqueta, int maximo,
                         Pattern formato, Map<String, String> errores) {
        String texto = Textos.recortarONulo(valor);
        if (texto == null) {
            errores.put(campo, etiqueta + " es obligatorio.");
        } else if (texto.length() > maximo) {
            errores.put(campo, etiqueta + " no puede superar los " + maximo + " caracteres.");
        } else if (!formato.matcher(texto).matches()) {
            errores.put(campo, etiqueta + " solo puede contener letras, espacios, apóstrofos y guiones.");
        }
        return texto;
    }

    private void validarCedula(String valor, Usuario usuario, Map<String, String> errores) {
        String cedula = Textos.recortarONulo(valor);
        if (cedula == null) {
            errores.put("cedula", "La cédula es obligatoria.");
        } else if (!CEDULA.matcher(cedula).matches()) {
            errores.put("cedula", "La cédula debe tener entre 5 y 20 dígitos, sin puntos ni espacios.");
        }
        usuario.setCedula(cedula);
    }

    private void validarFechaNacimiento(String valor, Usuario usuario, Map<String, String> errores) {
        String texto = Textos.recortarONulo(valor);
        if (texto == null) {
            errores.put("fecha_nacimiento", "La fecha de nacimiento es obligatoria.");
            return;
        }
        try {
            LocalDate fecha = LocalDate.parse(texto);
            if (!fecha.isBefore(LocalDate.now()) || fecha.isBefore(LocalDate.of(1900, 1, 1))) {
                errores.put("fecha_nacimiento", "La fecha de nacimiento no es válida.");
            } else {
                usuario.setFechaNacimiento(fecha);
            }
        } catch (DateTimeParseException excepcion) {
            errores.put("fecha_nacimiento", "La fecha de nacimiento no es válida.");
        }
    }

    private void validarCorreo(String valor, Usuario usuario, Map<String, String> errores) {
        String correo = Textos.recortarONulo(valor);
        if (correo == null) {
            errores.put("correo", "El correo es obligatorio.");
            return;
        }
        correo = correo.toLowerCase();
        if (correo.length() > 150) {
            errores.put("correo", "El correo no puede superar los 150 caracteres.");
        } else if (!CORREO.matcher(correo).matches()) {
            errores.put("correo", "Ingrese un correo válido, por ejemplo nombre@dominio.com.");
        }
        usuario.setCorreo(correo);
    }

    private void validarTelefono(String valor, Usuario usuario, Map<String, String> errores) {
        String telefono = Textos.recortarONulo(valor);
        if (telefono != null) {
            telefono = telefono.replace(" ", "");
            if (!TELEFONO.matcher(telefono).matches()) {
                errores.put("telefono", "El teléfono debe tener entre 7 y 15 dígitos.");
            }
        }
        usuario.setTelefono(telefono);
    }

    private void validarContrasena(String contrasena, String confirmacion, Map<String, String> errores) {
        if (contrasena == null || contrasena.isEmpty()) {
            errores.put("contrasena", "La contraseña es obligatoria.");
        } else if (contrasena.length() < CONTRASENA_MINIMA || contrasena.length() > CONTRASENA_MAXIMA) {
            errores.put("contrasena", "La contraseña debe tener entre " + CONTRASENA_MINIMA
                    + " y " + CONTRASENA_MAXIMA + " caracteres.");
        } else if (!contrasena.matches(".*\\p{L}.*") || !contrasena.matches(".*\\d.*")) {
            errores.put("contrasena", "La contraseña debe incluir al menos una letra y un número.");
        } else if (!contrasena.equals(confirmacion)) {
            errores.put("confirmacion", "La confirmación no coincide con la contraseña.");
        }
    }
}
