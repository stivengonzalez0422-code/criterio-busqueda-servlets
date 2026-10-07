<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Detalle del criterio"/>
<c:set var="claseContenedor" value="narrow"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jspf" %>
<a class="back-link" href="${pageContext.request.contextPath}/criterios">← Volver al listado</a>
<header class="page-header compact">
    <div>
        <p class="eyebrow">Criterio #<c:out value="${criterio.idCriterio}"/></p>
        <h1><c:out value="${criterio.destino}"/></h1>
    </div>
    <div class="module-actions">
        <a class="button button-secondary" href="${pageContext.request.contextPath}/ofertas/criterio?id=${criterio.idCriterio}">Ver ofertas</a>
        <a class="button button-primary" href="${pageContext.request.contextPath}/criterios/editar?id=${criterio.idCriterio}">Editar</a>
    </div>
</header>
<section class="card detail-card">
    <dl class="detail-list">
        <div><dt>Fecha de inicio</dt><dd><c:out value="${criterio.fechaInicio}"/></dd></div>
        <div><dt>Fecha de fin</dt><dd><c:out value="${criterio.fechaFin}"/></dd></div>
        <div><dt>Precio máximo</dt><dd>$ <fmt:formatNumber value="${criterio.precioMaximo}" maxFractionDigits="2"/></dd></div>
        <div><dt>Horario</dt><dd><c:out value="${empty criterio.horario ? 'Sin preferencia' : criterio.horario}"/></dd></div>
        <div><dt>Preferencias</dt><dd><c:out value="${empty criterio.preferencias ? 'Sin preferencias' : criterio.preferencias}"/></dd></div>
        <div><dt>Estado</dt><dd><c:out value="${criterio.estado ? 'Activo' : 'Inactivo'}"/></dd></div>
        <div><dt>Creado</dt><dd><c:out value="${criterio.fechaCreacion}"/></dd></div>
    </dl>
</section>
<%@ include file="/WEB-INF/views/layout/pie.jspf" %>
