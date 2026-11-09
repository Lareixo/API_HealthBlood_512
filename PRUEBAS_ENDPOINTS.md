# Pruebas de los endpoints REST — API_HealthBlood_512 (Gluapi)

Documento de evidencia con las solicitudes, respuestas reales y códigos de estado obtenidos
al ejecutar la API desplegada en Tomcat 11 con MySQL 8 (`gluco_api`).

- **URL corta (navegador):** `http://localhost:8080/API_HealthBlood_512`
- **URL completa (API):** `http://localhost:8080/API_HealthBlood_512/api/glucosa`
- **Formato:** formulario (`application/x-www-form-urlencoded`) y JSON (`application/json`) como compatibilidad
- **Fecha de ejecución:** 08/11/2026
- **Resultado global:** 34 pruebas ejecutadas — **34 OK / 0 fallidas**

Ambas formas de acceso funcionan: la ruta corta reenvía internamente a `/api/glucosa/...`,
así que se puede escribir directamente en el navegador
`http://localhost:8080/API_HealthBlood_512/getAllUsuarios`, etc.

Ejecución automatizada:

```powershell
cd C:\Users\sebar\Documents\NetBeansProjects\API_HealthBlood_512
powershell -ExecutionPolicy Bypass -File .\pruebas-endpoints.ps1
```

---

## 1. Resumen de endpoints

| # | Método | Ruta completa | Ruta corta (navegador) | Parámetros | Éxito | Errores documentados |
|---|--------|---------------|------------------------|------------|-------|----------------------|
| 1 | GET | `/api/glucosa/saludar` | `/saludar` | — | `200` | — |
| 2 | GET | `/api/glucosa/saludarPersonalizado` | `/saludarPersonalizado` | `nombre` (query) | `200` | — |
| 3 | GET | `/api/glucosa/Numero` | `/numero` | `numero` (query) | `200` | — |
| 4 | GET | `/api/glucosa/getAllUsuarios` | `/getAllUsuarios` | — | `200` | `500` si falla la BD |
| 5 | POST | `/api/glucosa/addUsuario` | `/addUsuario` | Form params: `nombre`, `primerApellido`, `segundoApellido`, `email` | `201` | `400` datos faltantes o longitud inválida, `500` error BD |
| 6 | POST | `/api/glucosa/addUsuario` | `/addUsuario` | Body JSON (compatibilidad) | `201` | `400` datos faltantes / JSON inválido, `500` error BD |
| 7 | GET | `/api/glucosa/addUsuario` | `/addUsuario` | `nombre`, `primerApellido`, `email` (query) | `201` | `400` datos faltantes |
| 8 | POST | `/api/glucosa/addMedida` | `/addMedida` | Form params: `idUsuario`, `glucosa`, `fechaHora`, `contexto`, `notas` | `201` | `400` datos inválidos o usuario inexistente, `500` error BD |
| 9 | POST | `/api/glucosa/addMedida` | `/addMedida` | Body JSON (compatibilidad) | `201` | `400` datos inválidos / JSON inválido, `500` error BD |
| 10 | GET | `/api/glucosa/addMedida` | `/addMedida` | `idUsuario`, `glucosa`, `fechaHora`, `contexto`, `notas` (query) | `201` | `400` datos inválidos |
| 11 | GET | `/api/glucosa/getAllMedidas` | `/getAllMedidas` | — | `200` | `500` si falla la BD |
| 12 | GET | `/api/glucosa/getMedidasUsuario` | `/getMedidasUsuario` | `idUsuario` (query) | `200` | `400` parámetro ausente o `<= 0` |
| 13 | GET | `/api/glucosa/getMedidasUsuario/{idUsuario}` | `/getMedidasUsuario/{idUsuario}` | `idUsuario` (ruta) | `200` | `400` id `<= 0` |
| 14 | GET | `/api/glucosa/getByUsuarioId` | `/getByUsuarioId` | `idUsuario` (query) | `200` | `400` parámetro ausente o `<= 0` |
| 15 | GET | `/api/glucosa/getByUsuarioId/{idUsuario}` | `/getByUsuarioId/{idUsuario}` | `idUsuario` (ruta) | `200` | `400` id `<= 0` |

