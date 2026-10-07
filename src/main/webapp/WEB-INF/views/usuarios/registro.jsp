<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Crear cuenta"/>
<c:set var="claseContenedor" value="narrow"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jspf" %>
<header class="page-header compact">
    <div>
        <p class="eyebrow">Registro</p>
        <h1>Crear cuenta</h1>
        <p class="muted">Los campos marcados con * son obligatorios.</p>
    </div>
</header>
<form class="card form-card" action="${pageContext.request.contextPath}/registro" method="post">
    <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
    <div class="form-grid">
        <div class="field">
            <label for="nombre">Nombre <span aria-hidden="true">*</span></label>
            <input id="nombre" name="nombre" type="text" maxlength="100" required autocomplete="given-name"
                   value="<c:out value='${param.nombre}'/>">
            <c:if test="${not empty errors.nombre}"><span class="field-error" role="alert"><c:out value="${errors.nombre}"/></span></c:if>
        </div>
        <div class="field">
            <label for="apellido">Apellido <span aria-hidden="true">*</span></label>
            <input id="apellido" name="apellido" type="text" maxlength="100" required autocomplete="family-name"
                   value="<c:out value='${param.apellido}'/>">
            <c:if test="${not empty errors.apellido}"><span class="field-error" role="alert"><c:out value="${errors.apellido}"/></span></c:if>
        </div>
        <div class="field">
            <label for="cedula">Cédula <span aria-hidden="true">*</span></label>
            <input id="cedula" name="cedula" type="text" inputmode="numeric" maxlength="20" required
                   pattern="[0-9]{5,20}" value="<c:out value='${param.cedula}'/>">
            <c:if test="${not empty errors.cedula}"><span class="field-error" role="alert"><c:out value="${errors.cedula}"/></span></c:if>
        </div>
        <div class="field">
            <label for="fecha_nacimiento">Fecha de nacimiento <span aria-hidden="true">*</span></label>
            <input id="fecha_nacimiento" name="fecha_nacimiento" type="date" required autocomplete="bday"
                   value="<c:out value='${param.fecha_nacimiento}'/>">
            <c:if test="${not empty errors.fecha_nacimiento}"><span class="field-error" role="alert"><c:out value="${errors.fecha_nacimiento}"/></span></c:if>
        </div>
        <div class="field">
            <label for="correo">Correo electrónico <span aria-hidden="true">*</span></label>
            <input id="correo" name="correo" type="email" maxlength="150" required autocomplete="email"
                   value="<c:out value='${param.correo}'/>">
            <c:if test="${not empty errors.correo}"><span class="field-error" role="alert"><c:out value="${errors.correo}"/></span></c:if>
        </div>
        <div class="field">
            <label for="telefono">Teléfono</label>
            <input id="telefono" name="telefono" type="tel" maxlength="20" autocomplete="tel"
                   value="<c:out value='${param.telefono}'/>">
            <c:if test="${not empty errors.telefono}"><span class="field-error" role="alert"><c:out value="${errors.telefono}"/></span></c:if>
        </div>
        <div class="field">
            <label for="contrasena">Contraseña <span aria-hidden="true">*</span></label>
            <input id="contrasena" name="contrasena" type="password" minlength="8" maxlength="72" required
                   autocomplete="new-password">
            <small class="field-hint">Entre 8 y 72 caracteres, con al menos una letra y un número.</small>
            <c:if test="${not empty errors.contrasena}"><span class="field-error" role="alert"><c:out value="${errors.contrasena}"/></span></c:if>
        </div>
        <div class="field">
            <label for="confirmacion">Confirmar contraseña <span aria-hidden="true">*</span></label>
            <input id="confirmacion" name="confirmacion" type="password" minlength="8" maxlength="72" required
                   autocomplete="new-password">
            <c:if test="${not empty errors.confirmacion}"><span class="field-error" role="alert"><c:out value="${errors.confirmacion}"/></span></c:if>
        </div>
        <div class="field field-full">
            <label class="check-label" for="mostrar-contrasena">
                <input id="mostrar-contrasena" type="checkbox" data-mostrar-contrasena="#contrasena,#confirmacion">
                Mostrar contraseñas
            </label>
        </div>
    </div>
    <div class="form-actions">
        <button class="button button-primary" type="submit">Crear cuenta</button>
        <a class="button button-secondary" href="${pageContext.request.contextPath}/login">Ya tengo cuenta</a>
    </div>
</form>
<%@ include file="/WEB-INF/views/layout/pie.jspf" %>
