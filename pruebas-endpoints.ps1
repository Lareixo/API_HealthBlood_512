<#
    Pruebas automaticas de los endpoints REST de API_HealthBlood_512 (Gluapi)

    Uso:
        powershell -ExecutionPolicy Bypass -File .\pruebas-endpoints.ps1

    Requisitos:
        - Tomcat corriendo con la aplicacion desplegada.
        - MySQL80 activo con la base gluco_api.

    Nota:
        Las pruebas de creacion insertan usuarios y medidas de prueba en la base.
        Ver la seccion de limpieza en PRUEBAS_ENDPOINTS.md.
#>

param(
    [string]$BaseUrl = "http://localhost:8080/API_HealthBlood_512/api/glucosa",
    [string]$ShortBaseUrl = "http://localhost:8080/API_HealthBlood_512",
    [int]$TimeoutSec = 20
)

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$ErrorActionPreference = "Stop"

$script:total  = 0
$script:ok     = 0
$script:failed = 0

function Invoke-ApiTest {
    param(
        [string]$Nombre,
        [string]$Metodo,
        [string]$Ruta,
        [string]$Body = $null,
        [string]$ContentType = "application/json",
        [int]$Esperado = 200,
        [string]$Validar = $null,
        [string]$Base = $null
    )

    $script:total++
    $url = if ($Base) { "$Base$Ruta" } else { "$BaseUrl$Ruta" }

    $params = @{
        Uri            = $url
        Method         = $Metodo
        UseBasicParsing = $true
        TimeoutSec     = $TimeoutSec
    }

    if ($Body) {
        $params.Body        = $Body
        $params.ContentType = $ContentType
    }

    $status    = 0
    $contenido = ""

    try {
        $r         = Invoke-WebRequest @params
        $status    = [int]$r.StatusCode
        $contenido = $r.Content
    }
    catch {
        $resp = $_.Exception.Response
        if ($resp) {
            $status = [int]$resp.StatusCode
            if ($_.ErrorDetails -and $_.ErrorDetails.Message) {
                $contenido = $_.ErrorDetails.Message
            }
            else {
                $sr = New-Object System.IO.StreamReader($resp.GetResponseStream())
                $contenido = $sr.ReadToEnd()
                $sr.Close()
            }
        }
        else {
            $status    = -1
            $contenido = $_.Exception.Message
        }
    }

    $okStatus  = ($status -eq $Esperado)
    $okValidar = $true
    if ($Validar -and $contenido) {
        $okValidar = ($contenido -match $Validar)
    }
    $ok = $okStatus -and $okValidar

    if ($ok) { $script:ok++ } else { $script:failed++ }

    $etiqueta  = if ($ok) { "[OK]" } else { "[FALLO]" }
    $color     = if ($ok) { "Green" } else { "Red" }

    Write-Host ""
    Write-Host ("=" * 72) -ForegroundColor DarkGray
    Write-Host ("{0} {1}" -f $etiqueta, $Nombre) -ForegroundColor $color
    Write-Host ("{0} {1}" -f $Metodo, $url)
    if ($Body) {
        Write-Host ("Body   : {0}" -f $Body)
    }
    Write-Host ("Status : esperado {0} / obtenido {1}" -f $Esperado, $status) -ForegroundColor $(if ($okStatus) { "Gray" } else { "Red" })
    if ($Validar) {
        Write-Host ("Validacion de contenido: {0}" -f $(if ($okValidar) { "coincide" } else { "NO coincide" })) -ForegroundColor $(if ($okValidar) { "Gray" } else { "Red" })
    }
    Write-Host "Respuesta:"
    Write-Host $contenido
}

Write-Host ""
Write-Host "PRUEBAS DE ENDPOINTS - API_HealthBlood_512 (Gluapi)" -ForegroundColor Cyan
Write-Host ("URL base: {0}" -f $BaseUrl) -ForegroundColor Cyan

# ---------------------------------------------------------------
# Endpoints generales
# ---------------------------------------------------------------
Invoke-ApiTest -Nombre "Saludar" `
    -Metodo GET -Ruta "/saludar" `
    -Esperado 200 -Validar "Hola desde Java"

Invoke-ApiTest -Nombre "Saludo personalizado" `
    -Metodo GET -Ruta "/saludarPersonalizado?nombre=Sebastian" `
    -Esperado 200 -Validar "Hola Sebastian desde Java"

Invoke-ApiTest -Nombre "Cuadrado de un numero" `
    -Metodo GET -Ruta "/Numero?numero=7" `
    -Esperado 200 -Validar "49"

# ---------------------------------------------------------------
# Usuarios
# ---------------------------------------------------------------
Invoke-ApiTest -Nombre "Listar usuarios (GET USER)" `
    -Metodo GET -Ruta "/getAllUsuarios" `
    -Esperado 200 -Validar '"idUsuario"'

$emailPruebaForm = "prueba.form@gluapi.com"
$bodyUsuarioForm = "nombre=Usuario&primerApellido=Prueba&segundoApellido=Endpoint&email=$emailPruebaForm"

