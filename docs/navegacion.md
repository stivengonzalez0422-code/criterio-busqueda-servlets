# Mapa de navegación

```mermaid
flowchart TD
    raiz["/ (raíz)"] -->|sin sesión| login
    raiz -->|con sesión| inicio
    login["/login<br/>Iniciar sesión"] -->|credenciales correctas| inicio
    login -->|Crear cuenta| registro["/registro<br/>Crear cuenta"]
    registro -->|cuenta creada| login

    inicio["/inicio<br/>Panel principal"] --> lista
    inicio --> nuevo
    lista["/criterios<br/>Mis criterios"] --> nuevo["/criterios/nuevo<br/>Formulario"]
    lista --> ver["/criterios/ver?id=<br/>Detalle"]
    lista --> editar["/criterios/editar?id=<br/>Formulario"]
    lista -->|Eliminar POST con confirmación| lista
    lista --> ofertas["/ofertas/criterio?id=<br/>Vuelos · Hospedajes · Restaurantes"]
    ver --> ofertas
    ver --> editar
    nuevo -->|Crear POST| lista
    editar -->|Guardar POST| lista

    inicio -->|Cerrar sesión POST| login
```

| Pantalla | Quién accede | Enlaces visibles |
| --- | --- | --- |
| Login y registro | Cualquier visitante | Entre sí |
| Inicio, criterios, detalle, formularios y ofertas | Solo usuarios con sesión | Menú superior (Inicio, Criterios de búsqueda, Cerrar sesión) |
| Error (400, 403, 404, 405, 500) | Cualquiera | Volver al inicio |
