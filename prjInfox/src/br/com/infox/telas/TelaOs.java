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
import java.util.HashMap;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Paulo Oliveira
 */
public class TelaOs extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    private String tipo = null;

    /**
     * Metodo conatrutos da TelaOs
     */
    public TelaOs() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    /**
     * metodo responsavel pela busca de cliente usando o nome como referencia
     */
    private void pesquisarCliente() {
        String sql = "select idcli as id, nomecli as nome, fonecli as fone  from tbcliente where nomecli like ?";
        try {
            pst = conexao.prepareStatement(sql);
            //passa o conteúdo da cauxa de pesquisa para o ? da string sql
            //o "%" completa a string sql
            pst.setString(1, txtCli.getText() + "%");
            rs = pst.executeQuery();
            // a linha abixo preenche a tabela usando recursos da bilioteca
            //rs2.xml.jar
            tblClientes.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    /**
     * Metodo para setar o id do usuario no campo id
     */
    private void setar() {
        int set = tblClientes.getSelectedRow();
        txtIdCli.setText(tblClientes.getModel().getValueAt(set, 0).toString());

    }

    /**
     * Metodo usado para criar uma nova ordem de serviço
     */
    private void addOs() {
        String sql = "insert into tbos (tipo , situacao, equipamento, defeito, servico, tecnico, valor, idcli) values (?,?,?,?,?,?,?,?)";
        if (txtIdCli.getText().isEmpty() || txtEquip.getText().isEmpty() || txtDef.getText().isEmpty() || cboOsSit.getSelectedItem() == " ") {

            JOptionPane.showMessageDialog(null, "<html><h2> Preencha todos os campos necessários</h2></html>");

        } else {
            try {

                pst = conexao.prepareStatement(sql);
                pst.setString(1, tipo);
                pst.setString(2, cboOsSit.getSelectedItem().toString());
                pst.setString(3, txtEquip.getText());
                pst.setString(4, txtDef.getText());
                pst.setString(5, txtServ.getText());
                pst.setString(6, txtTec.getText());
                //.replace() troca o ponto pela virgula
                pst.setString(7, txtValor.getText().replace(",", "."));
                pst.setString(8, txtIdCli.getText());
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    //Tela de confirmação de emissão de ordem de serviço
                    JOptionPane.showMessageDialog(null, "<html> <h2> <span Style = color:blue> Ordem de serviço cadastrada com sucesso </html>");

                    // fazendo a busca da os recem cadrastrada
                    sql = "select max(os) from tbos  ";
                    //exceçãao para a segunda busca
                    try {
                        pst = conexao.prepareStatement(sql);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            txtOs.setText(rs.getString(1));
                        } else {
                            JOptionPane.showMessageDialog(null, "Não foi possível "
                                    + "encontrar o id da os cadrastrada");
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                    //fim da exceçao 

                    // A condiçao abaixo diz se o cadastro falhou
                    // verificando se a proxima ação será imprimir ou não a os 
                    int decisao = JOptionPane.showConfirmDialog(null, "<html><h2><b> DESEJA IMPRIMIR A OS? </html> ",
                            "Atenção", JOptionPane.YES_NO_OPTION);
                    if (decisao == 0) {
                        imprimirOs();
                        limpar();
                    } else {
                        limpar();
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Algo deu errado");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }

    }

    /**
     * metodo usado para fazer a busca de uma ordem de serviço
     */
    private void buscarOs() {
        // pedindo o número da os que se deseja buscar
        String confirmar = JOptionPane.showInputDialog("numero da os");

        String sql = "select date_format(data_os,'%d/%m/%y - %h:%i'), os, tipo, "
                + "situacao,equipamento,defeito, servico, tecnico, valor "
                + "from tbos  where os = ? ";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, confirmar);
            rs = pst.executeQuery();
            if (rs.next()) {
                txtOs.setText(rs.getString(2));
                txtData.setText(rs.getString(1));
                cboOsSit.setSelectedItem(rs.getString(4));
                txtEquip.setText(rs.getString(5));
                txtServ.setText(rs.getString(7));
                txtTec.setText(rs.getString(8));
                txtValor.setText(rs.getString(9));
                txtDef.setText(rs.getString(6));

                if ("orçamento".equals(rs.getString(3))) {
                    rbtOrc.doClick();
                } else {
                    rbtOrdem.doClick();
                }
                //gerenciando os botoes
                btnBusca.setEnabled(false);
                btnAdd.setEnabled(false);
                tblClientes.setVisible(false);
                btnDelete.setEnabled(true);
                btnPrint.setEnabled(true);
                btnAlter.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(null, "Os não encontrada");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    /**
     * Metodo usado para alterar uma ordem de serviço
     */
    private void alter() {
        String sql = "update tbos set tipo =?, situacao =?, equipamento =?, defeito =?,  servico =?, tecnico =?, valor =?"
                + "where os =? ";
        if (txtEquip.getText().isEmpty() || txtDef.getText().isEmpty() || cboOsSit.getSelectedItem() == " "
                || txtTec.getText().isEmpty() || txtValor.getText().isEmpty() || txtServ.getText().isEmpty()) {

            JOptionPane.showMessageDialog(null, "<html><h2> Preencha todos os campos necessários</h2></html>");
        } else {
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, tipo);
                pst.setString(2, cboOsSit.getSelectedItem().toString());
                pst.setString(3, txtEquip.getText());
                pst.setString(4, txtDef.getText());
                pst.setString(5, txtServ.getText());
                pst.setString(6, txtTec.getText());
                pst.setString(7, txtValor.getText());
                pst.setString(8, txtOs.getText());
                int executar = pst.executeUpdate();
                if (executar > 0) {
                    JOptionPane.showMessageDialog(null, "<html><h2>Alterações realizadas com sucesso ");

                    txtOs.setText(null);
                    txtData.setText(null);
                }

                limpar();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    /**
     * Metodo para apagar uma ordem de serviço
     */
    private void deletar() {
        String sql = " delete from tbos where os =? ";
        int decisao = JOptionPane.showConfirmDialog(null, "Certeza que deseja excluir esse serviço?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (decisao == 0) {
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtOs.getText());
                pst.execute();
                JOptionPane.showMessageDialog(null, "<html><h2>Serviço apagado com sucesso ");
                limpar();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    /**
     * Metodo para imprimir uma OS
     */
    @SuppressWarnings("unchecked")
    private void imprimirOs() {

        try {
            //a calsee abaixo captura o id da os
            HashMap filtro = new HashMap();
            filtro.put("os", Integer.valueOf(txtOs.getText()));
            // o comando abaixo usa fraeworks para encontrar o arqquivo 
           JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/reports/os.jasper") , filtro, conexao
                );

            // o framework abaixo apresenta o que foi encontrado no caminho acima 
            JasperViewer.viewReport(print, false);
            limpar();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Metodo usado para limpar e gerenciar os objetos da tela os
     */
    private void limpar() {
        //limpando todos os campos
        txtEquip.setText(null);
        txtDef.setText(null);
        txtServ.setText(null);
        txtValor.setText(null);
        txtTec.setText(null);
        txtIdCli.setText(null);
        cboOsSit.setSelectedItem(" ");
        txtOs.setText(null);
        txtData.setText(null);

        //Gerênciando os botões 
        //habilitando
        //verificando se estão desabilitados para habilitar
        if (!btnBusca.isEnabled() && !btnAdd.isEnabled()) {
            btnAdd.setEnabled(true);
            btnBusca.setEnabled(true);
        }

        //desabilitando botoes
        //verificando se estao ativados
        if (btnDelete.isEnabled() && btnPrint.isEnabled() && btnAlter.isEnabled() && btnAlter.isEnabled()) {
            btnDelete.setEnabled(false);
            btnPrint.setEnabled(false);
            btnAlter.setEnabled(false);
            btnPrint.setEnabled(false);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtOs = new javax.swing.JTextField();
        txtData = new javax.swing.JTextField();
        rbtOrc = new javax.swing.JRadioButton();
        rbtOrdem = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        cboOsSit = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        txtCli = new javax.swing.JTextField();
        txtIdCli = new javax.swing.JTextField();
        txtDef = new javax.swing.JTextField();
        txtEquip = new javax.swing.JTextField();
        txtServ = new javax.swing.JTextField();
        txtTec = new javax.swing.JTextField();
        txtValor = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        btnBusca = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnAlter = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();

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

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("OS");
        setPreferredSize(new java.awt.Dimension(640, 480));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("N°OS");

        jLabel2.setText("DATA");

        txtOs.setEditable(false);

        txtData.setEditable(false);

        buttonGroup1.add(rbtOrc);
        rbtOrc.setText("orçamento");
        rbtOrc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOrcActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbtOrdem);
        rbtOrdem.setText("os");
        rbtOrdem.setActionCommand("orçamento");
        rbtOrdem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOrdemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtOs, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel1)
                        .addGap(54, 54, 54)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(rbtOrc)
                        .addGap(33, 33, 33)
                        .addComponent(rbtOrdem)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtOrc)
                    .addComponent(rbtOrdem))
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Situação");

        cboOsSit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Na bancada", "Entrega ok", "Orçamento reprovaado", "Aguardando aprovação", "Aguardando peças", "Abandonado pelo cliente", "Retornou" }));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("CLIENTE"));

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "id", "Nome", "Fone"
            }
        ));
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblClientes);

        jLabel4.setText("*id");

        txtCli.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtCliCaretUpdate(evt);
            }
        });

        txtIdCli.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(txtCli, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                        .addComponent(txtIdCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addGap(43, 43, 43))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(txtCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIdCli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))))
                .addGap(20, 20, 20)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtTec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTecActionPerformed(evt);
            }
        });

        txtValor.setText("0");
        txtValor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtValorActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("*Equipamento");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("*Defeito");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Serviço");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Tecnico");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Valor total");

        btnBusca.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/aaaBuscar.png"))); // NOI18N
        btnBusca.setBorder(null);
        btnBusca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscaActionPerformed(evt);
            }
        });

        btnAdd.setBackground(new java.awt.Color(102, 204, 0));
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/aaadd.png"))); // NOI18N
        btnAdd.setBorder(null);
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(204, 51, 0));
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/aaapagar.png"))); // NOI18N
        btnDelete.setBorder(null);
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnAlter.setBackground(new java.awt.Color(255, 204, 0));
        btnAlter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/aaalterar.png"))); // NOI18N
        btnAlter.setBorder(null);
        btnAlter.setEnabled(false);
        btnAlter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterActionPerformed(evt);
            }
        });

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/aImprime.png"))); // NOI18N
        btnPrint.setToolTipText("Imprimir OS");
        btnPrint.setBorder(null);
        btnPrint.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPrint.setEnabled(false);
        btnPrint.setPreferredSize(new java.awt.Dimension(80, 80));
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(cboOsSit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(1, 1, 1)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEquip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtServ, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                    .addComponent(txtDef)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtTec, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel10)
                        .addGap(26, 26, 26)
                        .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAdd)
                        .addGap(42, 42, 42)
                        .addComponent(btnBusca)
                        .addGap(41, 41, 41)
                        .addComponent(btnAlter)
                        .addGap(36, 36, 36)
                        .addComponent(btnDelete)
                        .addGap(105, 105, 105)
                        .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDef, txtEquip, txtServ});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboOsSit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEquip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtServ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBusca, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnAdd, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnAlter, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnDelete, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(75, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtDef, txtEquip, txtServ});

        setBounds(0, 0, 697, 514);
    }// </editor-fold>//GEN-END:initComponents

    private void txtTecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTecActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTecActionPerformed

    private void txtValorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtValorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtValorActionPerformed

    private void btnAlterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterActionPerformed
        // chamando o metodo alterar
        alter();

    }//GEN-LAST:event_btnAlterActionPerformed

    private void txtCliCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtCliCaretUpdate
        // TODO add your handling code here:
        pesquisarCliente();
    }//GEN-LAST:event_txtCliCaretUpdate

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        // TODO add your handling code here:
        setar();
    }//GEN-LAST:event_tblClientesMouseClicked

    private void rbtOrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOrcActionPerformed
        // TODO add your handling code here:
        tipo = "orçamento";
    }//GEN-LAST:event_rbtOrcActionPerformed

    private void rbtOrdemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOrdemActionPerformed
        // TODO add your handling code here:
        tipo = "OS";
    }//GEN-LAST:event_rbtOrdemActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // programando uma ação ao abrir a tela form
        rbtOrc.doClick();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        addOs();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnBuscaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscaActionPerformed
        // TODO add your handling code here:
        buscarOs();
    }//GEN-LAST:event_btnBuscaActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // chamando o metodo deletar
        deletar();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // immprimindo a os
        int confirma = JOptionPane.showConfirmDialog(null, "<html>Confirma imprimir essa <b>OS?</b>", "Atenção",
                JOptionPane.YES_NO_OPTION);
        if (confirma == 0) {
            imprimirOs();
        }
    }//GEN-LAST:event_btnPrintActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAlter;
    private javax.swing.JButton btnBusca;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnPrint;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboOsSit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JRadioButton rbtOrc;
    private javax.swing.JRadioButton rbtOrdem;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtCli;
    private javax.swing.JTextField txtData;
    private javax.swing.JTextField txtDef;
    private javax.swing.JTextField txtEquip;
    private javax.swing.JTextField txtIdCli;
    private javax.swing.JTextField txtOs;
    private javax.swing.JTextField txtServ;
    private javax.swing.JTextField txtTec;
    private javax.swing.JTextField txtValor;
    // End of variables declaration//GEN-END:variables
}
