<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Ofertas para ${criterio.destino}"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jspf" %>
<a class="back-link" href="${pageContext.request.contextPath}/criterios">← Volver a mis criterios</a>
<header class="page-header compact">
    <div>
        <p class="eyebrow">Módulo de ofertas</p>
        <h1>Ofertas para <c:out value="${criterio.destino}"/></h1>
        <p class="muted">
            Del <c:out value="${criterio.fechaInicio}"/> al <c:out value="${criterio.fechaFin}"/> ·
            presupuesto hasta $ <fmt:formatNumber value="${criterio.precioMaximo}" maxFractionDigits="2"/>
        </p>
    </div>
</header>

<section class="card" aria-labelledby="vuelos-titulo">
    <div class="card-heading">
        <h2 id="vuelos-titulo">Vuelos</h2>
        <span class="muted"><c:out value="${vuelos.size()}"/> opciones</span>
    </div>
    <c:choose>
        <c:when test="${empty vuelos}">
            <p class="empty-state">No hay vuelos disponibles hacia este destino, en esas fechas y dentro del presupuesto.</p>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>Ruta</th><th>Salida</th><th>Llegada</th><th>Duración</th><th>Aerolínea</th><th>Precio</th></tr></thead>
                    <tbody>
                    <c:forEach var="vuelo" items="${vuelos}">
                        <tr>
                            <td><c:out value="${vuelo.origen}"/> → <c:out value="${vuelo.destino}"/></td>
                            <td><c:out value="${vuelo.fechaSalidaTexto}"/></td>
                            <td><c:out value="${vuelo.fechaLlegadaTexto}"/></td>
                            <td><c:out value="${vuelo.duracionTexto}"/></td>
                            <td><c:out value="${vuelo.aerolinea}"/></td>
                            <td><strong>$ <fmt:formatNumber value="${vuelo.precio}" maxFractionDigits="2"/></strong></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<section class="card section-gap" aria-labelledby="hospedajes-titulo">
    <div class="card-heading">
        <h2 id="hospedajes-titulo">Hospedajes</h2>
        <span class="muted"><c:out value="${hospedajes.size()}"/> opciones</span>
    </div>
    <c:choose>
        <c:when test="${empty hospedajes}">
            <p class="empty-state">No hay hospedajes disponibles en este destino dentro del presupuesto.</p>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>Nombre</th><th>Ubicación</th><th>Calificación</th><th>Descripción</th><th>Precio por noche</th></tr></thead>
                    <tbody>
                    <c:forEach var="hospedaje" items="${hospedajes}">
                        <tr>
                            <td><strong><c:out value="${hospedaje.nombre}"/></strong></td>
                            <td><c:out value="${hospedaje.ubicacion}"/></td>
                            <td><c:out value="${empty hospedaje.calificacion ? '–' : hospedaje.calificacion}"/></td>
                            <td class="wrap"><c:out value="${hospedaje.descripcion}"/></td>
                            <td><strong>$ <fmt:formatNumber value="${hospedaje.precioNoche}" maxFractionDigits="2"/></strong></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<section class="card section-gap" aria-labelledby="restaurantes-titulo">
    <div class="card-heading">
        <h2 id="restaurantes-titulo">Restaurantes</h2>
        <span class="muted"><c:out value="${restaurantes.size()}"/> opciones</span>
    </div>
    <c:choose>
        <c:when test="${empty restaurantes}">
            <p class="empty-state">No hay restaurantes disponibles en este destino dentro del presupuesto.</p>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>Nombre</th><th>Ubicación</th><th>Tipo de comida</th><th>Calificación</th><th>Precio promedio</th></tr></thead>
                    <tbody>
                    <c:forEach var="restaurante" items="${restaurantes}">
                        <tr>
                            <td><strong><c:out value="${restaurante.nombre}"/></strong></td>
                            <td><c:out value="${restaurante.ubicacion}"/></td>
                            <td><c:out value="${restaurante.tipoComida}"/></td>
                            <td><c:out value="${empty restaurante.calificacion ? '–' : restaurante.calificacion}"/></td>
                            <td><strong>$ <fmt:formatNumber value="${restaurante.precioPromedio}" maxFractionDigits="2"/></strong></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</section>
<%@ include file="/WEB-INF/views/layout/pie.jspf" %>