Invoke-ApiTest -Nombre "Crear usuario valido (form params)" `
    -Metodo POST -Ruta "/addUsuario" `
    -Body $bodyUsuarioForm `
    -ContentType "application/x-www-form-urlencoded" `
    -Esperado 201 -Validar '"idUsuario"'

$emailPruebaJson = "prueba.json@gluapi.com"
$bodyUsuarioJson = '{"nombre":"Usuario","primerApellido":"Prueba","segundoApellido":"Endpoint","email":"' + $emailPruebaJson + '"}'

Invoke-ApiTest -Nombre "Crear usuario valido (JSON compatible)" `
    -Metodo POST -Ruta "/addUsuario" `
    -Body $bodyUsuarioJson `
    -ContentType "application/json" `
    -Esperado 201 -Validar '"idUsuario"'

Invoke-ApiTest -Nombre "Crear usuario sin email (form, error 400)" `
    -Metodo POST -Ruta "/addUsuario" `
    -Body "nombre=Sin&primerApellido=Correo" `
    -ContentType "application/x-www-form-urlencoded" `
    -Esperado 400 -Validar "obligatorios"

Invoke-ApiTest -Nombre "Crear usuario con JSON invalido (error 400)" `
    -Metodo POST -Ruta "/addUsuario" `
    -Body '{"nombre": "Sin cerrar"' `
    -ContentType "application/json" `
    -Esperado 400

# ---------------------------------------------------------------
# Medidas
# ---------------------------------------------------------------
$marcaMedidaForm = "MedidaPruebaEndpoints"
$bodyMedidaForm = "idUsuario=1&glucosa=123&fechaHora=2026-11-08T14:30:00&contexto=Prueba&notas=$marcaMedidaForm"

Invoke-ApiTest -Nombre "Crear medida valida (form params)" `
    -Metodo POST -Ruta "/addMedida" `
    -Body $bodyMedidaForm `
    -ContentType "application/x-www-form-urlencoded" `
    -Esperado 201 -Validar '"idMedida"'

$marcaMedidaJson = "MedidaPruebaJson"
$bodyMedidaJson = '{"idUsuario":1,"glucosa":99,"fechaHora":"2026-11-08T16:00:00","contexto":"JSON","notas":"' + $marcaMedidaJson + '"}'

Invoke-ApiTest -Nombre "Crear medida valida (JSON compatible)" `
    -Metodo POST -Ruta "/addMedida" `
    -Body $bodyMedidaJson `
    -ContentType "application/json" `
    -Esperado 201 -Validar '"idMedida"'

Invoke-ApiTest -Nombre "Crear medida sin glucosa (form, error 400)" `
    -Metodo POST -Ruta "/addMedida" `
    -Body "idUsuario=1&contexto=SinGlucosa" `
    -ContentType "application/x-www-form-urlencoded" `
    -Esperado 400 -Validar "glucosa"

Invoke-ApiTest -Nombre "Crear medida con usuario inexistente (error 400)" `
    -Metodo POST -Ruta "/addMedida" `
    -Body "idUsuario=99999&glucosa=100" `
    -ContentType "application/x-www-form-urlencoded" `
    -Esperado 400 -Validar "usuario"

Invoke-ApiTest -Nombre "Crear medida con fecha invalida (error 400)" `
    -Metodo POST -Ruta "/addMedida" `
    -Body "idUsuario=1&glucosa=100&fechaHora=no-es-fecha" `
    -ContentType "application/x-www-form-urlencoded" `
    -Esperado 400 -Validar "fechaHora"

Invoke-ApiTest -Nombre "Todas las medidas (GET ALL)" `
    -Metodo GET -Ruta "/getAllMedidas" `
    -Esperado 200 -Validar '"idMedida"'

Invoke-ApiTest -Nombre "Todas las medidas incluyen la medida insertada" `
    -Metodo GET -Ruta "/getAllMedidas" `
    -Esperado 200 -Validar $marcaMedidaForm

Invoke-ApiTest -Nombre "Medidas por usuario incluyen la medida insertada" `
    -Metodo GET -Ruta "/getMedidasUsuario?idUsuario=1" `
    -Esperado 200 -Validar $marcaMedidaForm

Invoke-ApiTest -Nombre "Alias getByUsuarioId (query string)" `
    -Metodo GET -Ruta "/getByUsuarioId?idUsuario=1" `
    -Esperado 200 -Validar $marcaMedidaForm

Invoke-ApiTest -Nombre "Alias getByUsuarioId (id en la ruta)" `
    -Metodo GET -Ruta "/getByUsuarioId/1" `
    -Esperado 200 -Validar $marcaMedidaForm

Invoke-ApiTest -Nombre "Medidas por usuario (query string)" `
    -Metodo GET -Ruta "/getMedidasUsuario?idUsuario=1" `
    -Esperado 200 -Validar '"idUsuario":1'

