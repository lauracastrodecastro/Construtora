/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Fase;
import model.Obra;
import model.ObraFase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author laura.castro
 */
public class daoObraFase {
    
    private static final String SELECT = "SELECT ofa.*, o.descricao, f.dsFase FROM obras_fases ofa JOIN obras o ON o.idObra=ofa.idObra JOIN fases f ON f.idFase=ofa.idFase ";
    
    public boolean inserir(ObraFase item) throws SQLException {
        String sql = "INSERT INTO obras_fases (idObra, idFase, dtInicioFase, dtTerminoFase) VALUES (?, ?, ?, ?)";
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) { bind(ps, item); return ps.executeUpdate() > 0; }
    }
    
    public boolean atualizar(ObraFase item) throws SQLException {
        String sql = "UPDATE obras_fases SET dtInicioFase=?, dtTerminoFase=? WHERE idObra=? AND idFase=?";
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, date(item.getDtInicioFase())); ps.setDate(2, date(item.getDtTerminoFase())); ps.setInt(3, item.getObra().getIdObra()); ps.setInt(4, item.getFase().getIdFase()); return ps.executeUpdate() > 0;
        }
    }
    
    public ObraFase buscarPorIds(int idObra, int idFase) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(SELECT + "WHERE ofa.idObra=? AND ofa.idFase=?")) {
            ps.setInt(1, idObra); ps.setInt(2, idFase); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    
    public List<ObraFase> listar() throws SQLException {
        List<ObraFase> itens = new ArrayList<>();
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY ofa.idObra, ofa.idFase"); ResultSet rs = ps.executeQuery()) { while (rs.next()) itens.add(map(rs)); } return itens;
    }
    
    public boolean excluir(int idObra, int idFase) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM obras_fases WHERE idObra=? AND idFase=?")) { ps.setInt(1, idObra); ps.setInt(2, idFase); return ps.executeUpdate() > 0; }
    }
    
    private static void bind(PreparedStatement ps, ObraFase item) throws SQLException {
        ps.setInt(1, item.getObra().getIdObra()); ps.setInt(2, item.getFase().getIdFase()); ps.setDate(3, date(item.getDtInicioFase())); ps.setDate(4, date(item.getDtTerminoFase()));
    }
    
    private static ObraFase map(ResultSet rs) throws SQLException {
        Date inicio = rs.getDate("dtInicioFase"), termino = rs.getDate("dtTerminoFase");
        Obra obra = new Obra(); obra.setIdObra(rs.getInt("idObra")); obra.setDescricao(rs.getString("descricao"));
        Fase fase = new Fase(rs.getInt("idFase"), rs.getString("dsFase")); return new ObraFase(obra, fase, local(inicio), local(termino));
    }
    
    private static Date date(java.time.LocalDate value) { return value == null ? null : Date.valueOf(value); }
    
    private static java.time.LocalDate local(Date value) { return value == null ? null : value.toLocalDate(); }
}
