# Configuración de servidores y base de datos

## Ambientes

| Ambiente | Para qué | Base de datos | Aplicación |
| --- | --- | --- | --- |
| Desarrollo / demostración | Uso normal y demostración al instructor | `agenda_asistente_viajes` | Tomcat local, puerto 8080 |
| Pruebas | Pruebas automáticas y script de integración | `agenda_asistente_viajes_test` (la crean las pruebas) | Tomcat local con `DB_URL` apuntando a la base de pruebas |

Versiones con las que se verificó el proyecto: Windows 11, JDK 17 (Microsoft Build of OpenJDK 17.0.10), Maven 3.9.9, Apache Tomcat 10.1.41 y MySQL 8.4.3 (incluido en Laragon).

## 1. Base de datos MySQL

1. Iniciar MySQL (por ejemplo, desde Laragon) y comprobar que escucha en el puerto 3306.
2. Crear el esquema. Es seguro repetirlo: usa `IF NOT EXISTS` y no borra datos.

   ```powershell
   mysql -u root --default-character-set=utf8mb4 -e "source sql/esquema.sql"
   ```

3. (Opcional) Cargar los catálogos de demostración. Hacerlo una sola vez.

   ```powershell
   mysql -u root --default-character-set=utf8mb4 -e "source sql/datos_demo.sql"
   ```

Los usuarios **no** se cargan por script: se crean desde la pantalla `/registro`.

Si el cliente `mysql` no está en el PATH, use la ruta completa (en Laragon: `C:\laragon\bin\mysql\<versión>\bin\mysql.exe`) o ejecute los scripts desde MySQL Workbench.

## 2. Variables de entorno de la aplicación

| Variable | Valor por defecto | Descripción |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://localhost:3306/agenda_asistente_viajes?serverTimezone=UTC` | URL JDBC |
| `DB_USER` | `root` | Usuario de MySQL |
| `DB_PASSWORD` | vacío | Contraseña de MySQL |

También se aceptan como propiedades del sistema (`-DDB_URL=...`), que tienen prioridad. **No guarde contraseñas reales en el repositorio.**

Con Tomcat instalado como carpeta, lo más cómodo es crear `bin\setenv.bat` (no se versiona):

```bat
set DB_URL=jdbc:mysql://localhost:3306/agenda_asistente_viajes?serverTimezone=UTC
set DB_USER=root
set DB_PASSWORD=su_clave_local
```

Tomcat solo ve las variables definidas antes de arrancarlo; si cambia una, reinícielo.

## 3. Apache Tomcat 10.1

1. Instalar JDK 17 y definir `JAVA_HOME`.
2. Descargar Tomcat 10.1 (Jakarta Servlet 6.0). Tomcat 9 **no** sirve: usa `javax.*`.
3. Copiar `criterio-busqueda-servlets.war` a `webapps\` y ejecutar `bin\startup.bat` (o `bin\catalina.bat run` para ver el registro en la consola).
4. Abrir `http://localhost:8080/criterio-busqueda-servlets/`.

Sin cambios en `conf\`: la aplicación no necesita `context.xml` ni `server.xml` propios. La configuración de sesión y las páginas de error están en `WEB-INF/web.xml`.

## 4. Construcción con Maven

```powershell
mvn clean package            # compila, ejecuta las pruebas y genera target\criterio-busqueda-servlets.war
mvn clean package -DskipTests
```

Las pruebas de integración usan la base `agenda_asistente_viajes_test` y **nunca tocan la base real**. Si MySQL no está disponible se omiten (no fallan). Usan `DB_USER` y `DB_PASSWORD` del entorno.

## Problemas frecuentes

| Síntoma | Causa probable | Solución |
| --- | --- | --- |
| Página "No fue posible consultar los criterios" | MySQL apagado, contraseña incorrecta o base inexistente | Revisar `logs\localhost.*.log` de Tomcat y las variables `DB_*` |
| Error `Access denied for user` | `DB_USER` / `DB_PASSWORD` incorrectos | Corregir la variable y reiniciar Tomcat |
| `404` en todo | Contexto distinto del esperado | El contexto es el nombre del WAR: `criterio-busqueda-servlets` |
| `ClassNotFoundException: javax.servlet...` | Se usó Tomcat 9 | Usar Tomcat 10.1 |
| Puerto 3306 ocupado | Dos servidores MySQL instalados | Detener uno o cambiar el puerto en `DB_URL` |
