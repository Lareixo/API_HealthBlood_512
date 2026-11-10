/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.lasalle.controller.ControllerMedida;
import org.lasalle.controller.ControllerrUsuario;
import org.lasalle.model.Medida;
import org.lasalle.model.Usuario;



/**
 *
 * @author osmar
 */
@Path("glucosa")
public class RestGlucosa {

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter[] FORMATOS_FECHA_HORA = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    };

    
    @Path("saludar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludar (){
        String respuesta = """
                           { "response" : "Hola desde Java" }
                           """;
        return Response.ok(respuesta).build();
     
    }

    /**
     * GET /api/glucosa/estado
     * Comprueba que la API puede hablar con la base de datos.
     */
    @Path("estado")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response estado() {
        try {
            int usuarios = new ControllerrUsuario().getUsuarios().size();

            String respuesta = "{\"status\":\"ok\",\"database\":\"ok\",\"usuarios\":"
                    + usuarios + "}";

            return Response.ok(respuesta).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"status\":\"error\",\"database\":\"error\"}")
                    .build();
        }
    }
     
    @Path("saludarPersonalizado")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludarPersonalizado(
            @QueryParam("nombre") String nombre
    ){
        String respuesta = """
                           {"response" : "Hola %s desde Java"}
                           """;
        
        respuesta = String.format(respuesta, nombre);
        
        return Response.ok(respuesta).build();
    }
    
    @Path("Numero")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response Numero(
            @QueryParam("numero") int numero
    ){
        int cuadrado = numero * numero;
        String respuesta = """
                           {"response" : "El cuadrado del numero %s es: %s"}
                           """;
        
        respuesta = String.format(respuesta, numero, cuadrado);
        
        return Response.ok(respuesta).build();
    }
    
    
