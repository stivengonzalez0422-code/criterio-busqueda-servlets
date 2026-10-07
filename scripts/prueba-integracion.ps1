<#
.SYNOPSIS
    Prueba de integración por HTTP de la aplicación desplegada (Windows PowerShell 5.1).
.DESCRIPTION
    Recorre el flujo completo: registro, inicio de sesión, CRUD de criterios, ofertas y controles de
    seguridad (CSRF, aislamiento entre usuarios, rutas protegidas). Crea usuarios de prueba con
    correos únicos: ejecútela contra una base de pruebas (ver docs/pruebas.md), no contra la de uso real.
.EXAMPLE
    .\scripts\prueba-integracion.ps1 -BaseUrl http://localhost:8080/criterio-busqueda-servlets
#>
param([string]$BaseUrl = 'http://localhost:8080/criterio-busqueda-servlets')

$ErrorActionPreference = 'Stop'
$script:fallos = 0
$script:total = 0

function Nueva-Sesion { New-Object System.Net.CookieContainer }

# Solicitud HTTP sin seguir redirecciones, para poder comprobar el código y el destino de cada respuesta.
function Pedir {
    param([string]$Metodo, [string]$Ruta, $Cuerpo = $null, $Sesion)
    $solicitud = [System.Net.HttpWebRequest]::Create("$BaseUrl$Ruta")
    $solicitud.Method = $Metodo
    $solicitud.AllowAutoRedirect = $false
    $solicitud.CookieContainer = $Sesion
    if ($null -ne $Cuerpo) {
        $pares = foreach ($clave in $Cuerpo.Keys) {
            '{0}={1}' -f [uri]::EscapeDataString($clave), [uri]::EscapeDataString([string]$Cuerpo[$clave])
        }
        $bytes = [Text.Encoding]::UTF8.GetBytes(($pares -join '&'))
        $solicitud.ContentType = 'application/x-www-form-urlencoded; charset=UTF-8'
        $solicitud.ContentLength = $bytes.Length
        $flujo = $solicitud.GetRequestStream()
        $flujo.Write($bytes, 0, $bytes.Length)
        $flujo.Close()
    }
    try {
        $respuesta = $solicitud.GetResponse()
    } catch [System.Net.WebException] {
        $respuesta = $_.Exception.Response
        if ($null -eq $respuesta) { throw }
    }
    $lector = New-Object System.IO.StreamReader($respuesta.GetResponseStream(), [Text.Encoding]::UTF8)
    $contenido = $lector.ReadToEnd()
    $lector.Close()
    $resultado = [pscustomobject]@{ Estado = [int]$respuesta.StatusCode; Ubicacion = $respuesta.Headers['Location']; Contenido = $contenido }
    $respuesta.Close()
    return $resultado
}
function Token {
    param($Html)
    if ($Html -match 'name="_csrf" value="([^"]+)"') { return $Matches[1] }
    throw 'La página no contiene token CSRF.'
}

function Verificar {
    param([string]$Nombre, [scriptblock]$Prueba)
    $script:total++
    try {
        & $Prueba
        Write-Host "  OK     $Nombre" -ForegroundColor Green
    } catch {
        $script:fallos++
        Write-Host "  FALLO  $Nombre -> $($_.Exception.Message)" -ForegroundColor Red
    }
}

function Afirmar { param([bool]$Condicion, [string]$Mensaje) if (-not $Condicion) { throw $Mensaje } }

