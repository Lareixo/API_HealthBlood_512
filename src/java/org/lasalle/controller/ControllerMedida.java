
package org.lasalle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.lasalle.model.Medida;

public class ControllerMedida {

    private static final String SELECT_BASE =
            "SELECT idMedida, idUsuario, glucosa, fechaHora, contexto, notas FROM medida";

    /**
     * Obtiene todas las medidas registradas.
     */
    public List<Medida> getAllMedidas() throws SQLException {

        List<Medida> lista = new ArrayList<>();

        String sql = SELECT_BASE + " ORDER BY fechaHora, idMedida";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                lista.add(fillMedida(rs));
            }

        } finally {
            connMysql.close();
        }

        return lista;
    }

    /**
     * Obtiene las medidas de un usuario en especifico.
     */
    public List<Medida> getMedidasByUsuario(int idUsuario) throws SQLException {

        List<Medida> lista = new ArrayList<>();

        String sql = SELECT_BASE + " WHERE idUsuario = ? ORDER BY fechaHora, idMedida";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, idUsuario);

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    lista.add(fillMedida(rs));
                }
            }

        } finally {
            connMysql.close();
        }

        return lista;
    }

    /**
     * Guarda una nueva medida y devuelve el id generado por MySQL.
     */
    public int addMedida(Medida medida) throws SQLException {

        String sql = "INSERT INTO medida (idUsuario, glucosa, fechaHora, contexto, notas) "
                + "VALUES (?, ?, ?, ?, ?)";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstm.setInt(1, medida.getIdUsuario());
            pstm.setInt(2, medida.getGlucosa());

            if (medida.getFechaHora() == null || medida.getFechaHora().trim().isEmpty()) {
                pstm.setNull(3, Types.TIMESTAMP);
            } else {
                pstm.setString(3, medida.getFechaHora());
            }

            if (medida.getContexto() == null || medida.getContexto().trim().isEmpty()) {
                pstm.setNull(4, Types.VARCHAR);
            } else {
                pstm.setString(4, medida.getContexto());
            }

            if (medida.getNotas() == null || medida.getNotas().trim().isEmpty()) {
                pstm.setNull(5, Types.VARCHAR);
            } else {
                pstm.setString(5, medida.getNotas());
            }

            int filas = pstm.executeUpdate();

            if (filas == 0) {
                throw new SQLException("No se inserto ningun registro en la tabla medida");
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
     * Obtiene una medida por su id.
     */
    public Medida getMedidaById(int idMedida) throws SQLException {

        String sql = SELECT_BASE + " WHERE idMedida = ?";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, idMedida);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return fillMedida(rs);
                }
                return null;
            }

        } finally {
            connMysql.close();
        }
    }

    /**
     * Actualiza los datos de una medida existente.
     */
    public boolean updateMedida(Medida medida) throws SQLException {

        String sql = "UPDATE medida SET idUsuario = ?, glucosa = ?, fechaHora = ?, "
                + "contexto = ?, notas = ? WHERE idMedida = ?";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, medida.getIdUsuario());
            pstm.setInt(2, medida.getGlucosa());

            if (medida.getFechaHora() == null || medida.getFechaHora().trim().isEmpty()) {
                pstm.setNull(3, Types.TIMESTAMP);
            } else {
                pstm.setString(3, medida.getFechaHora());
            }

            if (medida.getContexto() == null || medida.getContexto().trim().isEmpty()) {
                pstm.setNull(4, Types.VARCHAR);
            } else {
                pstm.setString(4, medida.getContexto());
            }

            if (medida.getNotas() == null || medida.getNotas().trim().isEmpty()) {
                pstm.setNull(5, Types.VARCHAR);
            } else {
                pstm.setString(5, medida.getNotas());
            }

            pstm.setInt(6, medida.getIdMedida());

            return pstm.executeUpdate() > 0;

        } finally {
            connMysql.close();
        }
    }

    /**
     * Elimina una medida por su id.
     */
    public boolean deleteMedida(int idMedida) throws SQLException {

        String sql = "DELETE FROM medida WHERE idMedida = ?";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, idMedida);

            return pstm.executeUpdate() > 0;

        } finally {
            connMysql.close();
        }
    }

    /**
     * Cuenta las medidas registradas para un usuario.
     */
    public int contarMedidasByUsuario(int idUsuario) throws SQLException {

        String sql = "SELECT COUNT(*) FROM medida WHERE idUsuario = ?";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, idUsuario);

            try (ResultSet rs = pstm.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }

        } finally {
            connMysql.close();
        }
    }

    /**
     * Elimina todas las medidas de un usuario (para el borrado en cascada).
     */
    public boolean deleteMedidasByUsuario(int idUsuario) throws SQLException {

        String sql = "DELETE FROM medida WHERE idUsuario = ?";

        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        if (conn == null) {
            throw new SQLException("No se pudo conectar con MySQL");
        }

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, idUsuario);

            pstm.executeUpdate();
            return true;

        } finally {
            connMysql.close();
        }
    }

    public Medida fillMedida(ResultSet rs) throws SQLException {

        return new Medida(
                rs.getInt("idMedida"),
                rs.getInt("idUsuario"),
                rs.getInt("glucosa"),
                rs.getString("fechaHora"),
                rs.getString("contexto"),
                rs.getString("notas")
        );
    }
}
