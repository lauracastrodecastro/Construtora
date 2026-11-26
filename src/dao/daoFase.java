/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Fase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author laura.castro
 */
public class daoFase {
    
    public boolean inserir(Fase fase) throws SQLException {
        String sql = "INSERT INTO fases (dsFase) VALUES (?)";
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fase.getDsFase()); if (ps.executeUpdate() == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) fase.setIdFase(rs.getInt(1)); } return true;
        }
    }
    
    public boolean atualizar(Fase fase) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE fases SET dsFase=? WHERE idFase=?")) {
            ps.setString(1, fase.getDsFase()); ps.setInt(2, fase.getIdFase()); return ps.executeUpdate() > 0;
        }
    }
    
    public Fase buscarPorId(int id) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM fases WHERE idFase=?")) {
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    
    public List<Fase> listar() throws SQLException {
        List<Fase> itens = new ArrayList<>();
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM fases ORDER BY idFase"); ResultSet rs = ps.executeQuery()) { while (rs.next()) itens.add(map(rs)); }
        return itens;
    }
    
    public boolean excluir(int id) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM fases WHERE idFase=?")) { ps.setInt(1, id); return ps.executeUpdate() > 0; }
    }
    
    private static Fase map(ResultSet rs) throws SQLException { return new Fase(rs.getInt("idFase"), rs.getString("dsFase")); }
}
