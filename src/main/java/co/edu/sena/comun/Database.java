package co.edu.sena.comun;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Punto único de acceso a MySQL. Lee la configuración de variables de entorno
 * (o de propiedades del sistema con el mismo nombre, que tienen prioridad).
 */
public final class Database {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/agenda_asistente_viajes?serverTimezone=UTC";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(
                setting("DB_URL", DEFAULT_URL),
                setting("DB_USER", "root"),
                setting("DB_PASSWORD", ""));
    }

    public static Connection getConnection(String url, String user, String password) throws SQLException {
        loadDriver();
        return DriverManager.getConnection(url, user, password);
    }

    // En Tomcat, DriverManager puede inicializarse antes de cargar el WAR y no ve el
    // driver de WEB-INF/lib; cargarlo explícitamente desde esta clase evita "No suitable driver".
    private static void loadDriver() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException exception) {
            throw new SQLException("No se encontró el controlador JDBC de MySQL (" + DRIVER + ").", exception);
        }
    }

    private static String setting(String name, String defaultValue) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            value = System.getenv(name);
        }
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