> En todos los casos un usuario existente pero **sin medidas** devuelve `200` con `[]`.

---

## 2. Endpoints generales

### 2.1 `GET /saludar`

**curl**

```bash
curl -i http://localhost:8080/API_HealthBlood_512/api/glucosa/saludar
```

**PowerShell**

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/API_HealthBlood_512/api/glucosa/saludar"
```

**Respuesta real — `200 OK`**

```json
{ "response" : "Hola desde Java" }
```

---

### 2.2 `GET /saludarPersonalizado?nombre=Sebastian`

**curl**

```bash
curl -i "http://localhost:8080/API_HealthBlood_512/api/glucosa/saludarPersonalizado?nombre=Sebastian"
```

**Respuesta real — `200 OK`**

```json
{"response" : "Hola Sebastian desde Java"}
```

---

### 2.3 `GET /Numero?numero=7`

**curl**

```bash
curl -i "http://localhost:8080/API_HealthBlood_512/api/glucosa/Numero?numero=7"
```

**Respuesta real — `200 OK`**

```json
{"response" : "El cuadrado del numero 7 es: 49"}
```

---

## 3. Usuarios

### 3.1 `GET /getAllUsuarios` — listar usuarios (get user)

Devuelve todos los usuarios registrados. Es el endpoint equivalente a "get user".

**curl**

```bash
curl -i http://localhost:8080/API_HealthBlood_512/api/glucosa/getAllUsuarios
```

**PowerShell**

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/API_HealthBlood_512/api/glucosa/getAllUsuarios" |
    Format-Table idUsuario, nombre, primerApellido, segundoApellido, email -AutoSize
```

**Respuesta real — `200 OK`**

```json
[
  {
    "idUsuario": 1,
    "nombre": "Sebastian",
    "primerApellido": "G",
    "segundoApellido": "R",
    "email": "sebastian@gmail.com"
  }
]
```

**Modelo JSON del usuario**

| Campo | Tipo | Obligatorio |
|-------|------|-------------|
| `idUsuario` | int | Generado por la BD |
| `nombre` | string | Sí |
| `primerApellido` | string | Sí |
| `segundoApellido` | string | No |
| `email` | string | Sí |

---

### 3.2 `POST /addUsuario` — crear usuario con form params

El endpoint principal recibe los datos como `application/x-www-form-urlencoded`
y los enlaza con `@FormParam`:

| Parámetro | Obligatorio | Longitud máxima |
|-----------|-------------|-----------------|
| `nombre` | Sí | 40 |
| `primerApellido` | Sí | 40 |
| `segundoApellido` | No | 40 |
| `email` | Sí | 50 |

**curl**

```bash
curl -i -X POST http://localhost:8080/API_HealthBlood_512/api/glucosa/addUsuario \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "nombre=Usuario" \
  --data-urlencode "primerApellido=Prueba" \
  --data-urlencode "segundoApellido=Endpoint" \
  --data-urlencode "email=prueba.form@gluapi.com"
```

**PowerShell**

```powershell
$form = @{
    nombre          = "Usuario"
    primerApellido  = "Prueba"
    segundoApellido = "Endpoint"
    email           = "prueba.form@gluapi.com"
}

Invoke-RestMethod -Method Post `
    -Uri "http://localhost:8080/API_HealthBlood_512/api/glucosa/addUsuario" `
    -ContentType "application/x-www-form-urlencoded" `
    -Body $form