function Registrar-Y-Entrar {
    param($Sesion, [string]$Correo, [string]$Cedula)
    $form = Pedir 'GET' '/registro' $null $Sesion
    $cuerpo = @{ _csrf = (Token $form.Contenido); nombre = 'Prueba'; apellido = 'Integración'; cedula = $Cedula;
                 fecha_nacimiento = '1995-04-12'; correo = $Correo; telefono = '3001234567';
                 contrasena = 'Clave2026'; confirmacion = 'Clave2026' }
    $registro = Pedir 'POST' '/registro' $cuerpo $Sesion
    Afirmar ($registro.Estado -eq 302) "El registro debía redirigir (estado $($registro.Estado))."
    $login = Pedir 'GET' '/login' $null $Sesion
    $entrada = Pedir 'POST' '/login' @{ _csrf = (Token $login.Contenido); correo = $Correo; contrasena = 'Clave2026' } $Sesion
    Afirmar ($entrada.Estado -eq 302 -and $entrada.Ubicacion -like '*/inicio') 'El inicio de sesión debía redirigir a /inicio.'
}

$unico = (Get-Date).ToString('yyyyMMddHHmmss')
$correoA = "a$unico@prueba.com"; $cedulaA = "1$unico"
$correoB = "b$unico@prueba.com"; $cedulaB = "2$unico"
$sesionA = Nueva-Sesion; $sesionB = Nueva-Sesion; $anonimo = Nueva-Sesion
$idCriterio = $null

Write-Host "Pruebas de integración contra $BaseUrl" -ForegroundColor Cyan

Write-Host "`n[Acceso y seguridad]"
Verificar 'La raíz redirige a /login sin sesión' {
    $r = Pedir 'GET' '/' $null $anonimo
    Afirmar ($r.Estado -eq 302 -and $r.Ubicacion -like '*/login') "Estado $($r.Estado), destino $($r.Ubicacion)"
}
Verificar 'Las rutas privadas redirigen a /login sin sesión' {
    foreach ($ruta in '/inicio', '/criterios', '/criterios/nuevo', '/ofertas/criterio?id=1') {
        $r = Pedir 'GET' $ruta $null $anonimo
        Afirmar ($r.Estado -eq 302 -and $r.Ubicacion -like '*/login') "$ruta -> estado $($r.Estado)"
    }
}
Verificar 'La página de login se muestra con token CSRF y sin caracteres corruptos' {
    $r = Pedir 'GET' '/login' $null $anonimo
    Afirmar ($r.Estado -eq 200 -and $r.Contenido -match 'Iniciar sesión') "Estado $($r.Estado)"
    Afirmar ($r.Contenido -notmatch 'Ã|Â') 'Se encontraron caracteres mal codificados (revise page-encoding en web.xml).'
    Token $r.Contenido | Out-Null
}
Verificar 'Un POST sin token CSRF se rechaza (403)' {
    $r = Pedir 'POST' '/login' @{ correo = 'x@x.com'; contrasena = 'Clave2026' } $anonimo
    Afirmar ($r.Estado -eq 403) "Estado $($r.Estado)"
}

Write-Host "`n[Módulo de usuarios]"
Verificar 'El registro con datos inválidos muestra errores' {
    $form = Pedir 'GET' '/registro' $null $anonimo
    $r = Pedir 'POST' '/registro' @{ _csrf = (Token $form.Contenido); nombre = ''; apellido = 'X'; cedula = 'abc';
        fecha_nacimiento = ''; correo = 'mal'; contrasena = '123'; confirmacion = '999' } $anonimo
    Afirmar ($r.Estado -eq 200 -and $r.Contenido -match 'El nombre es obligatorio' -and $r.Contenido -match 'cédula') 'No se mostraron los errores.'
}
Verificar 'Se registra y entra el usuario A' { Registrar-Y-Entrar $sesionA $correoA $cedulaA }
Verificar 'Un correo repetido se rechaza' {
    $form = Pedir 'GET' '/registro' $null $anonimo
    $r = Pedir 'POST' '/registro' @{ _csrf = (Token $form.Contenido); nombre = 'Otra'; apellido = 'Persona'; cedula = "9$unico";
        fecha_nacimiento = '1990-01-01'; correo = $correoA; contrasena = 'Clave2026'; confirmacion = 'Clave2026' } $anonimo
    Afirmar ($r.Estado -eq 200 -and $r.Contenido -match 'Ya existe una cuenta con ese correo') 'No se detectó el correo repetido.'
}
Verificar 'Una contraseña incorrecta no inicia sesión' {
    $login = Pedir 'GET' '/login' $null $anonimo
    $r = Pedir 'POST' '/login' @{ _csrf = (Token $login.Contenido); correo = $correoA; contrasena = 'Incorrecta1' } $anonimo
    Afirmar ($r.Estado -eq 200 -and $r.Contenido -match 'Correo o contraseña incorrectos') "Estado $($r.Estado)"
}
Verificar 'El panel de inicio saluda al usuario' {
    $r = Pedir 'GET' '/inicio' $null $sesionA
    Afirmar ($r.Estado -eq 200 -and $r.Contenido -match 'Hola, Prueba Integración') "Estado $($r.Estado)"
}

