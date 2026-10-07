<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Criterios de búsqueda"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jspf" %>
<header class="page-header">
    <div>
        <p class="eyebrow">Módulo de criterios</p>
        <h1>Mis criterios de búsqueda</h1>
        <p class="muted">Administre las preferencias de viaje que ha registrado.</p>
    </div>
    <a class="button button-primary" href="${pageContext.request.contextPath}/criterios/nuevo">Nuevo criterio</a>
</header>

<c:if test="${not empty mensaje}">
    <div class="notice notice-success" role="status"><c:out value="${mensaje}"/></div>
</c:if>

<section class="card" aria-labelledby="listado-titulo">
    <div class="card-heading">
        <h2 id="listado-titulo">Listado</h2>
        <span class="muted"><c:out value="${criterios.size()}"/> registros</span>
    </div>
    <c:choose>
        <c:when test="${empty criterios}">
            <p class="empty-state">Todavía no tiene criterios de búsqueda. Cree el primero con "Nuevo criterio".</p>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th>Destino</th>
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
                            <td><c:out value="${criterio.fechaInicio}"/> a <c:out value="${criterio.fechaFin}"/></td>
                            <td>$ <fmt:formatNumber value="${criterio.precioMaximo}" maxFractionDigits="2"/></td>
                            <td>
                                <span class="status ${criterio.estado ? 'status-active' : 'status-inactive'}">
                                    <c:out value="${criterio.estado ? 'Activo' : 'Inactivo'}"/>
                                </span>
                            </td>
                            <td class="actions">
                                <a href="${pageContext.request.contextPath}/ofertas/criterio?id=${criterio.idCriterio}">Ver ofertas</a>
                                <a href="${pageContext.request.contextPath}/criterios/ver?id=${criterio.idCriterio}">Ver</a>
                                <a href="${pageContext.request.contextPath}/criterios/editar?id=${criterio.idCriterio}">Editar</a>
                                <form action="${pageContext.request.contextPath}/criterios/eliminar" method="post"
                                      data-confirm="¿Eliminar este criterio de búsqueda? Esta acción no se puede deshacer.">
                                    <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
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
<%@ include file="/WEB-INF/views/layout/pie.jspf" %>