```

**Respuesta real — `201 Created`**

```json
{
  "idUsuario": 11,
  "nombre": "Usuario",
  "primerApellido": "Prueba",
  "segundoApellido": "Endpoint",
  "email": "prueba.form@gluapi.com"
}
```

> El `idUsuario` lo asigna MySQL (`AUTO_INCREMENT`). Al repetir la prueba se crea un
> usuario nuevo con otro id, porque la tabla `usuario` no tiene restricción `UNIQUE`
> sobre `email`.

---

### 3.2b `POST /addUsuario` — variante JSON (compatibilidad)

También se acepta el mismo endpoint con un body JSON; el método se elige según el
`Content-Type` de la solicitud:

```bash
curl -i -X POST http://localhost:8080/API_HealthBlood_512/api/glucosa/addUsuario \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Usuario","primerApellido":"Prueba","segundoApellido":"Endpoint","email":"prueba.json@gluapi.com"}'
```

La respuesta es igual: `201 Created` con el usuario insertado y su `idUsuario`.

---

### 3.2c `GET /addUsuario` — crear usuario desde el navegador

Variante pensada para probar el endpoint escribiendo la URL directamente:

```text
http://localhost:8080/API_HealthBlood_512/addUsuario?nombre=Navegador&primerApellido=Prueba&segundoApellido=Endpoint&email=navegador@gluapi.com
```

**curl**

```bash
curl -i "http://localhost:8080/API_HealthBlood_512/addUsuario?nombre=Navegador&primerApellido=Prueba&email=navegador@gluapi.com"
```

**Respuesta real — `201 Created`**

```json
{
  "idUsuario": 13,
  "nombre": "Navegador",
  "primerApellido": "Prueba",
  "email": "navegador@gluapi.com"
}
```

Si faltan `nombre`, `primerApellido` o `email`, responde `400` con el mismo mensaje de
campos obligatorios.

> Cada vez que se recarga esa URL se crea un usuario nuevo. Es útil para demostrar el
> endpoint, pero no se debe usar en producción (los `GET` no deberían modificar datos).

---

### 3.3 `POST /addUsuario` — error por campos obligatorios

**curl**

```bash
curl -i -X POST http://localhost:8080/API_HealthBlood_512/api/glucosa/addUsuario \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data "nombre=Sin&primerApellido=Correo"
```

**Respuesta real — `400 Bad Request`**

```json
{"result": "Los campos nombre, primerApellido y email son obligatorios"}
```

Se dispara cuando falta `nombre`, `primerApellido` o `email`, o cuando vienen vacíos.
También responde `400` si algún valor excede la longitud de su columna en MySQL.

---

### 3.4 `POST /addUsuario` — error por JSON inválido

**curl**

```bash
curl -i -X POST http://localhost:8080/API_HealthBlood_512/api/glucosa/addUsuario \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Sin cerrar"'
```

**Respuesta real — `400 Bad Request`**

```json
{"result": "El JSON enviado no es valido"}
```

---

## 4. Medidas de glucosa

### 4.0 `POST /addMedida` — insertar medida con form params

El endpoint recibe los datos como `application/x-www-form-urlencoded` y los enlaza
con `@FormParam`:

| Parámetro | Obligatorio | Longitud / formato |
|-----------|-------------|--------------------|
| `idUsuario` | Sí | Entero mayor a 0; debe existir en `usuario` |
| `glucosa` | Sí | Entero mayor a 0 |
| `fechaHora` | No | `yyyy-MM-dd HH:mm:ss`, `yyyy-MM-dd'T'HH:mm:ss` o variantes sin segundos. Si se omite, se usa la fecha/hora actual |
| `contexto` | No | Máximo 40 caracteres |
| `notas` | No | Máximo 100 caracteres |

**curl**

```bash
curl -i -X POST http://localhost:8080/API_HealthBlood_512/api/glucosa/addMedida \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "idUsuario=1" \
  --data-urlencode "glucosa=123" \
  --data-urlencode "fechaHora=2026-11-08T14:30:00" \
  --data-urlencode "contexto=Prueba" \
  --data-urlencode "notas=MedidaPruebaEndpoints"
