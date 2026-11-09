/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.controller;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author osmar
 */
public class ConnectionMysql {
    private Connection conn;

    /**
     * Lee una variable de entorno y usa un valor local por defecto cuando no
     * esta definida. Asi el proyecto sigue funcionando igual en la maquina
     * del desarrollador y tambien en Render/TiDB.
     */
    private static String env(String nombre, String valorPorDefecto) {
        String valor = System.getenv(nombre);
        return (valor == null || valor.trim().isEmpty())
                ? valorPorDefecto
                : valor.trim();
    }

    public Connection open() {
        String user = env("DB_USER", "root");
        String password = env("DB_PASSWORD", "admin");
        String dbName = env("DB_NAME", "gluco_api");
        String host = env("DB_HOST", "127.0.0.1");
        String port = env("DB_PORT", "3306");

        String url = env("DB_URL", "");
        if (url.isEmpty()) {
            url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;

            String parametros = env("DB_PARAMS",
                    "?useSSL=false&useUnicode=true&characterEncoding=utf-8"
                    + "&serverTimezone=America/Mexico_City&allowPublicKeyRetrieval=true");

            if (!parametros.startsWith("?")) {
                parametros = "?" + parametros;
            }

            url += parametros;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (Exception e) {
            System.err.println("No se pudo conectar a la base de datos: "
                    + e.getMessage());
            return null;
        }
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn = null;
            }
        }
    }
        
}
