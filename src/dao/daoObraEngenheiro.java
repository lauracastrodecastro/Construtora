/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Engenheiro;
import model.Funcao;
import model.Obra;
import model.ObraEngenheiro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author laura.castro
 */
public class daoObraEngenheiro {
    private static final String SELECT = "SELECT oe.*, o.descricao, e.dsNome, e.nrCREA, f.dsFuncao FROM obras_engenheiros oe " +
            "JOIN obras o ON o.idObra=oe.idObra JOIN engenheiros e ON e.idEngenheiro=oe.idEngenheiro JOIN funcoes f ON f.idFuncao=oe.idFuncao ";
    
    public boolean inserir(ObraEngenheiro item) throws SQLException {
        String sql = "INSERT INTO obras_engenheiros (idObra, idEngenheiro, dtAdmissaoEngenheiro, dtInicioTrabalho, dtFimTrabalho, idFuncao) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { bind(ps, item); return ps.executeUpdate() > 0; }
    }
    
    public boolean atualizar(ObraEngenheiro item) throws SQLException {
        String sql = "UPDATE obras_engenheiros SET dtAdmissaoEngenheiro=?, dtInicioTrabalho=?, dtFimTrabalho=?, idFuncao=? WHERE idObra=? AND idEngenheiro=?";
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, date(item.getDtAdmissaoEngenheiro())); ps.setDate(2, date(item.getDtInicioTrabalho())); ps.setDate(3, date(item.getDtFimTrabalho()));
            ps.setInt(4, item.getFuncao().getIdFuncao()); ps.setInt(5, item.getObra().getIdObra()); ps.setInt(6, item.getEngenheiro().getIdEngenheiro()); return ps.executeUpdate() > 0;
        }
    }
    
    public ObraEngenheiro buscarPorIds(int idObra, int idEngenheiro) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(SELECT + "WHERE oe.idObra=? AND oe.idEngenheiro=?")) {
            ps.setInt(1, idObra); ps.setInt(2, idEngenheiro); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    
    public List<ObraEngenheiro> listar() throws SQLException {
        List<ObraEngenheiro> itens = new ArrayList<>();
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY oe.idObra, oe.idEngenheiro"); ResultSet rs = ps.executeQuery()) { while (rs.next()) itens.add(map(rs)); } return itens;
    }
    
    public boolean excluir(int idObra, int idEngenheiro) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM obras_engenheiros WHERE idObra=? AND idEngenheiro=?")) {
            ps.setInt(1, idObra); ps.setInt(2, idEngenheiro); return ps.executeUpdate() > 0;
        }
    }
    
    private static void bind(PreparedStatement ps, ObraEngenheiro item) throws SQLException {
        ps.setInt(1, item.getObra().getIdObra()); ps.setInt(2, item.getEngenheiro().getIdEngenheiro());
        ps.setDate(3, date(item.getDtAdmissaoEngenheiro())); ps.setDate(4, date(item.getDtInicioTrabalho())); ps.setDate(5, date(item.getDtFimTrabalho())); ps.setInt(6, item.getFuncao().getIdFuncao());
    }
    
    private static ObraEngenheiro map(ResultSet rs) throws SQLException {
        Date admissao = rs.getDate("dtAdmissaoEngenheiro"), inicio = rs.getDate("dtInicioTrabalho"), fim = rs.getDate("dtFimTrabalho");
        Obra obra = new Obra(); obra.setIdObra(rs.getInt("idObra")); obra.setDescricao(rs.getString("descricao"));
        Engenheiro engenheiro = new Engenheiro(); engenheiro.setIdEngenheiro(rs.getInt("idEngenheiro")); engenheiro.setDsNome(rs.getString("dsNome")); engenheiro.setNrCREA(rs.getString("nrCREA"));
        Funcao funcao = new Funcao(rs.getInt("idFuncao"), rs.getString("dsFuncao"));
        return new ObraEngenheiro(obra, engenheiro, local(admissao), local(inicio), local(fim), funcao);
    }
    
    private static Date date(java.time.LocalDate value) { return value == null ? null : Date.valueOf(value); }
    private static java.time.LocalDate local(Date value) { return value == null ? null : value.toLocalDate(); }
}