```

**PowerShell**

```powershell
$medida = @{
    idUsuario = "1"
    glucosa   = "123"
    fechaHora = "2026-11-08T14:30:00"
    contexto  = "Prueba"
    notas     = "MedidaPruebaEndpoints"
}

Invoke-RestMethod -Method Post `
    -Uri "http://localhost:8080/API_HealthBlood_512/api/glucosa/addMedida" `
    -ContentType "application/x-www-form-urlencoded" `
    -Body $medida
```

**Respuesta real — `201 Created`**

```json
{
  "idMedida": 4,
  "idUsuario": 1,
  "glucosa": 123,
  "fechaHora": "2026-11-08 14:30:00",
  "contexto": "Prueba",
  "notas": "MedidaPruebaEndpoints"
}
```

Si falta `idUsuario` o `glucosa`, si la fecha no tiene un formato válido o si el
usuario no existe, responde `400 Bad Request` con un JSON explicando el error.

---

### 4.0b `POST /addMedida` — variante JSON (compatibilidad)

```bash
curl -i -X POST http://localhost:8080/API_HealthBlood_512/api/glucosa/addMedida \
  -H "Content-Type: application/json" \
  -d '{"idUsuario":1,"glucosa":99,"fechaHora":"2026-11-08T16:00:00","contexto":"JSON","notas":"MedidaPruebaJson"}'
```

La respuesta es igual: `201 Created` con la medida insertada y su `idMedida`.

También existe la variante `GET /addMedida?...` para probar desde el navegador:

```text
http://localhost:8080/API_HealthBlood_512/addMedida?idUsuario=1&glucosa=77&contexto=Navegador&notas=MedidaPruebaNavegador
```

---

### 4.0c `GET /getByUsuarioId` — alias de consulta por usuario

Además de `getMedidasUsuario`, se puede usar el alias:

```bash
curl -i "http://localhost:8080/API_HealthBlood_512/api/glucosa/getByUsuarioId?idUsuario=1"
curl -i "http://localhost:8080/API_HealthBlood_512/api/glucosa/getByUsuarioId/1"
```

Ruta corta equivalente:

```text
http://localhost:8080/API_HealthBlood_512/getbyusuarioid?idUsuario=1
```

Devuelve el mismo arreglo de medidas que `getMedidasUsuario`.

---

### 4.1 `GET /getAllMedidas` — todas las medidas

**curl**

```bash
curl -i http://localhost:8080/API_HealthBlood_512/api/glucosa/getAllMedidas
```

**PowerShell**

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/API_HealthBlood_512/api/glucosa/getAllMedidas" |
    Format-Table idMedida, idUsuario, glucosa, fechaHora, contexto, notas -AutoSize
```

**Respuesta real — `200 OK`**

```json
[
  {
    "idMedida": 1,
    "idUsuario": 1,
    "glucosa": 95,
    "fechaHora": "2026-11-06 08:00:00",
    "contexto": "Ayuno",
    "notas": "Medicion matutina"
  },
  {
    "idMedida": 2,
    "idUsuario": 1,
    "glucosa": 140,
    "fechaHora": "2026-11-06 14:00:00",
    "contexto": "Despues de comer",
    "notas": "Comida con arroz"
  },
  {
    "idMedida": 3,
    "idUsuario": 1,
    "glucosa": 110,
    "fechaHora": "2026-11-06 20:00:00",
    "contexto": "Antes de dormir"
  }
]
```

> Gson omite los campos nulos: la medida 3 no incluye la clave `notas`.

---

### 4.2 `GET /getMedidasUsuario?idUsuario=1` — medidas por usuario (query)

**curl**

```bash
curl -i "http://localhost:8080/API_HealthBlood_512/api/glucosa/getMedidasUsuario?idUsuario=1"
```

**PowerShell**

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/API_HealthBlood_512/api/glucosa/getMedidasUsuario?idUsuario=1"
```

**Respuesta real — `200 OK`**

