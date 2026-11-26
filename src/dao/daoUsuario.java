package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Usuario;

public class daoUsuario {

    public boolean inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (Nome, Email, Senha) VALUES (?, ?, ?)";

        try (Connection conexao = DataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getEmail());
            comando.setString(3, usuario.getSenha());

            if (comando.executeUpdate() == 0) {
                return false;
            }

            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (chaves.next()) {
                    usuario.setIdUsuario(chaves.getInt(1));
                }
            }

            return true;
        }
    }

    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET Nome = ?, Email = ?, Senha = ? "
                   + "WHERE idUsuario = ?";

        try (Connection conexao = DataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getEmail());
            comando.setString(3, usuario.getSenha());
            comando.setInt(4, usuario.getIdUsuario());

            return comando.executeUpdate() > 0;
        }
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT idUsuario, Nome, Email, Senha "
                   + "FROM usuarios WHERE idUsuario = ?";

        try (Connection conexao = DataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, id);

            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? mapear(resultado) : null;
            }
        }
    }

    public List<Usuario> listar() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT idUsuario, Nome, Email, Senha "
                   + "FROM usuarios ORDER BY idUsuario";

        try (Connection conexao = DataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                usuarios.add(mapear(resultado));
            }
        }

        return usuarios;
    }

    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE idUsuario = ?";

        try (Connection conexao = DataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, id);
            return comando.executeUpdate() > 0;
        }
    }

    public Usuario autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT idUsuario, Nome, Email, Senha "
                   + "FROM usuarios WHERE Email = ? AND Senha = ? LIMIT 1";

        try (Connection conexao = DataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, email);
            comando.setString(2, senha);

            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? mapear(resultado) : null;
            }
        }
    }

    private Usuario mapear(ResultSet resultado) throws SQLException {
        return new Usuario(
                resultado.getInt("idUsuario"),
                resultado.getString("Nome"),
                resultado.getString("Email"),
                resultado.getString("Senha"),
                0
        );
    }
}