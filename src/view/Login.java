package view;

import dao.daoUsuario;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.function.Consumer;

public class Login extends JPanel {
    public Login(Consumer<String> navegar) {
        setLayout(new GridBagLayout());
        JPanel card = Componentes.cartao();
        Componentes.titulos(card, "Construtora Balança-Mas-Não-Cai", "Acesso ao Sistema");
        card.add(Box.createVerticalStrut(8));

        JTextField email = new JTextField();
        JPasswordField senha = new JPasswordField();
        Componentes.linhaCampo(card, "E-mail", email);
        Componentes.linhaCampo(card, "Senha", senha);
        card.add(Box.createVerticalStrut(4));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        botoes.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton entrar = Componentes.acao("Entrar");
        JButton apagar = Componentes.secundaria("Apagar");

        entrar.addActionListener(e -> {
            String emailDigitado = email.getText().trim();
            String senhaDigitada = new String(senha.getPassword());

            if (emailDigitado.isEmpty() || senhaDigitada.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o e-mail e a senha.",
                        "Dados obrigatórios", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Usuario usuario = new daoUsuario().autenticar(emailDigitado, senhaDigitada);
                if (usuario == null) {
                    JOptionPane.showMessageDialog(this, "E-mail ou senha inválidos.",
                            "Acesso negado", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                navegar.accept("menu");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Não foi possível validar o login no banco: " + ex.getMessage(),
                        "Erro de conexão", JOptionPane.ERROR_MESSAGE);
            }
        });

        apagar.addActionListener(e -> {
            email.setText("");
            senha.setText("");
        });

        botoes.add(entrar);
        botoes.add(apagar);
        card.add(botoes);
        add(card);
    }
}

