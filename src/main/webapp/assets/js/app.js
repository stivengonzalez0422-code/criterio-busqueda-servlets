/*
 * JavaScript de la aplicación (sin librerías externas).
 * El servidor siempre vuelve a validar: estas ayudas solo mejoran la experiencia.
 */
(function () {
    'use strict';

    // Confirmación antes de enviar formularios con data-confirm (por ejemplo, eliminar).
    document.addEventListener('submit', function (event) {
        var mensaje = event.target.getAttribute('data-confirm');
        if (mensaje && !window.confirm(mensaje)) {
            event.preventDefault();
        }
    });

    // Evita enviar dos veces el mismo formulario.
    document.addEventListener('submit', function (event) {
        if (event.defaultPrevented) {
            return;
        }
        var boton = event.target.querySelector('button[type="submit"]');
        if (boton) {
            window.setTimeout(function () { boton.disabled = true; }, 0);
        }
    });

    // Formulario de criterios: la fecha de fin no puede ser anterior a la de inicio.
    function configurarFechas(formulario) {
        var inicio = formulario.querySelector('#fecha_inicio');
        var fin = formulario.querySelector('#fecha_fin');
        if (!inicio || !fin) {
            return;
        }
        function revisar() {
            fin.min = inicio.value || '';
            if (inicio.value && fin.value && fin.value < inicio.value) {
                fin.setCustomValidity('La fecha de fin debe ser igual o posterior a la de inicio.');
            } else {
                fin.setCustomValidity('');
            }
        }
        inicio.addEventListener('change', revisar);
        fin.addEventListener('change', revisar);
        revisar();
    }

    // Registro: la confirmación debe coincidir con la contraseña.
    function configurarContrasena() {
        var contrasena = document.getElementById('contrasena');
        var confirmacion = document.getElementById('confirmacion');
        if (!contrasena || !confirmacion) {
            return;
        }
        function revisar() {
            if (confirmacion.value && confirmacion.value !== contrasena.value) {
                confirmacion.setCustomValidity('La confirmación no coincide con la contraseña.');
            } else {
                confirmacion.setCustomValidity('');
            }
        }
        contrasena.addEventListener('input', revisar);
        confirmacion.addEventListener('input', revisar);
    }

    // Casilla "Mostrar contraseñas": data-mostrar-contrasena="#campo1,#campo2".
    function configurarMostrarContrasena() {
        var casilla = document.querySelector('[data-mostrar-contrasena]');
        if (!casilla) {
            return;
        }
        var campos = document.querySelectorAll(casilla.getAttribute('data-mostrar-contrasena'));
        casilla.addEventListener('change', function () {
            campos.forEach(function (campo) {
                campo.type = casilla.checked ? 'text' : 'password';
            });
        });
    }

    // Al volver con el botón "atrás" el navegador puede conservar los botones deshabilitados.
    window.addEventListener('pageshow', function () {
        document.querySelectorAll('button[type="submit"]').forEach(function (boton) {
            boton.disabled = false;
        });
    });

    document.addEventListener('DOMContentLoaded', function () {
        var formulario = document.querySelector('[data-formulario-criterio]');
        if (formulario) {
            configurarFechas(formulario);
        }
        configurarContrasena();
        configurarMostrarContrasena();
    });
})();
