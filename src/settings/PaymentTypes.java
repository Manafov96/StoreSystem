/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings;

import consultation.ConsultationDelivery;
import java.awt.Font;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import static Tools.getConnection.getConnection;

/**
 *
 * @author Viko
 */
public class PaymentTypes extends javax.swing.JFrame {

    /**
     * Creates new form PaymentTypes
     */
    public PaymentTypes() {
        initComponents();
        Show_Payments();
        jTable1.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
    }

    private void Show_Payments() {
        try {
            String ID = null;
            String nameBG = null;
            String nameEN = null;
            String nameDE = null;
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("select PM.ID, PM.NAME_BUL, PM.NAME_ENG, PM.NAME_GERM from N_PAYMENT_METHODS PM");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ID = rs.getString("ID");
                nameBG = rs.getString("NAME_BUL");
                nameEN = rs.getString("NAME_ENG");
                nameDE = rs.getString("NAME_GERM");
                model.addRow(new Object[]{ID, nameBG, nameEN, nameDE});
            }
        } catch (SQLException e) {
            Logger.getLogger(ConsultationDelivery.class.getName()).log(Level.SEVERE, null, e);
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

        jtbnUpdate = new javax.swing.JButton();
        jtbnExit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jtxtID = new javax.swing.JTextField();
        jtxtNameBG = new javax.swing.JTextField();
        jtxtNameDE = new javax.swing.JTextField();
        jtxtNameEN = new javax.swing.JTextField();
        jtbnAdd = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Начини на плащане");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                onExit(evt);
            }
        });

        jtbnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/article/Update32.png"))); // NOI18N
        jtbnUpdate.setText("Обнови");
        jtbnUpdate.setFocusPainted(false);
        jtbnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbnUpdateActionPerformed(evt);
            }
        });

        jtbnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/Delete32.png"))); // NOI18N
        jtbnExit.setText("Изход");
        jtbnExit.setFocusPainted(false);
        jtbnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbnExitActionPerformed(evt);
            }
        });

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Номер", "Име(БГ)", "Име(ENG)", "Име(DE)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(10);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(200);
        }

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("Начини на плащане");

        jtxtID.setEditable(false);

        jtbnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/article/Add32.png"))); // NOI18N
        jtbnAdd.setText("Добави");
        jtbnAdd.setFocusPainted(false);
        jtbnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbnAddActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/article/clear24.png"))); // NOI18N
        jButton1.setText("Изчисти");
        jButton1.setFocusPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Номер:");

        jLabel3.setText("Име(БГ):");

        jLabel4.setText("Име(EN):");

        jLabel5.setText("Име(DE):");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(364, 364, 364)
                .addComponent(jtbnUpdate)
                .addGap(87, 87, 87)
                .addComponent(jtbnExit)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(378, 378, 378)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(86, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtID, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtNameBG, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtNameEN, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jtxtNameDE, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jtbnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 859, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(78, 78, 78))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtbnAdd)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtNameDE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtNameEN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtNameBG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtbnUpdate)
                    .addComponent(jtbnExit))
                .addGap(0, 30, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void onExit(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onExit
        setVisible(false);
    }//GEN-LAST:event_onExit

    private void jtbnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbnExitActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jtbnExitActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        jtxtID.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString());
        jtxtNameBG.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 1).toString());
        jtxtNameEN.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 2).toString());
        jtxtNameDE.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 3).toString());
        jtbnAdd.setEnabled(false);
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jtxtID.setText("");
        jtxtNameBG.setText("");
        jtxtNameEN.setText("");
        jtxtNameDE.setText("");
        jtbnAdd.setEnabled(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jtbnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbnAddActionPerformed
        if (jtxtNameBG.getText().isEmpty() || jtxtNameEN.getText().isEmpty() || jtxtNameDE.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Моля попълнете всички полета!");
        } else {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("insert into N_PAYMENT_METHODS (NAME_BUL, NAME_ENG, NAME_GERM)\n"
                        + "values (?, ?, ?)");
                ps.setString(1, jtxtNameBG.getText());
                ps.setString(2, jtxtNameEN.getText());
                ps.setString(3, jtxtNameDE.getText());

                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Записано в базата данни!");
                jtxtID.setText("");
                jtxtNameBG.setText("");
                jtxtNameEN.setText("");
                jtxtNameDE.setText("");

                Show_Payments();

            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jtbnAddActionPerformed

    private void jtbnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbnUpdateActionPerformed
        if (jTable1.getSelectedRow() == -1 || jtxtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Моля изберете типа плащане, който искате да редактирате!");
        } else {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("update N_PAYMENT_METHODS\n"
                        + "set NAME_BUL = ?,\n"
                        + "    NAME_ENG = ?,\n"
                        + "    NAME_GERM = ?\n"
                        + "where (ID = ?)");
                ps.setString(1, jtxtNameBG.getText());
                ps.setString(2, jtxtNameEN.getText());
                ps.setString(3, jtxtNameDE.getText());
                ps.setString(4, jtxtID.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Успешно направихте промени!");
                jtxtID.setText("");
                jtxtNameBG.setText("");
                jtxtNameEN.setText("");
                jtxtNameDE.setText("");

                Show_Payments();
                jtbnAdd.setEnabled(true);
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jtbnUpdateActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PaymentTypes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PaymentTypes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PaymentTypes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PaymentTypes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PaymentTypes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jtbnAdd;
    private javax.swing.JButton jtbnExit;
    private javax.swing.JButton jtbnUpdate;
    private javax.swing.JTextField jtxtID;
    private javax.swing.JTextField jtxtNameBG;
    private javax.swing.JTextField jtxtNameDE;
    private javax.swing.JTextField jtxtNameEN;
    // End of variables declaration//GEN-END:variables
}
