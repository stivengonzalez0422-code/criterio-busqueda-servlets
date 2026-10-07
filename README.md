# Agenda con tu asistente virtual — módulos integrados

Aplicación web en **Java 17, Servlets, JSP/JSTL, HTML5, CSS3, JavaScript, JDBC y MySQL**, construida con **Maven** y desplegada en **Apache Tomcat 10.1**. Proyecto SENA, evidencia GA8-220501096-AA1-EV01: *desarrollar software a partir de la integración de sus módulos componentes*.

La base de datos es `agenda_asistente_viajes`. La tabla principal del módulo original es `criterio_busqueda` (relación Usuario 1:N Criterio de búsqueda).

## Módulos

| Módulo | Qué hace | Tablas |
| --- | --- | --- |
| **Usuarios** | Registro, inicio y cierre de sesión con contraseña protegida | `usuario` |
| **Criterios de búsqueda** | Registrar, consultar, ver, actualizar y eliminar criterios propios | `criterio_busqueda` |
| **Ofertas** | Muestra vuelos, hospedajes y restaurantes que cumplen un criterio | `vuelo`, `hospedaje`, `restaurante` |

Integración: el usuario autenticado es dueño de sus criterios (nadie ve los de otros) y cada criterio se convierte en una búsqueda de ofertas. Los detalles están en [docs/arquitectura.md](docs/arquitectura.md).

Los formularios usan `GET` para mostrar y consultar, y `POST` (con token CSRF) para crear, actualizar, eliminar, registrar e iniciar o cerrar sesión. Nada se elimina por `GET`.

## Inicio rápido

Requisitos: JDK 17, Maven 3.8+, Tomcat 10.1 y MySQL 8.

```powershell
# 1. Base de datos (seguro de repetir; no borra datos)
mysql -u root --default-character-set=utf8mb4 -e "source sql/esquema.sql"
mysql -u root --default-character-set=utf8mb4 -e "source sql/datos_demo.sql"   # opcional, una sola vez

# 2. Credenciales de MySQL (solo si no usa root sin contraseña)
$env:DB_USER = "root"
$env:DB_PASSWORD = "su_clave_local"

# 3. Construir y probar
mvn clean package

# 4. Desplegar: copiar target\criterio-busqueda-servlets.war a webapps de Tomcat 10.1 y arrancarlo
```

Abrir `http://localhost:8080/criterio-busqueda-servlets/`, crear una cuenta en **Crear cuenta** e iniciar sesión. Los usuarios no se cargan por script: se registran desde la aplicación.

Detalles de servidores, variables y problemas frecuentes: [docs/configuracion.md](docs/configuracion.md).

## Documentación

| Documento | Contenido |
| --- | --- |
| [docs/arquitectura.md](docs/arquitectura.md) | Capas, librerías, paquetes, componentes, clases y patrones |
| [docs/modulos.md](docs/modulos.md) | Entradas, salidas y rutas de cada módulo y componente |
| [docs/navegacion.md](docs/navegacion.md) | Mapa de navegación |
| [docs/seguridad.md](docs/seguridad.md) | Mecanismos de seguridad y límites conocidos |
| [docs/configuracion.md](docs/configuracion.md) | Configuración de Tomcat, MySQL y ambientes |
| [docs/pruebas.md](docs/pruebas.md) | Informe de pruebas y defectos corregidos |
| [docs/despliegue.md](docs/despliegue.md) | URLs y ejecutables |

## Estructura

```text
src/main/java/co/edu/sena/
  usuarios/{model,dao,service,validation,web}
  criterios/{model,dao,validation,web}
  ofertas/{model,dao,web}
  seguridad/       Filtros, sesión y hash de contraseñas
  inicio/          Panel principal
  comun/           Conexión JDBC y utilidades
src/main/webapp/
  WEB-INF/views/   JSP por módulo y plantilla común (layout)
  WEB-INF/web.xml  Sesión, codificación y páginas de error
  assets/          CSS y JavaScript
src/test/java/     Pruebas JUnit por módulo
sql/               Esquema y datos de demostración
scripts/           Prueba de integración HTTP
docs/              Documentación
```

## Pruebas

- `mvn clean package` ejecuta 42 pruebas JUnit (28 unitarias y 14 de integración con MySQL). Las de integración usan la base `agenda_asistente_viajes_test` y se omiten si MySQL no está disponible.
- `scripts/prueba-integracion.ps1` recorre la aplicación desplegada (22 comprobaciones). Ver [docs/pruebas.md](docs/pruebas.md).

## Repositorio

<https://github.com/stivengonzalez0422-code/criterio-busqueda-servlets>
