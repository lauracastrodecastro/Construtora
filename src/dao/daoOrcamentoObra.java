/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Obra;
import model.Orcamento;
import model.OrcamentoObra;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author laura.castro
 */
public class daoOrcamentoObra {
    
    private static final String SELECT = "SELECT oo.*, o.descricao FROM orcamentos_obras oo JOIN obras o ON o.idObra=oo.idObra ";
    
    public boolean inserir(OrcamentoObra item) throws SQLException {
        String sql = "INSERT INTO orcamentos_obras (idOrcamento, idObra, dtInicio, dtFim, vlPrevisto, vlRealizado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { bind(ps, item); return ps.executeUpdate() > 0; }
    }
    
    public boolean atualizar(OrcamentoObra item) throws SQLException {
        String sql = "UPDATE orcamentos_obras SET dtInicio=?, dtFim=?, vlPrevisto=?, vlRealizado=? WHERE idOrcamento=? AND idObra=?";
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, date(item.getDtInicio())); ps.setDate(2, date(item.getDtFim())); ps.setBigDecimal(3, item.getVlPrevisto()); ps.setBigDecimal(4, item.getVlRealizado());
            ps.setInt(5, item.getOrcamento().getIdOrcamento()); ps.setInt(6, item.getObra().getIdObra()); return ps.executeUpdate() > 0;
        }
    }
    
    public OrcamentoObra buscarPorIds(int idOrcamento, int idObra) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(SELECT + "WHERE oo.idOrcamento=? AND oo.idObra=?")) {
            ps.setInt(1, idOrcamento); ps.setInt(2, idObra); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    
    public List<OrcamentoObra> listar() throws SQLException {
        List<OrcamentoObra> itens = new ArrayList<>();
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY oo.idOrcamento, oo.idObra"); ResultSet rs = ps.executeQuery()) { while (rs.next()) itens.add(map(rs)); } return itens;
    }
    
    public boolean excluir(int idOrcamento, int idObra) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM orcamentos_obras WHERE idOrcamento=? AND idObra=?")) { ps.setInt(1, idOrcamento); ps.setInt(2, idObra); return ps.executeUpdate() > 0; }
    }
    
    private static void bind(PreparedStatement ps, OrcamentoObra item) throws SQLException {
        ps.setInt(1, item.getOrcamento().getIdOrcamento()); ps.setInt(2, item.getObra().getIdObra()); ps.setDate(3, date(item.getDtInicio())); ps.setDate(4, date(item.getDtFim()));
        ps.setBigDecimal(5, item.getVlPrevisto()); ps.setBigDecimal(6, item.getVlRealizado());
    }
    
    private static OrcamentoObra map(ResultSet rs) throws SQLException {
        Date inicio = rs.getDate("dtInicio"), fim = rs.getDate("dtFim");
        Orcamento orcamento = new Orcamento(); orcamento.setIdOrcamento(rs.getInt("idOrcamento"));
        Obra obra = new Obra(); obra.setIdObra(rs.getInt("idObra")); obra.setDescricao(rs.getString("descricao"));
        return new OrcamentoObra(orcamento, obra, local(inicio), local(fim), rs.getBigDecimal("vlPrevisto"), rs.getBigDecimal("vlRealizado"));
    }
    
    private static Date date(java.time.LocalDate value) { return value == null ? null : Date.valueOf(value); }
    
    private static java.time.LocalDate local(Date value) { return value == null ? null : value.toLocalDate(); }
}
