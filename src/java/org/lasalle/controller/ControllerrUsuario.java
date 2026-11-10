
package org.lasalle.controller;

import java.util.ArrayList;
import java.util.List;
import org.lasalle.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

public class ControllerrUsuario {

    public List<Usuario> getUsuarios() throws SQLException {

        List<Usuario> lista = new ArrayList<>();

        String sql = "SELECT * FROM usuario";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                lista.add(fillUsuario(rs));
            }

        } finally {
            connMysql.close();
        }

        return lista;
    }

    /**
     * Guarda un nuevo usuario y devuelve el id generado por MySQL.
     */
    public int addUsuario(Usuario user) throws SQLException {

        String sql = "INSERT INTO usuario (nombre, primerApellido, segundoApellido, email) "
                + "VALUES (?, ?, ?, ?)";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstm.setString(1, user.getNombre());
            pstm.setString(2, user.getPrimerApellido());

            if (user.getSegundoApellido() == null || user.getSegundoApellido().trim().isEmpty()) {
                pstm.setNull(3, Types.VARCHAR);
            } else {
                pstm.setString(3, user.getSegundoApellido());
            }

            pstm.setString(4, user.getEmail());

            int filas = pstm.executeUpdate();

            if (filas == 0) {
                throw new SQLException("No se inserto ningun registro en la tabla usuario");
            }

            try (ResultSet rs = pstm.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return -1;

        } finally {
            connMysql.close();
        }
    }

    /**
     * Obtiene un usuario por su id.
     */
    public Usuario getUsuarioById(int idUsuario) throws SQLException {

        String sql = "SELECT * FROM usuario WHERE idUsuario = ?";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, idUsuario);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return fillUsuario(rs);
                }
                return null;
            }

        } finally {
            connMysql.close();
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     */
    public boolean updateUsuario(Usuario user) throws SQLException {

        String sql = "UPDATE usuario SET nombre = ?, primerApellido = ?, "
                + "segundoApellido = ?, email = ? WHERE idUsuario = ?";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, user.getNombre());
            pstm.setString(2, user.getPrimerApellido());

            if (user.getSegundoApellido() == null || user.getSegundoApellido().trim().isEmpty()) {
                pstm.setNull(3, Types.VARCHAR);
            } else {
                pstm.setString(3, user.getSegundoApellido());
            }

            pstm.setString(4, user.getEmail());
            pstm.setInt(5, user.getIdUsuario());

            return pstm.executeUpdate() > 0;

        } finally {
            connMysql.close();
        }
    }

    /**
     * Elimina un usuario por su id.
     */
    public boolean deleteUsuario(int idUsuario) throws SQLException {

        String sql = "DELETE FROM usuario WHERE idUsuario = ?";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, idUsuario);

            return pstm.executeUpdate() > 0;

        } finally {
            connMysql.close();
        }
    }

    /**
     * Indica si existe un usuario con el id indicado.
     */
    public boolean existeUsuario(int idUsuario) throws SQLException {

        String sql = "SELECT 1 FROM usuario WHERE idUsuario = ? LIMIT 1";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, idUsuario);

            try (ResultSet rs = pstm.executeQuery()) {
                return rs.next();
            }

        } finally {
            connMysql.close();
        }
    }

    public Usuario fillUsuario(ResultSet rs) throws SQLException {

        Usuario user = new Usuario();

        user.setIdUsuario(rs.getInt("idUsuario"));
        user.setNombre(rs.getString("nombre"));
        user.setPrimerApellido(rs.getString("primerApellido"));
        user.setSegundoApellido(rs.getString("segundoApellido"));
        user.setEmail(rs.getString("email"));

        return user;
    }
}
