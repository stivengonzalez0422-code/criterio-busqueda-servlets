<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Detalle del criterio | Agenda de viajes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<main class="container narrow">
    <a class="back-link" href="${pageContext.request.contextPath}/criterios">← Volver al listado</a>
    <header class="page-header compact">
        <div>
            <p class="eyebrow">Criterio #<c:out value="${criterio.idCriterio}"/></p>
            <h1><c:out value="${criterio.destino}"/></h1>
        </div>
        <a class="button button-primary" href="${pageContext.request.contextPath}/criterios/editar?id=${criterio.idCriterio}">Editar</a>
    </header>
    <section class="card detail-card">
        <dl class="detail-list">
            <div><dt>Usuario</dt><dd>#<c:out value="${criterio.idUsuario}"/></dd></div>
            <div><dt>Fecha de inicio</dt><dd><c:out value="${criterio.fechaInicio}"/></dd></div>
            <div><dt>Fecha de fin</dt><dd><c:out value="${criterio.fechaFin}"/></dd></div>
            <div><dt>Precio máximo</dt><dd>$<c:out value="${criterio.precioMaximo}"/></dd></div>
            <div><dt>Horario</dt><dd><c:out value="${empty criterio.horario ? 'Sin preferencia' : criterio.horario}"/></dd></div>
            <div><dt>Preferencias</dt><dd><c:out value="${empty criterio.preferencias ? 'Sin preferencias' : criterio.preferencias}"/></dd></div>
            <div><dt>Estado</dt><dd><c:out value="${criterio.estado ? 'Activo' : 'Inactivo'}"/></dd></div>
            <div><dt>Creado</dt><dd><c:out value="${criterio.fechaCreacion}"/></dd></div>
        </dl>
    </section>
</main>
</body>
</html>
