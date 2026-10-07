# Informe de pruebas

Fecha de ejecución: 5 de octubre de 2026.

## Ambiente de pruebas

| Elemento | Valor |
| --- | --- |
| Sistema operativo | Windows 11 |
| JDK / Maven | OpenJDK 17.0.10 / Apache Maven 3.9.9 |
| Servidor de aplicaciones | Apache Tomcat 10.1.41 |
| Base de datos | MySQL 8.4.3 (Laragon), base `agenda_asistente_viajes_test` |
| JUnit | 5.11.4 |

Las pruebas automáticas crean la base de pruebas con el mismo `sql/esquema.sql` del proyecto y la vacían antes de cada prueba. No tocan `agenda_asistente_viajes`.

## 1. Pruebas unitarias y de integración con JUnit

Comando: `mvn clean package` → **42 pruebas, 0 fallos, 0 errores, 0 omitidas.**

| Módulo | Clase de prueba | Tipo | Pruebas | Qué verifica |
| --- | --- | --- | ---: | --- |
| Usuarios | `UsuarioValidatorTest` | Unitaria | 6 | Campos obligatorios, cédula, correo, nombre, fecha de nacimiento, contraseña y teléfono |
| Usuarios | `AutenticacionServiceTest` | Unitaria (DAO en memoria) | 6 | Registro con solo el hash, correo/cédula repetidos, datos inválidos, inicio de sesión, usuario inactivo |
| Usuarios | `UsuarioDaoJdbcTest` | Integración (MySQL) | 3 | Insertar, buscar por correo (incluida la columna `contraseña`), existencia, correo único |
| Criterios | `CriterioValidatorTest` | Unitaria | 6 | Normalización, fechas invertidas, precio negativo o con más de 2 decimales, destino largo, casilla `estado` |
| Criterios | `CriterioBusquedaDaoTest` | Integración (MySQL) | 5 | Crear, consultar, aislamiento entre usuarios, actualizar/eliminar solo si es del dueño, clave foránea |
| Ofertas | `OfertaDaoTest` | Integración (MySQL) | 6 | Coincidencia por destino, fechas, disponibilidad, estado, precio; comodines `%` literales; horas sin desplazamiento |
| Ofertas | `VueloTest` | Unitaria | 2 | Formato de duración y fechas |
| Seguridad | `PasswordHasherTest` | Unitaria | 5 | Hash con sal, verificación, formatos inválidos, tokens CSRF únicos |
| Común | `TextosTest` | Unitaria | 3 | Recorte, enteros positivos, escape de `LIKE` |
| **Total** | | | **42** | 28 unitarias + 14 de integración |

Los reportes de Surefire se generan en `target/surefire-reports/`.

## 2. Pruebas de integración HTTP

Script: `scripts/prueba-integracion.ps1` (Windows PowerShell 5.1). Recorre la aplicación desplegada en Tomcat, con dos usuarios distintos. Resultado: **22 de 22 comprobaciones superadas.**

| Grupo | Comprobaciones |
| --- | --- |
| Acceso y seguridad | Redirección a `/login` sin sesión (raíz y rutas privadas); login sin caracteres corruptos y con token CSRF; POST sin token → 403 |
| Usuarios | Registro inválido muestra errores; registro e inicio de sesión; correo repetido rechazado; contraseña incorrecta rechazada; panel con el nombre |
| Criterios | Creación inválida muestra errores; crear y ver en el listado; detalle y edición por GET; actualizar por POST |
| Ofertas | Vuelos, hospedaje y restaurante esperados para el criterio; ninguna oferta de otro destino |
| Aislamiento | El usuario B no ve el listado de A; recibe 404 al ver, editar, consultar ofertas o eliminar un criterio de A |
| Eliminación segura | `GET` no elimina; `POST` sin token → 403; el dueño elimina con `POST` y token |
| Sesión | Cerrar sesión por POST bloquea el acceso |

Requisitos para repetirlo: aplicación desplegada contra una **base de pruebas** que tenga los catálogos de `datos_demo.sql` cargados (el script crea usuarios de prueba con correos únicos y espera las ofertas de demostración de Cartagena).

```powershell
.\scripts\prueba-integracion.ps1 -BaseUrl http://localhost:8080/criterio-busqueda-servlets
```

## 3. Prueba manual en navegador

Se recorrió en el navegador integrado de VS Code: registro, inicio de sesión, creación de un criterio, listado y ofertas. Se observó el aspecto de las pantallas, los menús y los mensajes.

## 4. Defectos encontrados y corregidos

| # | Defecto | Cómo se detectó | Corrección | Prueba que lo cubre |
| --- | --- | --- | --- | --- |
| 1 | `No suitable driver found for jdbc:mysql` al ejecutar en Tomcat: el CRUD entregado antes no conectaba. | Prueba del WAR en Tomcat | `Database` carga el driver explícitamente | Todo el script HTTP (necesita conexión real) |
| 2 | Menú y pie de página con caracteres corruptos (`Ã³`): los fragmentos `.jspf` no se leían como UTF-8. | Revisión visual en el navegador | `page-encoding` UTF-8 en `web.xml` | Comprobación de caracteres corruptos en el script HTTP |
| 3 | Las horas de los vuelos aparecían 5 horas antes (07:15 se mostraba como 02:15) por la conversión de zona horaria del driver. | Revisión visual de las ofertas | Leer `DATETIME` con `getObject(..., LocalDateTime.class)` | `lasHorasDeLosVuelosSeLeenTalComoEstanGuardadas` (se comprobó que falla con el código anterior) |

Los defectos 2 y 3 no los detectaban las pruebas anteriores: por eso se añadieron las comprobaciones indicadas.

## 5. Alcance y límites

- El JavaScript (`assets/js/app.js`) se probó manualmente; no hay pruebas automáticas de navegador.
- Los servlets se prueban a través del script HTTP, no con pruebas unitarias con simulación de solicitudes.
- Los catálogos de ofertas son datos de demostración: no hay un módulo para administrarlos.
- No se probó con Tomcat instalado como servicio de Windows ni detrás de HTTPS.
- La versión de Java verificada es la 17. No se probó con otras versiones.
