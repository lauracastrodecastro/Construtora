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
import java.util.function.Consumer;

public class Exclusao extends JPanel {
    private final String entidade;
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Código", "Registro"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public Exclusao(String entidade, Consumer<String> navegar) {
        this.entidade = entidade;
        setLayout(new GridBagLayout());
        JPanel card = Componentes.cartao();
        Componentes.titulos(card, "Excluir " + entidade, "Selecione um registro para remover.");
        JTable tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new Dimension(430, 160));
        card.add(scroll);
        card.add(Box.createVerticalStrut(12));
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        JButton excluir = Componentes.acao("Excluir selecionado");
        excluir.addActionListener(e -> excluir(tabela));
        botoes.add(excluir);
        JButton atualizar = Componentes.secundaria("Atualizar");
        atualizar.addActionListener(e -> carregar());
        botoes.add(atualizar);
        JButton voltar = Componentes.secundaria("Voltar");
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
                case "Engenheiros" -> { for (Engenheiro x : new daoEngenheiro().listar()) modelo.addRow(new Object[]{x.getIdEngenheiro(), x.getDsNome()}); }
                case "Usuários" -> { for (Usuario x : new daoUsuario().listar()) modelo.addRow(new Object[]{x.getIdUsuario(), x.getNome()}); }
                case "Funções" -> { for (Funcao x : new daoFuncao().listar()) modelo.addRow(new Object[]{x.getIdFuncao(), x.getDsFuncao()}); }
                default -> { for (Fase x : new daoFase().listar()) modelo.addRow(new Object[]{x.getIdFase(), x.getDsFase()}); }
            }
        } catch (SQLException ex) { erro(ex); }
    }

    private void excluir(JTable tabela) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um registro.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = ((Number) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0)).intValue();
        int confirma = JOptionPane.showConfirmDialog(this, "Deseja excluir o registro selecionado?", "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (confirma != JOptionPane.YES_OPTION) return;
        try {
            boolean removido = switch (entidade) {
                case "Engenheiros" -> new daoEngenheiro().excluir(id);
                case "Usuários" -> new daoUsuario().excluir(id);
                case "Funções" -> new daoFuncao().excluir(id);
                default -> new daoFase().excluir(id);
            };
            if (removido) carregar();
            else JOptionPane.showMessageDialog(this, "O registro não foi encontrado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) { erro(ex); }
    }

    private void erro(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Não foi possível acessar o banco de dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
