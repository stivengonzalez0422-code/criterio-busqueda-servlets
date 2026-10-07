# Documentación por módulo y componente

Para cada componente se indican las **entradas** (lo que recibe) y las **salidas** (lo que entrega). Todas las rutas son relativas al contexto de la aplicación: `/criterio-busqueda-servlets`.

## Módulo 1 · Usuarios (`co.edu.sena.usuarios`)

Registro e inicio de sesión sobre la tabla `usuario`.

| Componente | Entrada | Salida |
| --- | --- | --- |
| `AutenticacionServlet` (`/login`, `/registro`, `/logout`) | GET: nada. POST: campos del formulario y `_csrf` | Vista `login.jsp` / `registro.jsp`, o redirección (`/inicio`, `/login`) |
| `AutenticacionService.registrar` | Mapa con `nombre, apellido, cedula, fecha_nacimiento, correo, telefono, contrasena, confirmacion` | Mapa de errores por campo (vacío = usuario creado con hash de contraseña) |
| `AutenticacionService.autenticar` | `correo`, `contrasena` | `Optional<Usuario>` (vacío si los datos son incorrectos o el usuario está inactivo) |
| `UsuarioValidator` | Mapa de valores y un `Usuario` vacío | Errores por campo; el `Usuario` queda con los datos normalizados (correo en minúsculas, teléfono sin espacios) |
| `UsuarioDao` / `UsuarioDaoJdbc` | Correo, cédula o un `Usuario` | `Optional<Usuario>`, booleanos de existencia, id generado |

| Método | Ruta | Acción |
| --- | --- | --- |
| GET | `/login` | Formulario de inicio de sesión |
| POST | `/login` | Valida credenciales; éxito → `/inicio` |
| GET | `/registro` | Formulario de registro |
| POST | `/registro` | Crea la cuenta; éxito → `/login` con mensaje |
| POST | `/logout` | Cierra la sesión → `/login` |

Reglas del registro: nombre y apellido solo letras; cédula de 5 a 20 dígitos; fecha de nacimiento pasada; correo válido y único; teléfono opcional de 7 a 15 dígitos; contraseña de 8 a 72 caracteres con letra y número, confirmada. Cédula y correo repetidos se rechazan.

## Módulo 2 · Criterios de búsqueda (`co.edu.sena.criterios`)

CRUD sobre `criterio_busqueda`, limitado al usuario autenticado.

| Componente | Entrada | Salida |
| --- | --- | --- |
| `CriterioBusquedaServlet` (`/criterios/*`) | GET con `id` o POST con campos y `_csrf`; usuario de la sesión | Vistas `list`, `form`, `detail`; redirección tras escribir; 400/404 si el id no es válido o no es del usuario |
| `CriterioValidator` | Mapa `destino, fecha_inicio, fecha_fin, precio_maximo, horario, preferencias, estado` | Errores por campo; el modelo queda normalizado (textos recortados, precio a 2 decimales) |
| `CriterioBusquedaDao` | Ids de criterio y de usuario, o un `CriterioBusqueda` | Listas, `Optional`, booleanos de filas afectadas, id generado |
| `CriterioBusqueda` | – | Modelo con los 10 campos de la tabla |

| Método | Ruta | Acción |
| --- | --- | --- |
| GET | `/criterios` | Listado de mis criterios |
| GET | `/criterios/nuevo` | Formulario de creación |
| POST | `/criterios/crear` | Crea el criterio (el `id_usuario` sale de la sesión) |
| GET | `/criterios/ver?id=N` | Detalle |
| GET | `/criterios/editar?id=N` | Formulario de edición |
| POST | `/criterios/actualizar` | Actualiza (solo si es del usuario) |
| POST | `/criterios/eliminar` | Elimina (solo si es del usuario; nunca por GET) |

Reglas: destino obligatorio (máx. 100); fechas válidas con `fecha_fin ≥ fecha_inicio`; precio entre 0 y 9.999.999.999,99 con máximo 2 decimales; horario máx. 100 caracteres. Son las mismas restricciones `CHECK` de la base de datos.

## Módulo 3 · Ofertas (`co.edu.sena.ofertas`)

Consulta de vuelos, hospedajes y restaurantes que cumplen un criterio. Es de solo lectura.

| Componente | Entrada | Salida |
| --- | --- | --- |
| `OfertaServlet` (`/ofertas/criterio`) | `id` del criterio; usuario de la sesión | Vista `resultados.jsp` con tres listas; 404 si el criterio no es del usuario |
| `OfertaDao.buscarVuelos` | Un `CriterioBusqueda` | Hasta 20 vuelos con `destino` coincidente, fecha de salida dentro del rango, `precio ≤ precio_maximo`, disponibles y activos; orden por precio |
| `OfertaDao.buscarHospedajes` | Un `CriterioBusqueda` | Hospedajes con `ubicacion` coincidente y `precio_noche ≤ precio_maximo`, disponibles y activos |
| `OfertaDao.buscarRestaurantes` | Un `CriterioBusqueda` | Restaurantes con `ubicacion` coincidente y `precio_promedio ≤ precio_maximo`, disponibles y activos; por calificación |
| `Vuelo`, `Hospedaje`, `Restaurante` | – | Modelos (`Vuelo` ofrece fechas y duración ya formateadas) |

| Método | Ruta | Acción |
| --- | --- | --- |
| GET | `/ofertas/criterio?id=N` | Ofertas para el criterio N |

Criterios de coincidencia: el destino se compara por contenido y sin distinguir mayúsculas (`Carta` coincide con `Cartagena`). Los campos `horario` y `preferencias` del criterio se guardan pero **no filtran** las ofertas, porque las tablas de ofertas no tienen un campo equivalente. La duración del vuelo (`duracion`) se interpreta en minutos; el SQL original no indica la unidad.

## Componentes transversales

| Componente | Entrada | Salida |
| --- | --- | --- |
| `InicioServlet` (`""`, `/inicio`) | Sesión | Panel con el conteo de criterios; la raíz redirige a `/inicio` o `/login` |
| `AutenticacionFilter` | Cualquier solicitud a `/inicio`, `/criterios/*`, `/ofertas/*` | Pasa la solicitud o redirige a `/login`; añade `Cache-Control: no-store` |
| `CsrfFilter` | Todas las solicitudes | Fija UTF-8, añade cabeceras de seguridad, crea el token y responde 403 a POST sin token válido |
| `SesionUsuario` | Solicitud | Usuario actual, inicio de sesión (con cambio de id de sesión), cierre |
| `PasswordHasher` | Contraseña | Hash PBKDF2-HMAC-SHA256 con sal; verificación en tiempo constante |
| `Database` | Variables `DB_URL`, `DB_USER`, `DB_PASSWORD` | Conexión JDBC (carga el driver explícitamente) |
| `Flash` | Mensaje | Mensaje de una sola lectura tras una redirección |
| `Textos` | Textos y parámetros | Utilidades de normalización y escape de `LIKE` |

## Datos (`sql/`)

| Archivo | Contenido |
| --- | --- |
| `esquema.sql` | Tablas `usuario`, `criterio_busqueda`, `vuelo`, `hospedaje`, `restaurante` con `IF NOT EXISTS` (definiciones del SQL original del proyecto) |
| `datos_demo.sql` | Catálogos de demostración (11 vuelos, 8 hospedajes, 6 restaurantes). **No crea usuarios.** |