Write-Host "`n[Módulo de criterios]"
Verificar 'La creación con datos inválidos muestra errores' {
    $inicio = Pedir 'GET' '/criterios/nuevo' $null $sesionA
    $r = Pedir 'POST' '/criterios/crear' @{ _csrf = (Token $inicio.Contenido); destino = ''; fecha_inicio = '2026-12-10';
        fecha_fin = '2026-12-01'; precio_maximo = '-5' } $sesionA
    Afirmar ($r.Estado -eq 200 -and $r.Contenido -match 'El destino es obligatorio' -and $r.Contenido -match 'igual o posterior') 'Faltan errores.'
}
Verificar 'Se crea un criterio y aparece en el listado' {
    $inicio = Pedir 'GET' '/criterios/nuevo' $null $sesionA
    $r = Pedir 'POST' '/criterios/crear' @{ _csrf = (Token $inicio.Contenido); destino = 'Cartagena'; fecha_inicio = '2026-12-01';
        fecha_fin = '2026-12-31'; precio_maximo = '500000'; horario = 'Mañana'; preferencias = 'Playa'; estado = 'true' } $sesionA
    Afirmar ($r.Estado -eq 302) "Estado $($r.Estado)"
    $lista = Pedir 'GET' '/criterios' $null $sesionA
    Afirmar ($lista.Contenido -match 'El criterio se creó correctamente' -and $lista.Contenido -match 'Cartagena') 'No aparece en el listado.'
    Afirmar ($lista.Contenido -match 'ver\?id=(\d+)') 'No se encontró el identificador.'
    $script:idCriterio = $Matches[1]
}
Verificar 'Se consulta el detalle y el formulario de edición (GET)' {
    $ver = Pedir 'GET' "/criterios/ver?id=$idCriterio" $null $sesionA
    $editar = Pedir 'GET' "/criterios/editar?id=$idCriterio" $null $sesionA
    Afirmar ($ver.Estado -eq 200 -and $ver.Contenido -match 'Cartagena') 'Detalle incorrecto.'
    Afirmar ($editar.Estado -eq 200 -and $editar.Contenido -match 'Guardar cambios') 'Formulario incorrecto.'
}
Verificar 'Se actualiza el criterio (POST)' {
    $editar = Pedir 'GET' "/criterios/editar?id=$idCriterio" $null $sesionA
    $r = Pedir 'POST' '/criterios/actualizar' @{ _csrf = (Token $editar.Contenido); id_criterio = $idCriterio; destino = 'Cartagena';
        fecha_inicio = '2026-12-01'; fecha_fin = '2026-12-31'; precio_maximo = '450000'; horario = 'Tarde'; preferencias = 'Playa'; estado = 'true' } $sesionA
    Afirmar ($r.Estado -eq 302) "Estado $($r.Estado)"
    $ver = Pedir 'GET' "/criterios/ver?id=$idCriterio" $null $sesionA
    Afirmar ($ver.Contenido -match 'Tarde' -and $ver.Contenido -match '450\.000') 'No se guardó el cambio.'
}

