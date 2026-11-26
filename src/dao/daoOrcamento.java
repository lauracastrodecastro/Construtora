/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Engenheiro;
import model.Orcamento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author laura.castro
 */
public class daoOrcamento {
    
    private static final String SELECT = "SELECT o.*, e.dsNome, e.nrCREA, e.dtAdmissao FROM orcamentos o LEFT JOIN engenheiros e ON e.idEngenheiro=o.idEngenheiroResponsavel ";
    
    public boolean inserir(Orcamento orcamento) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO orcamentos (dtAprovacao, idEngenheiroResponsavel) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, date(orcamento.getDtAprovacao())); int id = orcamento.getEngenheiroResponsavel() == null ? 0 : orcamento.getEngenheiroResponsavel().getIdEngenheiro();
            if (id == 0) ps.setNull(2, Types.INTEGER); else ps.setInt(2, id);
            if (ps.executeUpdate() == 0) return false; try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) orcamento.setIdOrcamento(rs.getInt(1)); } return true;
        }
    }
    
    public boolean atualizar(Orcamento orcamento) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE orcamentos SET dtAprovacao=?, idEngenheiroResponsavel=? WHERE idOrcamento=?")) {
            ps.setDate(1, date(orcamento.getDtAprovacao())); int id = orcamento.getEngenheiroResponsavel() == null ? 0 : orcamento.getEngenheiroResponsavel().getIdEngenheiro();
            if (id == 0) ps.setNull(2, Types.INTEGER); else ps.setInt(2, id); ps.setInt(3, orcamento.getIdOrcamento()); return ps.executeUpdate() > 0;
        }
    }
    
    public Orcamento buscarPorId(int id) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(SELECT + "WHERE o.idOrcamento=?")) { ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; } }
    }
    
    public List<Orcamento> listar() throws SQLException {
        List<Orcamento> itens = new ArrayList<>();
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY o.idOrcamento"); ResultSet rs = ps.executeQuery()) { while (rs.next()) itens.add(map(rs)); } return itens;
    }
    
    public boolean excluir(int id) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM orcamentos WHERE idOrcamento=?")) { ps.setInt(1, id); return ps.executeUpdate() > 0; }
    }
    
    private static Orcamento map(ResultSet rs) throws SQLException {
        Date aprovacao = rs.getDate("dtAprovacao"), admissao = rs.getDate("dtAdmissao"); int idEngenheiro = rs.getInt("idEngenheiroResponsavel");
        Engenheiro engenheiro = rs.wasNull() ? null : new Engenheiro(idEngenheiro, rs.getString("dsNome"), rs.getString("nrCREA"), admissao == null ? null : admissao.toLocalDate());
        return new Orcamento(rs.getInt("idOrcamento"), aprovacao == null ? null : aprovacao.toLocalDate(), engenheiro);
    }
    
    private static Date date(java.time.LocalDate value) { return value == null ? null : Date.valueOf(value); }
}
