<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Criterios de búsqueda | Agenda de viajes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<main class="container">
    <header class="page-header">
        <div>
            <p class="eyebrow">Agenda de viajes</p>
            <h1>Criterios de búsqueda</h1>
            <p class="muted">Administre las preferencias de viaje registradas.</p>
        </div>
        <a class="button button-primary" href="${pageContext.request.contextPath}/criterios/nuevo">Nuevo criterio</a>
    </header>

    <c:if test="${not empty mensaje}">
        <div class="notice notice-success" role="status"><c:out value="${mensaje}"/></div>
    </c:if>
    <c:if test="${not empty errorGeneral}">
        <div class="notice notice-error" role="alert"><c:out value="${errorGeneral}"/></div>
    </c:if>

    <section class="card" aria-labelledby="listado-titulo">
        <div class="card-heading">
            <h2 id="listado-titulo">Listado</h2>
            <span class="muted"><c:out value="${criterios.size()}"/> registros</span>
        </div>
        <c:choose>
            <c:when test="${empty criterios}">
                <p class="empty-state">Todavía no hay criterios de búsqueda registrados.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Destino</th>
                            <th>Usuario</th>
                            <th>Fechas</th>
                            <th>Precio máximo</th>
                            <th>Estado</th>
                            <th><span class="visually-hidden">Acciones</span></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="criterio" items="${criterios}">
                            <tr>
                                <td><strong><c:out value="${criterio.destino}"/></strong></td>
                                <td>#<c:out value="${criterio.idUsuario}"/></td>
                                <td><c:out value="${criterio.fechaInicio}"/> a <c:out value="${criterio.fechaFin}"/></td>
                                <td>$<c:out value="${criterio.precioMaximo}"/></td>
                                <td>
                                    <span class="status ${criterio.estado ? 'status-active' : 'status-inactive'}">
                                        <c:out value="${criterio.estado ? 'Activo' : 'Inactivo'}"/>
                                    </span>
                                </td>
                                <td class="actions">
                                    <a href="${pageContext.request.contextPath}/criterios/ver?id=${criterio.idCriterio}">Ver</a>
                                    <a href="${pageContext.request.contextPath}/criterios/editar?id=${criterio.idCriterio}">Editar</a>
                                    <form action="${pageContext.request.contextPath}/criterios/eliminar" method="post"
                                          onsubmit="return confirm('¿Eliminar este criterio de búsqueda?');">
                                        <input type="hidden" name="id_criterio" value="${criterio.idCriterio}">
                                        <button class="link-button danger-link" type="submit">Eliminar</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
    <footer class="page-footer">Módulo de criterios de búsqueda · Proyecto Agenda Asistente de Viajes</footer>
</main>
</body>
</html>