Write-Host "`n[Módulo de ofertas - integración con criterios]"
Verificar 'Las ofertas coinciden con el criterio' {
    $r = Pedir 'GET' "/ofertas/criterio?id=$idCriterio" $null $sesionA
    Afirmar ($r.Estado -eq 200) "Estado $($r.Estado)"
    Afirmar ($r.Contenido -match 'Viva Air' -and $r.Contenido -match 'Hostal Muralla Viva' -and $r.Contenido -match 'La Cevichería') 'Faltan ofertas esperadas.'
    Afirmar ($r.Contenido -notmatch 'Poblado') 'Aparece una oferta de otro destino.'
}

Write-Host "`n[Aislamiento entre usuarios]"
Verificar 'Se registra y entra el usuario B' { Registrar-Y-Entrar $sesionB $correoB $cedulaB }
Verificar 'B no ve criterios de A en su listado' {
    $r = Pedir 'GET' '/criterios' $null $sesionB
    Afirmar ($r.Contenido -notmatch 'Cartagena') 'B ve el criterio de A.'
}
Verificar 'B no puede ver, editar ni consultar ofertas de un criterio de A (404)' {
    foreach ($ruta in "/criterios/ver?id=$idCriterio", "/criterios/editar?id=$idCriterio", "/ofertas/criterio?id=$idCriterio") {
        $r = Pedir 'GET' $ruta $null $sesionB
        Afirmar ($r.Estado -eq 404) "$ruta -> estado $($r.Estado)"
    }
}
Verificar 'B no puede eliminar un criterio de A (404)' {
    $lista = Pedir 'GET' '/criterios' $null $sesionB
    $r = Pedir 'POST' '/criterios/eliminar' @{ _csrf = (Token $lista.Contenido); id_criterio = $idCriterio } $sesionB
    Afirmar ($r.Estado -eq 404) "Estado $($r.Estado)"
}

Write-Host "`n[Eliminación segura]"
Verificar 'Eliminar mediante GET no está permitido' {
    $r = Pedir 'GET' "/criterios/eliminar?id_criterio=$idCriterio" $null $sesionA
    Afirmar ($r.Estado -eq 404) "Estado $($r.Estado)"
    $ver = Pedir 'GET' "/criterios/ver?id=$idCriterio" $null $sesionA
    Afirmar ($ver.Estado -eq 200) 'El criterio desapareció con un GET.'
}
Verificar 'Eliminar por POST sin token CSRF se rechaza (403)' {
    $r = Pedir 'POST' '/criterios/eliminar' @{ id_criterio = $idCriterio } $sesionA
    Afirmar ($r.Estado -eq 403) "Estado $($r.Estado)"
}
Verificar 'El dueño elimina su criterio por POST con token' {
    $lista = Pedir 'GET' '/criterios' $null $sesionA
    $r = Pedir 'POST' '/criterios/eliminar' @{ _csrf = (Token $lista.Contenido); id_criterio = $idCriterio } $sesionA
    Afirmar ($r.Estado -eq 302) "Estado $($r.Estado)"
    $ver = Pedir 'GET' "/criterios/ver?id=$idCriterio" $null $sesionA
    Afirmar ($ver.Estado -eq 404) 'El criterio sigue existiendo.'
}

Write-Host "`n[Cierre de sesión]"
Verificar 'Cerrar sesión por POST bloquea el acceso' {
    $inicio = Pedir 'GET' '/inicio' $null $sesionA
    $r = Pedir 'POST' '/logout' @{ _csrf = (Token $inicio.Contenido) } $sesionA
    Afirmar ($r.Estado -eq 302 -and $r.Ubicacion -like '*/login') "Estado $($r.Estado)"
    $privada = Pedir 'GET' '/criterios' $null $sesionA
    Afirmar ($privada.Estado -eq 302) 'Todavía se accede a /criterios.'
}

Write-Host ""
if ($script:fallos -eq 0) {
    Write-Host "Resultado: $script:total de $script:total pruebas superadas." -ForegroundColor Green
    exit 0
}
Write-Host "Resultado: $script:fallos fallo(s) en $script:total pruebas." -ForegroundColor Red
exit 1
