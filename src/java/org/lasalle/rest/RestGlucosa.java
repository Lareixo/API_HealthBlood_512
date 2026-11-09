/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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

        if (user == null) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "Los campos nombre, primerApellido y email son obligatorios");
        }

        String nombre = limpiar(user.getNombre());
        String primerApellido = limpiar(user.getPrimerApellido());
        String segundoApellido = limpiar(user.getSegundoApellido());
        String email = limpiar(user.getEmail());

        if (estaVacio(nombre) || estaVacio(primerApellido) || estaVacio(email)) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "Los campos nombre, primerApellido y email son obligatorios");
        }

        // La tabla usuario define VARCHAR(40) para nombres/apellidos y VARCHAR(50) para email.
        if (nombre.length() > 40
                || primerApellido.length() > 40
                || (segundoApellido != null && segundoApellido.length() > 40)
                || email.length() > 50) {

            return respuestaError(Response.Status.BAD_REQUEST,
                    "Longitud invalida: nombre y apellidos maximo 40 caracteres, email maximo 50");
        }

        user.setNombre(nombre);
        user.setPrimerApellido(primerApellido);
        user.setSegundoApellido(segundoApellido);
        user.setEmail(email);

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

        if (medida == null) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "Los campos idUsuario y glucosa son obligatorios");
        }

        Integer idUsuario = medida.getIdUsuario();
        Integer glucosa = medida.getGlucosa();

        if (idUsuario == null || idUsuario <= 0) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El campo idUsuario es obligatorio y debe ser mayor a 0");
        }

        if (glucosa == null || glucosa <= 0) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El campo glucosa es obligatorio y debe ser mayor a 0");
        }

        String contexto = limpiar(medida.getContexto());
        String notas = limpiar(medida.getNotas());

        if (contexto != null && contexto.length() > 40) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El campo contexto permite maximo 40 caracteres");
        }

        if (notas != null && notas.length() > 100) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El campo notas permite maximo 100 caracteres");
        }

        String fecha = normalizarFechaHora(medida.getFechaHora());

        if (fecha == null) {
            return respuestaError(Response.Status.BAD_REQUEST,
                    "El campo fechaHora debe tener un formato valido, por ejemplo 2026-11-08 14:30:00");
        }

        try {
            ControllerrUsuario cu = new ControllerrUsuario();

            if (!cu.existeUsuario(idUsuario)) {
                return respuestaError(Response.Status.BAD_REQUEST,
                        "El usuario indicado no existe");
            }

            medida.setIdUsuario(idUsuario);
            medida.setGlucosa(glucosa);
            medida.setFechaHora(fecha);
            medida.setContexto(contexto);
            medida.setNotas(notas);

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
