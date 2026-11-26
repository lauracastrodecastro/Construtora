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
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Formulario extends JPanel {
    
    private final String entidade;
    private final String operacao;
    private final List<JTextField> campos = new ArrayList<>();
    private final List<JLabel> rotulos = new ArrayList<>();
    private final JTextField id = new JTextField(24);
    private JPasswordField senha;

    public Formulario(String entidade, String operacao, Consumer<String> navegar) {
        this.entidade = entidade;
        this.operacao = operacao;
        setLayout(new GridBagLayout());
        JPanel card = Componentes.cartao();
        Componentes.titulos(card, operacao + " " + singular(entidade), operacao.equals("Altera") ? "Informe o código do registro e os novos dados." : "");
        if (operacao.equals("Altera")) Componentes.linhaCampo(card, "Código", id);

        if (entidade.equals("Engenheiros")) {
            adicionar(card, "Nome do Engenheiro"); adicionar(card, "CREA"); adicionar(card, "Data de Admissão (AAAA-MM-DD)");
        } else if (entidade.equals("Usuários")) {
            adicionar(card, "Nome"); adicionar(card, "E-mail");
            senha = new JPasswordField(24); Componentes.linhaCampo(card, "Senha", senha);
        } else {
            adicionar(card, entidade.equals("Fases") ? "Descrição da Fase" : "Descrição da Função");
        }
        
    JPanel acoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        JButton salvar = Componentes.acao(operacao);
        salvar.addActionListener(e -> salvar());
        acoes.add(salvar);
        JButton limpar = Componentes.secundaria("Limpar");
        limpar.addActionListener(e -> limparCampos());
        acoes.add(limpar);
        JButton voltar = Componentes.secundaria("Voltar");
        voltar.addActionListener(e -> navegar.accept(entidade));
        acoes.add(voltar);
        card.add(acoes);
        add(card);
    }

    private void adicionar(JPanel card, String rotulo) {
        JTextField campo = new JTextField(24);
        campos.add(campo); rotulos.add(new JLabel(rotulo));
        Componentes.linhaCampo(card, rotulo, campo);
    }
    
    private void salvar() {
        try {
            boolean salvo;
            switch (entidade) {
                case "Engenheiros" -> {
                    Engenheiro x = new Engenheiro(idOperacao(), texto(0), texto(1), dataOuNull(texto(2)));
                    daoEngenheiro dao = new daoEngenheiro(); salvo = operacao.equals("Inclui") ? dao.inserir(x) : dao.atualizar(x);
                }
                case "Usuários" -> {
                    Usuario x = new Usuario(idOperacao(), texto(0), texto(1), senha == null ? "" : new String(senha.getPassword()), 0);
                    daoUsuario dao = new daoUsuario(); salvo = operacao.equals("Inclui") ? dao.inserir(x) : dao.atualizar(x);
                }
                case "Funções" -> {
                    Funcao x = new Funcao(idOperacao(), texto(0)); daoFuncao dao = new daoFuncao();
                    salvo = operacao.equals("Inclui") ? dao.inserir(x) : dao.atualizar(x);
                }
                default -> {
                    Fase x = new Fase(idOperacao(), texto(0)); daoFase dao = new daoFase();
                    salvo = operacao.equals("Inclui") ? dao.inserir(x) : dao.atualizar(x);
                }
            }
            
            if (salvo) {
                JOptionPane.showMessageDialog(this, "Registro salvo com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCampos();
                
            } else {
                JOptionPane.showMessageDialog(this, "Nenhum registro foi alterado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } 
            
            } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe um código numérico válido.", "Dados inválidos", JOptionPane.WARNING_MESSAGE);
            
            } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "A data deve estar no formato AAAA-MM-DD.", "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        
            } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Não foi possível salvar no banco de dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int idOperacao() {
        return operacao.equals("Inclui") ? 0 : Integer.parseInt(id.getText().trim());
    }
    private String texto(int indice) { return campos.get(indice).getText().trim(); }
    private LocalDate dataOuNull(String texto) { return texto.isEmpty() ? null : LocalDate.parse(texto); }
    private void limparCampos() {
        id.setText(""); campos.forEach(c -> c.setText(""));
        if (senha != null) senha.setText("");
    }
    private String singular(String e) {
        return switch (e) { case "Engenheiros" -> "Engenheiro"; case "Funções" -> "Função"; case "Usuários" -> "Usuário"; default -> "Fase"; };
    }
}
