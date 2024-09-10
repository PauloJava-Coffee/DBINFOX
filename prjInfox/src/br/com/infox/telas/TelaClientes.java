/*
 * The MIT License
 *
 * Copyright 2024 Paulo Oliveira.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.com.infox.telas;

import java.sql.*;
import br.com.infox.dal.ModuloConexao;
import javax.swing.JOptionPane;
// a linha abaixo importa a biblioteca rs2.xml.jar
import net.proteanit.sql.DbUtils;

/**
 *
 * @author paulo Oliveira
 */
public class TelaClientes extends javax.swing.JInternalFrame {

    //Variável de conexão
    Connection conexao;
    //variaveis do pacote java.sql
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Metodo contrutor TelaClientes
     */
    public TelaClientes() {
        initComponents();
        conexao = ModuloConexao.conector();
        
    }

    /**
     * Metodo usado para cadastrar um novo cliente
     */
    private void addCliente() {
        String sql = "insert into tbcliente(nomecli,endcli,fonecli,emailcli) values (?,?,?,?)";
        if (txtCliNome.getText().isEmpty() || txtCliFone.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Preencha todos os campos");

        } else {
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCliNome.getText());
                pst.setString(2, txtCliEnd.getText());
                pst.setString(3, txtCliFone.getText());
                pst.setString(4, txtCliEmail.getText());
                // a variavel abaixo chama o metodo que executa a açao de alterar
                //a tabela e o metodo retorna um numero maior que zero para a variavel
                int clienteAdd = pst.executeUpdate();
                //verifica se o cliente realmente foi adicionado e mostra uma 
                //mensagem de confirmaçao ao usuário
                // obs: caso o cliente seja adicionado , a variavel sera maior que 0f
                if (clienteAdd > 0) {
                    JOptionPane.showMessageDialog(null, "Cliente adicionado com sucesso");
                    txtCliNome.setText(null);
                    txtCliEnd.setText(null);
                    txtCliFone.setText(null);
                    txtCliEmail.setText(null);

                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    /**
     * Metodo responsável por remover um cliente
     */
    private void removeCli() {
        String sql = "delete from tbcliente where nomecli = ? and endcli = ? and fonecli = ?";
        if (txtCliNome.getText().isEmpty()) {

            JOptionPane.showMessageDialog(null, "<html> <h3>escreva nome, endereço e telefone do cliente que deseja remover</html>");
        } else {
            int confirm = JOptionPane.showConfirmDialog(null, "deseja excluir este cliente?", "Atenção", JOptionPane.YES_NO_OPTION);
            if (confirm == 0) {

                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtCliNome.getText());
                    pst.setString(2, txtCliEnd.getText());
                    pst.setString(3, txtCliFone.getText());
                    int remover = pst.executeUpdate();
                    if (remover > 0) {
                        JOptionPane.showMessageDialog(null, "Cliente removido com sucesso");
                        limpar();
                    }

                } catch (Exception e) {

                }
            }
        }

    }