```json
[
  {
    "idMedida": 1,
    "idUsuario": 1,
    "glucosa": 95,
    "fechaHora": "2026-11-06 08:00:00",
    "contexto": "Ayuno",
    "notas": "Medicion matutina"
  },
  {
    "idMedida": 2,
    "idUsuario": 1,
    "glucosa": 140,
    "fechaHora": "2026-11-06 14:00:00",
    "contexto": "Despues de comer",
    "notas": "Comida con arroz"
  },
  {
    "idMedida": 3,
    "idUsuario": 1,
    "glucosa": 110,
    "fechaHora": "2026-11-06 20:00:00",
    "contexto": "Antes de dormir"
  }
]
```

---

### 4.3 `GET /getMedidasUsuario/1` — medidas por usuario (id en la ruta)

**curl**

```bash
curl -i http://localhost:8080/API_HealthBlood_512/api/glucosa/getMedidasUsuario/1
```

**Respuesta real — `200 OK`**

Devuelve exactamente el mismo arreglo que la variante con query string
(las 3 medidas del usuario 1).

---

### 4.4 `GET /getMedidasUsuario` — error sin parámetro

**curl**

```bash
curl -i http://localhost:8080/API_HealthBlood_512/api/glucosa/getMedidasUsuario
```

**Respuesta real — `400 Bad Request`**

```json
{"result": "El parametro idUsuario es obligatorio y debe ser mayor a 0"}
```

---

### 4.5 `GET /getMedidasUsuario?idUsuario=0` — error por id inválido

**curl**

```bash
curl -i "http://localhost:8080/API_HealthBlood_512/api/glucosa/getMedidasUsuario?idUsuario=0"
```

**Respuesta real — `400 Bad Request`**

```json
{"result": "El parametro idUsuario es obligatorio y debe ser mayor a 0"}
```

Mismo comportamiento para valores negativos.

---

### 4.6 `GET /getMedidasUsuario?idUsuario=999` — usuario sin registros

**curl**

```bash
curl -i "http://localhost:8080/API_HealthBlood_512/api/glucosa/getMedidasUsuario?idUsuario=999"
```

**Respuesta real — `200 OK`**

```json
[]
```

Un id válido pero sin medidas **no** es un error: devuelve lista vacía.

---

## 5. Casos de error adicionales

### 5.1 Ruta inexistente

**curl**

```bash
curl -i http://localhost:8080/API_HealthBlood_512/api/glucosa/noExiste
```

**Respuesta real — `404 Not Found`**

Página HTML de error de Tomcat (`HTTP Status 404 – Not Found`), no JSON.
Esto confirma que solo las rutas declaradas en `RestGlucosa` están publicadas bajo
`/api/glucosa`.

---

## 6. Resultado de la ejecución automática

Salida resumida de `pruebas-endpoints.ps1`:

```text
PRUEBAS DE ENDPOINTS - API_HealthBlood_512 (Gluapi)
URL base: http://localhost:8080/API_HealthBlood_512/api/glucosa

[OK] Saludar
[OK] Saludo personalizado
[OK] Cuadrado de un numero
[OK] Listar usuarios (GET USER)
[OK] Crear usuario valido (form params)
[OK] Crear usuario valido (JSON compatible)
[OK] Crear usuario sin email (form, error 400)
[OK] Crear usuario con JSON invalido (error 400)
[OK] Crear medida valida (form params)
[OK] Crear medida valida (JSON compatible)
[OK] Crear medida sin glucosa (form, error 400)
[OK] Crear medida con usuario inexistente (error 400)
[OK] Crear medida con fecha invalida (error 400)
[OK] Todas las medidas (GET ALL)
[OK] Todas las medidas incluyen la medida insertada
[OK] Medidas por usuario incluyen la medida insertada
[OK] Alias getByUsuarioId (query string)
[OK] Alias getByUsuarioId (id en la ruta)
[OK] Medidas por usuario (query string)
[OK] Medidas por usuario (id en la ruta)
[OK] Medidas sin parametro (error 400)
[OK] Medidas con idUsuario=0 (error 400)
[OK] Medidas de un usuario sin registros (lista vacia)

PRUEBAS CON RUTAS CORTAS DE NAVEGADOR
[OK] Corta: Saludar
[OK] Corta: listar usuarios
[OK] Corta: todas las medidas
[OK] Corta: medidas por usuario (query)
[OK] Corta: medidas por usuario (ruta)
[OK] Corta: medidas por usuario (alias getbyusuarioid)
[OK] Corta: todas las medidas incluyen la nueva
[OK] Corta: insertar medida (POST)
[OK] Corta: insertar medida desde el navegador (GET)
[OK] Corta: crear usuario desde el navegador (GET)
[OK] Corta: crear usuario sin datos (error 400)

========================================================================
RESUMEN: 34 pruebas | 34 OK | 0 fallidas
========================================================================
```

