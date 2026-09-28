<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${editing ? 'Editar criterio' : 'Nuevo criterio'}"/> | Agenda de viajes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<main class="container narrow">
    <a class="back-link" href="${pageContext.request.contextPath}/criterios">← Volver al listado</a>
    <header class="page-header compact">
        <div>
            <p class="eyebrow">Criterios de búsqueda</p>
            <h1><c:out value="${editing ? 'Editar criterio' : 'Nuevo criterio'}"/></h1>
            <p class="muted">Complete los datos del viaje que desea buscar.</p>
        </div>
    </header>
    <c:if test="${not empty errorGeneral}">
        <div class="notice notice-error" role="alert"><c:out value="${errorGeneral}"/></div>
    </c:if>
    <form class="card form-card" action="${pageContext.request.contextPath}/criterios/${action}" method="post">
        <c:if test="${editing}">
            <input type="hidden" name="id_criterio" value="${criterio.idCriterio}">
        </c:if>
        <div class="form-grid">
            <div class="field">
                <label for="id_usuario">ID de usuario <span aria-hidden="true">*</span></label>
                <input id="id_usuario" name="id_usuario" type="number" min="1" step="1" required
                       value="<c:out value='${param.id_usuario != null ? param.id_usuario : criterio.idUsuario}'/>"
                       aria-describedby="id_usuario-error">
                <small class="field-hint">Debe corresponder a un usuario ya registrado.</small>
                <c:if test="${not empty errors.id_usuario}"><span class="field-error" id="id_usuario-error"><c:out value="${errors.id_usuario}"/></span></c:if>
            </div>
            <div class="field">
                <label for="destino">Destino <span aria-hidden="true">*</span></label>
                <input id="destino" name="destino" type="text" maxlength="100" required
                       value="<c:out value='${param.destino != null ? param.destino : criterio.destino}'/>">
                <c:if test="${not empty errors.destino}"><span class="field-error"><c:out value="${errors.destino}"/></span></c:if>
            </div>
            <div class="field">
                <label for="fecha_inicio">Fecha de inicio <span aria-hidden="true">*</span></label>
                <input id="fecha_inicio" name="fecha_inicio" type="date" required
                       value="<c:out value='${param.fecha_inicio != null ? param.fecha_inicio : criterio.fechaInicio}'/>">
                <c:if test="${not empty errors.fecha_inicio}"><span class="field-error"><c:out value="${errors.fecha_inicio}"/></span></c:if>
            </div>
            <div class="field">
                <label for="fecha_fin">Fecha de fin <span aria-hidden="true">*</span></label>
                <input id="fecha_fin" name="fecha_fin" type="date" required
                       value="<c:out value='${param.fecha_fin != null ? param.fecha_fin : criterio.fechaFin}'/>">
                <c:if test="${not empty errors.fecha_fin}"><span class="field-error"><c:out value="${errors.fecha_fin}"/></span></c:if>
            </div>
            <div class="field">
                <label for="precio_maximo">Precio máximo <span aria-hidden="true">*</span></label>
                <input id="precio_maximo" name="precio_maximo" type="number" min="0" max="9999999999.99"
                       step="0.01" required
                       value="<c:out value='${param.precio_maximo != null ? param.precio_maximo : criterio.precioMaximo}'/>">
                <c:if test="${not empty errors.precio_maximo}"><span class="field-error"><c:out value="${errors.precio_maximo}"/></span></c:if>
            </div>
            <div class="field">
                <label for="horario">Horario preferido</label>
                <input id="horario" name="horario" type="text" maxlength="100"
                       value="<c:out value='${param.horario != null ? param.horario : criterio.horario}'/>">
                <c:if test="${not empty errors.horario}"><span class="field-error"><c:out value="${errors.horario}"/></span></c:if>
            </div>
            <div class="field field-full">
                <label for="preferencias">Preferencias</label>
                <textarea id="preferencias" name="preferencias" rows="4" maxlength="16383"><c:out value="${param.preferencias != null ? param.preferencias : criterio.preferencias}"/></textarea>
                <c:if test="${not empty errors.preferencias}"><span class="field-error"><c:out value="${errors.preferencias}"/></span></c:if>
            </div>
            <div class="field field-full">
                <label class="check-label" for="estado">
                    <input id="estado" name="estado" type="checkbox" value="true"
                           ${param.estado != null ? (param.estado == 'true' ? 'checked' : '') : (criterio.estado ? 'checked' : '')}>
                    Criterio activo
                </label>
            </div>
        </div>
        <div class="form-actions">
            <button class="button button-primary" type="submit"><c:out value="${editing ? 'Guardar cambios' : 'Crear criterio'}"/></button>
            <a class="button button-secondary" href="${pageContext.request.contextPath}/criterios">Cancelar</a>
        </div>
    </form>
</main>
</body>
</html>
