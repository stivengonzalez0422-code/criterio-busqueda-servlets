# Criterios de búsqueda de viajes

Aplicación web pequeña para administrar los registros de `criterio_busqueda` de la base `agenda_asistente_viajes`. Está construida con Java 17, Maven, Jakarta Servlets, JSP/JSTL y JDBC para MySQL. No incluye registro ni creación de usuarios: el `id_usuario` debe existir previamente en la tabla `usuario`.

## Funciones

- Consultar el listado y el detalle de criterios.
- Crear, actualizar y eliminar criterios.
- Usar solicitudes `GET` para consultar y mostrar formularios; las operaciones de escritura usan `POST`.
- Validar los campos requeridos, fechas, precio, longitudes y existencia del usuario.
- Ejecutar consultas parametrizadas con `PreparedStatement`.

## Requisitos

- JDK 17 o superior.
- Maven 3.8 o superior.
- Apache Tomcat 10.1 (Jakarta Servlet 6.0).
- MySQL 8 con la base de datos y las tablas `usuario` y `criterio_busqueda`.

El SQL existente del proyecto define el esquema. Si la tabla aún no está creada, ejecute `sql\criterio_busqueda.sql` después de confirmar que existe la tabla `usuario`. No ejecute un script que elimine o reinicie datos existentes.

## Configuración local

Configure estas variables de entorno antes de iniciar Tomcat:

| Variable | Valor predeterminado | Descripción |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://localhost:3306/agenda_asistente_viajes?serverTimezone=UTC` | URL JDBC |
| `DB_USER` | `root` | Usuario de MySQL local |
| `DB_PASSWORD` | vacío | Contraseña de MySQL |

En Windows PowerShell, por ejemplo:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/agenda_asistente_viajes?serverTimezone=UTC"
$env:DB_USER = "root"
$env:DB_PASSWORD = "su_clave_local"
```

No guarde contraseñas reales en Git. El conector JDBC se incluye en el WAR mediante Maven.

## Construcción y ejecución

Desde la raíz del proyecto:

```powershell
mvn clean test package
```

Copie `target\criterio-busqueda-servlets.war` a la carpeta `webapps` de Tomcat 10.1, inicie Tomcat y abra:

```text
http://localhost:8080/criterio-busqueda-servlets/criterios
```

Si Tomcat usa otro puerto o contexto, ajuste la URL. La cuenta MySQL configurada debe tener permisos de lectura y escritura sobre `agenda_asistente_viajes`.

## Prueba del flujo

1. Verifique que MySQL esté iniciado y que haya al menos un usuario registrado por el proceso correspondiente.
2. Abra el listado de criterios.
3. Cree un criterio con un `id_usuario` existente, destino, fechas válidas y precio no negativo.
4. Consulte, edite y elimine un registro desde las acciones del listado.

Si `usuario` está vacía, el formulario no creará usuarios ficticios: indicará que el identificador ingresado no existe.

## Estructura

```text
src/main/java/co/edu/sena/criterios/
  dao/                 Acceso JDBC al criterio y validación de usuario
  model/               Modelo de dominio
  util/                Conexión JDBC
  validation/          Reglas de validación de formularios
  web/                 Servlet de rutas GET y POST
src/main/webapp/
  WEB-INF/views/       Vistas JSP
  assets/              Estilos de la aplicación
```