---

## 7. Cómo probarlo desde el navegador

Todos los endpoints `GET` se pueden abrir directamente con la ruta corta:

- <http://localhost:8080/API_HealthBlood_512/saludar>
- <http://localhost:8080/API_HealthBlood_512/saludarPersonalizado?nombre=Sebastian>
- <http://localhost:8080/API_HealthBlood_512/numero?numero=7>
- <http://localhost:8080/API_HealthBlood_512/getAllUsuarios>
- <http://localhost:8080/API_HealthBlood_512/getAllMedidas>
- <http://localhost:8080/API_HealthBlood_512/getMedidasUsuario?idUsuario=1>
- <http://localhost:8080/API_HealthBlood_512/getMedidasUsuario/1>
- <http://localhost:8080/API_HealthBlood_512/getbyusuarioid?idUsuario=1>
- <http://localhost:8080/API_HealthBlood_512/getByUsuarioId?idUsuario=1>
- <http://localhost:8080/API_HealthBlood_512/getByUsuarioId/1>
- <http://localhost:8080/API_HealthBlood_512/addMedida?idUsuario=1&glucosa=77&contexto=Navegador&notas=MedidaPruebaNavegador>
- <http://localhost:8080/API_HealthBlood_512/addUsuario?nombre=Navegador&primerApellido=Prueba&email=navegador@gluapi.com>

Y también con la ruta completa, por ejemplo:

- <http://localhost:8080/API_HealthBlood_512/api/glucosa/getAllUsuarios>
- <http://localhost:8080/API_HealthBlood_512/api/glucosa/getAllMedidas>
- <http://localhost:8080/API_HealthBlood_512/api/glucosa/getByUsuarioId/1>

Los `POST` se prueban con `curl`, Postman, Thunder Client o el script de PowerShell,
enviando los campos como form params (`application/x-www-form-urlencoded`).
La variante JSON sigue disponible usando `Content-Type: application/json`.

---

## 8. Notas de operación

- Tomcat debe estar corriendo en el puerto **8080** con la aplicación desplegada desde
  `build\web` (NetBeans lo hace con **Run Project (F6)**).
- MySQL debe estar activo (`servicio MySQL80`) con la base `gluco_api`.
- El **HTTP Monitor** de NetBeans debe permanecer desactivado; si se activa, Tomcat 11
  falla con `NoClassDefFoundError: javax/servlet/Filter`.
- Cada ejecución de las pruebas de creación inserta registros nuevos
  (no hay `UNIQUE` en `email`). Si se quieren limpiar, ejecutar:

```sql
-- Primero las medidas de prueba (por la clave foránea)
DELETE FROM medida
WHERE notas LIKE 'MedidaPrueba%';

-- Después los usuarios de prueba
DELETE FROM usuario
WHERE email IN (
    'prueba.form@gluapi.com',
    'prueba.json@gluapi.com',
    'navegador.endpoint@gluapi.com'
);
```

Las medidas se eliminan primero porque `medida.idUsuario` referencia a `usuario.idUsuario`.
