package co.edu.sena.comun;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Prepara una base de datos de pruebas separada (agenda_asistente_viajes_test) con el esquema
 * del proyecto. Nunca toca la base real. Si MySQL no está disponible, las pruebas que la
 * usan se omiten.
 */
public final class BaseDatosPrueba {
    private static final String NOMBRE = "agenda_asistente_viajes_test";
    private static final String URL_SERVIDOR = "jdbc:mysql://localhost:3306/?serverTimezone=UTC";
    private static final String URL_PRUEBAS = "jdbc:mysql://localhost:3306/" + NOMBRE + "?serverTimezone=UTC";

    private BaseDatosPrueba() {
    }

    public static boolean preparar() {
        String usuario = valor("DB_USER", "root");
        String clave = valor("DB_PASSWORD", "");
        try (Connection servidor = Database.getConnection(URL_SERVIDOR, usuario, clave);
             Statement statement = servidor.createStatement()) {
            statement.execute("CREATE DATABASE IF NOT EXISTS " + NOMBRE
                    + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        } catch (SQLException exception) {
            return false;
        }
        System.setProperty("DB_URL", URL_PRUEBAS);
        System.setProperty("DB_USER", usuario);
        System.setProperty("DB_PASSWORD", clave);
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {
            for (String sentencia : sentenciasDelEsquema()) {
                statement.execute(sentencia);
            }
            return true;
        } catch (SQLException | IOException exception) {
            throw new IllegalStateException("No se pudo preparar la base de datos de pruebas.", exception);
        }
    }

    public static void limpiar() throws SQLException {
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM criterio_busqueda");
            statement.execute("DELETE FROM usuario");
            statement.execute("DELETE FROM vuelo");
            statement.execute("DELETE FROM hospedaje");
            statement.execute("DELETE FROM restaurante");
        }
    }

    public static void ejecutar(String sql, Object... parametros) throws SQLException {
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                statement.setObject(i + 1, parametros[i]);
            }
            statement.executeUpdate();
        }
    }

    // Lee sql/esquema.sql sin las líneas de comentario y sin CREATE DATABASE / USE.
    private static List<String> sentenciasDelEsquema() throws IOException {
        StringBuilder texto = new StringBuilder();
        for (String linea : Files.readAllLines(Path.of("sql", "esquema.sql"), StandardCharsets.UTF_8)) {
            if (!linea.trim().startsWith("--")) {
                texto.append(linea).append('\n');
            }
        }
        List<String> sentencias = new ArrayList<>();
        for (String sentencia : texto.toString().split(";")) {
            String limpia = sentencia.trim();
            String mayusculas = limpia.toUpperCase();
            if (!limpia.isEmpty() && !mayusculas.startsWith("CREATE DATABASE") && !mayusculas.startsWith("USE ")) {
                sentencias.add(limpia);
            }
        }
        return sentencias;
    }

    private static String valor(String nombre, String porDefecto) {
        String valor = System.getenv(nombre);
        return valor == null || valor.isBlank() ? porDefecto : valor;
    }
}
