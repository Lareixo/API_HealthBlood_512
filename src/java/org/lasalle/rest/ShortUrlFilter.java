package org.lasalle.rest;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Permite invocar los endpoints con rutas cortas desde el navegador.
 *
 * Ejemplos:
 *   /API_HealthBlood_512/getAllUsuarios
 *   /API_HealthBlood_512/getMedidasUsuario?idUsuario=1
 *   /API_HealthBlood_512/getMedidasUsuario/1
 *
 * Internamente reenvia la peticion a /api/glucosa/..., por lo que las rutas
 * originales siguen funcionando igual.
 */
public class ShortUrlFilter implements Filter {

    private static final Map<String, String> ENDPOINTS = new LinkedHashMap<>();

    static {
        ENDPOINTS.put("saludar", "saludar");
        ENDPOINTS.put("estado", "estado");
        ENDPOINTS.put("saludarpersonalizado", "saludarPersonalizado");
        ENDPOINTS.put("numero", "Numero");
        ENDPOINTS.put("getallusuarios", "getAllUsuarios");
        ENDPOINTS.put("getusuariobyid", "getUsuarioById");
        ENDPOINTS.put("addusuario", "addUsuario");
        ENDPOINTS.put("updateusuario", "updateUsuario");
        ENDPOINTS.put("deleteusuario", "deleteUsuario");
        ENDPOINTS.put("addmedida", "addMedida");
        ENDPOINTS.put("insertarmedida", "addMedida");
        ENDPOINTS.put("getallmedidas", "getAllMedidas");
        ENDPOINTS.put("getmedidabyid", "getMedidaById");
        ENDPOINTS.put("updatemedida", "updateMedida");
        ENDPOINTS.put("deletemedida", "deleteMedida");
        ENDPOINTS.put("getmedidasusuario", "getMedidasUsuario");
        ENDPOINTS.put("getbyusuarioid", "getByUsuarioId");
        ENDPOINTS.put("getmedidasbyusuarioid", "getByUsuarioId");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Sin configuracion adicional.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {

            HttpServletRequest req = (HttpServletRequest) request;
            String context = req.getContextPath();
            String path = req.getRequestURI().substring(context.length());

            if (path.length() > 1) {

                String[] partes = path.substring(1).split("/");
                String clave = partes[0].toLowerCase(Locale.ROOT);
                String endpoint = ENDPOINTS.get(clave);

                if (endpoint != null) {

                    StringBuilder destino = new StringBuilder("/api/glucosa/")
                            .append(endpoint);

                    // Conserva segmentos adicionales, por ejemplo /getMedidasUsuario/1
                    for (int i = 1; i < partes.length; i++) {
                        destino.append('/').append(partes[i]);
                    }

                    // La query string original se conserva automaticamente en el forward.
                    req.getRequestDispatcher(destino.toString()).forward(request, response);
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Sin recursos que liberar.
    }
}
