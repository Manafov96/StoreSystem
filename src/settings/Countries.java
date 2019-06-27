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
public class Countries extends javax.swing.JFrame {

    /**
     * Creates new form Countries
     */
    public Countries() {
        initComponents();
        Show_Channels();
        jTable1.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
    }

    private void Show_Channels() {
        try {
            String ID = null;
            String name = null;
            String sorting = null;
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("select C.ID, C.NAME, C.SORTING from N_COUNTRIES C order by C.SORTING");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ID = rs.getString("ID");
                name = rs.getString("NAME");
                sorting = rs.getString("SORTING");
                model.addRow(new Object[]{ID, name, sorting});
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

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jtbnUpdate = new javax.swing.JButton();
        jtbnExit = new javax.swing.JButton();
        jtxtID = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtxtName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxtSorting = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jtbnAdd = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Държави");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                onExit(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("Държави");

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Номер", "Държава", "Подредба"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
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
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(20);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(400);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(20);
        }

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

        jtxtID.setEditable(false);

        jLabel4.setText("Номер:");

        jLabel3.setText("Име:");

        jLabel5.setText("Подредба:");

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(291, 291, 291)
                .addComponent(jtbnUpdate)
                .addGap(62, 62, 62)
                .addComponent(jtbnExit)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(366, 366, 366))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jtxtID, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(39, 39, 39)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jtxtSorting, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addComponent(jtbnAdd)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addGap(51, 51, 51))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 803, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtSorting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtbnAdd)))
                .addGap(26, 26, 26)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtbnUpdate)
                    .addComponent(jtbnExit))
                .addGap(34, 34, 34))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jtbnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbnUpdateActionPerformed
        if (jTable1.getSelectedRow() == -1 || jtxtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Моля изберете държава, която искате да редактирате!");
        } else {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("update N_COUNTRIES\n"
                        + "set NAME = ?,\n"
                        + "    SORTING = ?\n"
                        + "where (ID = ?)");
                ps.setString(1, jtxtName.getText());
                ps.setInt(2, Integer.parseInt(jtxtSorting.getText()));
                ps.setString(3, jtxtID.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Успешно направихте промени!");
                jtxtID.setText("");
                jtxtName.setText("");
                jtxtSorting.setText("");

                Show_Channels();
                jtbnAdd.setEnabled(true);
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jtbnUpdateActionPerformed

    private void jtbnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbnExitActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jtbnExitActionPerformed

    private void jtbnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbnAddActionPerformed
        if (jtxtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Моля попълнете име на държава!");
        } else {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("insert into N_COUNTRIES (NAME, SORTING)\n"
                        + "values (?, ?)\n");
                ps.setString(1, jtxtName.getText());
                ps.setString(2, jtxtSorting.getText());

                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Записано в базата данни!");
                jtxtID.setText("");
                jtxtName.setText("");
                jtxtSorting.setText("");

                Show_Channels();

            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jtbnAddActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jtxtID.setText("");
        jtxtName.setText("");
        jtxtSorting.setText("");
        jtbnAdd.setEnabled(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void onExit(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onExit
        setVisible(false);
    }//GEN-LAST:event_onExit

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        jtxtID.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString());
        jtxtName.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 1).toString());
        if (!jTable1.getValueAt(jTable1.getSelectedRow(), 2).toString().isEmpty()) {
            jtxtSorting.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 2).toString());
        }
        jtbnAdd.setEnabled(false);
    }//GEN-LAST:event_jTable1MouseClicked

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
            java.util.logging.Logger.getLogger(Countries.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Countries.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Countries.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Countries.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Countries().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jtbnAdd;
    private javax.swing.JButton jtbnExit;
    private javax.swing.JButton jtbnUpdate;
    private javax.swing.JTextField jtxtID;
    private javax.swing.JTextField jtxtName;
    private javax.swing.JTextField jtxtSorting;
    // End of variables declaration//GEN-END:variables
}
