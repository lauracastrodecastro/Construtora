/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Engenheiro;
/**
 *
 * @author laura.castro
 */
public class daoEngenheiro {
    
    public boolean inserir(Engenheiro engenheiro) throws SQLException {
        String sql = "INSERT INTO engenheiros (dsNome, nrCREA, dtAdmissao) VALUES (?, ?, ?)";
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, engenheiro.getDsNome());
            ps.setString(2, engenheiro.getNrCREA());
            ps.setDate(3, date(engenheiro.getDtAdmissao()));
            if (ps.executeUpdate() == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) engenheiro.setIdEngenheiro(keys.getInt(1)); }
            return true;
        }
    }

    public boolean atualizar(Engenheiro engenheiro) throws SQLException {
        String sql = "UPDATE engenheiros SET dsNome=?, nrCREA=?, dtAdmissao=? WHERE idEngenheiro=?";
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, engenheiro.getDsNome()); ps.setString(2, engenheiro.getNrCREA());
            ps.setDate(3, date(engenheiro.getDtAdmissao())); ps.setInt(4, engenheiro.getIdEngenheiro());
            return ps.executeUpdate() > 0;
        }
    }
    
    public Engenheiro buscarPorId(int id) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM engenheiros WHERE idEngenheiro=?")) {
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<Engenheiro> listar() throws SQLException {
        List<Engenheiro> itens = new ArrayList<>();
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM engenheiros ORDER BY idEngenheiro"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) itens.add(map(rs));
        }
        return itens;
    }

    public boolean excluir(int id) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM engenheiros WHERE idEngenheiro=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        }
    }

    private static Engenheiro map(ResultSet rs) throws SQLException {
        Date d = rs.getDate("dtAdmissao");
        return new Engenheiro(rs.getInt("idEngenheiro"), rs.getString("dsNome"), rs.getString("nrCREA"), d == null ? null : d.toLocalDate());
    }
    private static Date date(java.time.LocalDate d) { return d == null ? null : Date.valueOf(d); }
}
