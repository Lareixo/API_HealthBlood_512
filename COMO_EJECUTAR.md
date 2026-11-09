# Cómo ejecutar el proyecto para la clase

Proyecto: `C:\Users\sebar\Documents\NetBeansProjects\API_HealthBlood_512`

La aplicación necesita dos cosas encendidas:

1. **MySQL** escuchando en el puerto `3306`.
2. **Tomcat 11** con la aplicación desplegada en el puerto `8080`.

---

## Opción rápida: script de inicio

Abre PowerShell en la carpeta del proyecto y ejecuta:

```powershell
powershell -ExecutionPolicy Bypass -File .\iniciar-proyecto.ps1
```

El script:

- Verifica que MySQL esté escuchando; si no, intenta iniciar el servicio `MySQL80`.
- Verifica que la aplicación responda; si no, inicia Tomcat 11.
- Abre el navegador en la aplicación.

Si cambiaste código y quieres recompilar antes de abrir:

```powershell
powershell -ExecutionPolicy Bypass -File .\iniciar-proyecto.ps1 -Recompilar
```

> Si el script no puede iniciar MySQL por permisos, abre `services.msc`,
> busca **MySQL80** y presiona **Iniciar**. Luego vuelve a ejecutar el script.

---

## Opción con NetBeans

1. Abre **NetBeans 25**.
2. `File` → `Open Project`.
3. Selecciona:
   `C:\Users\sebar\Documents\NetBeansProjects\API_HealthBlood_512`
4. Verifica que MySQL esté corriendo:
   - Abre `services.msc`.
   - Busca **MySQL80**.
   - Si está detenido, presiona **Iniciar**.
5. En NetBeans, revisa que el servidor **Apache Tomcat 11.0.27** esté configurado.
   - Home: `C:\apache-tomcat-11.0.27`
   - URL: `http://localhost:8080`
6. Presiona **F6** o `Run` → `Run Project`.
7. Espera a que Tomcat termine de desplegar.
8. Abre en el navegador:

```text
http://localhost:8080/API_HealthBlood_512/saludar
```

---

## Opción manual (sin NetBeans)

### 1. Iniciar MySQL

Con PowerShell como administrador:

```powershell
Start-Service MySQL80
```

O desde `services.msc` → **MySQL80** → **Iniciar**.

Verificar que el puerto esté abierto:

```powershell
Test-NetConnection 127.0.0.1 -Port 3306
```

### 2. Iniciar Tomcat 11

```powershell
$env:CATALINA_HOME = "C:\apache-tomcat-11.0.27"
$env:CATALINA_BASE = "C:\Users\sebar\AppData\Roaming\NetBeans\25\apache-tomcat-11.0.27_base"
$env:JAVA_HOME = "C:\Program Files\BellSoft\LibericaJDK-25-Full"

& "$env:CATALINA_HOME\bin\catalina.bat" start
```

Espera unos segundos y abre:

```text
http://localhost:8080/API_HealthBlood_512/saludar
```

Para detener Tomcat:

```powershell
& "$env:CATALINA_HOME\bin\catalina.bat" stop
```

### 3. Recompilar y desplegar el WAR manualmente

```powershell
cd C:\Users\sebar\Documents\NetBeansProjects\API_HealthBlood_512

& "C:\Program Files\NetBeans-25\netbeans\extide\ant\bin\ant.bat" -f build.xml dist

Copy-Item ".\dist\API_HealthBlood_512.war" `
  "C:\Users\sebar\AppData\Roaming\NetBeans\25\apache-tomcat-11.0.27_base\webapps\API_HealthBlood_512.war" `
  -Force
```

Si Tomcat ya estaba corriendo, el WAR nuevo se redespliega automáticamente.
Si no, inicia Tomcat con el comando del paso anterior.

---

## Rutas cortas para la presentación

Abre estas URLs directamente en el navegador:

```text
http://localhost:8080/API_HealthBlood_512/saludar
http://localhost:8080/API_HealthBlood_512/saludarPersonalizado?nombre=Sebastian
http://localhost:8080/API_HealthBlood_512/numero?numero=7
http://localhost:8080/API_HealthBlood_512/getAllUsuarios
http://localhost:8080/API_HealthBlood_512/getAllMedidas
http://localhost:8080/API_HealthBlood_512/getMedidasUsuario?idUsuario=1
http://localhost:8080/API_HealthBlood_512/getMedidasUsuario/1
http://localhost:8080/API_HealthBlood_512/getbyusuarioid?idUsuario=1
http://localhost:8080/API_HealthBlood_512/getByUsuarioId?idUsuario=1
http://localhost:8080/API_HealthBlood_512/getByUsuarioId/1
```

Insertar desde el navegador (solo para demostración; cada recarga inserta otro registro):

```text
http://localhost:8080/API_HealthBlood_512/addUsuario?nombre=Navegador&primerApellido=Prueba&segundoApellido=Endpoint&email=navegador.prueba@gluapi.com
http://localhost:8080/API_HealthBlood_512/addMedida?idUsuario=1&glucosa=120&fechaHora=2026-11-08T14:30:00&contexto=Prueba&notas=MedidaDesdeNavegador
http://localhost:8080/API_HealthBlood_512/insertarmedida?idUsuario=1&glucosa=110&contexto=Prueba&notas=MedidaAliasNavegador
```

---

## Si algo no funciona

### La aplicación no abre

Verifica que Tomcat esté escuchando:

```powershell
Test-NetConnection 127.0.0.1 -Port 8080
```

Si no responde, inicia Tomcat con la opción manual.

### Error de conexión con MySQL

Verifica el puerto `3306`:

```powershell
Test-NetConnection 127.0.0.1 -Port 3306
```

Si está cerrado, inicia el servicio **MySQL80**.

La aplicación usa estas credenciales en
`src\java\org\lasalle\controller\ConnectionMysql.java`:

```text
usuario: root
contraseña: admin
base: gluco_api
```

Si en otra computadora tu MySQL usa otra contraseña, cambia esa línea o ajusta
la contraseña de MySQL.

### El puerto 8080 está ocupado

Puede haber otro Tomcat viejo corriendo. Cierra esa ventana o detén el proceso
antes de iniciar el Tomcat 11. El proyecto está configurado para **Tomcat 11**,
no para Tomcat 10.1.

### La base no existe

Importa el script SQL:

```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p gluco_api < .\db\gluco_api.sql
```

La contraseña solicitada debe ser `admin` (o la que tengas configurada).

---

## Si lo vas a presentar en otra computadora

Necesitas:

1. Java 21 o superior.
2. MySQL 8.
3. Tomcat 11.
4. NetBeans (opcional, solo si quieres editar/compilar).
5. Copiar la carpeta del proyecto.
6. Importar `db\gluco_api.sql`.

Luego:

- Ajusta las rutas del script `iniciar-proyecto.ps1` si el Tomcat o el JDK están
  en otra ubicación.
- Ajusta la contraseña en `ConnectionMysql.java` si tu MySQL no usa `admin`.
- Configura Tomcat 11 en NetBeans o despliega el WAR manualmente.

La forma más sencilla de llevar todo es copiar:

```text
API_HealthBlood_512\
├─ dist\API_HealthBlood_512.war
├─ db\gluco_api.sql
└─ ...
```

---

## Resumen para mañana

1. Abre el proyecto o ejecuta `iniciar-proyecto.ps1`.
2. Confirma que MySQL esté encendido.
3. Abre `http://localhost:8080/API_HealthBlood_512/getAllMedidas`.
4. Si ves el JSON, la aplicación está lista para la presentación.
