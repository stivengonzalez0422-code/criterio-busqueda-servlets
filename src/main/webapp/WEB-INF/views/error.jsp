<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>No fue posible completar la solicitud | Agenda de viajes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/app.css">
</head>
<body>
<main class="container narrow">
    <section class="card detail-card">
        <p class="eyebrow">Agenda de viajes</p>
        <h1>No fue posible completar la solicitud</h1>
        <p class="notice notice-error" role="alert"><c:out value="${errorGeneral}"/></p>
        <a class="button button-primary" href="${pageContext.request.contextPath}/criterios">Volver al listado</a>
    </section>
</main>
</body>
</html>
