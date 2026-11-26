/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Engenheiro;
import model.Obra;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author laura.castro
 */
public class daoObra {
    private static final String SELECT = "SELECT o.*, e.dsNome, e.nrCREA AS CREAEngenheiro, e.dtAdmissao AS admissaoEngenheiro " +
            "FROM obras o LEFT JOIN engenheiros e ON e.idEngenheiro=o.idEngenheiro ";

    public boolean inserir(Obra obra) throws SQLException {
        String sql = "INSERT INTO obras (Descricao, dtInicio, dtFim, idEngenheiro, nrCrea, dtDdmissaoEngenheiro) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, obra.getDescricao()); ps.setDate(2, date(obra.getDtInicio())); ps.setDate(3, date(obra.getDtFim()));
            setId(ps, 4, obra.getEngenheiroResponsavel() == null ? 0 : obra.getEngenheiroResponsavel().getIdEngenheiro());
            ps.setString(5, obra.getNrCrea()); ps.setDate(6, date(obra.getDtAdmissaoEngenheiro()));
            if (ps.executeUpdate() == 0) return false;
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) obra.setIdObra(rs.getInt(1)); }
            return true;
        }
    }

    public boolean atualizar(Obra obra) throws SQLException {
        String sql = "UPDATE obras SET Descricao=?, dtInicio=?, dtFim=?, idEngenheiro=?, nrCrea=?, dtDdmissaoEngenheiro=? WHERE idObra=?";
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, obra.getDescricao()); ps.setDate(2, date(obra.getDtInicio())); ps.setDate(3, date(obra.getDtFim()));
            setId(ps, 4, obra.getEngenheiroResponsavel() == null ? 0 : obra.getEngenheiroResponsavel().getIdEngenheiro());
            ps.setString(5, obra.getNrCrea()); ps.setDate(6, date(obra.getDtAdmissaoEngenheiro())); ps.setInt(7, obra.getIdObra());
            return ps.executeUpdate() > 0;
        }
    }
    
    public Obra buscarPorId(int id) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(SELECT + "WHERE o.idObra=?")) {
            ps.setInt(1, id); try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<Obra> listar() throws SQLException {
        List<Obra> itens = new ArrayList<>();
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY o.idObra"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) itens.add(map(rs));
        }
        return itens;
    }

    public boolean excluir(int id) throws SQLException {
        try (Connection c = DataSource.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM obras WHERE idObra=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        }
    }
    
    private static Obra map(ResultSet rs) throws SQLException {
        Date inicio = rs.getDate("dtInicio"), fim = rs.getDate("dtFim"), admissaoObra = rs.getDate("dtDdmissaoEngenheiro");
        Date admissaoEngenheiro = rs.getDate("admissaoEngenheiro");
        int idEngenheiro = rs.getInt("idEngenheiro");
        Engenheiro engenheiro = rs.wasNull() ? null : new Engenheiro(idEngenheiro, rs.getString("dsNome"),
                rs.getString("CREAEngenheiro"), admissaoEngenheiro == null ? null : admissaoEngenheiro.toLocalDate());
        return new Obra(rs.getInt("idObra"), rs.getString("Descricao"), local(inicio), local(fim), engenheiro,
                rs.getString("nrCrea"), local(admissaoObra));
    }

    private static void setId(PreparedStatement ps, int index, int id) throws SQLException {
        if (id == 0) ps.setNull(index, Types.INTEGER); else ps.setInt(index, id);
    }
    private static Date date(java.time.LocalDate value) { return value == null ? null : Date.valueOf(value); }
    private static java.time.LocalDate local(Date value) { return value == null ? null : value.toLocalDate(); }
}
