# Seguridad

Mecanismos implementados y dónde están.

| Riesgo | Mecanismo | Componente |
| --- | --- | --- |
| Contraseñas expuestas | Se guardan con PBKDF2-HMAC-SHA256, 210.000 iteraciones y sal aleatoria de 16 bytes; nunca en claro | `PasswordHasher` |
| Acceso sin iniciar sesión | Las rutas `/inicio`, `/criterios/*` y `/ofertas/*` redirigen a `/login` | `AutenticacionFilter` |
| Ver o modificar datos de otro usuario | Toda consulta de criterio incluye `id_usuario` de la sesión; si no coincide se responde 404 | `CriterioBusquedaDao`, `CriterioBusquedaServlet`, `OfertaServlet` |
| CSRF | Token aleatorio por sesión en el campo `_csrf`; todo POST sin token válido recibe 403 | `CsrfFilter` |
| Eliminar con un enlace | La eliminación solo existe como POST; un GET devuelve 404 | `CriterioBusquedaServlet` |
| Inyección SQL | `PreparedStatement` en todas las consultas; comodines de `LIKE` escapados | DAO, `Textos` |
| XSS | Salida de datos con `<c:out>` en las vistas | JSP |
| Fijación de sesión | Se cambia el id de sesión y el token al iniciar sesión | `SesionUsuario.iniciar` |
| Enumeración de cuentas | El inicio de sesión responde igual si el correo no existe o la contraseña falla, y gasta tiempo similar | `AutenticacionService` |
| Cookies | `HttpOnly`; tiempo de sesión de 30 minutos | `web.xml` |
| Caché de páginas privadas | `Cache-Control: no-store` | `AutenticacionFilter` |
| Clickjacking y tipos MIME | `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff` | `CsrfFilter` |
| Credenciales en el código | La conexión se configura con variables de entorno; no hay contraseñas en el repositorio | `Database` |

## Límites conocidos

- No hay bloqueo por intentos fallidos de inicio de sesión.
- No hay recuperación de contraseña ni verificación del correo.
- No hay roles: todos los usuarios tienen los mismos permisos sobre sus propios datos.
- La cookie de sesión no es `Secure` porque el entorno de desarrollo usa HTTP; en producción debe publicarse con HTTPS y activar `<secure>` en `web.xml`.
- MySQL en desarrollo se usa con el usuario `root`; en un servidor real conviene un usuario con permisos solo sobre `agenda_asistente_viajes`.
