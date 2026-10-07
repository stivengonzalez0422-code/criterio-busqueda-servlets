package co.edu.sena.seguridad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    @Test
    void elHashNoContieneLaContrasenaYSeVerifica() {
        String hash = PasswordHasher.hash("Clave2026");

        assertFalse(hash.contains("Clave2026"));
        assertTrue(hash.startsWith("pbkdf2_sha256$"));
        assertTrue(hash.length() < 255);
        assertTrue(PasswordHasher.verify("Clave2026", hash));
    }

    @Test
    void rechazaContrasenasIncorrectas() {
        String hash = PasswordHasher.hash("Clave2026");

        assertFalse(PasswordHasher.verify("clave2026", hash));
        assertFalse(PasswordHasher.verify("", hash));
        assertFalse(PasswordHasher.verify(null, hash));
    }

    @Test
    void usaUnaSalDistintaEnCadaHash() {
        assertNotEquals(PasswordHasher.hash("Clave2026"), PasswordHasher.hash("Clave2026"));
    }

    @Test
    void ignoraHashesConFormatoInvalido() {
        assertFalse(PasswordHasher.verify("Clave2026", null));
        assertFalse(PasswordHasher.verify("Clave2026", "texto-plano"));
        assertFalse(PasswordHasher.verify("Clave2026", "pbkdf2_sha256$abc$%%%$%%%"));
    }

    @Test
    void generaTokensCsrfUnicos() {
        assertNotEquals(SesionUsuario.nuevoToken(), SesionUsuario.nuevoToken());
        assertTrue(SesionUsuario.nuevoToken().length() >= 43);
    }
}