@Path("getAllUsuarios")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Response getAllUsuarios() {

    ControllerrUsuario cu = new ControllerrUsuario();
    Gson gson = new Gson();

    try {
        List<Usuario> lista = cu.getUsuarios();

        String out = gson.toJson(lista);

        return Response.ok(out).build();

    } catch (Exception e) {

        e.printStackTrace();

        String error = """
            {"result": "Error al obtener los usuarios"}
            """;

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
    }
}

    /**
     * GET /api/glucosa/getUsuarioById?idUsuario=1
     * Devuelve un usuario por su id.
     */
    @Path("getUsuarioById")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsuarioById(@QueryParam("idUsuario") Integer idUsuario) {
        return responderUsuario(idUsuario);
    }

    /**
     * GET /api/glucosa/getUsuarioById/1
     * Variante con el id en la ruta.
     */
    @Path("getUsuarioById/{idUsuario}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsuarioByIdPorRuta(@PathParam("idUsuario") int idUsuario) {
        return responderUsuario(idUsuario);
    }

    private Response responderUsuario(Integer idUsuario) {

        if (idUsuario == null || idUsuario <= 0) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El parametro idUsuario es obligatorio y debe ser mayor a 0");
        }

        try {
            ControllerrUsuario cu = new ControllerrUsuario();
            Usuario user = cu.getUsuarioById(idUsuario);

            if (user == null) {
                return respuestaError(Response.Status.NOT_FOUND,
                        "Usuario no encontrado");
            }

            return Response.ok(new Gson().toJson(user)).build();

        } catch (Exception e) {

            e.printStackTrace();

            return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al obtener el usuario");
        }
    }

    /**
     * POST /api/glucosa/addUsuario
     * Content-Type: application/x-www-form-urlencoded
     *
     * Parametros: nombre, primerApellido, segundoApellido (opcional) y email.
     * Ejemplo: nombre=Juan&primerApellido=Perez&email=juan@correo.com
     */
    @Path("addUsuario")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUsuario(
            @FormParam("nombre") String nombre,
            @FormParam("primerApellido") String primerApellido,
            @FormParam("segundoApellido") String segundoApellido,
            @FormParam("email") String email) {

        Usuario user = new Usuario();
        user.setNombre(nombre);
        user.setPrimerApellido(primerApellido);
        user.setSegundoApellido(segundoApellido);
        user.setEmail(email);

        return guardarUsuario(user);
    }

    /**
     * Variante del POST que recibe el usuario en un body JSON.
     * Se conserva para no romper clientes que ya usan este formato.
     *
     * POST /api/glucosa/addUsuario
     * Body: {"nombre":"...","primerApellido":"...","segundoApellido":"...","email":"..."}
     */
    @Path("addUsuario")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUsuarioJson(String json) {

        try {
            Usuario user = new Gson().fromJson(json, Usuario.class);
            return guardarUsuario(user);

        } catch (com.google.gson.JsonSyntaxException e) {

            return respuestaError(Response.Status.BAD_REQUEST,
                    "El JSON enviado no es valido");

        } catch (Exception e) {

            e.printStackTrace();

            return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al guardar el usuario");
        }
    }

    @Path("addUsuario")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUsuarioDesdeNavegador(
            @QueryParam("nombre") String nombre,
            @QueryParam("primerApellido") String primerApellido,
            @QueryParam("segundoApellido") String segundoApellido,
            @QueryParam("email") String email) {

        Usuario user = new Usuario();
        user.setNombre(nombre);
        user.setPrimerApellido(primerApellido);
        user.setSegundoApellido(segundoApellido);
        user.setEmail(email);

        return guardarUsuario(user);
    }

    private Response guardarUsuario(Usuario user) {

        String error = validarUsuario(user);

        if (error != null) {
            return respuestaError(Response.Status.BAD_REQUEST, error);
        }

        try {
            ControllerrUsuario cu = new ControllerrUsuario();
            int idUsuario = cu.addUsuario(user);

            if (idUsuario <= 0) {
                return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                        "No se pudo obtener el id del usuario insertado");
            }

            user.setIdUsuario(idUsuario);

            return Response.status(Response.Status.CREATED)
                    .entity(new Gson().toJson(user))
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al guardar el usuario");
        }
    }

    private static String validarUsuario(Usuario user) {

        if (user == null) {
            return "Los campos nombre, primerApellido y email son obligatorios";
        }

        String nombre = limpiar(user.getNombre());
        String primerApellido = limpiar(user.getPrimerApellido());
        String segundoApellido = limpiar(user.getSegundoApellido());
        String email = limpiar(user.getEmail());

        if (estaVacio(nombre) || estaVacio(primerApellido) || estaVacio(email)) {
            return "Los campos nombre, primerApellido y email son obligatorios";
        }

        // La tabla usuario define VARCHAR(40) para nombres/apellidos y VARCHAR(50) para email.
        if (nombre.length() > 40
                || primerApellido.length() > 40
                || (segundoApellido != null && segundoApellido.length() > 40)
                || email.length() > 50) {

            return "Longitud invalida: nombre y apellidos maximo 40 caracteres, email maximo 50";
        }

        user.setNombre(nombre);
        user.setPrimerApellido(primerApellido);
        user.setSegundoApellido(segundoApellido);
        user.setEmail(email);

        return null;
    }

    /**
     * PUT /api/glucosa/updateUsuario/1
     * Body JSON con los datos completos del usuario.
     */
    @Path("updateUsuario/{idUsuario}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUsuario(@PathParam("idUsuario") int idUsuario, String json) {
        return actualizarUsuario(idUsuario, json);
    }

    /**
     * PUT /api/glucosa/updateUsuario?idUsuario=1
     * Variante con el id como parametro de consulta.
     */
    @Path("updateUsuario")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUsuarioPorQuery(@QueryParam("idUsuario") Integer idUsuario, String json) {

        if (idUsuario == null || idUsuario <= 0) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El parametro idUsuario es obligatorio y debe ser mayor a 0");
        }

        return actualizarUsuario(idUsuario, json);
    }

    private Response actualizarUsuario(int idUsuario, String json) {

        try {
            Usuario user = new Gson().fromJson(json, Usuario.class);
            String error = validarUsuario(user);

            if (error != null) {
                return respuestaError(Response.Status.BAD_REQUEST, error);
            }

            ControllerrUsuario cu = new ControllerrUsuario();

            if (!cu.existeUsuario(idUsuario)) {
                return respuestaError(Response.Status.NOT_FOUND,
                        "Usuario no encontrado");
            }

            user.setIdUsuario(idUsuario);

            if (!cu.updateUsuario(user)) {
                return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                        "No se pudo actualizar el usuario");
            }

            String respuesta = "{\"result\":\"Usuario actualizado\",\"usuario\":"
                    + new Gson().toJson(user) + "}";

            return Response.ok(respuesta).build();

        } catch (com.google.gson.JsonSyntaxException e) {

            return respuestaError(Response.Status.BAD_REQUEST,
                    "El JSON enviado no es valido");

        } catch (Exception e) {

            e.printStackTrace();

            return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al actualizar el usuario");
        }
    }

    /**
     * DELETE /api/glucosa/deleteUsuario/1
     * Por defecto no borra un usuario con medidas. Usar ?cascade=true para
     * eliminar tambien sus medidas.
     */
    @Path("deleteUsuario/{idUsuario}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUsuario(@PathParam("idUsuario") int idUsuario,
            @QueryParam("cascade") boolean cascade) {
        return eliminarUsuario(idUsuario, cascade);
    }

    /**
     * DELETE /api/glucosa/deleteUsuario?idUsuario=1&cascade=true
     * Variante con parametros de consulta.
     */
    @Path("deleteUsuario")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUsuarioPorQuery(@QueryParam("idUsuario") Integer idUsuario,
            @QueryParam("cascade") boolean cascade) {

        if (idUsuario == null || idUsuario <= 0) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El parametro idUsuario es obligatorio y debe ser mayor a 0");
        }

        return eliminarUsuario(idUsuario, cascade);
    }

    private Response eliminarUsuario(int idUsuario, boolean cascade) {

        try {
            ControllerrUsuario cu = new ControllerrUsuario();

            if (!cu.existeUsuario(idUsuario)) {
                return respuestaError(Response.Status.NOT_FOUND,
                        "Usuario no encontrado");
            }

            ControllerMedida cm = new ControllerMedida();
            int medidas = cm.contarMedidasByUsuario(idUsuario);

            if (medidas > 0 && !cascade) {
                return respuestaError(Response.Status.CONFLICT,
                        "El usuario tiene " + medidas
                        + " medida(s) registrada(s). Use ?cascade=true para eliminarlas junto con el usuario");
            }

            if (medidas > 0) {
                cm.deleteMedidasByUsuario(idUsuario);
            }

            if (!cu.deleteUsuario(idUsuario)) {
                return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                        "No se pudo eliminar el usuario");
            }

            String respuesta = "{\"result\":\"Usuario eliminado\",\"idUsuario\":"
                    + idUsuario + ",\"medidasEliminadas\":" + medidas + "}";

            return Response.ok(respuesta).build();

        } catch (Exception e) {

            e.printStackTrace();

            return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al eliminar el usuario");
        }
    }

    private static String limpiar(String valor) {
        return valor == null ? null : valor.trim();
    }

    private static boolean estaVacio(String valor) {
        return valor == null || valor.isEmpty();
    }

    private static Response respuestaError(Response.Status estado, String mensaje) {
        String json = "{\"result\":" + new Gson().toJson(mensaje) + "}";

        return Response.status(estado)
                .entity(json)
                .build();
    }

    /**
     * POST /api/glucosa/addMedida
     * Content-Type: application/x-www-form-urlencoded
     *
     * Parametros: idUsuario, glucosa, fechaHora (opcional), contexto (opcional)
     * y notas (opcional).
     * Ejemplo: idUsuario=1&glucosa=120&fechaHora=2026-11-08T14:30:00
     */
    @Path("addMedida")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMedida(
            @FormParam("idUsuario") String idUsuario,
            @FormParam("glucosa") String glucosa,
            @FormParam("fechaHora") String fechaHora,
            @FormParam("contexto") String contexto,
            @FormParam("notas") String notas) {

        return guardarMedida(idUsuario, glucosa, fechaHora, contexto, notas);
    }

    /**
     * Variante JSON del POST /addMedida.
     * Body: {"idUsuario":1,"glucosa":120,"fechaHora":"2026-11-08 14:30:00",
     *        "contexto":"Ayuno","notas":"..."}
     */
    @Path("addMedida")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMedidaJson(String json) {

        try {
            Medida medida = new Gson().fromJson(json, Medida.class);
            return guardarMedida(medida);

        } catch (com.google.gson.JsonSyntaxException e) {

            return respuestaError(Response.Status.BAD_REQUEST,
                    "El JSON enviado no es valido");

        } catch (Exception e) {

            e.printStackTrace();

            return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al guardar la medida");
        }
    }

    /**
     * GET /api/glucosa/addMedida
     * Variante para probar la insercion desde el navegador.
     */
    @Path("addMedida")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMedidaDesdeNavegador(
            @QueryParam("idUsuario") String idUsuario,
            @QueryParam("glucosa") String glucosa,
            @QueryParam("fechaHora") String fechaHora,
            @QueryParam("contexto") String contexto,
            @QueryParam("notas") String notas) {

        return guardarMedida(idUsuario, glucosa, fechaHora, contexto, notas);
    }

    private Response guardarMedida(String idUsuario, String glucosa,
            String fechaHora, String contexto, String notas) {

        Integer id = parseEntero(idUsuario);

        if (id == null || id <= 0) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El campo idUsuario es obligatorio y debe ser mayor a 0");
        }

        Integer valorGlucosa = parseEntero(glucosa);

        if (valorGlucosa == null || valorGlucosa <= 0) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El campo glucosa es obligatorio y debe ser mayor a 0");
        }

        String fecha = normalizarFechaHora(fechaHora);

        if (fecha == null) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El campo fechaHora debe tener un formato valido, por ejemplo 2026-11-08 14:30:00");
        }

        String contextoLimpio = limpiar(contexto);
        String notasLimpio = limpiar(notas);

        if (contextoLimpio != null && contextoLimpio.length() > 40) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El campo contexto permite maximo 40 caracteres");
        }

        if (notasLimpio != null && notasLimpio.length() > 100) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El campo notas permite maximo 100 caracteres");
        }

        Medida medida = new Medida(0, id, valorGlucosa, fecha,
                contextoLimpio, notasLimpio);

        return guardarMedida(medida);
    }

    private Response guardarMedida(Medida medida) {

        String error = validarMedida(medida);

        if (error != null) {
            return respuestaError(Response.Status.BAD_REQUEST, error);
        }

        try {
            ControllerrUsuario cu = new ControllerrUsuario();

            if (!cu.existeUsuario(medida.getIdUsuario())) {
                return respuestaError(Response.Status.BAD_REQUEST,
                        "El usuario indicado no existe");
            }

            ControllerMedida cm = new ControllerMedida();
            int idMedida = cm.addMedida(medida);

            if (idMedida <= 0) {
                return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                        "No se pudo obtener el id de la medida insertada");
            }

            medida.setIdMedida(idMedida);

            return Response.status(Response.Status.CREATED)
                    .entity(new Gson().toJson(medida))
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al guardar la medida");
        }
    }

    private static String validarMedida(Medida medida) {

        if (medida == null) {
            return "Los campos idUsuario y glucosa son obligatorios";
        }

        if (medida.getIdUsuario() <= 0) {
            return "El campo idUsuario es obligatorio y debe ser mayor a 0";
        }

        if (medida.getGlucosa() <= 0) {
            return "El campo glucosa es obligatorio y debe ser mayor a 0";
        }

        String contexto = limpiar(medida.getContexto());
        String notas = limpiar(medida.getNotas());

        if (contexto != null && contexto.length() > 40) {
            return "El campo contexto permite maximo 40 caracteres";
        }

        if (notas != null && notas.length() > 100) {
            return "El campo notas permite maximo 100 caracteres";
        }

        String fecha = normalizarFechaHora(medida.getFechaHora());

        if (fecha == null) {
            return "El campo fechaHora debe tener un formato valido, por ejemplo 2026-11-08 14:30:00";
        }

        medida.setFechaHora(fecha);
        medida.setContexto(contexto);
        medida.setNotas(notas);

        return null;
    }

    /**
     * PUT /api/glucosa/updateMedida/1
     * Body JSON con los datos completos de la medida.
     */
    @Path("updateMedida/{idMedida}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMedida(@PathParam("idMedida") int idMedida, String json) {
        return actualizarMedida(idMedida, json);
    }

    /**
     * PUT /api/glucosa/updateMedida?idMedida=1
     * Variante con el id como parametro de consulta.
     */
    @Path("updateMedida")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMedidaPorQuery(@QueryParam("idMedida") Integer idMedida, String json) {

        if (idMedida == null || idMedida <= 0) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El parametro idMedida es obligatorio y debe ser mayor a 0");
        }

        return actualizarMedida(idMedida, json);
    }

    private Response actualizarMedida(int idMedida, String json) {

        try {
            Medida medida = new Gson().fromJson(json, Medida.class);
            String error = validarMedida(medida);

            if (error != null) {
                return respuestaError(Response.Status.BAD_REQUEST, error);
            }

            ControllerMedida cm = new ControllerMedida();

            if (cm.getMedidaById(idMedida) == null) {
                return respuestaError(Response.Status.NOT_FOUND,
                        "Medida no encontrada");
            }

            ControllerrUsuario cu = new ControllerrUsuario();

            if (!cu.existeUsuario(medida.getIdUsuario())) {
                return respuestaError(Response.Status.BAD_REQUEST,
                        "El usuario indicado no existe");
            }

            medida.setIdMedida(idMedida);

            if (!cm.updateMedida(medida)) {
                return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                        "No se pudo actualizar la medida");
            }

            String respuesta = "{\"result\":\"Medida actualizada\",\"medida\":"
                    + new Gson().toJson(medida) + "}";

            return Response.ok(respuesta).build();

        } catch (com.google.gson.JsonSyntaxException e) {

            return respuestaError(Response.Status.BAD_REQUEST,
                    "El JSON enviado no es valido");

        } catch (Exception e) {

            e.printStackTrace();

            return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al actualizar la medida");
        }
    }

    /**
     * DELETE /api/glucosa/deleteMedida/1
     */
    @Path("deleteMedida/{idMedida}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMedida(@PathParam("idMedida") int idMedida) {
        return eliminarMedida(idMedida);
    }

    /**
     * DELETE /api/glucosa/deleteMedida?idMedida=1
     */
    @Path("deleteMedida")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMedidaPorQuery(@QueryParam("idMedida") Integer idMedida) {

        if (idMedida == null || idMedida <= 0) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El parametro idMedida es obligatorio y debe ser mayor a 0");
        }

        return eliminarMedida(idMedida);
    }

    private Response eliminarMedida(int idMedida) {

        try {
            ControllerMedida cm = new ControllerMedida();

            if (cm.getMedidaById(idMedida) == null) {
                return respuestaError(Response.Status.NOT_FOUND,
                        "Medida no encontrada");
            }

            if (!cm.deleteMedida(idMedida)) {
                return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                        "No se pudo eliminar la medida");
            }

            String respuesta = "{\"result\":\"Medida eliminada\",\"idMedida\":"
                    + idMedida + "}";

            return Response.ok(respuesta).build();

        } catch (Exception e) {

            e.printStackTrace();

            return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al eliminar la medida");
        }
    }

    private static Integer parseEntero(String valor) {

        if (estaVacio(valor)) {
            return null;
        }

        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String normalizarFechaHora(String valor) {

        if (estaVacio(valor)) {
            return LocalDateTime.now().format(FORMATO_FECHA_HORA);
        }

        String fecha = valor.trim();

        for (DateTimeFormatter formato : FORMATOS_FECHA_HORA) {
            try {
                return LocalDateTime.parse(fecha, formato)
                        .format(FORMATO_FECHA_HORA);
            } catch (DateTimeParseException e) {
                // Se prueba el siguiente formato.
            }
        }

        return null;
    }

    /**
     * GET /api/glucosa/getAllMedidas
     * Devuelve todas las medidas registradas.
     */
    @Path("getAllMedidas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMedidas() {

        ControllerMedida cm = new ControllerMedida();
        Gson gson = new Gson();

        try {
            List<Medida> lista = cm.getAllMedidas();

            return Response.ok(gson.toJson(lista)).build();

        } catch (Exception e) {

            e.printStackTrace();

            String error = """
                {"result": "Error al obtener las medidas"}
                """;

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build();
        }
    }

    /**
     * GET /api/glucosa/getMedidaById?idMedida=1
     * Devuelve una medida por su id.
     */
    @Path("getMedidaById")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedidaById(@QueryParam("idMedida") Integer idMedida) {
        return responderMedida(idMedida);
    }

    /**
     * GET /api/glucosa/getMedidaById/1
     * Variante con el id en la ruta.
     */
    @Path("getMedidaById/{idMedida}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedidaByIdPorRuta(@PathParam("idMedida") int idMedida) {
        return responderMedida(idMedida);
    }

    private Response responderMedida(Integer idMedida) {

        if (idMedida == null || idMedida <= 0) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El parametro idMedida es obligatorio y debe ser mayor a 0");
        }

        try {
            ControllerMedida cm = new ControllerMedida();
            Medida medida = cm.getMedidaById(idMedida);

            if (medida == null) {
                return respuestaError(Response.Status.NOT_FOUND,
                        "Medida no encontrada");
            }

            return Response.ok(new Gson().toJson(medida)).build();

        } catch (Exception e) {

            e.printStackTrace();

            return respuestaError(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error al obtener la medida");
        }
    }

    /**
     * GET /api/glucosa/getMedidasUsuario?idUsuario=1
     * Devuelve las medidas de un usuario.
     */
    @Path("getMedidasUsuario")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedidasUsuario(@QueryParam("idUsuario") Integer idUsuario) {
        return responderMedidasUsuario(idUsuario);
    }

    /**
     * GET /api/glucosa/getMedidasUsuario/1
     * Variante con el id del usuario en la ruta.
     */
    @Path("getMedidasUsuario/{idUsuario}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedidasUsuarioPorRuta(@PathParam("idUsuario") int idUsuario) {
        return responderMedidasUsuario(idUsuario);
    }

    /**
     * GET /api/glucosa/getByUsuarioId?idUsuario=1
     * Alias de getMedidasUsuario para consultar las medidas por usuario.
     */
    @Path("getByUsuarioId")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByUsuarioId(@QueryParam("idUsuario") Integer idUsuario) {
        return responderMedidasUsuario(idUsuario);
    }

    /**
     * GET /api/glucosa/getByUsuarioId/1
     * Alias con el id del usuario en la ruta.
     */
    @Path("getByUsuarioId/{idUsuario}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByUsuarioIdPorRuta(@PathParam("idUsuario") int idUsuario) {
        return responderMedidasUsuario(idUsuario);
    }

    private Response responderMedidasUsuario(Integer idUsuario) {

        if (idUsuario == null || idUsuario <= 0) {

            String error = """
                {"result": "El parametro idUsuario es obligatorio y debe ser mayor a 0"}
                """;

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
        }

        ControllerMedida cm = new ControllerMedida();
        Gson gson = new Gson();

        try {
            List<Medida> lista = cm.getMedidasByUsuario(idUsuario);

            return Response.ok(gson.toJson(lista)).build();

        } catch (Exception e) {

            e.printStackTrace();

            String error = """
                {"result": "Error al obtener las medidas del usuario"}
                """;

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build();
        }
    }
}
