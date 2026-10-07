<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="codigo" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<c:set var="tituloPagina" value="No fue posible completar la solicitud"/>
<c:set var="claseContenedor" value="narrow"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jspf" %>
<section class="card detail-card">
    <p class="eyebrow">Agenda de viajes</p>
    <h1>
        <c:choose>
            <c:when test="${codigo == 404}">Página no encontrada</c:when>
            <c:when test="${codigo == 403}">Solicitud no permitida</c:when>
            <c:otherwise>No fue posible completar la solicitud</c:otherwise>
        </c:choose>
    </h1>
    <p class="notice notice-error" role="alert">
        <c:choose>
            <c:when test="${not empty errorGeneral}"><c:out value="${errorGeneral}"/></c:when>
            <c:when test="${codigo == 404}">El recurso que busca no existe o no le pertenece.</c:when>
            <c:when test="${codigo == 403}">La sesión expiró o la solicitud no es válida. Vuelva a cargar la página e inténtelo de nuevo.</c:when>
            <c:when test="${codigo == 400}">Los datos enviados no son válidos.</c:when>
            <c:otherwise>Ocurrió un error inesperado. Intente nuevamente.</c:otherwise>
        </c:choose>
    </p>
    <a class="button button-primary" href="${pageContext.request.contextPath}/inicio">Ir al inicio</a>
</section>
<%@ include file="/WEB-INF/views/layout/pie.jspf" %>
