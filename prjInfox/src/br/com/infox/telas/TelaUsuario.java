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

/**
 *
 * @author Paulo Oliveira
 */
public class TelaUsuario extends javax.swing.JInternalFrame {

    // Usando a variável conexao do DAL
    Connection conexao;
    //criando variáveis especiais para a conexao com o bando
    //prepared statement e ResultSet são frameworks da do pacote java.sql
    // E servem para preparar e executar as instruções SQL
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Metodo construtor da tela de usuário
     */
    public TelaUsuario() {
        initComponents();
        // chamando o metodo de conexao com o bando de dados
        conexao = ModuloConexao.conector();
    }

    /**
     * Metodo usado para buscar informações sobre o usuário
     *
     */
    private void consultar() {
        String sql = "select * from tbusuarios where iduser =?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtUsId.getText());
            rs = pst.executeQuery();
            if (rs.next()) {
                txtusoNome.setText(rs.getString(2));
                txtUsoFone.setText(rs.getString(3));
                txtUsoLogin.setText(rs.getString(4));
                txtUsoSenha.setText(rs.getString(5));
                cboUsoPerfil.setSelectedItem(rs.getString(6));

            } else {
                JOptionPane.showMessageDialog(null, "Usuário não cadastrado");
                txtusoNome.setText(null);
                txtUsoFone.setText(null);
                txtUsoLogin.setText(null);
                txtUsoSenha.setText(null);
            }
            //conexao.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Metodo usado para cadastrar um novo usuário
     */
    private void cadastro() {
        String sql = "insert into tbusuarios values (?,?,?,?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtUsId.getText());
            pst.setString(2, txtusoNome.getText());
            pst.setString(3, txtUsoFone.getText());
            pst.setString(4, txtUsoLogin.getText());
            pst.setString(5, txtUsoSenha.getText());
            pst.setString(6, cboUsoPerfil.getSelectedItem().toString());
            // Verifica o preenchimento dos campos obrigatórios 
            if (txtUsId.getText().isEmpty() || txtusoNome.getText().isEmpty() || txtUsoLogin.getText().isEmpty() || txtUsoSenha.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
            } else {
                // a linha abaixo verifica se o usuario foi cadastrado com sucesso
                int cadastrado = pst.executeUpdate();
                if (cadastrado > 0) {
                    JOptionPane.showMessageDialog(null, "Usuário cadastrado com sucesso");
                    txtusoNome.setText(null);
                    txtUsId.setText(null);
                    txtUsoFone.setText(null);
                    txtUsoLogin.setText(null);
                    txtUsoSenha.setText(null);

                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    /**
     * Metodo responsável por deletar um usuário
     */
    private void deleteLine() {
        String sql = "delete from tbusuarios where iduser = ?";
        // solicita ao usuário a confirmação da exclusão dos dados  
        int confirma = JOptionPane.showConfirmDialog(null, "Tem ceteza que deseja excluir o registro?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == 0) {

            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtUsId.getText());
                int deletado = pst.executeUpdate();
                System.out.println();
                // Aviso ao usuário que a exclusão foi um sucesso
                if (deletado > 0) {
                    JOptionPane.showMessageDialog(null, "Usuário deletado com sucesso");
                    txtusoNome.setText(null);
                    txtUsId.setText(null);
                    txtUsoFone.setText(null);
                    txtUsoLogin.setText(null);
                    txtUsoSenha.setText(null);

                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }

    }

    /**
     * Metodo usado para modificar informações do usuário
     *
     * @param id
     * @param nome
     * @param tel
     * @param log
     * @param perfil
     * @param senha
     */
    private void alterTable(String id, String nome, String tel, String log, String perfil, String senha) {
        String sql = null;
        senha = txtUsoSenha.getText();
        id = txtUsId.getText();
        nome = txtusoNome.getText();
        tel = txtUsoFone.getText();
        log = txtUsoLogin.getText();
        perfil = cboUsoPerfil.getSelectedItem().toString();

        // O metodo so irá executar o comando upadate se o campo do id estiver preenchido
        if (!"".equals(id)) {
            //System.out.println(log);
            if (!"".equals(nome)) {
                sql = "update tbusuarios set usuario =? where iduser = ?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtusoNome.getText());
                    pst.setString(2, txtUsId.getText());
                    pst.execute();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
            // Verifica se o campo está vazio , caso esteja , nao irá executar o update na coluna FONE
            if (!"".equals(tel)) {
                sql = "update tbusuarios set fone = ? where iduser = ?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtUsoFone.getText());
                    pst.setString(2, txtUsId.getText());
                    pst.execute();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
            if (!"".equals(log)) {
                sql = "update tbusuarios set login = ? where iduser = ?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtUsoLogin.getText());
                    pst.setString(2, txtUsId.getText());
                    pst.execute();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
            if (!"".equals(perfil)) {
                sql = "update tbusuarios set perfil = ? where iduser = ?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, cboUsoPerfil.getSelectedItem().toString());
                    pst.setString(2, txtUsId.getText());
                    pst.execute();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
            if (!"".equals(senha)) {
                sql = "update tbusuarios set senha = ? where iduser = ?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtUsoSenha.getText());
                    pst.setString(2, txtUsId.getText());
                    pst.execute();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
            JOptionPane.showMessageDialog(null, "Informações modificadas com sucesso");
            txtusoNome.setText(null);
            txtUsId.setText(null);
            txtUsoFone.setText(null);
            txtUsoLogin.setText(null);
            txtUsoSenha.setText(null);

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtUsId = new javax.swing.JTextField();
        txtusoNome = new javax.swing.JTextField();
        txtUsoLogin = new javax.swing.JTextField();
        txtUsoSenha = new javax.swing.JTextField();
        cboUsoPerfil = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        txtUsoFone = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        btnAlter = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnBusca = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Usuários");
        setPreferredSize(new java.awt.Dimension(944, 518));

        jLabel1.setText("*id");

        jLabel2.setText("*Nome");

        jLabel3.setText("*Login");

        jLabel4.setText("*Senha");

        jLabel5.setText("*Perfil");

        txtUsoLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsoLoginActionPerformed(evt);
            }
        });

        txtUsoSenha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsoSenhaActionPerformed(evt);
            }
        });

        cboUsoPerfil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "admin", "user" }));

        jLabel6.setText("Fone");

        txtUsoFone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsoFoneActionPerformed(evt);
            }
        });

        btnAdd.setBackground(new java.awt.Color(102, 204, 0));
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/aaadd.png"))); // NOI18N
        btnAdd.setToolTipText("Adicionar");
        btnAdd.setBorder(null);
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnAlter.setBackground(new java.awt.Color(255, 204, 0));
        btnAlter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/aaalterar.png"))); // NOI18N
        btnAlter.setToolTipText("Alterar");
        btnAlter.setBorder(null);
        btnAlter.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAlter.setPreferredSize(new java.awt.Dimension(80, 80));
        btnAlter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(204, 51, 0));
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/aaapagar.png"))); // NOI18N
        btnDelete.setToolTipText("Deletar");
        btnDelete.setBorder(null);
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnBusca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/aaaBuscar.png"))); // NOI18N
        btnBusca.setToolTipText("Consultar");
        btnBusca.setBorder(null);
        btnBusca.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBusca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscaActionPerformed(evt);
            }
        });

        jLabel7.setText("*Campos obrigatórios");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtusoNome)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtUsId, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addGap(91, 91, 91))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(txtUsoFone)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3)
                                .addGap(30, 30, 30)
                                .addComponent(txtUsoLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboUsoPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtUsoSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(356, 356, 356))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(860, 860, 860))
            .addGroup(layout.createSequentialGroup()
                .addGap(209, 209, 209)
                .addComponent(btnAdd)
                .addGap(18, 18, 18)
                .addComponent(btnBusca)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAlter, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDelete)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUsId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel7)))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtusoNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsoFone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel3)
                    .addComponent(txtUsoLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsoSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboUsoPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnAdd)
                        .addComponent(btnBusca))
                    .addComponent(btnDelete)
                    .addComponent(btnAlter, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        setBounds(0, 0, 578, 413);
    }// </editor-fold>//GEN-END:initComponents

    private void txtUsoLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsoLoginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsoLoginActionPerformed

    private void txtUsoSenhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsoSenhaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsoSenhaActionPerformed

    private void txtUsoFoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsoFoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsoFoneActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteLine();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnBuscaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscaActionPerformed
        // TODO add your handling code here:
        consultar();
    }//GEN-LAST:event_btnBuscaActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        cadastro();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnAlterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterActionPerformed
        // TODO add your handling code here:
        alterTable(null, null, null, null, null, null);
    }//GEN-LAST:event_btnAlterActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAlter;
    private javax.swing.JButton btnBusca;
    private javax.swing.JButton btnDelete;
    private javax.swing.JComboBox<String> cboUsoPerfil;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField txtUsId;
    private javax.swing.JTextField txtUsoFone;
    private javax.swing.JTextField txtUsoLogin;
    private javax.swing.JTextField txtUsoSenha;
    private javax.swing.JTextField txtusoNome;
    // End of variables declaration//GEN-END:variables
}
