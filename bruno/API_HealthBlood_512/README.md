# Colección Bruno - API HealthBlood 512 (CRUD)

Esta colección prueba el CRUD completo de **usuarios** y **medidas** de la API.

## Cómo abrirla en Bruno

1. Abre **Bruno**.
2. Clic en **Collection** → **Open Collection**.
3. Selecciona esta carpeta:

   ```text
   C:\Users\sebar\Documents\NetBeansProjects\API_HealthBlood_512\bruno\API_HealthBlood_512
   ```

4. Arriba a la derecha, selecciona el environment:
   - **Render**: usa la API desplegada en Render.
   - **Local**: usa `http://localhost:8080/API_HealthBlood_512`.

## Orden recomendado

### Usuarios

1. `Listar usuarios` (GET)
2. `Obtener usuario por ID` (GET)
3. `Crear usuario` (POST) → guarda el id en la variable `idUsuario`
4. `Actualizar usuario` (PUT) → usa `idUsuario`
5. `Eliminar usuario` (DELETE) → usa `idUsuario`
6. `Eliminar usuario en cascada` (DELETE, solo si tiene medidas)

### Medidas

1. `Listar medidas` (GET)
2. `Obtener medida por ID` (GET)
3. `Crear medida` (POST) → guarda el id en la variable `idMedida`
4. `Actualizar medida` (PUT) → usa `idMedida`
5. `Eliminar medida` (DELETE) → usa `idMedida`
6. `Medidas por usuario` (GET)

> **Importante:** las requests de crear guardan el id creado en el environment.
> Ejecuta `Crear usuario` antes de `Actualizar/Eliminar usuario`, y `Crear medida`
> antes de `Actualizar/Eliminar medida`, para no modificar datos reales.
>
> `Eliminar usuario en cascada` borra también todas las medidas de ese usuario.
> Úsala solo con usuarios de prueba.

## Variables del environment

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `baseUrl` | URL base de la API | Render o Local |
| `idUsuario` | Usuario que se consulta/edita/elimina | `1` |
| `idMedida` | Medida que se consulta/edita/elimina | `4` |

## Endpoints del CRUD

| Operación | Método | Ruta |
|---|---|---|
| Listar usuarios | GET | `/getAllUsuarios` |
| Obtener usuario | GET | `/getUsuarioById/{id}` |
| Crear usuario | POST | `/addUsuario` |
| Actualizar usuario | PUT | `/updateUsuario/{id}` |
| Eliminar usuario | DELETE | `/deleteUsuario/{id}` |
| Eliminar usuario + medidas | DELETE | `/deleteUsuario/{id}?cascade=true` |
| Listar medidas | GET | `/getAllMedidas` |
| Obtener medida | GET | `/getMedidaById/{id}` |
| Crear medida | POST | `/addMedida` |
| Actualizar medida | PUT | `/updateMedida/{id}` |
| Eliminar medida | DELETE | `/deleteMedida/{id}` |
| Medidas por usuario | GET | `/getMedidasUsuario?idUsuario={id}` |
