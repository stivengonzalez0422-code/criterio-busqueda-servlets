<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Inicio"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jspf" %>
<header class="page-header">
    <div>
        <p class="eyebrow">Panel principal</p>
        <h1>Hola, <c:out value="${sessionScope.usuarioSesion.nombreCompleto}"/></h1>
        <p class="muted">Defina sus criterios de viaje y consulte las ofertas que los cumplen.</p>
    </div>
</header>

<section class="stat-grid" aria-label="Módulos">
    <article class="card module-card">
        <h2>Criterios de búsqueda</h2>
        <p class="muted">Registre destino, fechas, presupuesto y preferencias de sus viajes.</p>
        <p class="stat">
            <c:choose>
                <c:when test="${totalCriterios != null}"><c:out value="${totalCriterios}"/></c:when>
                <c:otherwise>–</c:otherwise>
            </c:choose>
            <span class="muted">criterios registrados</span>
        </p>
        <div class="module-actions">
            <a class="button button-primary" href="${pageContext.request.contextPath}/criterios/nuevo">Nuevo criterio</a>
            <a class="button button-secondary" href="${pageContext.request.contextPath}/criterios">Ver mis criterios</a>
        </div>
    </article>
    <article class="card module-card">
        <h2>Ofertas de viaje</h2>
        <p class="muted">Vuelos, hospedajes y restaurantes que coinciden con cada criterio. Para verlas, abra la lista de criterios y elija "Ver ofertas".</p>
        <div class="module-actions">
            <a class="button button-secondary" href="${pageContext.request.contextPath}/criterios">Elegir un criterio</a>
        </div>
    </article>
</section>
<%@ include file="/WEB-INF/views/layout/pie.jspf" %>