    /**
     * Metodo usado para pesquisar um cliente
     */
    private void pesquisarCliente() {
        String sql = "select idcli as id, nomecli as nome, endcli as endereço, fonecli as fone , emailcli as email from tbcliente where nomecli like ?";
        try {
            pst = conexao.prepareStatement(sql);
            //passa o conteúdo da cauxa de pesquisa para o ? da string sql
            //o "%" completa a string sql
            pst.setString(1, txtCliPesquisar.getText() + "%");
            rs = pst.executeQuery();
            // a linha abixo preenche a tabela usando recursos da bilioteca
            //rs2.xml.jar
            tblClientes.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {

        }
    }

    /**
     * Metodo usado para preencher as informações do cliente na tela
     */

    private void setar() {
        int set = tblClientes.getSelectedRow();
        txtCliNome.setText(tblClientes.getModel().getValueAt(set, 1).toString());
        txtCliEnd.setText(tblClientes.getModel().getValueAt(set, 2).toString());
        txtCliFone.setText(tblClientes.getModel().getValueAt(set, 3).toString());
        txtCliEmail.setText(tblClientes.getModel().getValueAt(set, 4).toString());
        txtIdCli.setText(tblClientes.getModel().getValueAt(set, 0).toString());

    }

    /**
     * Metodo usado para alterar um registro de cliente
     */
    private void alterar() {
        String sql = null;
        //estrutura de decisão verifica se o campo do nomme do cliente esta vazio 
        //antes de executar os comandos 
        if (txtIdCli.getText().isEmpty()) {

        } else {
            //estutura verifica se o campo do endereço esta preencido
            //caso esteja executara o comando de alterar por aquilo que estiver
            // escrito no campo
            if (!txtCliNome.getText().isEmpty()) {
                sql = " update tbcliente set nomecli = ? where idcli = ?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtCliNome.getText());
                    pst.setString(2, txtIdCli.getText());
                    pst.execute();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }

            //estutura verifica se o campo do endereço esta preencido
            //caso esteja executara o comando de alterar por aquilo que estiver
            // escrito no campo
            if (!txtCliEnd.getText().isEmpty()) {
                sql = " update tbcliente set endcli = ? where idcli = ?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtCliEnd.getText());
                    pst.setString(2, txtIdCli.getText());
                    pst.execute();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }

            //estutura verifica se o campo do telefone esta preencido
            //caso esteja executara o comando de alterar por aquilo que estiver
            // escrito no campo
            if (!txtCliFone.getText().isEmpty()) {
                sql = " update  tbcliente set fonecli = ?  where idcli = ?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtCliFone.getText());
                    pst.setString(2, txtIdCli.getText());
                    pst.execute();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }

            }

            //estutura verifica se o campo do email esta preencido
            //caso esteja executara o comando de alterar por aquilo que estiver
            // escrito no campo
            if (!txtCliEmail.getText().isEmpty()) {
                sql = " update  tbcliente set emailcli = ? where idcli = ?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtCliEmail.getText());
                    pst.setString(2, txtIdCli.getText());
                    pst.execute();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }

            }
            if (!txtCliEmail.getText().isEmpty() || !txtCliEmail.getText().isEmpty() || !txtCliEnd.getText().isEmpty() || !txtCliNome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "<html><h2><span Style = color:blue> Alteração realizada com sucesso </span></html>");
                limpar();
            } else {
                JOptionPane.showMessageDialog(null, "<html><h2><span Style = color:red> Nenhuma alteração realizada </span></html>");
            }
        }
    }

    /**
     * Metodo usado para limpar as inforações da tela
     */
    private void limpar() {
        txtCliNome.setText(null);
        txtCliEnd.setText(null);
        txtCliFone.setText(null);
        txtCliEmail.setText(null);
        txtIdCli.setText(null);
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtCliFone = new javax.swing.JTextField();
        txtCliEmail = new javax.swing.JTextField();
        txtCliNome = new javax.swing.JTextField();
        txtCliEnd = new javax.swing.JTextField();
        btnAlter = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        txtCliPesquisar = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        txtIdCli = new javax.swing.JTextField();

        jButton1.setText("jButton1");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setBorder(javax.swing.BorderFactory.createTitledBorder("                                                                                              Clientes"));
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);

        jLabel1.setText("*Nome");

        jLabel2.setText("Endereço");

        jLabel3.setText("Email");

        jLabel4.setText("*Telefone");

        jLabel5.setText("*Campos obrigatórios");

        txtCliEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCliEndActionPerformed(evt);
            }
        });

        btnAlter.setBackground(new java.awt.Color(255, 204, 51));
        btnAlter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/aaalterar.png"))); // NOI18N
        btnAlter.setToolTipText("Alterar");
        btnAlter.setBorder(null);
        btnAlter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(153, 51, 0));
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/aaapagar.png"))); // NOI18N
        btnDelete.setToolTipText("Deletar");
        btnDelete.setBorder(null);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnAdd.setBackground(new java.awt.Color(0, 255, 153));
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/aaadd.png"))); // NOI18N
        btnAdd.setToolTipText("Adicionar");
        btnAdd.setBorder(null);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        txtCliPesquisar.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtCliPesquisarCaretUpdate(evt);
            }
        });
        txtCliPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCliPesquisarKeyReleased(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/aPesquisa.png"))); // NOI18N

        tblClientes = new javax.swing.JTable(){
            public boolean isCellEdtable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "id", "Nome", "fone", "endereço", "e-mail"
            }
        ));
        tblClientes.setFocusable(false);
        tblClientes.getTableHeader().setReorderingAllowed(false);
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblClientes);

        jLabel7.setText("id");

        txtIdCli.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(txtCliEnd, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtCliEmail, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtCliFone, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCliNome, javax.swing.GroupLayout.PREFERRED_SIZE, 554, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 64, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addGap(19, 19, 19))))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(txtIdCli, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(205, 205, 205)
                        .addComponent(btnAdd)
                        .addGap(43, 43, 43)
                        .addComponent(btnAlter)
                        .addGap(53, 53, 53)
                        .addComponent(btnDelete)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtIdCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCliNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCliEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCliFone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCliEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnDelete)
                        .addComponent(btnAlter, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(btnAdd, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(697, 515));
    }// </editor-fold>//GEN-END:initComponents

    private void txtCliEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCliEndActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCliEndActionPerformed

    private void btnAlterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterActionPerformed
        // TODO add your handling code here:
        alterar();
        pesquisarCliente();
    }//GEN-LAST:event_btnAlterActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        addCliente();
        pesquisarCliente();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        removeCli();
        pesquisarCliente();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void txtCliPesquisarCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtCliPesquisarCaretUpdate
        // TODO add your handling code here:
        pesquisarCliente();
    }//GEN-LAST:event_txtCliPesquisarCaretUpdate

    private void txtCliPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliPesquisarKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCliPesquisarKeyReleased

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        // TODO add your handling code here:
        setar();
    }//GEN-LAST:event_tblClientesMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAlter;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtCliEmail;
    private javax.swing.JTextField txtCliEnd;
    private javax.swing.JTextField txtCliFone;
    private javax.swing.JTextField txtCliNome;
    private javax.swing.JTextField txtCliPesquisar;
    private javax.swing.JTextField txtIdCli;
    // End of variables declaration//GEN-END:variables
}
