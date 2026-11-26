package view;

import dao.daoEngenheiro;
import dao.daoFase;
import dao.daoFuncao;
import dao.daoUsuario;
import model.Engenheiro;
import model.Fase;
import model.Funcao;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class Consulta extends JPanel {

    private final String entidade;
    private final JTable tabela;
    private final DefaultTableModel modelo;

    public Consulta(String entidade, Consumer<String> navegar) {
        this.entidade = entidade;
        setLayout(new GridBagLayout());
        JPanel card = Componentes.cartao();
        Componentes.titulos(card, "Consulta de " + entidade, "");
        String[] colunas = switch (entidade) {
            case "Engenheiros" -> new String[]{"Código", "Nome", "CREA", "Data de admissão"};
            case "Usuários" -> new String[]{"Código", "Nome", "E-mail", "Status"};
            case "Funções" -> new String[]{"Código", "Descrição da Função"};
            default -> new String[]{"Código", "Descrição da Fase"};
        };
        
    modelo = new DefaultTableModel(colunas, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tabela = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new Dimension(460, 180));
        card.add(scroll);
        card.add(Box.createVerticalStrut(12));
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        JButton atualizar = Componentes.acao("Atualizar");
        atualizar.addActionListener(e -> carregar());
        botoes.add(atualizar);
        JButton voltar = Componentes.link("Voltar");
        voltar.addActionListener(e -> navegar.accept(entidade));
        botoes.add(voltar);
        card.add(botoes);
        add(card);
        carregar();
    }
    
    private void carregar() {
        modelo.setRowCount(0);
        try {
            switch (entidade) {
                case "Engenheiros" -> { for (Engenheiro x : new daoEngenheiro().listar()) modelo.addRow(new Object[]{x.getIdEngenheiro(), x.getDsNome(), x.getNrCREA(), x.getDtAdmissao()}); }
                case "Usuários" -> { for (Usuario x : new daoUsuario().listar()) modelo.addRow(new Object[]{x.getIdUsuario(), x.getNome(), x.getEmail(), x.getFlStatus()}); }
                case "Funções" -> { for (Funcao x : new daoFuncao().listar()) modelo.addRow(new Object[]{x.getIdFuncao(), x.getDsFuncao()}); }
                default -> { for (Fase x : new daoFase().listar()) modelo.addRow(new Object[]{x.getIdFase(), x.getDsFase()}); }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Não foi possível consultar o banco de dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