Invoke-ApiTest -Nombre "Medidas por usuario (id en la ruta)" `
    -Metodo GET -Ruta "/getMedidasUsuario/1" `
    -Esperado 200 -Validar '"idUsuario":1'

Invoke-ApiTest -Nombre "Medidas sin parametro (error 400)" `
    -Metodo GET -Ruta "/getMedidasUsuario" `
    -Esperado 400 -Validar "obligatorio"

Invoke-ApiTest -Nombre "Medidas con idUsuario=0 (error 400)" `
    -Metodo GET -Ruta "/getMedidasUsuario?idUsuario=0" `
    -Esperado 400 -Validar "obligatorio"

Invoke-ApiTest -Nombre "Medidas de un usuario sin registros (lista vacia)" `
    -Metodo GET -Ruta "/getMedidasUsuario?idUsuario=999" `
    -Esperado 200 -Validar "^\s*\[\s*\]\s*$"

# ---------------------------------------------------------------
# Rutas cortas para el navegador (sin /api/glucosa)
# ---------------------------------------------------------------
Write-Host ""
Write-Host "PRUEBAS CON RUTAS CORTAS DE NAVEGADOR" -ForegroundColor Cyan

Invoke-ApiTest -Nombre "Corta: Saludar" `
    -Metodo GET -Ruta "/saludar" -Base $ShortBaseUrl `
    -Esperado 200 -Validar "Hola desde Java"

Invoke-ApiTest -Nombre "Corta: listar usuarios" `
    -Metodo GET -Ruta "/getAllUsuarios" -Base $ShortBaseUrl `
    -Esperado 200 -Validar '"idUsuario"'

Invoke-ApiTest -Nombre "Corta: todas las medidas" `
    -Metodo GET -Ruta "/getAllMedidas" -Base $ShortBaseUrl `
    -Esperado 200 -Validar '"idMedida"'

Invoke-ApiTest -Nombre "Corta: medidas por usuario (query)" `
    -Metodo GET -Ruta "/getMedidasUsuario?idUsuario=1" -Base $ShortBaseUrl `
    -Esperado 200 -Validar '"idUsuario":1'

Invoke-ApiTest -Nombre "Corta: medidas por usuario (ruta)" `
    -Metodo GET -Ruta "/getMedidasUsuario/1" -Base $ShortBaseUrl `
    -Esperado 200 -Validar '"idUsuario":1'

Invoke-ApiTest -Nombre "Corta: medidas por usuario (alias getbyusuarioid)" `
    -Metodo GET -Ruta "/getbyusuarioid?idUsuario=1" -Base $ShortBaseUrl `
    -Esperado 200 -Validar $marcaMedidaForm

Invoke-ApiTest -Nombre "Corta: todas las medidas incluyen la nueva" `
    -Metodo GET -Ruta "/getallmedidas" -Base $ShortBaseUrl `
    -Esperado 200 -Validar $marcaMedidaForm

$bodyMedidaCorta = "idUsuario=1&glucosa=88&fechaHora=2026-11-08T15:00:00&contexto=PruebaCorta&notas=MedidaPruebaCorta"

Invoke-ApiTest -Nombre "Corta: insertar medida (POST)" `
    -Metodo POST -Ruta "/insertarmedida" -Base $ShortBaseUrl `
    -Body $bodyMedidaCorta `
    -ContentType "application/x-www-form-urlencoded" `
    -Esperado 201 -Validar '"idMedida"'

Invoke-ApiTest -Nombre "Corta: insertar medida desde el navegador (GET)" `
    -Metodo GET `
    -Ruta "/addMedida?idUsuario=1&glucosa=77&contexto=Navegador&notas=MedidaPruebaNavegador" `
    -Base $ShortBaseUrl `
    -Esperado 201 -Validar '"idMedida"'

Invoke-ApiTest -Nombre "Corta: crear usuario desde el navegador (GET)" `
    -Metodo GET `
    -Ruta "/addUsuario?nombre=Navegador&primerApellido=Prueba&segundoApellido=Endpoint&email=navegador.endpoint@gluapi.com" `
    -Base $ShortBaseUrl `
    -Esperado 201 -Validar '"idUsuario"'

Invoke-ApiTest -Nombre "Corta: crear usuario sin datos (error 400)" `
    -Metodo GET -Ruta "/addUsuario" -Base $ShortBaseUrl `
    -Esperado 400 -Validar "obligatorios"

# ---------------------------------------------------------------
# Resumen
# ---------------------------------------------------------------
Write-Host ""
Write-Host ("=" * 72) -ForegroundColor DarkGray
Write-Host ("RESUMEN: {0} pruebas | {1} OK | {2} fallidas" -f $script:total, $script:ok, $script:failed) `
    -ForegroundColor $(if ($script:failed -eq 0) { "Green" } else { "Red" })
Write-Host ("=" * 72) -ForegroundColor DarkGray

if ($script:failed -gt 0) { exit 1 }
exit 0
