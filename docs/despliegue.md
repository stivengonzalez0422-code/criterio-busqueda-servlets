# Despliegue y URLs

La aplicación se despliega en **Apache Tomcat 10.1 local**. Hasta ahora no existe un servidor público: las URLs funcionan en el equipo donde se ejecuta Tomcat.

Base: `http://localhost:8080/criterio-busqueda-servlets`

| Módulo | URL | Requiere sesión |
| --- | --- | --- |
| Entrada | `/` (redirige a `/inicio` o `/login`) | No |
| Usuarios | `/login`, `/registro` | No |
| Usuarios | `/logout` (solo POST) | Sí |
| Panel | `/inicio` | Sí |
| Criterios de búsqueda | `/criterios`, `/criterios/nuevo`, `/criterios/ver?id=N`, `/criterios/editar?id=N` | Sí |
| Ofertas | `/ofertas/criterio?id=N` | Sí |

## Ejecutables entregados

| Archivo | Descripción |
| --- | --- |
| `criterio-busqueda-servlets.war` | Aplicación empaquetada para copiar en `webapps` de Tomcat 10.1 |
| `scripts/prueba-integracion.ps1` | Prueba HTTP de extremo a extremo |

## Repositorio

<https://github.com/stivengonzalez0422-code/criterio-busqueda-servlets>
