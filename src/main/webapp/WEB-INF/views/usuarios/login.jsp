<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="tituloPagina" value="Iniciar sesión"/>
<c:set var="claseContenedor" value="narrow"/>
<%@ include file="/WEB-INF/views/layout/cabecera.jspf" %>
<header class="page-header compact">
    <div>
        <p class="eyebrow">Acceso</p>
        <h1>Iniciar sesión</h1>
        <p class="muted">Ingrese con el correo y la contraseña de su cuenta.</p>
    </div>
</header>
<c:if test="${not empty mensaje}">
    <div class="notice notice-success" role="status"><c:out value="${mensaje}"/></div>
</c:if>
<c:if test="${not empty errorGeneral}">
    <div class="notice notice-error" role="alert"><c:out value="${errorGeneral}"/></div>
</c:if>
<form class="card form-card" action="${pageContext.request.contextPath}/login" method="post">
    <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
    <div class="form-grid single">
        <div class="field">
            <label for="correo">Correo electrónico</label>
            <input id="correo" name="correo" type="email" maxlength="150" required autocomplete="username"
                   value="<c:out value='${param.correo}'/>">
        </div>
        <div class="field">
            <label for="contrasena">Contraseña</label>
            <input id="contrasena" name="contrasena" type="password" required autocomplete="current-password">
        </div>
    </div>
    <div class="form-actions">
        <button class="button button-primary" type="submit">Entrar</button>
        <a class="button button-secondary" href="${pageContext.request.contextPath}/registro">Crear cuenta</a>
    </div>
</form>
<%@ include file="/WEB-INF/views/layout/pie.jspf" %>
