param(
    [switch]$Recompilar,
    [switch]$NoBrowser
)

$ErrorActionPreference = "Stop"

$ProjectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$TomcatHome = "C:\apache-tomcat-11.0.27"
$TomcatBase = "C:\Users\sebar\AppData\Roaming\NetBeans\25\apache-tomcat-11.0.27_base"
$JavaHome = "C:\Program Files\BellSoft\LibericaJDK-25-Full"
$Ant = "C:\Program Files\NetBeans-25\netbeans\extide\ant\bin\ant.bat"

$AppUrl = "http://localhost:8080/API_HealthBlood_512/getAllMedidas"
$HomeUrl = "http://localhost:8080/API_HealthBlood_512/getAllMedidas"
$War = Join-Path $ProjectDir "dist\API_HealthBlood_512.war"

function Test-Puerto([int]$Puerto) {
    return (Test-NetConnection -ComputerName 127.0.0.1 -Port $Puerto -InformationLevel Quiet -WarningAction SilentlyContinue)
}

function Test-App {
    try {
        $respuesta = Invoke-WebRequest -Uri $AppUrl -UseBasicParsing -TimeoutSec 3
        return $respuesta.StatusCode -eq 200
    } catch {
        return $false
    }
}

$env:JAVA_HOME = $JavaHome

if ($Recompilar) {
    Write-Host "Compilando el proyecto y generando el WAR..." -ForegroundColor Cyan

    if (-not (Test-Path $Ant)) {
        throw "No se encontro Ant en: $Ant"
    }

    & $Ant -f (Join-Path $ProjectDir "build.xml") dist

    if ($LASTEXITCODE -ne 0) {
        throw "La compilacion fallo. Revisa la salida de Ant."
    }

    if (-not (Test-Path $TomcatBase)) {
        throw "No existe la carpeta de Tomcat: $TomcatBase"
    }

    $warDestino = Join-Path $TomcatBase "webapps\API_HealthBlood_512.war"
    Copy-Item $War $warDestino -Force
    Write-Host "WAR copiado a: $warDestino" -ForegroundColor Green
}

if (-not (Test-Puerto 3306)) {
    Write-Host "MySQL no esta escuchando. Intentando iniciar el servicio MySQL80..." -ForegroundColor Yellow

    try {
        Start-Service MySQL80 -ErrorAction Stop
    } catch {
        Write-Warning "No se pudo iniciar MySQL80 automaticamente. Abre services.msc y arrancalo manualmente."
    }

    for ($i = 0; $i -lt 20; $i++) {
        Start-Sleep -Seconds 1
        if (Test-Puerto 3306) { break }
    }
}

if (-not (Test-Puerto 3306)) {
    throw "MySQL no responde en el puerto 3306. Revisa el servicio MySQL80."
}

if (-not (Test-App)) {
    if (Test-Puerto 8080) {
        throw "El puerto 8080 esta ocupado, pero la aplicacion no responde. Cierra el otro Tomcat o revisa los logs."
    }

    Write-Host "Iniciando Tomcat 11..." -ForegroundColor Cyan

    $env:CATALINA_HOME = $TomcatHome
    $env:CATALINA_BASE = $TomcatBase
    $env:JAVA_HOME = $JavaHome

    & (Join-Path $TomcatHome "bin\catalina.bat") start

    Write-Host "Esperando a que la aplicacion responda..." -ForegroundColor Yellow

    for ($i = 0; $i -lt 40; $i++) {
        Start-Sleep -Seconds 1
        if (Test-App) { break }
    }
}

if (-not (Test-App)) {
    throw "La aplicacion no respondio. Revisa los logs en: $TomcatBase\logs"
}

Write-Host ""
Write-Host "Aplicacion lista: http://localhost:8080/API_HealthBlood_512" -ForegroundColor Green
Write-Host "Rutas de prueba: /saludar, /getAllUsuarios, /getAllMedidas, /getbyusuarioid?idUsuario=1" -ForegroundColor Green
Write-Host ""

if (-not $NoBrowser) {
    Start-Process $HomeUrl
}
