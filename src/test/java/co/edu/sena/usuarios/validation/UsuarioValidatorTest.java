package co.edu.sena.usuarios.validation;

import co.edu.sena.usuarios.model.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioValidatorTest {
    private final UsuarioValidator validator = new UsuarioValidator();

    private Map<String, String> datosValidos() {
        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", "  María José ");
        datos.put("apellido", "O'Neil-Gómez");
        datos.put("cedula", "1020304050");
        datos.put("fecha_nacimiento", "1995-04-12");
        datos.put("correo", "  Maria@Correo.COM ");
        datos.put("telefono", "300 123 4567");
        datos.put("contrasena", "Clave2026");
        datos.put("confirmacion", "Clave2026");
        return datos;
    }

    @Test
    void aceptaUnRegistroValidoYNormalizaLosDatos() {
        Usuario usuario = new Usuario();
        Map<String, String> errores = validator.validar(usuario, datosValidos());

        assertTrue(errores.isEmpty(), errores.toString());
        assertEquals("María José", usuario.getNombre());
        assertEquals("maria@correo.com", usuario.getCorreo());
        assertEquals("3001234567", usuario.getTelefono());
        assertEquals(LocalDate.of(1995, 4, 12), usuario.getFechaNacimiento());
    }

    @Test
    void exigeLosCamposObligatorios() {
        Map<String, String> errores = validator.validar(new Usuario(), new HashMap<>());

        assertTrue(errores.keySet().containsAll(
                java.util.List.of("nombre", "apellido", "cedula", "fecha_nacimiento", "correo", "contrasena")));
    }

    @Test
    void rechazaCedulaCorreoYNombreInvalidos() {
        Map<String, String> datos = datosValidos();
        datos.put("cedula", "12.345");
        datos.put("correo", "sin-arroba");
        datos.put("nombre", "Ana123");

        Map<String, String> errores = validator.validar(new Usuario(), datos);

        assertTrue(errores.containsKey("cedula"));
        assertTrue(errores.containsKey("correo"));
        assertTrue(errores.containsKey("nombre"));
    }

    @Test
    void rechazaFechasDeNacimientoFuturasOMalFormadas() {
        Map<String, String> datos = datosValidos();
        datos.put("fecha_nacimiento", LocalDate.now().plusDays(1).toString());
        assertTrue(validator.validar(new Usuario(), datos).containsKey("fecha_nacimiento"));

        datos.put("fecha_nacimiento", "12/04/1995");
        assertTrue(validator.validar(new Usuario(), datos).containsKey("fecha_nacimiento"));
    }

    @Test
    void validaLaFortalezaYLaConfirmacionDeLaContrasena() {
        Map<String, String> datos = datosValidos();
        datos.put("contrasena", "corta1");
        datos.put("confirmacion", "corta1");
        assertTrue(validator.validar(new Usuario(), datos).containsKey("contrasena"));

        datos.put("contrasena", "soloLetrasAqui");
        datos.put("confirmacion", "soloLetrasAqui");
        assertTrue(validator.validar(new Usuario(), datos).containsKey("contrasena"));

        datos.put("contrasena", "Clave2026");
        datos.put("confirmacion", "Otra2026");
        assertTrue(validator.validar(new Usuario(), datos).containsKey("confirmacion"));
    }

    @Test
    void elTelefonoEsOpcionalPeroDebeSerValido() {
        Map<String, String> datos = datosValidos();
        datos.put("telefono", "");
        assertTrue(validator.validar(new Usuario(), datos).isEmpty());

        datos.put("telefono", "abc");
        assertTrue(validator.validar(new Usuario(), datos).containsKey("telefono"));
    }
}
