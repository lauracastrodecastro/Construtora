/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Funcao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author laura.castro
 */
public class daoFuncao {
    
    public boolean inserir(Funcao funcao) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO funcoes (dsFuncao) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, funcao.getDsFuncao()); if (ps.executeUpdate() == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) funcao.setIdFuncao(rs.getInt(1)); } return true;
        }
    }
    
    public boolean atualizar(Funcao funcao) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE funcoes SET dsFuncao=? WHERE idFuncao=?")) {
            ps.setString(1, funcao.getDsFuncao()); ps.setInt(2, funcao.getIdFuncao()); return ps.executeUpdate() > 0;
        }
    }
    
    public Funcao buscarPorId(int id) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM funcoes WHERE idFuncao=?")) {
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    
    public List<Funcao> listar() throws SQLException {
        List<Funcao> itens = new ArrayList<>();
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM funcoes ORDER BY idFuncao"); ResultSet rs = ps.executeQuery()) { while (rs.next()) itens.add(map(rs)); }
        return itens;
    }
    
    public boolean excluir(int id) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM funcoes WHERE idFuncao=?")) { ps.setInt(1, id); return ps.executeUpdate() > 0; }
    }
    
    private static Funcao map(ResultSet rs) throws SQLException { return new Funcao(rs.getInt("idFuncao"), rs.getString("dsFuncao")); }
}
